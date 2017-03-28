package kostiskag.unitynetwork.rednode.RedThreads;

import kostiskag.unitynetwork.rednode.GUI.MonitorWindow;
import kostiskag.unitynetwork.rednode.RedNode.lvl3RedNode;
import kostiskag.unitynetwork.rednode.Functions.MD5Functions;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Auth client is the basic class for connecting to the blue node
 */
public class AuthClient extends Thread {

    private Socket socket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;
    private InetAddress serverIPAddress;
    private int serverPort;
    private int downPort;
    private int upport;
    private String username;
    private String password;
    private String hostname;
    private String vaddress;
    private String pre = "^AUTH ";
    private String receivedMessage = "none";

    public AuthClient(String username, String password, String hostname, InetAddress serverIPAddress, int serverPort) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.serverIPAddress = serverIPAddress;
        this.serverPort = serverPort;
    }

    public boolean auth() {
        if (serverPort == -1) {
            lvl3RedNode.login.writeInfo("WRONG AUTH PORT GIVEN");            
            return false;
        }

        try {            
            socket = new Socket(serverIPAddress, serverPort);
            socket.setSoTimeout(10000);
            try {
                inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outputWriter = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                receivedMessage = inputReader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            lvl3RedNode.login.monitor.writeToCommands(receivedMessage);

            String messageToSend = "REDNODE " + hostname;
            String received = sendAuthData(messageToSend);
            String[] args = received.split("\\s+");            
           
            try {
                password = MD5Functions.MD5(password);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String data = username + "lol!_you_just_cant_copy_hashes_and_use_them_from_the_webpage" + password;
            try {
                data = MD5Functions.MD5(data);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            messageToSend = "LEASE "+username+" "+ data;
            received = sendAuthData(messageToSend);                                                                                   
            
            if (received.startsWith("HOSTNAME FAILED 0")) {   
                lvl3RedNode.login.writeInfo("Wrong command");
                return false;
            } else if (received.startsWith("BLUENODE FAILED")) {                
                lvl3RedNode.login.writeInfo("BlueNode Error, try connecting from a different BN");
                return false;
            } else if (received.startsWith("USER FAILED 1")) {                
                lvl3RedNode.login.writeInfo("Wrong username or password");
                return false;
            } else if (received.startsWith("HOSTNAME FAILED 1")) {   
                lvl3RedNode.login.writeInfo("Wrong Hostname");
                return false;
            } else if (received.startsWith("HOSTNAME FAILED 2")) {   
                lvl3RedNode.login.writeInfo("Hostname allready in use");               
                lvl3RedNode.login.writeInfo("Check if you are not connected from another place, and if not contact BNs admin to inform him");
                return false;
            } else if (received.startsWith("HOSTNAME FAILED 3")) {                   
                lvl3RedNode.login.writeInfo("This hostname does not belong to this user");                 
                return false;
            } else {
                args = received.split("\\s+");
                upport = Integer.parseInt(args[2]);
                downPort = Integer.parseInt(args[3]);
                vaddress = args[4];
                return true;
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            lvl3RedNode.login.monitor.writeToCommands("HOST ERROR");
            lvl3RedNode.login.writeInfo("HOST ERROR");
            return false;
        } catch(java.net.ConnectException ex) {
            lvl3RedNode.login.monitor.writeToCommands("HOST NOT FOUND");
            lvl3RedNode.login.writeInfo("HOST NOT FOUND");            
            return false;
        }
        catch (IOException ex) {
            lvl3RedNode.login.monitor.writeToCommands("COULD NOT CONNECT TO THE BLUE NODE");
            lvl3RedNode.login.writeInfo("COULD NOT CONNECT TO THE BLUE NODE");
            return false;
        }
    }

    public String sendAuthData(String messageToSend) {
        //Apostolh toy mhnhmatos(eggrafh toy mhnhmatos sthn roh mesw tou writer).        
        outputWriter.println(messageToSend);
        lvl3RedNode.login.monitor.writeToCommands(messageToSend);
        String receivedMessage = "";
        try {
            receivedMessage = inputReader.readLine();
        } catch (java.net.SocketTimeoutException ex) {
            lvl3RedNode.login.monitor.writeToCommands("CONNECTION TIMEOUT");
            lvl3RedNode.login.writeInfo("CONNECTION TIMEOUT");
            lvl3RedNode.login.connection.killAuthCommands();
            return "BLUENODE FAILED";
        } catch (java.net.SocketException ex) {
            lvl3RedNode.login.monitor.writeToCommands("CONNECTION CLOSED");
            lvl3RedNode.login.writeInfo("CONNECTION CLOSED");
            lvl3RedNode.login.connection.killAuthCommands();
            return "BLUENODE FAILED";
        } catch (IOException ex) {
            ex.printStackTrace();
            lvl3RedNode.login.monitor.writeToCommands("CONNECTION CLOSED");
            lvl3RedNode.login.writeInfo("CONNECTION CLOSED");
            lvl3RedNode.login.connection.killAuthCommands();
            return "BLUENODE FAILED";
        }
        if (receivedMessage == null){
            lvl3RedNode.login.monitor.writeToCommands("CONNECTION CLOSED");
            lvl3RedNode.login.writeInfo("CONNECTION CLOSED");
            try {
                socket.close();                
            } catch (IOException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            lvl3RedNode.login.connection.killAuthCommands();
            return "BLUENODE FAILED";
        }
        lvl3RedNode.login.monitor.writeToCommands(receivedMessage);
        if (receivedMessage.startsWith("USER FAILED") || receivedMessage.startsWith("PASS FAILED") || receivedMessage.startsWith("HOSTNAME FAILED")) {
            
        } else if (receivedMessage.startsWith("BLUE NODE ERROR")) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            lvl3RedNode.login.connection.killAuthCommands();
        }
        return receivedMessage;
    }

    public boolean ping() {
        if (sendAuthData("PING").equals("PING OK")) {
            return true;
        } else {
            return false;
        }
    }

    public void exit() {
        sendAuthData("EXIT");
    }

    public int getDownPort() {
        return downPort;
    }

    public int getUpport() {
        return upport;
    }

    public String getVaddress() {
        return vaddress;
    }
}
