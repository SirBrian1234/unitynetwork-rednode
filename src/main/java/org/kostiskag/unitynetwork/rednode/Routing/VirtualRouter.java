package org.kostiskag.unitynetwork.rednode.Routing;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import org.kostiskag.unitynetwork.common.routing.packet.IPv4Packet;
import org.kostiskag.unitynetwork.rednode.App;
import org.kostiskag.unitynetwork.rednode.Routing.data.ARPGenerate;
import org.kostiskag.unitynetwork.rednode.Routing.data.MacAddress;
import org.kostiskag.unitynetwork.rednode.Routing.data.ReverseARPInstance;
import org.kostiskag.unitynetwork.rednode.Routing.packets.EthernetFrame;

/**
 * Gets packets from the incoming queue and writes them to the medium.
 *
 * @author Konstantinos Kagiampakis
 */
public class VirtualRouter extends Thread {

	private final String pre = "^VirtualRouter ";
	private final QueueManager interfaceWriteQueue;
	private final QueueManager receiveQueue;
	private final AtomicBoolean kill = new AtomicBoolean(false);
    
    public VirtualRouter(QueueManager interfaceWriteQueue, QueueManager receiveQueue) {
    	this.interfaceWriteQueue = interfaceWriteQueue; //App.login.connection.writeMan
    	this.receiveQueue = receiveQueue; //App.login.connection.downMan
    }

    @Override
    public void run() {

        while (!kill.get()) {
        	//getting a packet
        	byte[] packet = null;
            try {                
                packet = receiveQueue.poll();                
            } catch (java.lang.NullPointerException ex1) {
            	App.login.monitor.writeToIntWrite(pre+"discarded, null packet");
                continue;
            } catch (java.util.NoSuchElementException ex) {
            	App.login.monitor.writeToIntWrite(pre+"discarded, no element");
                continue;
            }

            InetAddress source;
            InetAddress dest; 
            try {
				source = IPv4Packet.getSourceAddress(packet);
				dest = IPv4Packet.getDestAddress(packet);
	        } catch (Exception e1) {
	        	App.login.monitor.writeToIntWrite(pre+"discarded, mallformed source or dest ip");
                e1.printStackTrace();
				continue;
			}
            
            int len = packet.length;
            if (len <= 0 || len > 1500){
            	App.login.monitor.writeToIntWrite(pre+"discarded, wrong size");
                continue;
            }

            String info = pre + "RECEIVED IPv4 Packet";
            info = info + "Source: " + source.getHostAddress() + " ";
            info = info + "Dest: " + dest.getHostAddress();
            info = info + "Len: " + len;
            App.login.monitor.writeToIntWrite(info);

            /* the collected ipv4 packet has to be encapsulated 
             * in a frame and offered to the interface's target queue
             */
            MacAddress sourceMac = null;
            if (App.login.connection.getMyMac() == null) {                
            	App.login.monitor.writeToIntWrite(pre+"no mac set for this host.");
                continue;
            }
            
        	try {
            	if (App.login.connection.getArpTable().isAssociated(source)) {
                    sourceMac = App.login.connection.getArpTable().getByIP(IPv4Packet.getSourceAddress(packet)).getMac();
				} else {
					InetAddress sourceIp = IPv4Packet.getSourceAddress(packet);
                    App.login.connection.getArpTable().lease(sourceIp);
                    ReverseARPInstance entry = App.login.connection.getArpTable().getByIP(sourceIp);
                    App.login.monitor.writeToIntRead("new mac: " +  entry.getMac().toString()+" for "+entry.getIp().getHostAddress());       
        	        byte[] arp = ARPGenerate.ArpGenerate(App.login.connection.getMyMac(), App.login.connection.getMyIP(), entry.getMac(), entry.getIp());
        	        if (arp == null){
        	            System.out.println("arp generate failed");
        	            App.login.monitor.writeToIntRead(pre+"ARP FAILED");
        	        } else {
        	             App.login.monitor.writeToIntWrite(pre+"GENERATING ARPS for "+entry.getIp().getHostAddress());
        	             for(int i=0; i<2; i++){
        	            	 interfaceWriteQueue.offer(arp);
        	             }
        	        }
                    sourceMac = App.login.connection.getArpTable().getByIP(IPv4Packet.getSourceAddress(packet)).getMac();
                }
                byte[] frame = EthernetFrame.buildFrame(packet, App.login.connection.getMyMac(), sourceMac);
                App.login.monitor.writeToIntWrite(pre + "FRAMED IP Dest: " + App.login.connection.getMyMac().toString() + " Source: " + sourceMac);                                
                interfaceWriteQueue.offer(frame);
        	} catch (Exception e) {
				e.printStackTrace();
			}
        }
        App.login.monitor.clearIntReadNumber();
        App.login.monitor.writeToIntWrite(pre+"ended");
    }

    public void kill() {
        kill.set(true);
        interfaceWriteQueue.exit();
    }
}
