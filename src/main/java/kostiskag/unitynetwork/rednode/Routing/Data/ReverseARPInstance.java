package kostiskag.unitynetwork.rednode.Routing.Data;

import java.net.InetAddress;
import kostiskag.unitynetwork.rednode.Routing.UploadManager;

/**
 *
 * @author kostis
 */
public class ReverseARPInstance {
    MacAddress mac;
    InetAddress ip;
    UploadManager trafficMan;

    public ReverseARPInstance() { 
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
    
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public void setMac(MacAddress mac) {
        this.mac = mac;
    }

}
