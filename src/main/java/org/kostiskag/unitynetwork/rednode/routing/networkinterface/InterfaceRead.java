package org.kostiskag.unitynetwork.rednode.routing.networkinterface;

import java.util.Arrays;

import org.kostiskag.unitynetwork.common.routing.QueueManager;
import org.kostiskag.unitynetwork.common.service.SimpleUnstoppedCyclicService;

import org.p2pvpn.tuntap.TunTap;

import org.kostiskag.unitynetwork.rednode.App;


/**
 *
 * @author Konstantinos Kagiampakis
 */
public class InterfaceRead extends SimpleUnstoppedCyclicService {

    private static final int MIN_ALLOWED_FRAME_LENGTH = 14;

    private final String pre = "^InterfaceRead ";
    private final TunTap tuntap;
    private final QueueManager<byte[]> readQueue;

    private int numberOfReadFrames;
    private byte[] buffer = new byte[2048];
    
    public InterfaceRead(TunTap tuntap, QueueManager readQueue) {
       this.tuntap = tuntap;
       this.readQueue = readQueue;
    }

    @Override
    protected void preActions() {
        System.out.println("@Interface read started at "+Thread.currentThread().getName());
    }

    @Override
    protected void postActions() {
        App.login.monitor.writeToIntRead(pre+"ended");
    }

    @Override
    protected void cyclicActions() {
        int len = tuntap.read(buffer);
        App.login.monitor.updateIntReadBufferNumber(readQueue.getlen());
        if (len > InterfaceRead.MIN_ALLOWED_FRAME_LENGTH) {
            byte[] frame = Arrays.copyOf(buffer, len);
            readQueue.offer(frame);
            App.login.monitor.updateIntReadNumber(++numberOfReadFrames);
        }
    }
}
