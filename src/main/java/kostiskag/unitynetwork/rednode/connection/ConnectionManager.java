package kostiskag.unitynetwork.rednode.connection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.*;
import kostiskag.unitynetwork.rednode.Routing.data.MacAddress;
import kostiskag.unitynetwork.rednode.Routing.data.ReverseARPTable;
import kostiskag.unitynetwork.rednode.Routing.networkInterface.InterfaceRead;
import kostiskag.unitynetwork.rednode.Routing.networkInterface.InterfaceWrite;
import kostiskag.unitynetwork.rednode.Routing.packets.UnityPacket;
import kostiskag.unitynetwork.rednode.functions.DetectOS;
import kostiskag.unitynetwork.rednode.redThreads.AuthClient;
import kostiskag.unitynetwork.rednode.redThreads.RedReceive;
import kostiskag.unitynetwork.rednode.redThreads.KeepAlive;
import kostiskag.unitynetwork.rednode.redThreads.RedSend;

import org.p2pvpn.tuntap.TunTap;

/**
 *
 * @author kostis
 */
public class ConnectionManager extends Thread {

    //login credentials
    private final  String username;
    private final  String password;
    private final  String hostname;    
    private final  String blueNodeAddress;
    private final  int blueNodePort;
    private InetAddress fullBlueNodeAddress;
                 
    private String vaddress;
    private InetAddress myIP;
    
    private  TunTap tuntap;
    private  InterfaceRead read;
    private  InterfaceWrite write;
    private  EthernetRouter router;
    private  VirtualRouter vrouter;
    
    private  QueueManager upMan;
    private  QueueManager downMan;
    private  QueueManager readMan;
    private  QueueManager writeMan;
    private  KeepAlive ka;
    private  UploadManager trafficMan;   
    
     //services & sockets    
    private  AuthClient authClient;
    private  RedReceive receive;
    private  RedSend send;           
    
    //ehternet & routing       
    private  int osType;
    private  ReverseARPTable arpTable;
    private  MacAddress myMac;
    private  boolean interfaceSet;
    private  boolean isDHCPset;  
    private  boolean libError; 
    private  AtomicBoolean isClientDPinged;      
    private String command;
    private AtomicBoolean kill = new AtomicBoolean(false);
    
    public ConnectionManager(String Username, String Password, String Hostname, String BlueNodeAddress, int BlueNodePort) {        
    	this.username = Username;        
        this.password = Password;
        this.hostname = Hostname;                        
        this.blueNodeAddress = BlueNodeAddress;
        this.blueNodePort = BlueNodePort;      
        
        if (!(BlueNodePort > 0 && BlueNodePort <= 65535)) {
            App.login.writeInfo("WRONG PORT GIVEN");
            return;          
        }

        try {
            fullBlueNodeAddress = InetAddress.getByName(BlueNodeAddress);
        } catch (UnknownHostException ex) {
            App.login.writeInfo("WRONG ADDRESS SYNTAX GIVEN"); 
            return;
        }
        
        this.isClientDPinged = new AtomicBoolean(false);
        this.isDHCPset = false;
        this.libError = false;
        
        upMan = new QueueManager(20);
        downMan = new QueueManager(20);
        readMan = new QueueManager(20);
        writeMan = new QueueManager(20);        
        trafficMan = new UploadManager();
    }
    
    public String getHostname() {
		return hostname;
	}
    
    public MacAddress getMyMac() {
		return myMac;
	}
    
    public InetAddress getMyIP() {
		return myIP;
	}
    
    public ReverseARPTable getArpTable() {
		return arpTable;
	}
    
    public UploadManager getTrafficMan() {
		return trafficMan;
	}
    
    public QueueManager getUpMan() {
		return upMan;
	}
    
    public QueueManager getDownMan() {
		return downMan;
	}
    
    public QueueManager getReadMan() {
		return readMan;
	}
    
    public QueueManager getWriteMan() {
		return writeMan;
	}
    
    public boolean isInterfaceSet() {
		return interfaceSet;
	}
    
    public boolean isIsDHCPset() {
		return isDHCPset;
	}
    
    public boolean isClientDPinged() {
		return isClientDPinged.get();
	}
    
    public void setMyMac(MacAddress myMac) {
		this.myMac = myMac;
	}
    
    public void setIsDHCPset(boolean isDHCPset) {
		this.isDHCPset = isDHCPset;
	}
    
    public void setClientDPinged(boolean isClientDPinged) {
		this.isClientDPinged.set(isClientDPinged);
	}
    
    public void setLibError(boolean libError) {
		this.libError = libError;
	}
    
    /*
     * thats the life of a connection from birth to usage and death
     */
    @Override
    public void run() {        
        osType = new DetectOS().getType();
        App.login.writeInfo("Running On " + DetectOS.getString());                                                
        
        App.login.writeInfo("Starting Connection...");
        App.login.writeInfo("Loging in Blue Node " + blueNodeAddress + ":" + blueNodePort + " ...");
        authClient = new AuthClient(username, password, hostname, fullBlueNodeAddress, blueNodePort);
        if (!authClient.auth()) {
            App.login.monitor.setLogedOut();
            App.login.setLogedOut();
            return;
        }
        
        vaddress = authClient.getVaddress();
        try {
            myIP = InetAddress.getByName(vaddress);
        } catch (UnknownHostException ex) {
        	App.login.writeInfo("Collected mallformed IP address :"+vaddress);   
        	Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            App.login.monitor.setLogedOut();
            App.login.setLogedOut();
            return;
        }
        App.login.writeInfo("Login OK");   
        
        
        App.login.writeInfo("Initializing Reverse ARP table...");
        arpTable = new ReverseARPTable(myIP);
        
        App.login.writeInfo("Opening LINK...");
        openThreads(authClient.getServerReceivePort(), authClient.getServerSendPort());
        
        App.login.writeInfo("Init socket event notification...");
        authClient.start();        
        
        App.login.writeInfo("Testing LINK...");
        if (!LinkDiagnostic()) {
            App.login.writeInfo("LINK Error");
            App.login.writeInfo("Working On Commands.");            
        }
        if (!startInterface()) {
            App.login.writeInfo("INTERFACE ERROR");
            App.login.writeInfo("You may not send real data but you can send text and commands");
        } else if (!checkDHCP()) {
            App.login.writeInfo("DHCP ERROR");
            App.login.writeInfo("Your interface failed to set the primal settings");
            App.login.writeInfo("You may not send real data but you can send text and commands");            
        } else {
            App.login.writeInfo("Interface OK");
        }
        App.login.writeInfo("Connection Set");
        App.login.writeInfo("Wellcome " + hostname + " ~ " + vaddress);
        LoggedIn();        
        
        authCommands();

        App.login.writeInfo("Killing Tasks...");
        killTasks();
        App.login.writeInfo("Closing Interface...");
        killInterface();
        App.login.writeInfo("Connection Closed");
        App.login.monitor.clearWindow();
        App.login.monitor.setLogedOut();
        App.login.setLogedOut();
    }
   
    private void openThreads(int serverReceive, int serverSend) {
        //set
    	receive = new RedReceive(fullBlueNodeAddress, serverSend, downMan);
        send = new RedSend(fullBlueNodeAddress, serverReceive, upMan);
        ka = new KeepAlive(upMan, App.keepAliveTimeSec);
        //start
        send.start();
        receive.start();
        ka.start();
        //just a time to make sure all the services started and running properly before testing
        try {
            sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean LinkDiagnostic() {
        boolean link = true;
        boolean uping = true;
        boolean dping = true;
        for (int i = 0; i < 3; i++) {
            if (!ping()) {
                return false;
            } else if (!uPing()) {
                App.login.writeInfo("THERE IS A PROBLEM WITH UPLINK TRYING TO FIX IT");
                uping = false;
                urefresh();
            } else if (!dPing()) {
                App.login.writeInfo("THERE IS A PROBLEM WITH DOWNLINK TRYING TO FIX IT");
                dping = false;
                drefresh();
            } else {
                uping = true;
                dping = true;
                App.login.writeInfo("LINK OK");
                break;
            }
        }
        if (!dping) {
            App.login.writeInfo("DOWNLOAD IS BROKEN");
            link = false;
        }
        if (!uping) {
            App.login.writeInfo("UPLOAD IS BROKEN");
            link = false;
        }
        return link;
    }

    public void LoggedIn() {
        byte[] packet = UnityPacket.buildMessagePacket(myIP, myIP, "HELLO MESSAGE");
        upMan.offer(packet);
        App.login.monitor.setLogedIn();
        App.login.setLoggedIn();
    }

    public synchronized void authCommands() {        
        while (!kill.get()) {                                    
            try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            if (kill.get()) {
            	break;
            } else if (!command.isEmpty()) {
                if (command.equals("PING")) {
                    authClient.ping();
                } else if (command.equals("UPING")) {
                    uPing();
                } else if (command.equals("DPING")) {
                    dPing();
                } else if (command.equals("DREFRESH")) {
                    drefresh();
                } else if (command.equals("UREFRESH")) {
                    urefresh();
                } else if (command.equals("DIAGNOSTICS")) {
                    LinkDiagnostic();
                } else if (command.equals("EXIT")) {
                    App.login.writeInfo("Logging Out...");
                    authClient.exit();
                    break;
                } else {
                    authClient.sendAuthData(command);
                }
                command = "";                
            }            
        }
        //do not kill anything after this
        //the end sequence after kill is in run()
    }

    public synchronized void giveCommand(String command) {
        this.command = command;
        notify();        
    }
    
    public boolean ping() {
    	authClient.ping();
    	String input = authClient.receiveAuthData();  
    	if (input.equals("PING OK")) {
    		return true;
    	}
    	return false;
    }
    
    public boolean uPing() {
        byte[] packet = UnityPacket.buildUpingPacket();
        authClient.sendAuthData("UPING");
        authClient.receiveAuthData(); //SET
        for (int i = 0; i < 10; i++) {
            upMan.offer(packet);
            try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        String input = authClient.receiveAuthData();
        if (input.equals("UPING OK")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean dPing() {
        isClientDPinged.set(false);
        authClient.sendAuthData("DPING");
        //this is a pseudo synchronized wait for the response 
        for (int i = 0; i < 2*10*2; i++) {
            try {
                sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (isClientDPinged.get()) {
                break;
            }
        }
        return isClientDPinged.get();
    }

    public void drefresh() {
    	//receiveRefresh for the client -> sendRefresh for the server
        receive.kill();
        App.login.monitor.clearDown();
        authClient.sendAuthData("DREFRESH");
        String returnstr = authClient.receiveAuthData();
        String[] args = returnstr.split("\\s+");

        if (args.length !=2 && !args[0].equals("DREFRESH")) {
        	return;
        }
        
        int serverPort;
        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            return;
        }
        
        receive = new RedReceive(fullBlueNodeAddress, serverPort, downMan);
        receive.start();
        try {
            sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void urefresh() {
        send.kill();
        App.login.monitor.clearUp();
        authClient.sendAuthData("UREFRESH");
        String returnstr = authClient.receiveAuthData();
        String[] args = returnstr.split("\\s+");

        if (args.length !=2 && !args[0].equals("UREFRESH")) {
        	return;
        }
        
        int serverPort;
        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            return;
        }
        
        send = new RedSend(fullBlueNodeAddress, serverPort, upMan);
        send.start();
        try {
            sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMessage(String address, String message) {
        //converting into unity packet        
        InetAddress destvaddr = null;
        try {
            destvaddr = InetAddress.getByName(address);
            byte[] packet = UnityPacket.buildMessagePacket(myIP, destvaddr, message);
            upMan.offer(packet);
        } catch (UnknownHostException ex) {
            Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);           
        }       
    }
    
    public synchronized void killConnectionManager() {
        kill.set(true);
        notify();
    }

    private void killTasks() {
        send.kill();
        receive.kill();
        ka.kill();
    }
    
    private boolean startInterface() {
    	App.login.writeInfo("Setting Interface...");
        //starting the real interface                
        try {
            tuntap = TunTap.createTunTap();
        } catch (Exception ex) {
            return false;
        }

        if (tuntap.getDev() == null) {
            if (libError == false) {
                for (int i = 0; i < 3; i++) {
                    try {
                        tuntap = TunTap.createTunTap();
                    } catch (Exception ex) {
                        return false;
                    }
                    if (tuntap.getDev() != null) {
                        break;
                    }
                    try {
                        sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                App.login.writeInfo("COULD NOT OPEN INTERFACE, check if your interface is activated and if its not being used");
            }
            tuntap = null;
            return false;
        }


        if (tuntap != null) {
            read = new InterfaceRead(tuntap, readMan);
            read.start();

            write = new InterfaceWrite(tuntap, writeMan);
            write.start();

            router = new EthernetRouter(readMan, upMan, trafficMan);
            router.start();

            vrouter = new VirtualRouter(writeMan, downMan);
            vrouter.start();
            interfaceSet = true;
            return true;
        } else {
            return false;
        }
    }

    private void killInterface() {
        if (interfaceSet) {
            router.kill();
            vrouter.kill();
            read.kill();
            write.kill();
            try {
                sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                tuntap.close();
            } catch (Exception ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            tuntap = null;
        }
    }

    private boolean checkDHCP() {
        App.login.writeInfo("Checking DHCP...");
        if (osType == 2) {
            App.login.writeInfo("QUICK SET YOUR DHCP!!! Open a terminal and type: \n dhclient " + tuntap.getDev()+" to start the interface. You have 30 sec!");
        }
        for (int i = 0; i < 60; i++) {
            if (isDHCPset == true) {
                return true;
            } else {
                try {
                    sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }    
}