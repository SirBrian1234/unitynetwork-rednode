package org.kostiskag.unitynetwork.rednode.routing.networkinterface;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.kostiskag.unitynetwork.common.routing.QueueManager;
import org.kostiskag.unitynetwork.common.service.SimpleUnstoppedCyclicService;

import org.p2pvpn.tuntap.TunTap;

import org.kostiskag.unitynetwork.rednode.App;


/**
 *
 * @author Konstantinos Kagiampakis
 */
public class InterfaceWrite extends SimpleUnstoppedCyclicService {

    private final String pre = "^InterfaceWrite ";
    private final TunTap tuntap;
    private final QueueManager writeQueue;

    private AtomicBoolean kill = new AtomicBoolean(false);
    private int numberOfWrittenFrames;


    public InterfaceWrite(TunTap tuntap, QueueManager writeQueue) {
        this.tuntap = tuntap;
        this.writeQueue = writeQueue;
    }

    @Override
    protected void preActions() {
        System.out.println("@Interface write started at " + Thread.currentThread().getName());
    }

    @Override
    protected void postActions() {
        App.login.monitor.writeToIntWrite(pre+"ended");
    }

    @Override
    protected void cyclicActions() {
        byte[] data;
        try {
            data = writeQueue.poll();
        } catch (NullPointerException | NoSuchElementException ex) {
            return;
        }

        App.login.monitor.writeToIntWrite(pre + "WRITTING TO MEDIUM");
        tuntap.write(data, data.length);
        App.login.monitor.updateIntWriteBufferNumber(writeQueue.getlen());
        App.login.monitor.updateIntWriteNumber(++numberOfWrittenFrames);
    }

}
