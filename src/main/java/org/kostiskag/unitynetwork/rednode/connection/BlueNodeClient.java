package org.kostiskag.unitynetwork.rednode.connection;

import org.kostiskag.unitynetwork.common.utilities.CryptoUtilities;
import org.kostiskag.unitynetwork.common.utilities.SocketUtilities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;

public class BlueNodeClient {

	/**
     * Collects a standalone bluenode's public key
     * 
     * @author Konstantinos Kagiampakis
     */
	public static PublicKey getPubKey(String address, int port) throws IOException {
		InetAddress addr = SocketUtilities.getAddress(address);
		Socket socket = SocketUtilities.absoluteConnect(addr, port);
		if (socket == null) {
			return null;
		}
		
		try {
			DataInputStream reader = SocketUtilities.makeDataReader(socket);
			DataOutputStream writer = SocketUtilities.makeDataWriter(socket);
			
			String[] args = SocketUtilities.sendReceivePlainStringData("GETPUB", reader, writer);
			SocketUtilities.connectionClose(socket);
			return (PublicKey) CryptoUtilities.base64StringRepresentationToObject(args[0]);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		SocketUtilities.connectionClose(socket);
		return null;
	}
}
