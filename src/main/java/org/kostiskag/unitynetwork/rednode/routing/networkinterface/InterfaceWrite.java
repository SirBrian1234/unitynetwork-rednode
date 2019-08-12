package org.kostiskag.unitynetwork.rednode.routing.networkinterface;

import org.kostiskag.unitynetwork.rednode.App;
import org.kostiskag.unitynetwork.rednode.routing.QueueManager;

import java.util.concurrent.atomic.AtomicBoolean;

import org.p2pvpn.tuntap.TunTap;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class InterfaceWrite extends Thread {

    private final String pre = "^InterfaceWrite ";
    private final TunTap tuntap;
    private final QueueManager writeQueue;
    private AtomicBoolean kill = new AtomicBoolean(false);

    public InterfaceWrite(TunTap tuntap, QueueManager writeQueue) {
        this.tuntap = tuntap;
        this.writeQueue = writeQueue;
    }

    @Override
    public void run() {
        System.out.println("@Interface write started at " + Thread.currentThread().getName());

        int i = 0;
        byte[] data;
        while (!kill.get()) {
            try {
                data = writeQueue.poll();
            } catch (java.lang.NullPointerException ex1) {
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }
            
            App.login.monitor.writeToIntWrite(pre + "WRITTING TO MEDIUM");
            tuntap.write(data, data.length);
            App.login.monitor.updateIntWriteBufferNumber(writeQueue.getlen());
            App.login.monitor.updateIntWriteNumber(i);
            i++;
        }
        App.login.monitor.writeToIntWrite(pre+"ended");
    }

    public void kill() {
        kill.set(true);
    }
}
