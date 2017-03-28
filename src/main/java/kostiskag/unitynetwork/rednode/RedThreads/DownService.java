package kostiskag.unitynetwork.rednode.RedThreads;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import kostiskag.unitynetwork.rednode.GUI.MonitorWindow;
import kostiskag.unitynetwork.rednode.RedNode.lvl3RedNode;
import kostiskag.unitynetwork.rednode.Routing.Data.Packet;
import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kostis
 *
 * here we will listen for incoming traffic a fish packet is needed to start
 *
 *
 */
public class DownService extends Thread {

    private static String pre = "^DOWN ";
    private boolean kill = false;
    private static InetAddress address;
    private static int port;
    private static DatagramSocket clientSocket;
    private String vaddress;
    private String modifiedSentence;

    public DownService(String vaddres, InetAddress address, int port) {
        this.address = address;
        this.port = port;
        this.vaddress = vaddres;
    }

    @Override
    public void run() {

        byte[] sendData;
        byte[] buffer = new byte[2048];
        byte[] ACK = ("00004 [ACK]").getBytes();
        
        clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (java.net.BindException ex1) {
            lvl3RedNode.login.writeInfo(pre + "SOCKET ALLREADY BINED EXCEPTION");
        } catch (SocketException ex) {
            Logger.getLogger(DownService.class.getName()).log(Level.SEVERE, null, ex);
        }

        sendData = "FISH PACKET".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        try {
            for (int i = 0; i < 3; i++) {
                clientSocket.send(sendPacket);
            }
        } catch (java.net.SocketException ex1) {
            lvl3RedNode.login.monitor.writeToDown("FISH PACKET SEND ERROR");
            return;
        } catch (IOException ex) {            
            Logger.getLogger(DownService.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        while (!kill) {
            try {
                clientSocket.receive(receivePacket);
            } catch (java.net.SocketException ex) {
                return;
            } catch (IOException ex) {
                Logger.getLogger(DownService.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            int len = receivePacket.getLength();
            if (len > 0 && len <= 1500) {
                byte[] packet = new byte[len];
                System.arraycopy(receivePacket.getData(), 0, packet, 0, len);

                String version = Packet.getVersion(packet);
                //bn command packets
                if (version.equals("0")) {
                    byte[] payload = Packet.getPayloadU(packet);
                    modifiedSentence = new String(payload) + '\0';
                    if (modifiedSentence.startsWith("00001")) {
                        lvl3RedNode.login.monitor.writeToCommands("DPING OK");
                        lvl3RedNode.login.connection.isClientDPinged = true;
                    }
                    lvl3RedNode.login.monitor.writeToDown(version + " " + modifiedSentence);
                } 
                //rn command packets
                else if (version.equals("1")){
                    byte[] payload = Packet.getPayloadU(packet);
                    modifiedSentence = new String(payload) + '\0';
                    if (modifiedSentence.startsWith("00004")) {                        
                        if (lvl3RedNode.login.connection.arpTable.isAssociated(Packet.getUSourceAddress(packet)))
                            lvl3RedNode.login.connection.arpTable.getByIP(Packet.getUSourceAddress(packet)).getTrafficMan().gotACK();
                    }
                    lvl3RedNode.login.monitor.writeToDown(version + " " + modifiedSentence);
                } 
                //chat packets
                else if (version.equals("2")) {
                    byte[] payload = new byte[Packet.getPayloadU(packet).length];
                    payload = Packet.getPayloadU(packet);
                    modifiedSentence = new String(payload) + '\0';
                    lvl3RedNode.login.monitor.writeToDown(version + " " + modifiedSentence);
                } 
                //ipv4 packets
                else if (version.equals("45")) {
                    lvl3RedNode.login.monitor.writeToDown(version + " IPv4 Len: " + packet.length + " From " + Packet.getSourceAddress(packet).getHostAddress());
                    lvl3RedNode.login.monitor.jTextField3.setText(""+lvl3RedNode.login.connection.downMan.getlen());
                    lvl3RedNode.login.connection.downMan.offer(packet);                    
                    ACK = Packet.MakePacket(("00004 [ACK]").getBytes(), lvl3RedNode.login.connection.MyIP, Packet.getSourceAddress(packet), 1);
                    lvl3RedNode.login.connection.upMan.offer(ACK);
                }
            } else {
                System.out.println("wrong len");
            }
        }
        lvl3RedNode.login.connection.downMan.clear();
    }

    public void kill() {
        kill = true;
        clientSocket.close();
    }
}
