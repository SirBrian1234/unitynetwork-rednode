package kostiskag.unitynetwork.rednode.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PublicKey;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.functions.CryptoMethods;
import kostiskag.unitynetwork.rednode.functions.SocketFunctions;

/**
 * 
 * @author Konstantinos Kagiampakis
 */
public class TrackerClient {

	private Socket socket;
	private BufferedReader socketReader;
	private PrintWriter socketWriter;
	private String RecBlueNodeAddress;
	private int RecBlueNodePort;
	private boolean connected = false;

	public TrackerClient(String trackerAddress, int trackerPort) {
		
		InetAddress trackerInetAddress = SocketFunctions.getAddress(trackerAddress);
		
		socket = null;
		if (trackerInetAddress != null) {
			socket = SocketFunctions.absoluteConnect(trackerInetAddress, trackerPort);
		} else {
			App.login.writeInfo("Tracker connection failed - host not found.");
			return;
		}
		
		if (socket == null) {
			App.login.writeInfo("Tracker connection failed.");
			return;
		}

		socketReader = SocketFunctions.makeReadWriter(socket);
		socketWriter = SocketFunctions.makeWriteWriter(socket);
		String args[] = SocketFunctions.readData(socketReader);
		args = SocketFunctions.sendData("REDNODE " + App.login.hostname, socketWriter, socketReader);

		if (args[0].equals("OK")) {
			connected = true;
		}
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	private void closeCon() {
		if (!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean getRecomendedBlueNode() {
		if (connected) {
			String[] args = SocketFunctions.sendData("GETRBN", socketWriter, socketReader);
			closeCon();
			if (!args[0].equals("NONE")) {
				App.login.writeInfo("Tracker Gave BN " + args[0] + " " + args[1] + " " + args[2] + " " + args[3]);
				RecBlueNodeAddress = args[1];
				RecBlueNodePort = Integer.parseInt(args[2]);
				return true;
			} else {
				App.login.writeInfo(
						"The Tracker is up but does not have any associated BlueNodes.\nIn other words, the network is down as there are no BlueNodes to carry the traffic.\nPlease select either to connect to another Network or define a standalone BlueNode from the Advanced Settings tab.");
				return false;
			}
		}
		return false;
	}
	
    public String offerPubKey(String ticket) {
    	if (connected) {
	    	PublicKey pub = App.rednodeKeys.getPublic(); 
	    	String[] args = SocketFunctions.sendData("OFFERPUB"+" "+ticket+" "+CryptoMethods.objectToBase64StringRepresentation(pub), socketWriter, socketReader);        
	        closeCon();	        
	        return args[0];
    	}
    	return null;
    }

    public String revokePubKey() {
		if (connected) {
			String[] args = SocketFunctions.sendData("REVOKEPUB", socketWriter, socketReader);        
	        closeCon();
	        return args[0];
		}
		return null;
	}
    
    /**
     * Collect's a tracker's public key
     * It's a bit hardwired as after collection
     * it writes a file and updates bn's tracker public key
     * to use.
     */
	public static void getPubKey() {
		/*
		InetAddress addr = SocketFunctions.getAddress(App.bn.trackerAddress);
		int port = App.bn.trackerPort;
		Socket socket = SocketFunctions.absoluteConnect(addr, port);
		if (socket == null) {
			return;
		}
		BufferedReader reader = SocketFunctions.makeReadWriter(socket);
		PrintWriter writer = SocketFunctions.makeWriteWriter(socket);
		String args[] = SocketFunctions.readData(reader);
		
		args = SocketFunctions.sendData("GETPUB", writer, reader);        
        App.bn.trackerPublicKey = (PublicKey) CryptoMethods.base64StringRepresentationToObject(args[0]);
        CryptoMethods.objectToFile(App.bn.trackerPublicKey, new File(App.trackerPublicKeyFileName));
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/	
	}

	public int getRecomendedBlueNodePort() {
		return RecBlueNodePort;
	}

	public String getRecomendedBlueNodeAddress() {
		return RecBlueNodeAddress;
	}
}
