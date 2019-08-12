package org.kostiskag.unitynetwork.rednode.redthreads;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.crypto.SecretKey;

import org.kostiskag.unitynetwork.common.utilities.CryptoUtilities;
import org.kostiskag.unitynetwork.common.utilities.HashUtilities;
import org.kostiskag.unitynetwork.common.utilities.SocketUtilities;
import org.kostiskag.unitynetwork.rednode.App;

/**
 * Auth client is responsible to connect to the blue node's TCP auth port
 * 
 * @author Konstantinos Kagiampakis
 */
public class AuthClient extends Thread {

	private final String pre = "^AuthClient ";
	private final String username;
	private final String password;
	private final String hostname;
	private final InetAddress serverIPAddress;
	private final int serverPort;
	private final PublicKey blueNodePubKey;
	private SecretKey sessionKey;
    
	private Socket socket;
	private DataInputStream socketReader;
	private DataOutputStream socketWriter;
	
	private int serverReceivePort; // I use this port to receive data
	private int serverSendPort; // I use this port to send data
	
	private String vaddress;
	private String[] socketResponce;
	private AtomicBoolean kill = new AtomicBoolean(false);

	public AuthClient(String username, String password, String hostname, InetAddress serverIPAddress, int serverPort, PublicKey blueNodePubKey) {
		this.username = username;
		this.password = password;
		this.hostname = hostname;
		this.serverIPAddress = serverIPAddress;
		this.serverPort = serverPort;
		this.blueNodePubKey = blueNodePubKey;
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
			hashedData = HashUtilities.SHA256(App.SALT) +HashUtilities.SHA256(username) + HashUtilities.SHA256(App.SALT + password);
			hashedData = HashUtilities.SHA256(hashedData);
			
			socket = SocketUtilities.absoluteConnect(serverIPAddress, serverPort);
			//socket.setSoTimeout(timeout);
			
			socketReader = SocketUtilities.makeDataReader(socket);
			socketWriter = SocketUtilities.makeDataWriter(socket);
			
			sessionKey = CryptoUtilities.generateAESSessionkey();
			if (sessionKey == null) {
				throw new Exception("Could not generate session key.");
			}

			String keyStr = CryptoUtilities.objectToBase64StringRepresentation(sessionKey);
			SocketUtilities.sendRSAEncryptedStringData(keyStr, socketWriter, blueNodePubKey);
			
			String[] args = SocketUtilities.receiveAESEncryptedStringData(socketReader, sessionKey);
			System.out.println(args[0]);
			
			if(!args[0].equals("BLUENODE")) {
				throw new Exception("Bluenode wrong name.");
			}
			System.out.println(args[0]+" "+args[1]);
			
			//this bn is to be authenticated by the target bn
			args = SocketUtilities.sendReceiveAESEncryptedStringData("REDNODE "+hostname, socketReader, socketWriter, sessionKey);
			
			if (!args[0].equals("OK")) {
				//decode question
				byte[] question = CryptoUtilities.base64StringTobytes(args[0]);
				
				//decrypt with private
				String answer = CryptoUtilities.decryptWithPrivate(question, App.rednodeKeys.getPrivate());
				
				//send back plain answer
				args = SocketUtilities.sendReceiveAESEncryptedStringData(answer, socketReader, socketWriter, sessionKey);
				
				if (!args[0].equals("OK")) {
					throw new Exception("Could not connect to bluenode. RedNode authentication failed. \nPlease check that your given hostname matches your keypair.");
				} 
				System.out.println(args[0]);
			}
			
			args = SocketUtilities.sendReceiveAESEncryptedStringData("LEASE "+username+" "+hashedData, socketReader, socketWriter, sessionKey);
			if (args[0].equals("FAILED")) {
				if (args[1].equals("BLUENODE")) {
					App.login.writeInfo("BlueNode Error, try connecting from a different BN");
				} else if (args[1].equals("USER")) {
					App.login.writeInfo("Wrong hostname, username or password");
				} else if (args[1].equals("HOSTNAME")) {
					App.login.writeInfo("Hostname allready in use");
					App.login.writeInfo(
							"Check if you are not connected from another place, and if not contact BNs admin to inform him");
				} else {
					App.login.writeInfo("Failed to connect.");
				}
				return false;
			
			} else if (args.length == 4 && args[0].equals("REG_OK")) {
					System.out.println("reg ok!");
					vaddress = args[1];
					serverReceivePort = Integer.parseInt(args[2]); //the port where the server receives
					serverSendPort = Integer.parseInt(args[3]); //the port where the server sends
					return true;
			
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
		return false;
	}

	public void sendAuthData(String messageToSend) {
		try {
			SocketUtilities.sendAESEncryptedStringData(messageToSend, socketWriter, sessionKey);
			App.login.monitor.writeToCommands(pre+"send "+messageToSend);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String[] receiveAuthData() {
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
		String[] args;
		while (!kill.get()) {
			try {
				args = SocketUtilities.receiveAESEncryptedStringData(socketReader, sessionKey);
			} catch (Exception ex) {
				ex.printStackTrace();				
				sendKill("Auth connection closed.");				
				return;
			}
			
			if (args == null) {				
				sendKill("Received empty message.");				
				return;
			} else if (args[0].equals("BYE")) {
				sendKill("Receiced kill signal from Blue Node.");
				return;				
			} else {				
				socketResponce = args;
				String terminal = "";
				for (int i=0; i<socketResponce.length; i++) {
					terminal = terminal+args[i]+" ";
				}
				App.login.monitor.writeToCommands(pre+"received "+terminal);				
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
