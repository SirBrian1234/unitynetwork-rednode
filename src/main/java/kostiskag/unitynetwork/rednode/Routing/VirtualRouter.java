package kostiskag.unitynetwork.rednode.Routing;

import java.net.InetAddress;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.Data.Frame;
import kostiskag.unitynetwork.rednode.Routing.Data.MacAddress;
import kostiskag.unitynetwork.rednode.Routing.Data.Packet;

/**
 *
 * @author kostis
 */
public class VirtualRouter extends Thread {

    boolean kill = false;
    private String pre = "^VROUTER ";
    byte[] packet;
    private int len;

    public VirtualRouter() {
    }

    @Override
    public void run() {

        while (!kill) {
            //tha pairnei paketa apo thn oura tha ta tropopoiei kai tha ta grafei sthn oura gia to meso, ama einai adeia tha koimatai gia ligo                        

            try {                
                packet = App.login.connection.downMan.poll();                
            } catch (java.lang.NullPointerException ex1) {
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }

            String version = Packet.getVersion(packet);
            InetAddress source = Packet.getSourceAddress(packet);
            InetAddress dest = Packet.getDestAddress(packet);
            len = packet.length;

            if (!version.equals("45") || source == null || dest == null) {
                continue;
            }
            if (len <= 0 || len > 1500){
                System.out.println("Discarded, wrong size");
                continue;
            }

            String info = pre + "RECEIVED IPv4 Packet";
            info = info + "Version: " + version + " ";
            info = info + "Source: " + source.getHostAddress() + " ";
            info = info + "Dest: " + dest.getHostAddress();
            info = info + "Len: " + len;

            App.login.monitor.writeToIntWrite(info);

            //now importing all the stupid low level stuff (making a frame)
            MacAddress sourceMac = null;
            if (App.login.connection.MyMac != null) {                
                if (App.login.connection.arpTable.isAssociated(source)) {
                    sourceMac = App.login.connection.arpTable.getByIP(Packet.getSourceAddress(packet)).getMac();
                } else {
                    App.login.connection.arpTable.lease(Packet.getSourceAddress(packet));
                    sourceMac = App.login.connection.arpTable.getByIP(Packet.getSourceAddress(packet)).getMac();
                }
                byte[] frame = Frame.MakeFrame(packet, App.login.connection.MyMac, sourceMac);
                App.login.monitor.writeToIntWrite(pre + "FRAMED IP Dest: " + App.login.connection.MyMac.toString() + " Source: " + sourceMac);                                
                App.login.connection.writeMan.offer(frame);
            } else {
                System.out.println("null mac");
            }
                
        }
        App.login.connection.writeMan.clear();
        App.login.monitor.jTextField12.setText("");
    }

    public void kill() {
        kill = true;
    }
}
