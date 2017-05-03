package kostiskag.unitynetwork.rednode.redThreads;

import java.util.concurrent.atomic.AtomicBoolean;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.QueueManager;
import kostiskag.unitynetwork.rednode.Routing.packets.UnityPacket;

/**
 *
 * @author kostis
 */
public class KeepAlive extends Thread{

    private final String pre = "^KeepAlive ";
    private final QueueManager sendQueue;
    private final int keepAliveTime;
    private AtomicBoolean kill= new AtomicBoolean(false);        
    
    public KeepAlive(QueueManager sendQueue, int keepAliveTime) {                
        this.sendQueue = sendQueue; //App.login.connection.upMan
        this.keepAliveTime = keepAliveTime*1000; //App.login.connection.keepAliveTime
    }

    @Override
    public void run() {                
        
    	byte[] packet = UnityPacket.buildKeepAlivePacket();
        
    	while(!kill.get()){
            for (int i=0; i<3; i++){
                sendQueue.offer(packet);
            }
            
            try {
                sleep(keepAliveTime);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    	App.login.monitor.writeToCommands(pre+"ended");
    }  
    
    public void kill(){        
        kill.set(true);
    }    
}
