package kostiskag.unitynetwork.rednode.Routing;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.data.ARPGenerate;
import kostiskag.unitynetwork.rednode.Routing.data.ARPPacketRequest;
import kostiskag.unitynetwork.rednode.Routing.data.DHCPGenerate;
import kostiskag.unitynetwork.rednode.Routing.data.DHCPrequest;
import kostiskag.unitynetwork.rednode.Routing.packets.IPv4Packet;

/**
 * This linear progress code will help up organize ethernet
 * connection and issues like doing arps after dhcp and that kind of stuff.
 * 
 * @author kostis 
 */
public class EthernetConnection {

    private String pre = "^EthernetConnection ";
    boolean ack;
    private ARPPacketRequest arppacket;
    private byte[] answer;
    private boolean HadFirstDHCP = false;
    private boolean hadDHCPNak = false;

    public EthernetConnection() {
    	
    }

    boolean clearToSendIP(byte[] ippacket) {
        //it means we had dhcp association
        if (App.login.connection.IsDHCPset == true) {
            if (App.login.connection.MyIP.equals(IPv4Packet.getSourceAddress(ippacket))) {
                App.login.monitor.writeToIntRead(pre + "THE SOURCE IS ME");
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
        if (App.login.connection.IsDHCPset == true) {
            arppacket = new ARPPacketRequest(frame);
            if (arppacket.getTarget().equals(App.login.connection.MyIP)) {
                App.login.monitor.writeToIntRead(pre + "Checking on me :P");
            } else if (App.login.connection.arpTable.isAssociated(arppacket.getTarget())) {
                App.login.monitor.writeToIntRead(pre + "REGISTERED HOST");
                try {
					answer = ARPGenerate.ArpGenerate(App.login.connection.arpTable.getByIP(arppacket.getTarget()).getMac(), arppacket.getTarget());
					for (int i = 0; i < 2; i++) {
	                    App.login.connection.writeMan.offer(answer);
	                }
                } catch (Exception e) {
					e.printStackTrace();
				}                
            } else {
                App.login.monitor.writeToIntRead(pre + "NEW HOST");
                App.login.monitor.writeToIntRead("leasing: " + arppacket.getTarget().getHostAddress());
                try {
					App.login.connection.arpTable.lease(arppacket.getTarget());
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
    }

    void giveBootstrap(byte[] frame) {
        DHCPrequest req = new DHCPrequest(frame);
        if (HadFirstDHCP == false) {
            App.login.connection.MyMac = req.getSourceMac();
            HadFirstDHCP = true;
            App.login.monitor.writeToIntRead(pre + "MY MAC IS " + App.login.connection.MyMac.toString());
        }

        int type = req.getType();

        //dhcp logic       
        byte[] genframe = null;
        //discover
        if (type == 1) {
            if (req.asksIP()) {
                //he is veeery demanding by the way... you just cant ask for address just like that, but anyway the client has always right
                if (!req.getRequestIP().equals(App.login.connection.MyIP)) {
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
            if (!req.getRequestIP().equals(App.login.connection.MyIP)) {
                //nack
                genframe = DHCPGenerate.GenerateFrame(req, 2);
                hadDHCPNak = true;
            } else {
                //ack
                genframe = DHCPGenerate.GenerateFrame(req, 3);
                App.login.connection.IsDHCPset = true;
            }
        }
        App.login.connection.writeMan.offer(genframe);
    }
}
