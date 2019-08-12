package org.kostiskag.unitynetwork.rednode.routing;

import java.net.InetAddress;

import org.kostiskag.unitynetwork.common.routing.packet.IPv4Packet;
import org.kostiskag.unitynetwork.rednode.App;
import org.kostiskag.unitynetwork.rednode.routing.data.ARPGenerate;
import org.kostiskag.unitynetwork.rednode.routing.data.ARPPacketRequest;
import org.kostiskag.unitynetwork.rednode.routing.data.DHCPGenerate;
import org.kostiskag.unitynetwork.rednode.routing.data.DHCPrequest;

/**
 * This linear progress code will help up organize ethernet
 * connection and issues like doing arps after dhcp and that kind of stuff.
 * 
 * @author Konstantinos Kagiampakis 
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
        if (App.login.connection.isIsDHCPset()) {
        	InetAddress source = null;
			try {
				source = IPv4Packet.getSourceAddress(ippacket);
			} catch (Exception e) {
				e.printStackTrace();
			}
            if (App.login.connection.getMyIP().equals(source)) {
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
        if (App.login.connection.isIsDHCPset()) {
            arppacket = new ARPPacketRequest(frame);
            if (arppacket.getTarget().equals(App.login.connection.getMyIP())) {
                App.login.monitor.writeToIntRead(pre + "Checking on me :P");
            } else if (App.login.connection.getArpTable().isAssociated(arppacket.getTarget())) {
                App.login.monitor.writeToIntRead(pre + "REGISTERED HOST");
                try {
					answer = ARPGenerate.ArpGenerate(App.login.connection.getMyMac(), App.login.connection.getMyIP(), App.login.connection.getArpTable().getByIP(arppacket.getTarget()).getMac(), arppacket.getTarget());
					for (int i = 0; i < 2; i++) {
	                    App.login.connection.getWriteMan().offer(answer);
	                }
                } catch (Exception e) {
					e.printStackTrace();
				}                
            } else {
                App.login.monitor.writeToIntRead(pre + "NEW HOST");
                App.login.monitor.writeToIntRead("leasing: " + arppacket.getTarget().getHostAddress());
                try {
					App.login.connection.getArpTable().lease(arppacket.getTarget());
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
        }
    }

    void giveBootstrap(byte[] frame) {
        DHCPrequest req = new DHCPrequest(frame);
        if (HadFirstDHCP == false) {
            App.login.connection.setMyMac(req.getSourceMac());
            HadFirstDHCP = true;
            App.login.monitor.writeToIntRead(pre + "MY MAC IS " + App.login.connection.getMyMac().toString());
        }

        int type = req.getType();

        //dhcp logic       
        byte[] genframe = null;
        //discover
        if (type == 1) {
            if (req.asksIP()) {
                //he is veeery demanding by the way... you just cant ask for address just like that, but anyway the client has always right
                if (!req.getRequestIP().equals(App.login.connection.getMyIP())) {
                    //nak broadcast
                    genframe = DHCPGenerate.GenerateFrame(App.login.connection.getMyMac(), App.login.connection.getMyIP(), req, 2);
                    hadDHCPNak = true;
                } else {
                    //offer unicast i think... anyway... forget it...
                    genframe = DHCPGenerate.GenerateFrame(App.login.connection.getMyMac(), App.login.connection.getMyIP(), req, 1);
                }
            } else {
                //offer unicast
                genframe = DHCPGenerate.GenerateFrame(App.login.connection.getMyMac(), App.login.connection.getMyIP(), req, 1);
            }
        } //request
        else {
            if (!req.getRequestIP().equals(App.login.connection.getMyIP())) {
                //nack
                genframe = DHCPGenerate.GenerateFrame(App.login.connection.getMyMac(), App.login.connection.getMyIP(), req, 2);
                hadDHCPNak = true;
            } else {
                //ack
                genframe = DHCPGenerate.GenerateFrame(App.login.connection.getMyMac(), App.login.connection.getMyIP(), req, 3);
                App.login.connection.setIsDHCPset(true);
            }
        }
        App.login.connection.getWriteMan().offer(genframe);
    }
}
