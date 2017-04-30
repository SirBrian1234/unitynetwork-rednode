package kostiskag.unitynetwork.rednode.Routing.Data;

import java.net.InetAddress;
import kostiskag.unitynetwork.rednode.Routing.UploadManager;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class ReverseARPInstance {
    private final MacAddress mac;
    private final InetAddress ip;
    private final UploadManager trafficMan;

    public ReverseARPInstance(InetAddress ip, MacAddress mac) { 
    	this.ip = ip;
    	this.mac = mac;
        trafficMan = new UploadManager();
    }
    
    public InetAddress getIp() {
        return ip;
    }

    public MacAddress getMac() {
        return mac;
    }

    public UploadManager getTrafficMan() {
        return trafficMan;
    }   
}
