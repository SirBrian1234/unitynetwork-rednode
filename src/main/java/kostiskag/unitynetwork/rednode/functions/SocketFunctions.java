package kostiskag.unitynetwork.rednode.functions;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.functions.CryptoMethods;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class SocketFunctions {
	// socket stuff
	public static String pre = "^SOCKET ";

	public static InetAddress getAddress(String PhAddress) {
		InetAddress IPaddress = null;
		try {
			IPaddress = InetAddress.getByName(PhAddress);
		} catch (Exception ex) {
			return null;
		}
		return IPaddress;
	}

	public static Socket absoluteConnect(InetAddress IPaddress, int authPort) {
		Socket socket = null;
		if (authPort > 0 && authPort <= 65535) {
			try {
				socket = new Socket(IPaddress, authPort);
				socket.setSoTimeout(3000);
			} catch (java.net.NoRouteToHostException ex) {
				App.login.writeInfo(pre + "NO ROUTE");
				return null;
			} catch (java.net.UnknownHostException ex) {
				App.login.writeInfo(pre + "UNKNOWN HOST");
				return null;
		    } catch (java.net.ConnectException ex) {
				App.login.writeInfo(pre + "CONNECTION REFUSED");
				return null;
			} catch (java.net.SocketTimeoutException ex) {
				App.login.writeInfo(pre + "CONNECTION TIMED OUT");
				return null;
			} catch (Exception ex) {
				App.login.writeInfo(pre + "CONNECTION ERROR");
				ex.printStackTrace();
				return null;
			}
			return socket;
		} else {
			App.login.writeInfo(pre + "A Wrong port number was defined");
			return null;
		}
	}

	public static DataInputStream makeDataReader(Socket socket) throws IOException {
    	//BufferedInputStream bin = new BufferedInputStream();
		DataInputStream dataStream = new DataInputStream(socket.getInputStream());
		return dataStream;
    }

    public static DataOutputStream makeDataWriter(Socket socket) throws IOException {
        DataOutputStream dataStream = new DataOutputStream(socket.getOutputStream());
        return dataStream;
    }
    
    public static void sendData(byte[] toSend, DataOutputStream writer) throws IOException {
    	writer.write(toSend);
    }
    
    public static byte[] receiveData(DataInputStream reader) throws IOException {
    	byte[] byteT = new byte[]{0x00};
    	byte[] bytes = new byte[2048];
    	int read = reader.read(bytes);
		if (read > 0) {
	    	byteT = new byte[read];
			System.arraycopy(bytes, 0, byteT, 0, read);
			
			if (byteT[0] == (int)13) {
				System.out.println(pre + "RECEIVED new line");
			} else if (byteT[0] == (int)10) {
				System.out.println(pre+ "received return char");
			}
		} else if (read == 0){
			System.out.println(pre + "RECEIVED zero");
		} else {
			System.out.println(pre + "RECEIVED "+read);
		}
    	return byteT;			
    }
    
    public static byte[] sendReceiveData(byte[] toSend, DataInputStream reader, DataOutputStream writer) throws Exception  {
    	sendData(toSend, writer);
    	byte[] received = receiveData(reader);
    	return received;
    }

    public static void sendPlainStringData(String message, DataOutputStream writer) throws Exception {
    	if (message == null) {
        	throw new Exception(pre+"NO DATA TO SEND");
        } else if (message.isEmpty()) {
        	//line feed
        	message = "\n\r";
        }        
    	//include a line feed and a return char
    	//message += "\n\r";
    	byte[] toSend = message.getBytes();        
        sendData(toSend, writer);
    }
    
    public static String[] receivePlainStringData(DataInputStream reader) throws IOException {
    	byte[] received = receiveData(reader);
    	String receivedMessage = new String(received, "utf-8");
        String[] args = receivedMessage.split("\\s+");
        return args;
    }
    
    public static String[] sendReceivePlainStringData(String data, DataInputStream reader, DataOutputStream writer) throws Exception  {
    	sendPlainStringData(data, writer);
    	String[] args = receivePlainStringData(reader);
    	return args;
    }
    
    public static void sendAESEncryptedStringData(String message, DataOutputStream writer, SecretKey sessionKey) throws Exception {
    	if (message == null) {
        	throw new Exception(pre+"NO DATA TO SEND");
        } else if (message.isEmpty()) {
        	//line feed
        	message = "\n";
        }        
    	//include a line feed and a return char
    	//message += "\n\r";
    	byte[] chiphered = CryptoMethods.aesEncrypt(message, sessionKey);
        sendData(chiphered, writer);
    }
    
    public static String[] receiveAESEncryptedStringData(DataInputStream reader, SecretKey sessionKey) throws IOException {
    	byte[] received = receiveData(reader);
    	String decrypted = CryptoMethods.aesDecrypt(received, sessionKey);
    	String[] args = decrypted.split("\\s+");
        return args;
    }
    
    public static String[] sendReceiveAESEncryptedStringData(String message, DataInputStream reader, DataOutputStream writer, SecretKey sessionKey) throws Exception  {
    	sendAESEncryptedStringData(message, writer, sessionKey);
    	return receiveAESEncryptedStringData(reader, sessionKey);
    }
    
    public static void sendRSAEncryptedStringData(String message, DataOutputStream writer, PublicKey key) throws Exception {
    	if (message == null) {
        	throw new Exception(pre+"NO DATA TO SEND");
        } else if (message.isEmpty()) {
        	//line feed
        	message = "\n";
        }        
    	//include a line feed and a return char
    	//message += "\n\r";
    	byte[] chiphered = CryptoMethods.encryptWithPublic(message, key);
        sendData(chiphered, writer);
    }
    
    public static String[] receiveRSAEncryptedStringData(DataInputStream reader, PrivateKey priv) throws IOException {
    	byte[] received = receiveData(reader);
    	String decrypted = CryptoMethods.decryptWithPrivate(received, priv);
    	return decrypted.split("\\s+");
    }

	public static void connectionClose(Socket socket) {
		if (socket == null) {
			System.out.println(pre + "CONNECTION CLOSE FAILED, NO CONNECTION");
			return;
		}
		try {
			socket.close();
		} catch (IOException ex) {
			Logger.getLogger(SocketFunctions.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
	
	/*
	 * Deprecated methods below
	 */

	public static BufferedReader makeReadWriter(Socket socket) {
		BufferedReader inputReader = null;
		try {
			inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return inputReader;
	}

	public static PrintWriter makeWriteWriter(Socket socket) {
		PrintWriter outputWriter = null;
		try {
			outputWriter = new PrintWriter(socket.getOutputStream(), true);
		} catch (IOException ex) {
			Logger.getLogger(SocketFunctions.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
		return outputWriter;
	}

	public static String[] sendData(String data, PrintWriter outputWriter, BufferedReader inputReader) {
		if (outputWriter == null || inputReader == null) {
			System.out.println(pre + "SEND DATA FAILED, NO CONNECTION");
			return null;
		} else if (data == null) {
			System.out.println(pre + "NO DATA TO SEND");
			return null;
		}

		outputWriter.println(data);
		String receivedMessage = null;
		String[] args = null;
		try {
			receivedMessage = inputReader.readLine();
		} catch (IOException ex) {
			Logger.getLogger(SocketFunctions.class.getName()).log(Level.SEVERE, null, ex);
		}

		System.out.println(pre + receivedMessage);
		args = receivedMessage.split("\\s+");
		return args;
	}

	public static void sendFinalData(String data, PrintWriter outputWriter) {
		if (outputWriter == null) {
			System.out.println(pre + "SEND FINAL DATA FAILED, NO CONNECTION");
			return;
		} else if (data == null) {
			System.out.println(pre + "NO DATA TO SEND");
			return;
		}
		outputWriter.println(data);
	}

	public static String[] readData(BufferedReader inputReader) {
		if (inputReader == null) {
			System.out.println(pre + "READ DATA FAILED, NO CONNECTION");
			return null;
		}

		String receivedMessage = null;
		String[] args = null;
		try {
			receivedMessage = inputReader.readLine();
		} catch (IOException ex) {
			Logger.getLogger(SocketFunctions.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.out.println(pre + receivedMessage);
		args = receivedMessage.split("\\s+");
		return args;
	}
}
