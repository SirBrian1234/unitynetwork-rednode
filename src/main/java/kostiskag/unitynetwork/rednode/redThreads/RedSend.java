package kostiskag.unitynetwork.rednode.redThreads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.packets.IPv4Packet;
import kostiskag.unitynetwork.rednode.Routing.packets.UnityPacket;
import kostiskag.unitynetwork.rednode.gui.MonitorWindow;

/**
 *
 * @author kostis
 */
public class RedSend extends Thread {

    private final String pre = "^RedSend ";
    
    private final InetAddress address;
    private final int port;
    private DatagramSocket socket;
    private AtomicBoolean kill = new AtomicBoolean(false);

    public RedSend(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        socket = null;
        try {
            socket = new DatagramSocket();
        } catch (java.net.BindException ex1) {
            App.login.writeInfo(pre + "SOCKET ALLREADY BINED EXCEPTION");            
        } catch (SocketException ex) {
            Logger.getLogger(RedSend.class.getName()).log(Level.SEVERE, null, ex);
        }

        byte[] packet;
        while (!kill.get()) {
            //tha pairnei paketa apo thn oura kai tha ta stelnei ama einai adeia tha koimatai gia ligo
            try {
                packet = App.login.connection.upMan.poll();
            } catch (java.lang.NullPointerException ex1){                
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }
            int len = packet.length;
            if (len <= 0 || len > 1500) {
                System.out.println("wrong length");
                continue;
            }
            
            DatagramPacket sendPacket = new DatagramPacket(packet, len, address, port);                        
            try {
                socket.send(sendPacket); 
                App.login.monitor.updateConDownBufferQueue(App.login.connection.upMan.getlen());
                String version = IPv4Packet.getVersion(packet);
                if (version.equals("0") || version.equals("1")) {
                    App.login.monitor.writeToConnectionUp(version + " " + new String(UnityPacket.getPayload(packet)));
                } else if (version.equals("45")){
                    App.login.monitor.writeToConnectionUp(version + " IPv4 Packet Len:" + packet.length + " To: " + IPv4Packet.getDestAddress(packet).getHostAddress());
                }
            } catch (java.net.SocketException ex1) {
                break;
            } catch (IOException ex) {
                Logger.getLogger(RedSend.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }                        
        }
        App.login.connection.upMan.clear();
    }

    public void kill() {
        kill.set(true);
        socket.close();        
    }
}
