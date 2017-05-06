package kostiskag.unitynetwork.rednode.redThreads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.functions.HashFunctions;

/**
 * Auth client is responsible to connect to the blue node's TCP auth port
 * 
 */
public class AuthClient extends Thread {

	private final String pre = "^AuthClient ";
	private final String username;
	private final String password;
	private final String hostname;
	private final InetAddress serverIPAddress;
	private final int serverPort;
	
	private Socket socket;
	private BufferedReader socketReader;
	private PrintWriter socketWriter;
	
	private int serverReceivePort; // I use this port to receive data
	private int serverSendPort; // I use this port to send data
	
	private String vaddress;
	private String socketResponce;
	private AtomicBoolean kill = new AtomicBoolean(false);

	public AuthClient(String username, String password, String hostname, InetAddress serverIPAddress, int serverPort) {
		this.username = username;
		this.password = password;
		this.hostname = hostname;
		this.serverIPAddress = serverIPAddress;
		this.serverPort = serverPort;
	}
	
	public String getVaddress() {
		return vaddress;
	}

	public int getServerSendPort() {
		return serverSendPort;
	}
	
	public int getServerReceivePort() {
		return serverReceivePort;
	}

	public boolean auth() {
		if (serverPort <= 0 || serverPort > 65535) {
			App.login.writeInfo(pre+"WRONG AUTH PORT GIVEN");
			return false;
		}

		try {
			String hashedData = null;
			hashedData = HashFunctions.SHA256(App.SALT) +HashFunctions.SHA256(username) + HashFunctions.SHA256(App.SALT + password);
			hashedData = HashFunctions.SHA256(hashedData);
			
			socket = new Socket(serverIPAddress, serverPort);
			socket.setSoTimeout(6000);
			
			socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			socketWriter = new PrintWriter(socket.getOutputStream(), true);
			String args[];
			
			String receivedMessage = socketReader.readLine();
			App.login.monitor.writeToCommands(pre+receivedMessage);

			String messageToSend = "REDNODE " + hostname;
			sendAuthData(messageToSend);
			receivedMessage = socketReader.readLine();
			App.login.monitor.writeToCommands(pre+receivedMessage);

			messageToSend = "LEASE "+username+" "+hashedData;
			sendAuthData(messageToSend);
			receivedMessage = socketReader.readLine();
			App.login.monitor.writeToCommands(pre+receivedMessage);
			
			if (receivedMessage.startsWith("FAILED")) {
				if (receivedMessage.startsWith("FAILED BLUENODE")) {
					App.login.writeInfo("BlueNode Error, try connecting from a different BN");
				} else if (receivedMessage.startsWith("FAILED USER")) {
					App.login.writeInfo("Wrong hostname, username or password");
				} else if (receivedMessage.startsWith("FAILED HOSTNAME")) {
					App.login.writeInfo("Hostname allready in use");
					App.login.writeInfo(
							"Check if you are not connected from another place, and if not contact BNs admin to inform him");
				} else {
					App.login.writeInfo("Failed to connect.");
				}
				return false;
			
			} else {
				args = receivedMessage.split("\\s+");
				if (args.length == 4 && args[0].equals("REG_OK")) {
					vaddress = args[1];
					serverReceivePort = Integer.parseInt(args[2]); //the port where the server receives
					serverSendPort = Integer.parseInt(args[3]); //the port where the server sends
					return true;
				} 
				return false;				
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			App.login.writeInfo("NO SUCH ALGORITHM ERROR");
			return false;
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			App.login.writeInfo("HOST ERROR");
			return false;
		} catch (java.net.ConnectException ex) {
			App.login.writeInfo("HOST NOT FOUND");
			return false;
		} catch (IOException ex) {
			App.login.writeInfo("COULD NOT CONNECT TO THE BLUE NODE");
			return false;
		} 
	}

	public void sendAuthData(String messageToSend) {
		socketWriter.println(messageToSend);
		App.login.monitor.writeToCommands(pre+"send "+messageToSend);
	}

	public String receiveAuthData() {
		try {
			sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return socketResponce;
	}

	public void whoami() {
		sendAuthData("WHOAMI");
	}

	public void ping() {
		sendAuthData("PING");
	}

	public void exit() {
		sendAuthData("EXIT");
	}
	
	@Override
	public void run() {
		// this waits for socket to drop and for the responses
		try {
			socket.setSoTimeout(0);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		String receivedMessage = null;
		while (!kill.get()) {
			try {
				receivedMessage = socketReader.readLine();
			} catch (Exception ex) {
				ex.printStackTrace();				
				sendKill("Auth connection closed.");				
				return;
			}
			
			if (receivedMessage == null) {				
				sendKill("Received empty message.");				
				return;
			} else if (receivedMessage.equals("BYE")) {
				sendKill("Receiced kill signal from Blue Node.");
				return;				
			} else {				
				socketResponce = receivedMessage;
				App.login.monitor.writeToCommands(pre+"received "+socketResponce);				
			}
		}
	}
	
	private void sendKill (String reason) {
		App.login.writeInfo(reason);
		kill.set(true);
		App.login.connection.killConnectionManager();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
