package kostiskag.unitynetwork.rednode.Routing.networkInterface;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.QueueManager;

import java.util.concurrent.atomic.AtomicBoolean;

import org.p2pvpn.tuntap.TunTap;

/**
 *
 * @author kostis
 */
public class InterfaceRead extends Thread{

    private final String pre = "^InterfaceRead ";
    private final TunTap tuntap;
    private final QueueManager readQueue;
    private AtomicBoolean kill = new AtomicBoolean(false);
    
    public InterfaceRead(TunTap tuntap, QueueManager readQueue) {
       this.tuntap = tuntap;
       this.readQueue = readQueue;
    }

    @Override
    public void run() {
        System.out.println("@Interface read started at "+Thread.currentThread().getName());                   
        
        byte[] buffer = new byte[2048];		    
        int i=0;
        while(!kill.get()){
            App.login.monitor.writeToIntRead(pre+"READING");
            int len = tuntap.read(buffer);
            App.login.monitor.updateIntReadBufferNumber(readQueue.getlen());
            if (len > 14) {
                byte[] frame = new byte[len];
                System.arraycopy(buffer, 0, frame, 0, len);                
                readQueue.offer(frame);                
                App.login.monitor.updateIntReadNumber(i);                 
                i++;
            }
        }       
        App.login.monitor.writeToIntRead(pre+"ended");
    }
    
    public void kill(){
        kill.set(true);                        
    }
}
