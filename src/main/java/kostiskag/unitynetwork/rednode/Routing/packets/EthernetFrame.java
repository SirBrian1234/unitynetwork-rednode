package kostiskag.unitynetwork.rednode.Routing.packets;

import kostiskag.unitynetwork.rednode.Routing.data.MacAddress;

/**
 *
 * @author kostis
 */
public class EthernetFrame {         
    
    public static boolean isIPv4(byte[] frame) {
    	if ((Integer.toHexString(frame[12])+Integer.toHexString(frame[13])).equals("80")){
    		return true;
    	}
    	return false;
    }
    
    public static boolean isARP(byte[] frame) {
    	if ((Integer.toHexString(frame[12])+Integer.toHexString(frame[13])).equals("86")){
    		return true;
    	}
    	return false;
    }
    
    public static String getFrameTypeInString(byte[] frame) {            
    	if ((Integer.toHexString(frame[12])+Integer.toHexString(frame[13])).equals("80")){
            return "IP";
        } else if ((Integer.toHexString(frame[12])+Integer.toHexString(frame[13])).equals("86")){
            return "ARP";
        } else {
            return "NOT KNOWN";
        }
    }
    
    public static MacAddress getDestMacAddress(byte[] frame){
        MacAddress addr = new MacAddress(frame, 0);
        return addr;
    }
    
    public static MacAddress getSourceMacAddress(byte[] frame){
        MacAddress addr = new MacAddress(frame, 6);
        return addr;
    }
    
    public static byte[] getFramePayload(byte[] frame) {        
        byte[] packet = new byte[frame.length-14];        
        System.arraycopy(frame, 14, packet, 0, packet.length);
        return packet;
    }
    
    public static byte[] buildFrame(byte[] payload, MacAddress dest, MacAddress source) {
        byte[]  type = new byte[] { 0x08 , 0x00};
        byte[] frame = new byte[dest.getAddress().length+source.getAddress().length+type.length+payload.length];                                
        
        System.arraycopy(dest.getAddress(),0,frame,0,dest.getAddress().length);
        System.arraycopy(source.getAddress(),0,frame,6,source.getAddress().length);
        System.arraycopy(type,0,frame,12,type.length);
        System.arraycopy(payload,0,frame,14,payload.length);
        
        return frame;
    }    
}
