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
 * @author Konstantinos Kagiampakis
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

            if (!destmac.isBroadcast()) {
            	//unicast packets
                App.login.monitor.writeToIntRead(pre + "Unicast");
                if (EthernetFrame.isIPv4(frame)) {
                    ippacket = EthernetFrame.getFramePayload(frame);
                    if (!IPv4Packet.isIPv4(ippacket)) {
                    	App.login.monitor.writeToIntRead(pre +"discarded, frame payload was not IPv4");
                    	continue;
                    }
                    
                    try {
						source = IPv4Packet.getSourceAddress(ippacket);
						dest = IPv4Packet.getDestAddress(ippacket);
                    } catch (Exception e1) {
						e1.printStackTrace();
						App.login.monitor.writeToIntRead(pre +"discarded, mallformed source or dest");
                    	continue;
					}
                    
                    int len = ippacket.length;
                    if (len <= 0 || len > 1500) {
                    	App.login.monitor.writeToIntRead(pre +"discarded, wrong size");
                        continue;
                    }

                    String info2 = pre + "IP Frame ";
                    info2 = info2 + "Source: " + source.getHostAddress() + " ";
                    info2 = info2 + "Dest: " + dest.getHostAddress();
                    App.login.monitor.writeToIntRead(info2);

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
