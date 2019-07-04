package org.kostiskag.unitynetwork.rednode.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.LinkedList;

import javax.crypto.SecretKey;

import org.kostiskag.unitynetwork.common.utilities.CryptoUtilities;
import org.kostiskag.unitynetwork.common.utilities.SocketUtilities;
import org.kostiskag.unitynetwork.rednode.App;
import org.kostiskag.unitynetwork.rednode.tables.TrackerEntry;

/**
 * 
 * @author Konstantinos Kagiampakis
 */
public class TrackerClient {

	InetAddress trackerInetAddress;
	int trackerPort;
	
	private Socket socket;
	private DataInputStream socketReader;
	private DataOutputStream socketWriter;
	private String RecBlueNodeAddress;
	private int RecBlueNodePort;
	private PublicKey RecBlueNodePub;
	private boolean connected = false;
	private TrackerEntry tracker;
	private SecretKey sessionKey;
	public String reason;
	private String hostname;

	public TrackerClient(TrackerEntry tracker, String hostname) throws UnknownHostException {
		this.hostname = hostname;
		this.tracker = tracker;
		this.trackerInetAddress = SocketUtilities.getAddress(tracker.getAddress());
		this.trackerPort = tracker.getPort();
		try {
			connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void connect() throws Exception {
		socket = null;
		if (trackerInetAddress != null) {
			socket = SocketUtilities.absoluteConnect(trackerInetAddress, trackerPort);
		} else {
			App.login.writeInfo("Tracker connection failed - host not found.");
			return;
		}
		
		if (socket == null) {
			App.login.writeInfo("Tracker connection failed.");
			return;
		}

		socketReader = SocketUtilities.makeDataReader(socket);
		socketWriter = SocketUtilities.makeDataWriter(socket);
		
		sessionKey = CryptoUtilities.generateAESSessionkey();
		if (sessionKey == null) {
			reason = "NO_SESSION_KEY";
			throw new Exception();
		}
		
		String keyStr = CryptoUtilities.objectToBase64StringRepresentation(sessionKey);
		SocketUtilities.sendRSAEncryptedStringData(keyStr, socketWriter, tracker.getPubKey());
		
		String[] args = SocketUtilities.receiveAESEncryptedStringData(socketReader, sessionKey);
		System.out.println(args[0]);
		
		if(!args[0].equals("UnityTracker")) {
			reason = "WELLCOME_MSG_ERROR";
			throw new Exception();
		}
		
		args = SocketUtilities.sendReceiveAESEncryptedStringData("REDNODE "+hostname, socketReader, socketWriter, sessionKey);

		if (args[0].equals("PUBLIC_NOT_SET")) {
			SocketUtilities.sendAESEncryptedStringData("EXIT", socketWriter, sessionKey);
			reason = "KEY_NOT_SET";
			throw new Exception("This RN's public key is not set.");
		}
		
		//decode question
		byte[] question = CryptoUtilities.base64StringTobytes(args[0]);
		
		//decrypt with private
		String answer = CryptoUtilities.decryptWithPrivate(question, App.rednodeKeys.getPrivate());
		
		//send back plain answer
		args = SocketUtilities.sendReceiveAESEncryptedStringData(answer, socketReader, socketWriter, sessionKey);
		
		if (args[0].equals("OK")) {
			connected = true;
		} else {
			throw new Exception();
		}
		System.out.println("connected "+connected);
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
			String[] args;
			try {
				args = SocketUtilities.sendReceiveAESEncryptedStringData("GETRBN",  socketReader, socketWriter, sessionKey);
				if (!args[0].equals("NONE")) {
					App.login.writeInfo("Tracker Gave BN "+args[0]+" "+args[1]+" " +args[2]+" "+args[3]+" "+args[4]);
					//arg0 is name
					RecBlueNodeAddress = args[1];
					RecBlueNodePort = Integer.parseInt(args[2]);
					//args3 is load
					RecBlueNodePub =  (PublicKey) CryptoUtilities.base64StringRepresentationToObject(args[4]);
					return true;
				} else {
					App.login.writeInfo(
							"The Tracker is up but does not have any associated BlueNodes.\nIn other words, the network is down as there are no BlueNodes to carry the traffic.\nPlease select either to connect to another Network or define a standalone BlueNode from the Advanced Settings tab.");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			closeCon();
		}
		return false;
	}
	
	public int getRecomendedBlueNodePort() {
		return RecBlueNodePort;
	}

	public String getRecomendedBlueNodeAddress() {
		return RecBlueNodeAddress;
	}
	
	public PublicKey getRecomendedBlueNodePub() {
		return RecBlueNodePub;
	}
	
	public LinkedList<String[]> getBNs() {
		LinkedList<String[]> fetched = new LinkedList<String[]>();
		if (connected) {
			try {
				SocketUtilities.sendAESEncryptedStringData("GETBNS", socketWriter, sessionKey);
				String received = SocketUtilities.receiveAESEncryptedString(socketReader, sessionKey);
				closeCon();
				String[] lines = received.split("\n+"); //split into sentences
				String[] args = lines[0].split("\\s+"); //the first sentence contains the number
				int count = Integer.parseInt(args[1]);  //for the given number read the rest sentences
				for (int i = 1; i < count+1; i++) {        	
					args = lines[i].split("\\s+");
					fetched.add(new String[]{args[0], args[1], args[2], args[3]});		
		        }	    	    				
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
		return fetched;
	}
	
	public PublicKey getBlueNodesPubKey(String name) {            
    	if (connected) {
	        String[] args = null;
			try {
				args = SocketUtilities.sendReceiveAESEncryptedStringData("GETBNPUB"+" "+name, socketReader, socketWriter, sessionKey);
				closeCon();
				if (args[0].equals("NONE")) {
		            return null;
		        } else {
		            return (PublicKey) CryptoUtilities.base64StringRepresentationToObject(args[0]);
			    }
			} catch (Exception e) {
				e.printStackTrace();
			}        
	        closeCon();
	    }
    	return null;
    }
    
    public PublicKey getRedNodesPubKey(String hostname) {            
    	if (connected) {
	        String[] args = null;
			try {
				args = SocketUtilities.sendReceiveAESEncryptedStringData("GETRNPUB"+" "+hostname, socketReader, socketWriter, sessionKey);
				closeCon();
				if (args[0].equals("NONE")) {
		            return null;
		        } else {
		            return (PublicKey) CryptoUtilities.base64StringRepresentationToObject(args[0]);
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}        
	        closeCon();
	    }
    	return null;
    }

    public String revokePubKey() {
		if (connected) {
			String[] args = null;
			try {
				args = SocketUtilities.sendReceiveAESEncryptedStringData("REVOKEPUB", socketReader, socketWriter, sessionKey);
			} catch (Exception e) {
				e.printStackTrace();
			}        
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
	public static void getPubKey(TrackerEntry element) throws UnknownHostException, IOException {
		InetAddress addr = SocketUtilities.getAddress(element.getAddress());		
		Socket socket = SocketUtilities.absoluteConnect(addr, element.getPort());
		if (socket == null) {
			return;
		}
		
		try {
			DataInputStream reader = SocketUtilities.makeDataReader(socket);
			DataOutputStream writer = SocketUtilities.makeDataWriter(socket);
			
			String[] args = SocketUtilities.sendReceivePlainStringData("GETPUB", reader, writer);
			element.setPubKey((PublicKey) CryptoUtilities.base64StringRepresentationToObject(args[0]));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		        
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}			
	}
	
	/**
	 * Offer public key needs an rsa connection based on tracker's public
	 * but no authentication for the bluenode's part
	 *  
	 * @param ticket the generated ticket for this hostname
	 * @param hostname the rednode's hostname
	 * @return an information message
	 */
	public static String offerPubKey(String ticket, String hostname, TrackerEntry tr) {
		String pre = "^OFFERPUB ";
		PublicKey trackerPublic = tr.getPubKey();
		
		if (trackerPublic == null) {
			System.err.println(pre+"no tracker public key was set.");
			return null;
		}
		
		int port = tr.getPort();
		Socket socket = null;
		try {
			InetAddress addr = SocketUtilities.getAddress(tr.getAddress());
			socket = SocketUtilities.absoluteConnect(addr, port);
			
			DataInputStream reader = SocketUtilities.makeDataReader(socket);
			DataOutputStream writer = SocketUtilities.makeDataWriter(socket);
			
			SecretKey sessionKey = CryptoUtilities.generateAESSessionkey();
			if (sessionKey == null) {
				throw new Exception();
			}
			
			String keyStr = CryptoUtilities.objectToBase64StringRepresentation(sessionKey);
			SocketUtilities.sendRSAEncryptedStringData(keyStr, writer, trackerPublic);
			
			String[] args = SocketUtilities.receiveAESEncryptedStringData(reader, sessionKey);
			System.out.println(args[0]);
			
			if(!args[0].equals("UnityTracker")) {
				throw new Exception();
			}
			
			args = SocketUtilities.sendReceiveAESEncryptedStringData("REDNODE"+" "+hostname, reader, writer, sessionKey);
		
			if (!args[0].equals("PUBLIC_NOT_SET")) {
				SocketUtilities.sendAESEncryptedStringData("EXIT", writer, sessionKey);
				socket.close();
				return "KEY_IS_SET";
			}
			
			PublicKey pub = App.rednodeKeys.getPublic();
	    	args = SocketUtilities.sendReceiveAESEncryptedStringData("OFFERPUB"+" "+ticket+" "+CryptoUtilities.objectToBase64StringRepresentation(pub), reader, writer, sessionKey);
			
	    	socket.close();
	    	return args[0];
	    	
			} catch (Exception e) {
				e.printStackTrace();
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}   
			}        
	        return "NOT_CONNECTED";
	}
}
