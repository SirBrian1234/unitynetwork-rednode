package kostiskag.unitynetwork.rednode.Routing.Data;

/**
 *
 * @author kostis
 */
public class Frame {
    
     public static byte[] MakeFrame(byte[] packet, MacAddress dest, MacAddress source) {
        byte[]  type = new byte[] { 0x08 , 0x00};
        byte[] frame = new byte[dest.getAddress().length+source.getAddress().length+type.length+packet.length];                                
        
        System.arraycopy(dest.getAddress(),0,frame,0,dest.getAddress().length);
        System.arraycopy(source.getAddress(),0,frame,6,source.getAddress().length);
        System.arraycopy(type,0,frame,12,type.length);
        System.arraycopy(packet,0,frame,14,packet.length);
        
        return frame;
    }
    
    public static FrameType getFrameType(byte[] frame) {            
        return new FrameType(frame);
    }
    
    public static MacAddress GetDestMacAddress(byte[] frame){
        MacAddress addr = new MacAddress(frame, 0);
        return addr;
    }
    
    public static MacAddress GetSourceMacAddress(byte[] frame){
        MacAddress addr = new MacAddress(frame, 6);
        return addr;
    }
}
