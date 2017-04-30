package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.data.DHCPrequest;
import kostiskag.unitynetwork.rednode.Routing.data.MacAddress;
import kostiskag.unitynetwork.rednode.Routing.packets.EthernetFrame;
import kostiskag.unitynetwork.rednode.Routing.packets.IPv4Packet;

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

    private String pre = "^EthernetRouter ";
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
        byte[] frameType;
        String frameTypeStr;
        byte[] ippacket;
        EthernetConnection connection = new EthernetConnection();

        while (!kill) {
            //tha pairnei paketa apo thn oura kai tha ta grafei sto meso, ama einai adeia tha koimatai gia ligo                        
            try {
                frame = App.login.connection.readMan.poll();
            } catch (java.lang.NullPointerException ex1) {
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }

            sourcemac = EthernetFrame.getSourceMacAddress(frame);
            destmac = EthernetFrame.getDestMacAddress(frame);
            frameType = EthernetFrame.getFrameTypeInBytes(frame);
            frameTypeStr = EthernetFrame.getFrameTypeInString(frame);

            if (sourcemac == null || destmac == null || frameType == null) {
                continue;
            }

            String info;
            info = pre + "READING ";
            info = info + frameTypeStr + " ";
            info = info + "Length: " + frame.length + " ";
            info = info + "Dest: " + destmac.toString() + " ";
            info = info + "Source: " + sourcemac.toString();

            App.login.monitor.writeToIntRead(info);

            //unicast packets
            if (!destmac.isBroadcast()) {
                App.login.monitor.writeToIntRead(pre + "Unicast");
                if (frameTypeStr.equals("IP")) {
                    ippacket = IPv4Packet.getPayload(frame);

                    source = IPv4Packet.getSourceAddress(ippacket);
                    dest = IPv4Packet.getDestAddress(ippacket);
                    pver = IPv4Packet.getVersion(ippacket);
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
                    App.login.monitor.writeToIntRead(info2);

                    if (connection.clearToSendIP(ippacket)) {                          
                        try {
							App.login.connection.arpTable.getByIP(dest).getTrafficMan().clearToSend();
							 App.login.connection.upMan.offer(ippacket);   
						} catch (Exception e) {
							e.printStackTrace();
						}                                            
                    }
                } else {
                    App.login.monitor.writeToIntRead(pre + "NOT INTRESTING FRAME");
                }
            } else {
                //broadcast packets
                App.login.monitor.writeToIntRead(pre + "Broadcast");
                if (frameTypeStr.equals("ARP")) {
                    App.login.monitor.writeToIntRead(pre + "ARP");
                    connection.giveARP(frame);
                } else if (frameTypeStr.equals("IP")) {
                    //make sure its bootstrap                        
                    if (DHCPrequest.isBootstrap(frame)) {
                        App.login.monitor.writeToIntRead(pre + "BOOTSTRAP");
                        connection.giveBootstrap(frame);
                    } else {
                        App.login.monitor.writeToIntRead(pre + "NOT RELEVANT");
                    }
                }
            }
        }
        App.login.connection.readMan.clear();
        App.login.monitor.clearIntReadNumber();
    }

    public void kill() {
        kill = true;
    }
}
