package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.App;
import org.p2pvpn.tuntap.TunTap;

/**
 *
 * @author kostis
 */
public class InterfaceWrite extends Thread {

    private String pre = "^WRITE ";
    boolean kill = false;
    byte[] data;
    private TunTap adapter;

    public InterfaceWrite(TunTap adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run() {
        System.out.println("@Interface write started at " + Thread.currentThread().getName());

        int i = 0;
        while (!kill) {
            //tha pairnei paketa apo thn oura kai tha ta grafei sto meso, ama einai adeia tha koimatai gia ligo                           
            try {
                data = App.login.connection.writeMan.poll();
            } catch (java.lang.NullPointerException ex1) {
                continue;
            } catch (java.util.NoSuchElementException ex) {
                continue;
            }
            
            App.login.monitor.writeToIntWrite(pre + "WRITTING TO MEDIUM");
            App.login.connection.tuntap.write(data, data.length);
            App.login.monitor.jTextField14.setText(""+App.login.connection.writeMan.getlen());
            App.login.monitor.jTextField12.setText("" + i);
            i++;
        }
    }

    public void kill() {
        kill = true;
    }
}
