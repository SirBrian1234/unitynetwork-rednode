/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.GUI.MonitorWindow;
import kostiskag.unitynetwork.rednode.RedNode.lvl3RedNode;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.p2pvpn.tuntap.TunTap;

/**
 *
 * @author kostis
 */
public class InterfaceRead extends Thread{

    private String pre = "^READ ";
    TunTap adapter = null;
    boolean kill = false;
    
    public InterfaceRead(TunTap adapter) {
       this.adapter = adapter;
    }

    @Override
    public void run() {
        System.out.println("@Interface read started at "+Thread.currentThread().getName());                   
        
        byte[] buffer = new byte[2048];		    
        int i=0;
        while(!kill){
            lvl3RedNode.login.monitor.writeToIntRead(pre+"READING");
            int len = lvl3RedNode.login.connection.tuntap.read(buffer);
            lvl3RedNode.login.monitor.jTextField13.setText(""+lvl3RedNode.login.connection.readMan.getlen());
            if (len > 14) {
                byte[] frame = new byte[len];
                System.arraycopy(buffer, 0, frame, 0, len);                
                lvl3RedNode.login.connection.readMan.offer(frame);                
                lvl3RedNode.login.monitor.jTextField11.setText(""+i);                 
                i++;
            }
        }                            
    }
    
    public void kill(){
        kill=true;                           
    }
}
