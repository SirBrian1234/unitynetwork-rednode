package kostiskag.unitynetwork.rednode.connection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Routing.*;
import kostiskag.unitynetwork.rednode.Routing.data.MacAddress;
import kostiskag.unitynetwork.rednode.Routing.data.ReverseARPTable;
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
    public  String Username = null;
    public  String Password = null;
    public  String Hostname = null;    
    public  String TrackerAddress = null;
    public  int TrackerPort = -1;
    public  String BlueNodeAddress = null;
    public InetAddress FullBlueNodeAddress = null;
    public  int BlueNodePort = -1;
                    
    private int DownPort;
    private int UpPort;
    public String Vaddress;
    public  InetAddress MyIP;
    
    private boolean kill = false;
    private boolean limited = true;
    private boolean nolink = true;
    
    public  TunTap tuntap;
    public  InterfaceRead read;
    public  InterfaceWrite write;
    public  EthernetRouter router;
    public  VirtualRouter vrouter;
    
    public  QueueManager upMan;
    public  QueueManager downMan;
    public  QueueManager readMan;
    public  QueueManager writeMan;
    
     //services & sockets    
    public  AuthClient authClient;
    public  RedReceive downlink;
    public  RedSend uplink;           
    public  boolean isUpTCP = false;
    public  String socketResponce;
    
    //ehternet & routing       
    public  int keepAliveTime = 5;                    
    public  int OSType=-1;
    public  ReverseARPTable arpTable;
    public  MacAddress MyMac;    
    public  boolean IsDHCPset;    
    public  boolean isClientDPinged;
    public  boolean libError=false;    
    
    public  KeepAlive ka;
    public  UploadManager trafficMan;    
    public  boolean DirectBNConnect;       
    private String command;
    
    public ConnectionManager(String Username, String Password, String Hostname, String BlueNodeAddress, int BlueNodePort) {        
        //initializing and reseting everything                                
        this.Hostname = null;
        this.Username = null;
        this.Password = null;
        this.Hostname = null;
        this.BlueNodeAddress = null;
        this.FullBlueNodeAddress = null;
        this.BlueNodePort = -1;
        this.DownPort = -1;
        this.UpPort = -1;                
        
        this.OSType = -1;
        this.authClient = null;
        
        this.Vaddress = null;
        this.MyIP = null;
        this.MyMac = null;
        this.Vaddress = null;
        this.isClientDPinged = false;
        this.IsDHCPset = false;
        this.libError = false;
        this.kill = false;
        
        //setting the right values
        this.Username = Username;        
        this.Password = Password;
        this.Hostname = Hostname;                        
        this.BlueNodeAddress = BlueNodeAddress;
        this.BlueNodePort = BlueNodePort;                
        
        arpTable = new ReverseARPTable(App.login.connection.MyIP);
        writeMan = new QueueManager(20);
        readMan = new QueueManager(20);
        upMan = new QueueManager(20);
        downMan = new QueueManager(20);
        trafficMan = new UploadManager();

        if (!(BlueNodePort > 0 && BlueNodePort <= 65535)) {
            App.login.writeInfo("WRONG PORT GIVEN");
            BlueNodePort = -1;            
        }

        try {
            FullBlueNodeAddress = InetAddress.getByName(BlueNodeAddress);
        } catch (UnknownHostException ex) {
            App.login.writeInfo("WRONG ADDRESS SYNTAX GIVEN");            
        }                        
    }

    /*
     * thats the life of a connection from birth to usage and death
     */
    @Override
    public void run() {        
        OSType = new DetectOS().getType();
        App.login.writeInfo("Running On " + DetectOS.getString());                                                
        App.login.writeInfo("Starting Connection...");
        App.login.writeInfo("Loging in Blue Node " + BlueNodeAddress + ":" + BlueNodePort + " ...");
        authClient = new AuthClient(Username, Password, Hostname, FullBlueNodeAddress, BlueNodePort);
        if (!authClient.auth()) {
            App.login.monitor.setLogedOut();
            App.login.setLogedOut();
            return;
        }
        App.login.writeInfo("Login OK");            
        Vaddress = authClient.getVaddress();
        DownPort = authClient.getDownPort();
        UpPort = authClient.getUpport();        
        try {
            MyIP = InetAddress.getByName(Vaddress);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        App.login.writeInfo("Opening LINK...");
        openThreads();
        
        App.login.writeInfo("Init socket event notification...");
        authClient.start();        
        
        App.login.writeInfo("Testing LINK...");
        if (!LinkDiagnostic()) {
            nolink = true;
            App.login.writeInfo("LINK Error");
            App.login.writeInfo("Working On Commands.");            
        }
        if (!startInterface()) {
            limited = true;
            App.login.writeInfo("INTERFACE ERROR");
            App.login.writeInfo("You may not send real data but you can send text and commands");
        } else if (!checkDHCP()) {
            limited = true;
            App.login.writeInfo("DHCP ERROR");
            App.login.writeInfo("Your interface failed to set the primal settings");
            App.login.writeInfo("You may not send real data but you can send text and commands");            
        } else {
            App.login.writeInfo("Interface OK");
        }
        App.login.writeInfo("Connection Set");
        App.login.writeInfo("Wellcome " + Hostname + " ~ " + Vaddress);
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
   
    public void openThreads() {
        //open manager
        upMan = new QueueManager(4);
        downMan = new QueueManager(500);
        //open downlink
        downlink = new RedReceive(FullBlueNodeAddress, DownPort);
        downlink.start();
        //open uplink
        uplink = new RedSend(FullBlueNodeAddress, UpPort);
        uplink.start();
        //open keep alive
        ka = new KeepAlive();
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
            } else if (!dPing()) {
                App.login.writeInfo("THERE IS A PROBLEM WITH DOWNLINK TRYING TO FIX IT");
                dping = false;
                drefresh();
            } else if (!uPing()) {
                App.login.writeInfo("THERE IS A PROBLEM WITH UPLINK TRYING TO FIX IT");
                uping = false;
                urefresh();
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

    public boolean startInterface() {

        App.login.writeInfo("Setting Interface...");
        //starting the real interface                
        try {
            tuntap = TunTap.createTunTap();
        } catch (Exception ex) {
            return false;
        }

        //stupid interface
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
            readMan = new QueueManager(20);
            writeMan = new QueueManager(1000);

            read = new InterfaceRead(App.login.connection.tuntap);
            read.start();

            write = new InterfaceWrite(App.login.connection.tuntap);
            write.start();

            router = new EthernetRouter();
            router.start();

            vrouter = new VirtualRouter();
            vrouter.start();
            return true;
        } else {
            return false;
        }
    }

    public void LoggedIn() {
        byte[] payload = ("[HELLO PACKET]").getBytes();
        byte[] data = UnityPacket.buildPacket(payload, MyIP, MyIP, 0);
        upMan.offer(data);
        App.login.monitor.setLogedIn();
        App.login.setLoggedIn();
    }

    public synchronized void giveCommand(String command) {
        notify();
        this.command = command;
    }
    
    public synchronized void authCommands() {        
        while (!kill) {                                    
            try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            if (kill) {
            	break;
            } else if (!command.isEmpty()) {
                if (command.equals("EXIT")) {
                    App.login.writeInfo("Logging Out...");
                    authClient.exit();
                    break;
                } else if (command.equals("PING")) {
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
                } else {
                    authClient.sendAuthData(command);
                }
                command = "";                
            }            
        }
    }

    public synchronized void killConnectionManager() {
        kill = true;
        notify();
    }

    private void killTasks() {
        uplink.kill();
        downlink.kill();
        ka.kill();
    }

    private void killInterface() {
        if (tuntap != null) {
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

    private void UpIsDown() {                
        byte[] payload = (Vaddress+" [U-TURN]").getBytes();
        byte[] data = UnityPacket.buildPacket(payload, MyIP, MyIP, 2);
        upMan.offer(data);
    }
    
    public boolean ping() {
    	authClient.ping();
    	String input = authClient.receiveAuthData();  
    	System.out.println(input);
    	if (input.equals("PING OK")) {
    		return true;
    	}
    	return false;
    }

    public void drefresh() {
        downlink.kill();
        App.login.monitor.clearDown();
        authClient.sendAuthData("DREFRESH");
        String returnstr = authClient.receiveAuthData();
        String[] args = returnstr.split("\\s+");

        if (args.length < 3) {
            return;
        }
        int downPort = Integer.parseInt(args[2]);
        if (downPort == -1) {
            return;
        }
        downlink = new RedReceive(FullBlueNodeAddress, downPort);
        downlink.start();
        try {
            sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        dPing();
    }

    public void urefresh() {
        uplink.kill();
        App.login.monitor.clearUp();
        authClient.sendAuthData("UREFRESH");
        String returnstr = authClient.receiveAuthData();
        String[] args = returnstr.split("\\s+");

        if (args.length < 3) {
            return;
        }
        int upPort = -1;
        try {
            upPort = Integer.parseInt(args[2]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            upPort = -1;
        }
        if (upPort == -1) {
            return;
        }
        uplink = new RedSend(FullBlueNodeAddress, upPort);
        uplink.start();
        try {
            sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        uPing();
    }

    public boolean uPing() {
        byte[] payload;
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName("0.0.0.0");
        } catch (UnknownHostException ex) {
            Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        payload = ("00001 [UPING PACKET]").getBytes();
        byte[] data = UnityPacket.buildPacket(payload, MyIP, addr, 0);
        for (int i = 0; i < 2; i++) {
            upMan.offer(data);
        }
        authClient.sendAuthData("UPING");
        String input = authClient.receiveAuthData();
        if (input.equals("UPING OK")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean dPing() {
        isClientDPinged = false;
        authClient.sendAuthData("DPING");
        for (int i = 0; i < 8; i++) {
            try {
                sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (isClientDPinged) {
                break;
            }
        }
        return isClientDPinged;
    }

    public void sendStringData(String address, String message) {
        //converting into unity packet <3        
        byte[] payload;
        InetAddress destvaddr = null;
        try {
            destvaddr = InetAddress.getByName(address);
        } catch (UnknownHostException ex) {
            Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        payload = new byte[message.getBytes().length];
        payload = (address + " " + message + "                               ").getBytes();
        byte[] data = UnityPacket.buildPacket(payload, MyIP, destvaddr, 2);
        upMan.offer(data);
    }

    private boolean checkDHCP() {
        App.login.writeInfo("Checking DHCP...");
        if (OSType == 2) {
            App.login.writeInfo("QUICK SET YOUR DHCP!!! Open a terminal and type: \n dhclient " + tuntap.getDev()+" to start the interface. You have 30 sec!");
        }
        for (int i = 0; i < 60; i++) {
            if (IsDHCPset == true) {
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