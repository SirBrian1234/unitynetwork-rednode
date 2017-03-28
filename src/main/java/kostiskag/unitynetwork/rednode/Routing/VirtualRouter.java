/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.GUI.MonitorWindow;
import kostiskag.unitynetwork.rednode.RedNode.lvl3RedNode;
import kostiskag.unitynetwork.rednode.Routing.Data.Frame;
import kostiskag.unitynetwork.rednode.Routing.Data.MacAddress;
import kostiskag.unitynetwork.rednode.Routing.Data.Packet;
import java.net.InetAddress;

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
                packet = lvl3RedNode.login.connection.downMan.poll();                
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

            lvl3RedNode.login.monitor.writeToIntWrite(info);

            //now importing all the stupid low level stuff (making a frame)
            MacAddress sourceMac = null;
            if (lvl3RedNode.login.connection.MyMac != null) {                
                if (lvl3RedNode.login.connection.arpTable.isAssociated(source)) {
                    sourceMac = lvl3RedNode.login.connection.arpTable.getByIP(Packet.getSourceAddress(packet)).getMac();
                } else {
                    lvl3RedNode.login.connection.arpTable.lease(Packet.getSourceAddress(packet));
                    sourceMac = lvl3RedNode.login.connection.arpTable.getByIP(Packet.getSourceAddress(packet)).getMac();
                }
                byte[] frame = Frame.MakeFrame(packet, lvl3RedNode.login.connection.MyMac, sourceMac);
                lvl3RedNode.login.monitor.writeToIntWrite(pre + "FRAMED IP Dest: " + lvl3RedNode.login.connection.MyMac.toString() + " Source: " + sourceMac);                                
                lvl3RedNode.login.connection.writeMan.offer(frame);
            } else {
                System.out.println("null mac");
            }
                
        }
        lvl3RedNode.login.connection.writeMan.clear();
        lvl3RedNode.login.monitor.jTextField12.setText("");
    }

    public void kill() {
        kill = true;
    }
}
