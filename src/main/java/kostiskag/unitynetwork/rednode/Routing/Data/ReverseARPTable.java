package kostiskag.unitynetwork.rednode.Routing.Data;

import java.net.InetAddress;
import kostiskag.unitynetwork.rednode.App;

/**
 *
 * @author kostis
 *
 * thiiiis is a reverse arp table firstly if an arp is detected the arp table
 * registers it
 */
public class ReverseARPTable {
    String pre = "^ReverseArpTable ";
    ReverseARPInstance[] table;
    ReverseARPInstance temp;
    int count;
    int size;
    int nextMac;

    public ReverseARPTable(int size) {
        this.size = size;
        table = new ReverseARPInstance[size];
        for (int i = 0; i < size; i++) {
            table[i] = new ReverseARPInstance();
        }
        count = 0;
        nextMac = 1;
    }

    public void lease(InetAddress ip) {
        if (ip.equals(App.login.connection.MyIP)) {            
            return;
        }

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
        table[count].setMac(mac);
        table[count].setIp(ip);
        App.login.monitor.writeToIntRead("new mac: " + table[count].getMac().toString()+" for "+table[count].getIp().getHostAddress());       
        byte[] arp = ARPGenerate.ArpGenerate(table[count].getMac(), table[count].getIp());
        if (arp == null){
            System.out.println("arp generate failed");
            App.login.monitor.writeToIntRead(pre+"ARP FAILED");
        } else {
             App.login.monitor.writeToIntWrite(pre+"GENERATING ARPS for "+table[count].getIp().getHostAddress());
             for(int i=0; i<2; i++){
                App.login.connection.writeMan.offer(arp);
             }
        }
        count++;
    }

    public void release(int id) {
        for (int i = 0; i < count; i++) {
            if (i == id) {
                if (count != 0) {
                    temp = table[count - 1];
                    table[count - 1] = table[i];
                    table[i] = temp;
                    count--;
                    return;
                }
            }
        }
    }

    public static byte toByte(boolean[] data) {
        int result = 0;
        for (int i = 0; i < data.length; i++) {
            int value = (data[i] ? 1 : 0) << i;
            result = result | value;
        }
        byte bresult = (byte) result;
        return bresult;
    }

    public boolean isAssociated(InetAddress target) {
        for (int i = 0; i < count; i++) {
            if (table[i].getIp().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public ReverseARPInstance getByIP(InetAddress ip) {
        for (int i = 0; i < count; i++) {
            if (ip.equals(table[i].getIp())) {
                return table[i];
            }
        }
        return null;
    }
}
