package kostiskag.unitynetwork.rednode.Routing.data;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This is a reverse ARP table. Why it is called reverse?
 * A normal table lies in a host which keeps a record of IP addresses associated with MAC addressed
 * in order to properly route the data. This table does the same thing, it keeps tuples of IP to MAC
 * addressed but it is used to feed the host with a mac when an IP's host is not known to him. The table
 * generates new macs for new ips in an increment style starting from number one.  
 *
 * @author Konstantinos Kagiampakis
 */
public class ReverseARPTable {
    private final String pre = "^ReverseArpTable ";
    private final LinkedList<ReverseARPInstance> list;
    private final InetAddress myIp;
    private int nextMac;

    public ReverseARPTable(InetAddress myIp) {
    	this.myIp = myIp;
    	list = new LinkedList<ReverseARPInstance>();
        nextMac = 1;               
    }

    public synchronized void lease(InetAddress ip) throws Exception {
    	//validate IP address
    	if (ip.equals(myIp)) {            
        	throw new Exception(pre+"The IP address is your own IP address.");
        }
        
        String address = ip.getHostAddress();        
        if (!address.startsWith("10.")) {
        	throw new Exception(pre+"Non network ip was given");
        } else if (address.equals("10.0.0.0") || address.equals("10.255.255.255")) {
        	throw new Exception(pre+"Special purpose ip can not be registered");
        }
        
        //check if it already exists
        Iterator<ReverseARPInstance> it = list.listIterator();
        while(it.hasNext()) {
        	ReverseARPInstance element = it.next();
        	if (element.getIp().equals(ip)) {
        		throw new Exception(pre+"Address is allready in table");
        	}
        }

        //now build a new mac
        byte[] addr = new byte[6];
        boolean[] head = new boolean[8];
        byte phead;

        //first we have the head where our mac is unicast and local set
        for (int i = 2; i < 8; i++) {
            head[i] = false;
        }
        head[1] = true;
        head[0] = false;
        phead = toByte(head);

        //then the rest of the mac is increasing by nextMac
        addr = new byte[]{
            phead,
            (byte) 0x0,
            (byte) (nextMac >>> 24),
            (byte) (nextMac >>> 16),
            (byte) (nextMac >>> 8),
            (byte) nextMac};

        nextMac++;
        MacAddress mac = new MacAddress(addr);  
        ReverseARPInstance newEntry = new ReverseARPInstance(ip, mac);
        list.add(newEntry);        
    }
    
    public synchronized int getLength() {
    	return list.size();
    }

    public synchronized void release(InetAddress ip) throws Exception {
    	Iterator<ReverseARPInstance> it = list.listIterator();
        while(it.hasNext()) {
        	ReverseARPInstance element = it.next();
        	if (element.getIp().equals(ip)) {
        		it.remove();
        		return;
        	}
        }
        throw new Exception(pre+"The ARP entry was non existent.");
    }

    public synchronized boolean isAssociated(InetAddress ip) {
    	Iterator<ReverseARPInstance> it = list.listIterator();
        while(it.hasNext()) {
        	ReverseARPInstance element = it.next();
        	if (element.getIp().equals(ip)) {
        		return true;
        	}
        }
        return false;
    }

    public synchronized ReverseARPInstance getByIP(InetAddress ip) throws Exception {
        Iterator<ReverseARPInstance> it = list.listIterator();
        while(it.hasNext()) {
        	ReverseARPInstance element = it.next();
        	if (element.getIp().equals(ip)) {
        		return element;
        	}
        }
        throw new Exception(pre+"The ARP entry was non existent.");
    }
    
    public synchronized ReverseARPInstance getByIPinBytes(byte[] addr) throws Exception {
    	InetAddress ip = InetAddress.getByAddress(addr);    	
        Iterator<ReverseARPInstance> it = list.listIterator();
        while(it.hasNext()) {
        	ReverseARPInstance element = it.next();
        	if (element.getIp().equals(ip)) {
        		return element;
        	}
        }
        throw new Exception(pre+"The ARP entry was non existent.");
    }
    
    private byte toByte(boolean[] data) {
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            int value = (data[i] ? 1 : 0) << i;
            result = result | value;
        }
        byte bresult = (byte) result;
        return bresult;
    }
}
