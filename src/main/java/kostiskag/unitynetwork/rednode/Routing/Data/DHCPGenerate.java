/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kostiskag.unitynetwork.rednode.Routing.Data;

import kostiskag.unitynetwork.rednode.RedNode.lvl3RedNode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kostis
 */
public class DHCPGenerate {

    private static MacAddress DhcpMacAddr;
    private static MacAddress DestMac;
    private static InetAddress dhcpIp;
    private static InetAddress DestIp;
    private static byte[] yclIp;
    private static byte[] TransactionId;
    private static String pre= "^DHCPGEN ";

    public static byte[] GenerateFrame(DHCPrequest req, int DHCPType) {
        String info = pre + "Generating a DHCP frame - ";
        //intializing globals
        DhcpMacAddr = new MacAddress("02:00:00:00:00:00");
        DestMac = lvl3RedNode.login.connection.MyMac;
        DestIp = lvl3RedNode.login.connection.MyIP;
        yclIp = new byte[]{0x00, 0x00, 0x00, 0x00};
        TransactionId = new byte[]{0x00, 0x00, 0x00, 0x00};
        try {
            dhcpIp = InetAddress.getByName("10.0.0.1");
        } catch (UnknownHostException ex) {
            Logger.getLogger(DHCPGenerate.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[][] opt = new byte[10][];


        /*
         * OPTIONS FOR OPTIONS
         *
         */

        //type
        opt[0] = new byte[]{0x35, 0x01, 0x02};

        //54 dhcp server ip
        byte[] didtype = new byte[]{0x36};
        byte[] didlen = new byte[]{0x04};
        dhcpIp = null;
        try {
            dhcpIp = InetAddress.getByName("10.0.0.1");
        } catch (UnknownHostException ex) {
            Logger.getLogger(DHCPGenerate.class.getName()).log(Level.SEVERE, null, ex);
        }
        opt[1] = new byte[didtype.length + didlen.length + dhcpIp.getAddress().length];
        System.arraycopy(didtype, 0, opt[1], 0, didtype.length);
        System.arraycopy(didlen, 0, opt[1], 1, didlen.length);
        System.arraycopy(dhcpIp.getAddress(), 0, opt[1], 2, dhcpIp.getAddress().length);

        //51 ip addr lease time
        byte[] lstype = new byte[]{0x33};
        byte[] lslen = new byte[]{0x04};
        byte[] lstime = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        opt[2] = new byte[lstype.length + lslen.length + lstime.length];
        System.arraycopy(lstype, 0, opt[2], 0, lstype.length);
        System.arraycopy(lslen, 0, opt[2], 1, lslen.length);
        System.arraycopy(lstime, 0, opt[2], 2, lstime.length);

        //12 Host name
        byte[] hostype = new byte[]{0x0c};
        byte[] hostlen = new byte[]{(byte) lvl3RedNode.login.connection.Hostname.getBytes().length};
        byte[] hostname = lvl3RedNode.login.connection.Hostname.getBytes();
        opt[3] = new byte[hostype.length + hostlen.length + hostname.length];
        System.arraycopy(hostype, 0, opt[3], 0, hostype.length);
        System.arraycopy(hostlen, 0, opt[3], 1, hostlen.length);
        System.arraycopy(hostname, 0, opt[3], 2, hostname.length);

        //1 subnet mask
        byte[] stype = new byte[]{0x01};
        byte[] slen = new byte[]{0x04};
        byte[] smask = new byte[4];
        try {
            smask = InetAddress.getByName("255.0.0.0").getAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(DHCPGenerate.class.getName()).log(Level.SEVERE, null, ex);
        }
        opt[4] = new byte[1 + slen.length + smask.length];
        System.arraycopy(stype, 0, opt[4], 0, stype.length);
        System.arraycopy(slen, 0, opt[4], 1, slen.length);
        System.arraycopy(smask, 0, opt[4], 2, smask.length);

        //3 router - NO ROUTER
        byte[] rtype = new byte[]{0x03};
        byte[] rlen = new byte[]{0x04};
        byte[] rip = new byte[4];
        try {
            rip = InetAddress.getByName("0.0.0.0").getAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(DHCPGenerate.class.getName()).log(Level.SEVERE, null, ex);
        }
        opt[5] = new byte[1 + rlen.length + rip.length];
        System.arraycopy(rtype, 0, opt[5], 0, rtype.length);
        System.arraycopy(rlen, 0, opt[5], 1, rlen.length);
        System.arraycopy(rip, 0, opt[5], 2, rip.length);

        //6 DNS SERVEEEER
        byte[] dtype = new byte[]{0x06};
        byte[] dlen = new byte[]{0x04};
        byte[] dip = new byte[4];
        try {
            dip = InetAddress.getByName("10.0.0.2").getAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(DHCPGenerate.class.getName()).log(Level.SEVERE, null, ex);
        }
        opt[6] = new byte[1 + dlen.length + dip.length];
        System.arraycopy(dtype, 0, opt[6], 0, dtype.length);
        System.arraycopy(dlen, 0, opt[6], 1, dlen.length);
        System.arraycopy(dip, 0, opt[6], 2, dip.length);

        //end of options
        byte[] end = new byte[]{(byte) -1};

        //basic intelligence      
        if (DHCPType == 0) {
            //dhcp offer (broadcast)
            opt[0] = new byte[]{0x35, 0x01, 0x02};
            DestMac = new MacAddress("ff:ff:ff:ff:ff:ff");
            DestIp = lvl3RedNode.login.connection.MyIP;
            yclIp = lvl3RedNode.login.connection.MyIP.getAddress();            
            lstime = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            info = info + "offer (broadcast)";
        } else if (DHCPType == 1) {
            //dhcp offer (unicast)
            opt[0] = new byte[]{0x35, 0x01, 0x02};
            DestMac = lvl3RedNode.login.connection.MyMac;
            DestIp = lvl3RedNode.login.connection.MyIP;
            yclIp = lvl3RedNode.login.connection.MyIP.getAddress();
            lstime = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
            info = info + "offer (unicast)";
        } else if (DHCPType == 2) {
            //6 DHCPNak 
            opt[0] = new byte[]{0x35, 0x01, 0x06};
            DestMac = lvl3RedNode.login.connection.MyMac;
            try {
                DestIp = InetAddress.getByName("255.255.255.255");
            } catch (UnknownHostException ex) {
                Logger.getLogger(DHCPGenerate.class.getName()).log(Level.SEVERE, null, ex);
            }
            opt[0] = new byte[]{0x35, 0x01, 0x06};
            yclIp = new byte[]{0x00, 0x00, 0x00, 0x00};
            lstime = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
            System.arraycopy(lstime, 0, opt[2], 2, lstime.length);
            info = info + "nack";
        } else if (DHCPType == 3) {
            //dhcp ack
            opt[0] = new byte[]{0x35, 0x01, 0x05};
            DestMac = lvl3RedNode.login.connection.MyMac;
            DestIp = lvl3RedNode.login.connection.MyIP;
            yclIp = lvl3RedNode.login.connection.MyIP.getAddress();
            lstime = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
            System.arraycopy(lstime, 0, opt[2], 2, lstime.length);                                 
            info = info + "ack";
        } 
        
        lvl3RedNode.login.monitor.writeToIntWrite(info);
        
        /*
         * BOOTP HEAD         
         */

        byte[] bootpOptions = new byte[200];
        int arraylen = 0;
        for (int l = 0; l < 7; l++) {
            System.arraycopy(opt[l], 0, bootpOptions, arraylen, opt[l].length);
            arraylen += opt[l].length;
        }
        System.arraycopy(end, 0, bootpOptions, arraylen, end.length);


        //bootp options
        byte[] bootpHead = new byte[240];
        bootpHead = GenerateBootPHeader(req);

        //udp
        byte[] udpHead = new byte[8];
        udpHead = GenerateUDPHead(bootpHead.length + bootpOptions.length);

        //ip header
        byte[] ipHead = new byte[20];
        ipHead = GenerateIPHead(bootpHead.length + bootpOptions.length + udpHead.length);

        //frame header
        byte[] framehead = new byte[14];
        framehead = GenerateFrameHead();

        //frame
        byte[] frame = new byte[framehead.length + ipHead.length + udpHead.length + bootpHead.length + bootpOptions.length];
        frame = GenerateFrame(bootpOptions, bootpHead, udpHead, ipHead, framehead);

        return frame;
    }

    public static byte[] GenerateBootPHeader(DHCPrequest req) {
        //bootstrap prot        
        //reply 0x02
        byte[] opcode = new byte[]{0x02};
        //eth 0x01
        byte[] NetType = new byte[]{0x01};
        //hw addr 0x06
        byte[] HwAddr = new byte[]{0x06};
        //hops 0x00
        byte[] hops = new byte[]{0x00};
        //transaction id
        TransactionId = new byte[4];
        TransactionId = req.getTransactionId();
        //seconds elapsed
        byte[] secs = new byte[]{0x00, 0x00};
        //flags
        byte[] bflags = new byte[]{0x00, 0x00};
        //client ip
        byte[] clIp = new byte[]{0x00, 0x00, 0x00, 0x00};
        //yclIp = lvl3RedNode.MyIP.getAddress();
        byte[] nextserv = new byte[]{0x00, 0x00, 0x00, 0x00};
        byte[] relag = new byte[]{0x00, 0x00, 0x00, 0x00};
        //client mac
        byte[] clmac = lvl3RedNode.login.connection.MyMac.getAddress();
        //padding
        byte[] padding = new byte[10];
        //server host name
        byte[] servHn = new byte[62];
        //boot file name
        byte[] bootfn = new byte[128];
        //magic cookie - dhcp
        byte[] dhcp = new byte[]{0x63, (byte) 0x82, 0x53, 0x63};

        //bootstrap header
        byte[] bstrapHead = new byte[240];
        System.arraycopy(opcode, 0, bstrapHead, 0, opcode.length);
        System.arraycopy(NetType, 0, bstrapHead, 1, NetType.length);
        System.arraycopy(HwAddr, 0, bstrapHead, 2, HwAddr.length);
        System.arraycopy(hops, 0, bstrapHead, 3, hops.length);
        System.arraycopy(TransactionId, 0, bstrapHead, 4, TransactionId.length);
        System.arraycopy(hops, 0, bstrapHead, 8, hops.length);
        System.arraycopy(secs, 0, bstrapHead, 9, secs.length);
        System.arraycopy(bflags, 0, bstrapHead, 10, bflags.length);
        System.arraycopy(clIp, 0, bstrapHead, 12, clIp.length);
        System.arraycopy(yclIp, 0, bstrapHead, 16, yclIp.length);
        System.arraycopy(nextserv, 0, bstrapHead, 20, nextserv.length);
        System.arraycopy(relag, 0, bstrapHead, 24, relag.length);
        System.arraycopy(clmac, 0, bstrapHead, 28, clmac.length);
        System.arraycopy(padding, 0, bstrapHead, 34, padding.length);
        System.arraycopy(servHn, 0, bstrapHead, 44, servHn.length);
        System.arraycopy(bootfn, 0, bstrapHead, 106, bootfn.length);
        System.arraycopy(dhcp, 0, bstrapHead, 236, dhcp.length);

        return bstrapHead;
    }

    public static byte[] GenerateUDPHead(int bootpLen) {
        //udp        
        //sourcep 0x00 0x43
        byte[] sourcePort = new byte[]{0x00, 0x43};
        //destp   0x00 0x44
        byte[] destPort = new byte[]{0x00, 0x44};
        //len 2bytes????????????????????????????????????????????????????????????
        byte[] udplen = new byte[]{0x00, 0x00};
        //2byte checksum
        byte[] udpcrc = new byte[]{0x00, 0x00};

        udplen = new byte[]{
            (byte) ((bootpLen + 8) >>> 8),
            (byte) (bootpLen + 8)};


        byte[] udpHead = new byte[8];
        System.arraycopy(sourcePort, 0, udpHead, 0, sourcePort.length);
        System.arraycopy(destPort, 0, udpHead, 2, destPort.length);
        System.arraycopy(udplen, 0, udpHead, 4, udplen.length);
        System.arraycopy(udpcrc, 0, udpHead, 6, udpcrc.length);

        return udpHead;
    }

    public static byte[] GenerateIPHead(int payloadlength) {
        //ip
        //version 0x45 0x40
        byte[] version = new byte[]{0x45, 0x40};
        //len 2byte???????????????????????????????????????????????????
        byte[] iplen = new byte[]{0x00, 0x00};
        //id 0x00 0x00
        byte[] id = new byte[]{0x00, 0x00};
        //flags 0x00
        byte[] flags = new byte[]{0x00};
        //offset 0x00
        byte[] offset = new byte[]{0x00};
        //time to live 0x40
        byte[] ttl = new byte[]{0x40};
        //prot udp 0x11
        byte[] udp = new byte[]{0x11};
        //2byte checksum??????????????????????????????????????????????????
        byte[] crc = new byte[]{0x00, 0x00};
        //src dhcp ip
        try {
            dhcpIp = InetAddress.getByName("10.0.0.1");
        } catch (UnknownHostException ex) {
            Logger.getLogger(DHCPGenerate.class.getName()).log(Level.SEVERE, null, ex);
        }
        //dest client ip in logic

        iplen = new byte[]{
            (byte) ((payloadlength + 20) >>> 8),
            (byte) (payloadlength + 20)};

        //ip header
        byte[] ipHead = new byte[20];
        System.arraycopy(version, 0, ipHead, 0, version.length);
        System.arraycopy(iplen, 0, ipHead, 2, iplen.length);
        System.arraycopy(id, 0, ipHead, 4, id.length);
        System.arraycopy(flags, 0, ipHead, 6, flags.length);
        System.arraycopy(offset, 0, ipHead, 7, offset.length);
        System.arraycopy(ttl, 0, ipHead, 8, ttl.length);
        System.arraycopy(udp, 0, ipHead, 9, udp.length);
        System.arraycopy(crc, 0, ipHead, 10, crc.length);
        System.arraycopy(dhcpIp.getAddress(), 0, ipHead, 12, dhcpIp.getAddress().length);
        System.arraycopy(DestIp.getAddress(), 0, ipHead, 16, DestIp.getAddress().length);

        long lcrc = CalculateChecksum.calculateChecksum(ipHead);

        crc = new byte[]{
            (byte) ((lcrc) >>> 8),
            (byte) (lcrc)};
        System.arraycopy(crc, 0, ipHead, 10, crc.length);
        return ipHead;
    }

    public static byte[] GenerateFrameHead() {
        //frame
        //dest mac in basic logic        
        //dhcp mac in start        
        //type 08 00
        byte[] type = new byte[]{0x08, 0x00};

        //frame
        byte[] framehead = new byte[14];
        System.arraycopy(DestMac.getAddress(), 0, framehead, 0, DestMac.getAddress().length);
        System.arraycopy(DhcpMacAddr.getAddress(), 0, framehead, 6, DhcpMacAddr.getAddress().length);
        System.arraycopy(type, 0, framehead, 12, type.length);

        return framehead;
    }

    public static byte[] GenerateFrame(byte[] bstrapOptions, byte[] bstrapHead, byte[] udpHead, byte[] ipHead, byte[] framehead) {
        byte[] frame = new byte[framehead.length + ipHead.length + udpHead.length + bstrapHead.length + bstrapOptions.length];
        System.arraycopy(framehead, 0, frame, 0, framehead.length);
        System.arraycopy(ipHead, 0, frame, 14, ipHead.length);
        System.arraycopy(udpHead, 0, frame, 34, udpHead.length);
        System.arraycopy(bstrapHead, 0, frame, 42, bstrapHead.length);
        System.arraycopy(bstrapOptions, 0, frame, 282, bstrapOptions.length);

        return frame;
    }
}
