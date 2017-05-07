package kostiskag.unitynetwork.rednode.Routing.data;

import java.net.InetAddress;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class ARPGenerate {

    public static byte[] ArpGenerate(MacAddress myMac, InetAddress myIp, MacAddress senderMacAddress, InetAddress senderIp) {        
        if (myMac==null){
            System.out.println("null personal mac, arp exit");
            return null;
        } else if (myIp==null){
            System.out.println("null ip, arp exit");
            return null;
        } else if (senderMacAddress==null){
            System.out.println("null sender mac, arp exit");
            return null;
        } else if (senderIp==null){
            System.out.println("null sender ip, arp exit");
            return null;
        } else {
                
        //dest
        //source
        byte[] frameType = new byte[]{0x08, 0x06};
        byte[] HWType = new byte[]{0x00, 0x01};
                
        byte[] ProtType = new byte[]{0x08, 0x00};
        byte[] HwSize = new byte[]{0x06};
        byte[] ProtSize = new byte[]{0x04};
        byte[] OpCode = new byte[]{0x00, 0x02};
        
        //sender mac
        //sender ip
        //target mac
        //target ip               
        byte[] frame = new byte[
                myMac.getAddress().length + 
                senderMacAddress.getAddress().length + 
                frameType.length +                 
                HWType.length+
                
                ProtType.length+
                HwSize.length+
                ProtSize.length+                
                OpCode.length + 
                
                senderMacAddress.getAddress().length+
                senderIp.getAddress().length+
                myMac.getAddress().length+
                myIp.getAddress().length
        ];                       
        
        System.arraycopy(myMac.getAddress(),0,frame,0,myMac.getAddress().length);       
        System.arraycopy(senderMacAddress.getAddress(),0,frame,6,senderMacAddress.getAddress().length);       
        System.arraycopy(frameType,0,frame,12,frameType.length);  
        System.arraycopy(HWType,0,frame,14,HWType.length);
        
        System.arraycopy(ProtType,0,frame,16,ProtType.length);
        System.arraycopy(HwSize,0,frame,18,HwSize.length);
        System.arraycopy(ProtSize,0,frame,19,ProtSize.length);
        System.arraycopy(OpCode,0,frame,20,OpCode.length);
        System.arraycopy(senderMacAddress.getAddress(),0,frame,22,senderMacAddress.getAddress().length);
        System.arraycopy(senderIp.getAddress(),0,frame,28,senderIp.getAddress().length);
        System.arraycopy(myMac.getAddress(),0,frame,32,myMac.getAddress().length);
        System.arraycopy(myIp.getAddress(),0,frame,38,myIp.getAddress().length);
            
        return frame;
        }     
    }
}
