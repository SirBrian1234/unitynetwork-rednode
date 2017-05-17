package kostiskag.unitynetwork.rednode.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PublicKey;
import java.util.LinkedList;

import javax.crypto.SecretKey;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.functions.CryptoMethods;
import kostiskag.unitynetwork.rednode.functions.SocketFunctions;
import kostiskag.unitynetwork.rednode.tables.trackerInstance;

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
	private trackerInstance tracker;
	private SecretKey sessionKey;
	public String reason;
	private String hostname;

	public TrackerClient(trackerInstance tracker, String hostname) {
		this.hostname = hostname;
		this.tracker = tracker;
		this.trackerInetAddress = SocketFunctions.getAddress(tracker.getAddress());
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
			socket = SocketFunctions.absoluteConnect(trackerInetAddress, trackerPort);
		} else {
			App.login.writeInfo("Tracker connection failed - host not found.");
			return;
		}
		
		if (socket == null) {
			App.login.writeInfo("Tracker connection failed.");
			return;
		}

		socketReader = SocketFunctions.makeDataReader(socket);
		socketWriter = SocketFunctions.makeDataWriter(socket);
		
		sessionKey = CryptoMethods.generateAESSessionkey();
		if (sessionKey == null) {
			reason = "NO_SESSION_KEY";
			throw new Exception();
		}
		
		String keyStr = CryptoMethods.objectToBase64StringRepresentation(sessionKey);
		SocketFunctions.sendRSAEncryptedStringData(keyStr, socketWriter, tracker.getPubKey());
		
		String[] args = SocketFunctions.receiveAESEncryptedStringData(socketReader, sessionKey);
		System.out.println(args[0]);
		
		if(!args[0].equals("UnityTracker")) {
			reason = "WELLCOME_MSG_ERROR";
			throw new Exception();
		}
		
		args = SocketFunctions.sendReceiveAESEncryptedStringData("REDNODE "+hostname, socketReader, socketWriter, sessionKey);

		if (args[0].equals("PUBLIC_NOT_SET")) {
			SocketFunctions.sendAESEncryptedStringData("EXIT", socketWriter, sessionKey);
			reason = "KEY_NOT_SET";
			throw new Exception("This RN's public key is not set.");
		}
		
		//decode question
		byte[] question = CryptoMethods.base64StringTobytes(args[0]);
		
		//decrypt with private
		String answer = CryptoMethods.decryptWithPrivate(question, App.rednodeKeys.getPrivate());
		
		//send back plain answer
		args = SocketFunctions.sendReceiveAESEncryptedStringData(answer, socketReader, socketWriter, sessionKey);
		
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
				args = SocketFunctions.sendReceiveAESEncryptedStringData("GETRBN",  socketReader, socketWriter, sessionKey);
				if (!args[0].equals("NONE")) {
					App.login.writeInfo("Tracker Gave BN "+args[0]+" "+args[1]+" " +args[2]+" "+args[3]+" "+args[4]);
					//arg0 is name
					RecBlueNodeAddress = args[1];
					RecBlueNodePort = Integer.parseInt(args[2]);
					//args3 is load
					RecBlueNodePub =  (PublicKey) CryptoMethods.base64StringRepresentationToObject(args[4]);
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
			String[] args;
			try {
				args = SocketFunctions.sendReceiveAESEncryptedStringData("GETBNS",  socketReader, socketWriter, sessionKey);
				if (!args[0].equals("NONE")) {
					int count = Integer.parseInt(args[1]);
		            for (int i = 0; i < count; i++) {
		                args = SocketFunctions.receiveAESEncryptedStringData(socketReader, sessionKey);
		                fetched.add(new String[]{args[0], args[1], args[2], args[3]});
		            }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
        	closeCon();
		}
		return fetched;
	}
	
	public PublicKey getBlueNodesPubKey(String name) {            
    	if (connected) {
	        String[] args = null;
			try {
				args = SocketFunctions.sendReceiveAESEncryptedStringData("GETBNPUB"+" "+name, socketReader, socketWriter, sessionKey);
				closeCon();
				if (args[0].equals("NONE")) {
		            return null;
		        } else {
		            return (PublicKey) CryptoMethods.base64StringRepresentationToObject(args[0]);
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
				args = SocketFunctions.sendReceiveAESEncryptedStringData("GETRNPUB"+" "+hostname, socketReader, socketWriter, sessionKey);
				closeCon();
				if (args[0].equals("NONE")) {
		            return null;
		        } else {
		            return (PublicKey) CryptoMethods.base64StringRepresentationToObject(args[0]);
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
				args = SocketFunctions.sendReceiveAESEncryptedStringData("REVOKEPUB", socketReader, socketWriter, sessionKey);
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
	public static void getPubKey(trackerInstance element) {
		InetAddress addr = SocketFunctions.getAddress(element.getAddress());		
		Socket socket = SocketFunctions.absoluteConnect(addr, element.getPort());
		if (socket == null) {
			return;
		}
		
		try {
			DataInputStream reader = SocketFunctions.makeDataReader(socket);
			DataOutputStream writer = SocketFunctions.makeDataWriter(socket);
			
			String[] args = SocketFunctions.sendReceivePlainStringData("GETPUB", reader, writer);
			element.setPubKey((PublicKey) CryptoMethods.base64StringRepresentationToObject(args[0]));
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
	public static String offerPubKey(String ticket, String hostname, trackerInstance tr) {
		String pre = "^OFFERPUB ";
		PublicKey trackerPublic = tr.getPubKey();
		
		if (trackerPublic == null) {
			System.err.println(pre+"no tracker public key was set.");
			return null;
		}
		
		int port = tr.getPort();
		InetAddress addr = SocketFunctions.getAddress(tr.getAddress());
		
		Socket socket = null;
		try {
			socket = SocketFunctions.absoluteConnect(addr, port);
			
			DataInputStream reader = SocketFunctions.makeDataReader(socket);
			DataOutputStream writer = SocketFunctions.makeDataWriter(socket);
			
			SecretKey sessionKey = CryptoMethods.generateAESSessionkey();
			if (sessionKey == null) {
				throw new Exception();
			}
			
			String keyStr = CryptoMethods.objectToBase64StringRepresentation(sessionKey);
			SocketFunctions.sendRSAEncryptedStringData(keyStr, writer, trackerPublic);
			
			String[] args = SocketFunctions.receiveAESEncryptedStringData(reader, sessionKey);
			System.out.println(args[0]);
			
			if(!args[0].equals("UnityTracker")) {
				throw new Exception();
			}
			
			args = SocketFunctions.sendReceiveAESEncryptedStringData("REDNODE"+" "+hostname, reader, writer, sessionKey);
		
			if (!args[0].equals("PUBLIC_NOT_SET")) {
				SocketFunctions.sendAESEncryptedStringData("EXIT", writer, sessionKey);
				socket.close();
				return "KEY_IS_SET";
			}
			
			PublicKey pub = App.rednodeKeys.getPublic();
	    	args = SocketFunctions.sendReceiveAESEncryptedStringData("OFFERPUB"+" "+ticket+" "+CryptoMethods.objectToBase64StringRepresentation(pub), reader, writer, sessionKey);
			
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
