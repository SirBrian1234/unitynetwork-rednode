/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.Routing.Data.Packet;
import kostiskag.unitynetwork.rednode.Routing.Data.MacAddress;
import kostiskag.unitynetwork.rednode.Routing.Data.DHCPrequest;
import kostiskag.unitynetwork.rednode.Routing.Data.FrameType;
import kostiskag.unitynetwork.rednode.Routing.Data.Frame;
import kostiskag.unitynetwork.rednode.RedNode.lvl3RedNode;
import java.net.InetAddress;

/**
 *
 * @author kostis
 *
 * This class is responsible for examining all frames we get from read and send
 * them to rednodes queue for exporting to the network
 *
 */
public class EthernetRouter extends Thread {

    private String pre = "^ETHOUTER ";
    private boolean kill = false;
    private String pver;
    private int len;

    @Override
    public void run() {
        byte[] frame;
        MacAddress sourcemac;
        MacAddress destmac;
        InetAddress source;
        InetAddress dest;
        FrameType type;
        byte[] ippacket;
        EthernetConnection connection = new EthernetConnection();

        while (!kill) {
            //tha pairnei paketa apo thn oura kai tha ta grafei sto meso, ama einai adeia tha koimatai gia ligo                        
            try {
                frame = lvl3RedNode.login.connection.readMan.poll();
            } catch (java.lang.NullPointerException ex1) {
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }

            sourcemac = Frame.GetSourceMacAddress(frame);
            destmac = Frame.GetDestMacAddress(frame);
            type = Frame.getFrameType(frame);

            if (sourcemac == null || destmac == null || type == null) {
                continue;
            }

            String info;
            info = pre + "READING ";
            info = info + type.toString() + " ";
            info = info + "Length: " + frame.length + " ";
            info = info + "Dest: " + destmac.toString() + " ";
            info = info + "Source: " + sourcemac.toString();

            lvl3RedNode.login.monitor.writeToIntRead(info);

            //unicast packets
            if (!destmac.isBroadcast()) {
                lvl3RedNode.login.monitor.writeToIntRead(pre + "Unicast");
                if (type.toString().equals("IP")) {
                    ippacket = Packet.GetPacket(frame);

                    source = Packet.getSourceAddress(ippacket);
                    dest = Packet.getDestAddress(ippacket);
                    pver = Packet.getVersion(ippacket);
                    len = ippacket.length;

                    if (source == null || dest == null || !pver.equals("45")) {
                        continue;
                    }
                    if (len <= 0 || len > 1500) {
                        System.out.println("Discarded, wrong size");
                        continue;
                    }

                    String info2 = pre + "IP Frame Version: " + pver + " ";
                    info2 = info2 + "Source: " + source.getHostAddress() + " ";
                    info2 = info2 + "Dest: " + dest.getHostAddress();
                    lvl3RedNode.login.monitor.writeToIntRead(info2);

                    if (connection.clearToSendIP(ippacket)) {                          
                        lvl3RedNode.login.connection.arpTable.getByIP(dest).getTrafficMan().clearToSend();
                        lvl3RedNode.login.connection.upMan.offer(ippacket);                        
                    }
                } else {
                    lvl3RedNode.login.monitor.writeToIntRead(pre + "NOT INTRESTING FRAME");
                }
            } else {
                //broadcast packets
                lvl3RedNode.login.monitor.writeToIntRead(pre + "Broadcast");
                if (type.toString().equals("ARP")) {
                    lvl3RedNode.login.monitor.writeToIntRead(pre + "ARP");
                    connection.giveARP(frame);
                } else if (type.toString().equals("IP")) {
                    //make sure its bootstrap                        
                    if (DHCPrequest.isBootstrap(frame)) {
                        lvl3RedNode.login.monitor.writeToIntRead(pre + "BOOTSTRAP");
                        connection.giveBootstrap(frame);
                    } else {
                        lvl3RedNode.login.monitor.writeToIntRead(pre + "NOT RELEVANT");
                    }
                }
            }
        }
        lvl3RedNode.login.connection.readMan.clear();
        lvl3RedNode.login.monitor.jTextField11.setText("");
    }

    public void kill() {
        kill = true;
    }
}
