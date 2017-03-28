/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kostiskag.unitynetwork.rednode.Routing.Data;

import kostiskag.unitynetwork.rednode.Routing.UploadManager;
import java.net.InetAddress;

/**
 *
 * @author kostis
 */
public class ReverseArp {
    MacAddress mac;
    InetAddress ip;
    UploadManager trafficMan;

    public ReverseArp() { 
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
