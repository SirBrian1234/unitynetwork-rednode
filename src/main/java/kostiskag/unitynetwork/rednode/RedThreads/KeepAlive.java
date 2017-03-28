package kostiskag.unitynetwork.rednode.RedThreads;

import kostiskag.unitynetwork.rednode.RedNode.lvl3RedNode;
import kostiskag.unitynetwork.rednode.Routing.Data.Packet;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kostis
 */
public class KeepAlive extends Thread{

    private static String pre = "^KA ";
    boolean kill=false;        
    
    public KeepAlive() {                
        
    }

    @Override
    public void run() {                
        
        byte[] payload = ("00000 "+lvl3RedNode.login.connection.Vaddress+" [KEEP ALIVE]").getBytes();
        InetAddress address = null;
        try {
            address = InetAddress.getByName("0.0.0.0");
        } catch (UnknownHostException ex) {
            Logger.getLogger(KeepAlive.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] data = Packet.MakePacket(payload,address,address,0);
        
        while(!kill){
            for (int i=0; i<3; i++){
                lvl3RedNode.login.connection.upMan.offer(data);
            }
            
            try {
                sleep(1000* lvl3RedNode.login.connection.keepAliveTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(KeepAlive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }  
    
    public void kill(){        
        this.kill = true;
    }    
}
