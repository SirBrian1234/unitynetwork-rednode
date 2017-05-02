package kostiskag.unitynetwork.rednode.redThreads;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.packets.IPv4Packet;
import kostiskag.unitynetwork.rednode.Routing.packets.UnityPacket;

/**
 * here we will listen for incoming traffic a fish packet is needed to start
 *
 * @author kostis
 * 
 */
public class RedReceive extends Thread {

    private final String pre = "^RedReceive ";
    private final InetAddress address;
    private final int port;
    private DatagramSocket socket;
    private AtomicBoolean kill = new AtomicBoolean(true);
    
    public RedReceive(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {

        byte[] sendData;
        byte[] buffer = new byte[2048];
        byte[] ACK = ("00004 [ACK]").getBytes();
        
        socket = null;
        try {
            socket = new DatagramSocket();
        } catch (java.net.BindException ex1) {
            App.login.writeInfo(pre + "SOCKET ALLREADY BINED EXCEPTION");
        } catch (SocketException ex) {
            Logger.getLogger(RedReceive.class.getName()).log(Level.SEVERE, null, ex);
        }

        sendData = "FISH PACKET".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        try {
            for (int i = 0; i < 3; i++) {
                socket.send(sendPacket);
            }
        } catch (java.net.SocketException ex1) {
            App.login.monitor.writeToConnectionDown("FISH PACKET SEND ERROR");
            return;
        } catch (IOException ex) {            
            Logger.getLogger(RedReceive.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        while (!kill.get()) {
            try {
                socket.receive(receivePacket);
            } catch (java.net.SocketException ex) {
                return;
            } catch (IOException ex) {
                Logger.getLogger(RedReceive.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            String modifiedSentence;
            int len = receivePacket.getLength();
            if (len > 0 && len <= 1500) {
                byte[] packet = new byte[len];
                System.arraycopy(receivePacket.getData(), 0, packet, 0, len);

                String version = IPv4Packet.getVersion(packet);
                if (version.equals("0")) {
                	//bn command packets
                    byte[] payload = UnityPacket.getPayload(packet);
                    modifiedSentence = new String(payload) + '\0';
                    if (modifiedSentence.startsWith("00001")) {
                        App.login.monitor.writeToCommands("DPING OK");
                        App.login.connection.isClientDPinged = true;
                    }
                    App.login.monitor.writeToConnectionDown(version + " " + modifiedSentence);
                    
                } else if (version.equals("1")){
                	//rn command packets
                    byte[] payload = UnityPacket.getPayload(packet);
                    modifiedSentence = new String(payload) + '\0';
                    if (modifiedSentence.startsWith("00004")) {                        
                        if (App.login.connection.arpTable.isAssociated(UnityPacket.getSourceAddress(packet)))
							try {
								App.login.connection.getTrafficMan().gotACK();
							} catch (Exception e) {
								e.printStackTrace();
							}
                    }
                    App.login.monitor.writeToConnectionDown(version + " " + modifiedSentence);
                    
                } else if (version.equals("2")) {
                	//chat packets
                    byte[] payload = new byte[UnityPacket.getPayload(packet).length];
                    payload = UnityPacket.getPayload(packet);
                    modifiedSentence = new String(payload) + '\0';
                    App.login.monitor.writeToConnectionDown(version + " " + modifiedSentence);
                    
                } else if (version.equals("45")) {
                	//ipv4 packets
                    App.login.monitor.writeToConnectionDown(version + " IPv4 Len: " + packet.length + " From " + IPv4Packet.getSourceAddress(packet).getHostAddress());
                    App.login.monitor.updateConUpBufferQueue(App.login.connection.downMan.getlen());
                    App.login.connection.downMan.offer(packet);                    
                    ACK = UnityPacket.buildPacket(("00004 [ACK]").getBytes(), App.login.connection.MyIP, IPv4Packet.getSourceAddress(packet), 1);
                    App.login.connection.upMan.offer(ACK);
                }
            } else {
                System.out.println("wrong len");
            }
        }
        App.login.connection.downMan.clear();
    }

    public void kill() {
        kill.set(true);
        socket.close();
    }
}
