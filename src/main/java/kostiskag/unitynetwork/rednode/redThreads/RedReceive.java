package kostiskag.unitynetwork.rednode.redThreads;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.QueueManager;
import kostiskag.unitynetwork.rednode.Routing.packets.IPv4Packet;
import kostiskag.unitynetwork.rednode.Routing.packets.UnityPacket;

/**
 * This class's thread listens for incoming traffic and writes them to the received Queue.
 * In addition, it sends acks to the sources
 * 
 * @author Konstantinos Kagiampakis
 */
public class RedReceive extends Thread {

    private final String pre = "^RedReceive ";
    private final InetAddress address;
    private final int port;
    private final QueueManager receiveQueue;
    private DatagramSocket socket;
    private AtomicBoolean kill = new AtomicBoolean(false);
    
    public RedReceive(InetAddress address, int port, QueueManager receiveQueue) {
        this.address = address;
        this.port = port;
        this.receiveQueue = receiveQueue; //App.login.connection.downMan        
    }

    @Override
    public void run() {

        byte[] sendData;
        byte[] buffer = new byte[2048];
        
        socket = null;
        try {
            socket = new DatagramSocket();
        } catch (java.net.BindException ex1) {
            App.login.writeInfo(pre + "SOCKET ALLREADY BIND EXCEPTION");
            return;
        } catch (SocketException ex) {
        	App.login.writeInfo(pre + "SOCKET EXCEPTION");
            Logger.getLogger(RedReceive.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        sendData = "FISH_PACKET".getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        try {
            for (int i = 0; i < 10; i++) {
                socket.send(sendPacket);
                System.out.println(pre+"fish packet sent to "+address.getHostAddress()+port);
                try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        } catch (java.net.SocketException ex1) {
            App.login.monitor.writeToConnectionDown(pre+"FISH PACKET SEND ERROR");
            return;
        } catch (IOException ex) {            
            Logger.getLogger(RedReceive.class.getName()).log(Level.SEVERE, null, ex);
            App.login.monitor.writeToConnectionDown(pre+"IO ERROR");
            return;
        }

        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
        while (!kill.get()) {
            try {
            	socket.receive(receivePacket);
            } catch (java.net.SocketException ex) {
            	App.login.monitor.writeToConnectionDown(pre+"socket exception.");
                return;
            } catch (IOException ex) {
            	App.login.monitor.writeToConnectionDown(pre+"IO exception.");
            	Logger.getLogger(RedReceive.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            int len = receivePacket.getLength();
            if (len == 0 || len > 1500) {
            	App.login.monitor.writeToConnectionDown(pre+"received wrong sized packet.");
            	continue;
            }
            
            byte[] packet = new byte[len];
            System.arraycopy(receivePacket.getData(), 0, packet, 0, len);

            if (UnityPacket.isUnity(packet)) {
                if (UnityPacket.isKeepAlive(packet)) {
                	App.login.monitor.writeToConnectionDown(pre+"KEEP ALIVE RECEIVED");
                
                } else if(UnityPacket.isUping(packet)) { 
                	App.login.monitor.writeToConnectionDown(pre+"UPING RECEIVED");
                	
                } else if(UnityPacket.isDping(packet)) {
                	App.login.monitor.writeToConnectionDown(pre+"DPING RECEIVED");
                	App.login.connection.setClientDPinged(true);
                    
                } else if (UnityPacket.isShortRoutedAck(packet)) {
                	App.login.monitor.writeToConnectionDown(pre+"SHORT ROUTED ACK RECEIVED");
                	try {
						App.login.connection.getTrafficMan().gotACK(UnityPacket.getShortRoutedAckTrackNum(packet));
					} catch (Exception e) {
						e.printStackTrace();
					}
                	
                } else if (UnityPacket.isLongRoutedAck(packet)) {
                	App.login.monitor.writeToConnectionDown(pre+"LONG ROUTED ACK RECEIVED");
                	/*
                	if (App.login.connection.arpTable.isAssociated(UnityPacket.getSourceAddress(packet))) {
						try {
							App.login.connection.getTrafficMan().gotACK();
						} catch (Exception e) {
							e.printStackTrace();
						}
            	    }
            	    */
                    
                } else if (UnityPacket.isMessage(packet)) {
                	String message;
					try {
						message = pre+UnityPacket.getSourceAddress(packet).getHostAddress()+": "+UnityPacket.getMessageMessage(packet) + '\0';
						App.login.monitor.writeToConnectionDown(message);
	                    
	                    //this is the place to build short routed acks
	                    byte[] ACKS = UnityPacket.buildShortRoutedAckPacket(0);
	                    App.login.connection.getUpMan().offer(ACKS);
	                    
	                    //this is the place to build end to end acks
	                    //byte[] ACKL = UnityPacket.buildLongRoutedAckPacket(App.login.connection.getMyIP(), UnityPacket.getSourceAddress(packet), 0);
	                    //App.login.connection.getUpMan().offer(ACKL);
	                    
					} catch (Exception e) {
						e.printStackTrace();
					}
                    
                } 
	        } else if (IPv4Packet.isIPv4(packet)) {
            	try {
					App.login.monitor.writeToConnectionDown(pre+"IPv4 RECEIVED " + packet.length + " Bytes from: " + IPv4Packet.getSourceAddress(packet).getHostAddress());
				
					App.login.monitor.updateConUpBufferQueue(receiveQueue.getlen());
					receiveQueue.offer(packet);                    
                    
                    //this is the place to build short routed acks
                    byte[] ACKS = UnityPacket.buildShortRoutedAckPacket(0);
                    App.login.connection.getUpMan().offer(ACKS);
                    
                    //this is the place to build end to end acks
                    //byte[] ACKL = UnityPacket.buildLongRoutedAckPacket(App.login.connection.getMyIP(), IPv4Packet.getSourceAddress(packet), 0);
                    //App.login.connection.getUpMan().offer(ACKL);
                
                } catch (Exception e) {
					e.printStackTrace();
				}
            }
            App.login.monitor.updateConDownBufferQueue(receiveQueue.getlen());
        }
        receiveQueue.clear();
        App.login.monitor.writeToConnectionDown(pre+"ended");
    }

    public void kill() {
        kill.set(true);
        socket.close();
    }
}
