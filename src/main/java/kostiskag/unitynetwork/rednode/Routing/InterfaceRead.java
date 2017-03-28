package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.App;
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
            App.login.monitor.writeToIntRead(pre+"READING");
            int len = App.login.connection.tuntap.read(buffer);
            App.login.monitor.jTextField13.setText(""+App.login.connection.readMan.getlen());
            if (len > 14) {
                byte[] frame = new byte[len];
                System.arraycopy(buffer, 0, frame, 0, len);                
                App.login.connection.readMan.offer(frame);                
                App.login.monitor.jTextField11.setText(""+i);                 
                i++;
            }
        }                            
    }
    
    public void kill(){
        kill=true;                           
    }
}
