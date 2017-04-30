package kostiskag.unitynetwork.rednode.Routing.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kostis
 */
public class DHCPrequest {

    byte[] bootp;
    byte[] magicCookie;
    byte[] transactionId;
    InetAddress source;
    InetAddress target;
    MacAddress sourcemac;
    MacAddress destmac;
    private boolean hostn = false;
    private int dhcptype = -1;
    private boolean list = false;
    private boolean classid = false;
    private boolean cliId = false;
    private int[] requests;
    private byte[] requestIP = new byte[4];
    private boolean asksIP = false;

    public DHCPrequest(byte[] frame) {
        magicCookie = new byte[]{0x00, 0x00, 0x00, 0x00};
        sourcemac = new MacAddress("ff:ff:ff:ff:ff:ff");
        transactionId = new byte[]{0x00, 0x00, 0x00, 0x00};
        dhcptype = 0;
        asksIP = false;
        requestIP = new byte[]{0x00, 0x00, 0x00, 0x00};

        byte[] mac = new byte[6];
        for (int i = 0; i < 6; i++) {
            mac[i] = frame[6 + i];
        }

        bootp = new byte[frame.length];
        int j = 42;
        for (int i = 0; i < frame.length - 42; i++) {
            bootp[i] = frame[j];
            j++;
        }

        sourcemac = new MacAddress(mac, 0);
        transactionId = new byte[]{bootp[4 + 0], bootp[4 + 1], bootp[4 + 2], bootp[4 + 3]};
        magicCookie = new byte[]{bootp[236], bootp[237], bootp[238], bootp[239]};

        //getting the services
        int off = 240;
        int i = 0;
        int type;
        int lenght;
        while ((int) bootp[off + i] != -1) {
            type = (int) bootp[off + i];
            lenght = (int) bootp[off + i + 1];

            if (type == 12) {
                hostn = true;
            } else if (type == 50) {
                asksIP = true;
                requestIP = new byte[]{(byte) bootp[off + i + 1 + 1 + 0], (byte) bootp[off + i + 1 + 1 + 1], (byte) bootp[off + i + 1 + 1 + 2], (byte) bootp[off + i + 1 + 1 + 3]};
            } else if (type == 53) {
                dhcptype = (int) bootp[off + i + 1 + 1];
            } else if (type == 55) {
                //deprecated - services
                /*
                 * requests = new int[lenght]; for (int k = 0; k < lenght; k++)
                 * { requests[k] = bootp[off + i + 1 + 1 + k]; }
                 *
                 */
            } else if (type == 60) {
                classid = true;
            } else if (type == 61) {
                cliId = true;
            } else {
            }
            i = i + 1 + lenght + 1;
        }
    }

    public MacAddress getSourceMac() {
        return sourcemac;
    }

    public byte[] getMagicCookie() {
        return magicCookie;
    }

    public static boolean isBootstrap(byte[] frame) {
        int off = 34;

        if (frame[off + 0] == 0x00 && frame[off + 1] == 0x44 && frame[off + 2] == 0x00 && frame[off + 3] == 0x43) {
            return true;
        } else {
            return false;
        }
    }

    public byte[] getTransactionId() {
        return transactionId;
    }

    public int getType() {
        return dhcptype;
    }

    public boolean asksIP() {
        return asksIP;
    }

    public InetAddress getRequestIP() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getByAddress(requestIP);
        } catch (UnknownHostException ex) {
            Logger.getLogger(DHCPrequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return addr;
    }
}
