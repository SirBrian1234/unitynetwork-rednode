package kostiskag.unitynetwork.rednode.RedThreads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.GUI.MonitorWindow;
import kostiskag.unitynetwork.rednode.Routing.Data.Packet;

/**
 *
 * @author kostis
 */
public class UpService extends Thread {

    private static String pre = "^UP ";
    private boolean kill = false;
    private String vaddress;
    private static DatagramSocket clientSocket;
    private static InetAddress address;
    private int port;
    private byte[] data;

    public UpService(String vaddress, InetAddress address, int port) {
        this.address = address;
        this.vaddress = vaddress;
        this.port = port;
    }

    @Override
    public void run() {
        clientSocket = null;
        try {
            clientSocket = new DatagramSocket();
        } catch (java.net.BindException ex1) {
            App.login.writeInfo(pre + "SOCKET ALLREADY BINED EXCEPTION");            
        } catch (SocketException ex) {
            Logger.getLogger(UpService.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (!kill) {
            //tha pairnei paketa apo thn oura kai tha ta stelnei ama einai adeia tha koimatai gia ligo
            try {
                data = App.login.connection.upMan.poll();
            } catch (java.lang.NullPointerException ex1){                
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }
            int len = data.length;
            if (len <= 0 || len > 1500) {
                System.out.println("wrong length");
                continue;
            }
            
            DatagramPacket sendPacket = new DatagramPacket(data, len, address, port);                        
            try {
                clientSocket.send(sendPacket); 
                App.login.monitor.updateConDownBufferQueue(App.login.connection.upMan.getlen());
                String version = Packet.getVersion(data);
                if (version.equals("0") || version.equals("1")) {
                    App.login.monitor.writeToConnectionUp(version + " " + new String(Packet.getPayloadU(data)));
                } else if (version.equals("45")){
                    App.login.monitor.writeToConnectionUp(version + " IPv4 Packet Len:" + data.length + " To: " + Packet.getDestAddress(data).getHostAddress());
                }
            } catch (java.net.SocketException ex1) {
                break;
            } catch (IOException ex) {
                Logger.getLogger(UpService.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }                        
        }
        App.login.connection.upMan.clear();
    }

    public void kill() {
        kill = true;
        clientSocket.close();        
    }
}
