/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.Routing.Data.Packet;
import kostiskag.unitynetwork.rednode.Routing.Data.ArpPacketRequest;
import kostiskag.unitynetwork.rednode.Routing.Data.ArpGenerate;
import kostiskag.unitynetwork.rednode.Routing.Data.DHCPrequest;
import kostiskag.unitynetwork.rednode.Routing.Data.DHCPGenerate;
import kostiskag.unitynetwork.rednode.GUI.MonitorWindow;
import kostiskag.unitynetwork.rednode.RedNode.lvl3RedNode;

/**
 *
 * @author kostis this linear progress code will help up organize ethernet
 * connection and issues like doing arps after dhcp anthat kind of stuff
 *
 */
public class EthernetConnection {

    private String pre = "^ETHCONN ";
    boolean ack;
    private ArpPacketRequest arppacket;
    private byte[] answer;
    private boolean HadFirstDHCP = false;
    private boolean hadDHCPNak = false;

    public EthernetConnection() {
    }

    boolean clearToSendIP(byte[] ippacket) {
        //it means we had dhcp association
        if (lvl3RedNode.login.connection.IsDHCPset == true) {
            if (lvl3RedNode.login.connection.MyIP.equals(Packet.getSourceAddress(ippacket))) {
                lvl3RedNode.login.monitor.writeToIntRead(pre + "THE SOURCE IS ME");
                //frame unicast, ip unicast, source is me, cant do much more, we have to send it
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    void giveARP(byte[] frame) {
        //if we had an association we can use arps
        if (lvl3RedNode.login.connection.IsDHCPset == true) {
            arppacket = new ArpPacketRequest(frame);
            if (arppacket.getTarget().equals(lvl3RedNode.login.connection.MyIP)) {
                lvl3RedNode.login.monitor.writeToIntRead(pre + "Checking on me :P");
            } else if (lvl3RedNode.login.connection.arpTable.isAssociated(arppacket.getTarget())) {
                lvl3RedNode.login.monitor.writeToIntRead(pre + "REGISTERED HOST");
                answer = ArpGenerate.ArpGenerate(lvl3RedNode.login.connection.arpTable.getByIP(arppacket.getTarget()).getMac(), arppacket.getTarget());
                for (int i = 0; i < 2; i++) {
                    lvl3RedNode.login.connection.writeMan.offer(answer);
                }
            } else {
                lvl3RedNode.login.monitor.writeToIntRead(pre + "NEW HOST");
                lvl3RedNode.login.monitor.writeToIntRead("leasing: " + arppacket.getTarget().getHostAddress());
                lvl3RedNode.login.connection.arpTable.lease(arppacket.getTarget());
            }
        }
    }

    void giveBootstrap(byte[] frame) {
        DHCPrequest req = new DHCPrequest(frame);
        if (HadFirstDHCP == false) {
            lvl3RedNode.login.connection.MyMac = req.getSourceMac();
            HadFirstDHCP = true;
            lvl3RedNode.login.monitor.writeToIntRead(pre + "MY MAC IS " + lvl3RedNode.login.connection.MyMac.toString());
        }

        int type = req.getType();

        //dhcp logic       
        byte[] genframe = null;
        //discover
        if (type == 1) {
            if (req.asksIP()) {
                //he is veeery demanding by the way... you just cant ask for address just like that, but anyway the client has always right
                if (!req.getRequestIP().equals(lvl3RedNode.login.connection.MyIP)) {
                    //nak broadcast
                    genframe = DHCPGenerate.GenerateFrame(req, 2);
                    hadDHCPNak = true;
                } else {
                    //offer unicast i think... anyway... forget it...
                    genframe = DHCPGenerate.GenerateFrame(req, 1);
                }
            } else {
                //offer unicast
                genframe = DHCPGenerate.GenerateFrame(req, 1);
            }
        } //request
        else {
            if (!req.getRequestIP().equals(lvl3RedNode.login.connection.MyIP)) {
                //nack
                genframe = DHCPGenerate.GenerateFrame(req, 2);
                hadDHCPNak = true;
            } else {
                //ack
                genframe = DHCPGenerate.GenerateFrame(req, 3);
                lvl3RedNode.login.connection.IsDHCPset = true;
            }
        }
        lvl3RedNode.login.connection.writeMan.offer(genframe);
    }
}
