package org.kostiskag.unitynetwork.rednode.tables;

import java.io.Serializable;
import java.security.PublicKey;

public class TrackerEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8738881975819394533L;
	private final String address;
	private final int port;
	private PublicKey pubKey;
	
	public TrackerEntry(String address, int port) {
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public PublicKey getPubKey() {
		return pubKey;
	}

	public void setPubKey(PublicKey pubKey) {
		this.pubKey = pubKey;
	}	
}
