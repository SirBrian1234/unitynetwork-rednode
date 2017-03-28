/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kostiskag.unitynetwork.rednode.Routing;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kostis
 */
public class UploadManager {

    private int len = 1;    
    private long oldTime;
    private long time;
    private long averageTime=250;

    public UploadManager() {
    }

    public synchronized void gotACK() {
        if (len >= 1 && len <= 10) {
            len++;
        } else if (len < 1){
            len = 1;
        }
        oldTime = time;
        time = System.currentTimeMillis();  
        if (time-oldTime < 500) {
            averageTime = (2*averageTime + (time-oldTime)/2)/3;        
        }
        if (averageTime > 1000)
            averageTime=1000;
        System.out.println("got ack, avtime "+averageTime);
        notify();
    }

    //clearToSend
    public synchronized void clearToSend() {        
        System.out.println("len "+len+ " average time "+averageTime);
        if (len <= -20){
            try {                
                System.out.println(" 2000 ");
                wait(2000); //calcultate expecttion time (between 2 acks) plus something more
            } catch (InterruptedException ex) {
                Logger.getLogger(UploadManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (len <= -10){
            try {                
                System.out.println(" time*3 " + averageTime*3);
                wait(averageTime*3); //calcultate expecttion time (between 2 acks) plus something more
            } catch (InterruptedException ex) {
                Logger.getLogger(UploadManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (len <= -5){
            try {
                System.out.println(" time*2 " + averageTime*2);
                wait(averageTime*2); //calcultate expecttion time (between 2 acks) plus something more
            } catch (InterruptedException ex) {
                Logger.getLogger(UploadManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (len <= 0) {
            try {
                System.out.println(" time "+ averageTime);
                wait(averageTime); //calcultate expecttion time (between 2 acks) plus something more
            } catch (InterruptedException ex) {
                Logger.getLogger(UploadManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        len--;        
    }
}
