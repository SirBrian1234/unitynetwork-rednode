package kostiskag.unitynetwork.rednode.Routing;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.data.ARPGenerate;
import kostiskag.unitynetwork.rednode.Routing.data.MacAddress;
import kostiskag.unitynetwork.rednode.Routing.data.ReverseARPInstance;
import kostiskag.unitynetwork.rednode.Routing.packets.EthernetFrame;
import kostiskag.unitynetwork.rednode.Routing.packets.IPv4Packet;

/**
 * Gets packets from the incoming queue and writes them to the medium.
 *
 * @author kostis
 */
public class VirtualRouter extends Thread {

	private final String pre = "^VirtualRouter ";
	private AtomicBoolean kill = new AtomicBoolean(false);
    
    public VirtualRouter() {
    	
    }

    @Override
    public void run() {

        while (!kill.get()) {
        	//getting a packet
        	byte[] packet = null;
            try {                
                packet = App.login.connection.downMan.poll();                
            } catch (java.lang.NullPointerException ex1) {
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }

            String version = IPv4Packet.getVersion(packet);
            InetAddress source = IPv4Packet.getSourceAddress(packet);
            InetAddress dest = IPv4Packet.getDestAddress(packet);
            int len = packet.length;

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

            //building a frame
            MacAddress sourceMac = null;
            if (App.login.connection.MyMac != null) {                
            	try {
	            	if (App.login.connection.arpTable.isAssociated(source)) {
	                    sourceMac = App.login.connection.arpTable.getByIP(IPv4Packet.getSourceAddress(packet)).getMac();
					} else {
						InetAddress sourceIp = IPv4Packet.getSourceAddress(packet);
	                    App.login.connection.arpTable.lease(sourceIp);
	                    ReverseARPInstance entry = App.login.connection.arpTable.getByIP(sourceIp);
	                    App.login.monitor.writeToIntRead("new mac: " +  entry.getMac().toString()+" for "+entry.getIp().getHostAddress());       
	        	        byte[] arp = ARPGenerate.ArpGenerate(entry.getMac(), entry.getIp());
	        	        if (arp == null){
	        	            System.out.println("arp generate failed");
	        	            App.login.monitor.writeToIntRead(pre+"ARP FAILED");
	        	        } else {
	        	             App.login.monitor.writeToIntWrite(pre+"GENERATING ARPS for "+entry.getIp().getHostAddress());
	        	             for(int i=0; i<2; i++){
	        	                App.login.connection.writeMan.offer(arp);
	        	             }
	        	        }
	                    sourceMac = App.login.connection.arpTable.getByIP(IPv4Packet.getSourceAddress(packet)).getMac();
	                }
	                byte[] frame = EthernetFrame.buildFrame(packet, App.login.connection.MyMac, sourceMac);
	                App.login.monitor.writeToIntWrite(pre + "FRAMED IP Dest: " + App.login.connection.MyMac.toString() + " Source: " + sourceMac);                                
	                App.login.connection.writeMan.offer(frame);
            	} catch (Exception e) {
					e.printStackTrace();
				}
            } else {
                System.out.println("null mac");
            }
                
        }
        App.login.connection.writeMan.clear();
        App.login.monitor.clearIntReadNumber();
    }

    public void kill() {
        kill.set(true);
    }
}
