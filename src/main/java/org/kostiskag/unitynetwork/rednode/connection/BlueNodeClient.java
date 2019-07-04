package org.kostiskag.unitynetwork.rednode.connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PublicKey;

import org.kostiskag.unitynetwork.rednode.functions.CryptoMethods;
import org.kostiskag.unitynetwork.rednode.functions.SocketFunctions;

public class BlueNodeClient {

	/**
     * Collects a standalone bluenode's public key
     * 
     * @author Konstantinos Kagiampakis
     */
	public static PublicKey getPubKey(String address, int port) {
		InetAddress addr = SocketFunctions.getAddress(address);		
		Socket socket = SocketFunctions.absoluteConnect(addr, port);
		if (socket == null) {
			return null;
		}
		
		try {
			DataInputStream reader = SocketFunctions.makeDataReader(socket);
			DataOutputStream writer = SocketFunctions.makeDataWriter(socket);
			
			String[] args = SocketFunctions.sendReceivePlainStringData("GETPUB", reader, writer);
			SocketFunctions.connectionClose(socket);
			return (PublicKey) CryptoMethods.base64StringRepresentationToObject(args[0]);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		SocketFunctions.connectionClose(socket);
		return null;
	}
}
