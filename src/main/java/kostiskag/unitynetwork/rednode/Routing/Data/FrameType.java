package kostiskag.unitynetwork.rednode.Routing.Data;

/**
 *
 * @author kostis
 */
public class FrameType {
    byte[] type;

    public FrameType(byte[] frame) {
        type = new byte[] { frame[12] , frame[13] };
    }
                
    public byte[] toByte(){                
        return type;
    }
            
    public String toHex(){                
        return (Integer.toHexString(type[0])+Integer.toHexString(type[1]));
    }

    @Override
    public String toString() {
        if ((Integer.toHexString(type[0])+Integer.toHexString(type[1])).equals("80")){
            return "IP";
        }
        else if ((Integer.toHexString(type[0])+Integer.toHexString(type[1])).equals("86")){
            return "ARP";
        }
        else {
            return "NOT KNOWN";
        }
    }
}
