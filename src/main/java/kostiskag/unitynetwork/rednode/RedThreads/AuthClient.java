package kostiskag.unitynetwork.rednode.RedThreads;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.functions.HashFunctions;

/**
 * Auth client is responsible to connect to the blue node's TCP auth port
 * 
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
	private String receivedMessage = "none";
	String socketResponce;
	private boolean kill = false;

	public AuthClient(String username, String password, String hostname, InetAddress serverIPAddress, int serverPort) {
		this.username = username;
		this.password = password;
		this.hostname = hostname;
		this.serverIPAddress = serverIPAddress;
		this.serverPort = serverPort;
	}

	public boolean auth() {
		if (serverPort == -1) {
			App.login.writeInfo("WRONG AUTH PORT GIVEN");
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
			App.login.monitor.writeToCommands(receivedMessage);

			String messageToSend = "REDNODE " + hostname;
			sendAuthData(messageToSend);
			String received = inputReader.readLine();
			String[] args = received.split("\\s+");

			String data = null;
			try {
				data = HashFunctions.SHA256(App.SALT) +HashFunctions.SHA256(username) + HashFunctions.SHA256(App.SALT + password);
				data = HashFunctions.SHA256(data);
			} catch (NoSuchAlgorithmException ex) {
				Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
			} catch (UnsupportedEncodingException ex) {
				Logger.getLogger(AuthClient.class.getName()).log(Level.SEVERE, null, ex);
			}

			messageToSend = "LEASE "+username+" "+data;
			sendAuthData(messageToSend);
			received = inputReader.readLine();

			if (received.startsWith("FAILED")) {
				
				if (received.startsWith("FAILED BLUENODE")) {
					App.login.writeInfo("BlueNode Error, try connecting from a different BN");
				} else if (received.startsWith("FAILED USER")) {
					App.login.writeInfo("Wrong hostname, username or password");
				} else if (received.startsWith("FAILED HOSTNAME")) {
					App.login.writeInfo("Hostname allready in use");
					App.login.writeInfo(
							"Check if you are not connected from another place, and if not contact BNs admin to inform him");
				} else {
					App.login.writeInfo("Failed to connect.");
				}
				
				return false;
			
			} else {
				args = received.split("\\s+");
				if (args[0].equals("REG_OK")) {
					upport = Integer.parseInt(args[1]);
					downPort = Integer.parseInt(args[2]);
					vaddress = args[3];
					return true;
				} 
				return false;				
			}
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
			App.login.monitor.writeToCommands("HOST ERROR");
			App.login.writeInfo("HOST ERROR");
			return false;
		} catch (java.net.ConnectException ex) {
			App.login.monitor.writeToCommands("HOST NOT FOUND");
			App.login.writeInfo("HOST NOT FOUND");
			return false;
		} catch (IOException ex) {
			App.login.monitor.writeToCommands("COULD NOT CONNECT TO THE BLUE NODE");
			App.login.writeInfo("COULD NOT CONNECT TO THE BLUE NODE");
			return false;
		}
	}

	public void sendAuthData(String messageToSend) {
		outputWriter.println(messageToSend);
		App.login.monitor.writeToCommands(messageToSend);
	}

	public String receiveAuthData() {
		try {
			sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		App.login.monitor.writeToCommands(socketResponce);
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

	public int getDownPort() {
		return downPort;
	}

	public int getUpport() {
		return upport;
	}

	public String getVaddress() {
		return vaddress;
	}

	// wait for socket to drop and for the responses
	@Override
	public void run() {
		try {
			socket.setSoTimeout(0);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		String receivedMessage = null;
		while (!kill) {
			try {
				receivedMessage = inputReader.readLine();
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
				App.login.writeInfo(socketResponce);
			}
		}
	}
	
	private void sendKill (String reason) {
		App.login.writeInfo(reason);
		kill = true;
		App.login.connection.killConnectionManager();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
