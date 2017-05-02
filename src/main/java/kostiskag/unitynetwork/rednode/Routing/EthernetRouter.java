package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.data.DHCPrequest;
import kostiskag.unitynetwork.rednode.Routing.data.MacAddress;
import kostiskag.unitynetwork.rednode.Routing.packets.EthernetFrame;
import kostiskag.unitynetwork.rednode.Routing.packets.IPv4Packet;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible to examine all the received frames from read and send
 * them to the rednode's send queue.
 *
 * @author kostis
 */
public class EthernetRouter extends Thread {

    private final String pre = "^EthernetRouter ";
    private final QueueManager interfaceReadQueue;
    private final QueueManager sendQueue;
    private final UploadManager trafficMan;
    private final AtomicBoolean kill = new AtomicBoolean(false);
    
    public EthernetRouter(QueueManager interfaceReadQueue, QueueManager sendQueue, UploadManager trafficMan) {
		this.interfaceReadQueue = interfaceReadQueue; //App.login.connection.readMan
		this.sendQueue = sendQueue; //App.login.connection.upMan
		this.trafficMan = trafficMan; //App.login.connection.arpTable.getByIP(dest).getTrafficMan()
	}
    
    @Override
    public void run() {
        byte[] frame;
        MacAddress sourcemac;
        MacAddress destmac;
        InetAddress source;
        InetAddress dest;
        String frameTypeStr;
        byte[] ippacket;
        EthernetConnection connection = new EthernetConnection();

        while (!kill.get()) {
            try {
            	frame = interfaceReadQueue.poll();
            } catch (java.lang.NullPointerException ex1) {
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }

            sourcemac = EthernetFrame.getSourceMacAddress(frame);
            destmac = EthernetFrame.getDestMacAddress(frame);
            frameTypeStr = EthernetFrame.getFrameTypeInString(frame);

            if (sourcemac == null || destmac == null) {
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
                if (EthernetFrame.isIPv4(frame)) {
                    ippacket = IPv4Packet.getPayload(frame);

                    source = IPv4Packet.getSourceAddress(ippacket);
                    dest = IPv4Packet.getDestAddress(ippacket);
                    String pver = IPv4Packet.getVersion(ippacket);
                    int len = ippacket.length;

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

                    //nai alla etsi kathusterei olo to interface gia ena destination einai atopo
                    if (connection.clearToSendIP(ippacket)) {                          
                        try {
							trafficMan.waitToSend();
							sendQueue.offer(ippacket);   
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
                if (EthernetFrame.isARP(frame)) {
                    App.login.monitor.writeToIntRead(pre + "ARP");
                    connection.giveARP(frame);
                } else if (EthernetFrame.isIPv4(frame)) {
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
        App.login.monitor.clearIntReadNumber();
        App.login.monitor.writeToCommands(pre + "ENDED");
    }

    public void kill() {
        kill.set(true);
        interfaceReadQueue.exit();
    }
}
