package org.kostiskag.unitynetwork.rednode.table;

import java.util.Objects;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.security.PublicKey;

import org.kostiskag.unitynetwork.common.address.PhysicalAddress;


public class TrackerEntry implements Serializable {

	private static final long serialVersionUID = 8738881975819394533L;
	private final PhysicalAddress address;
	private final int port;
	private PublicKey pubKey;
	
	public TrackerEntry(String address, int port) throws UnknownHostException {
		this(PhysicalAddress.valueOf(address), port);
	}

	public TrackerEntry(PhysicalAddress address, int port) {
		this.address = address;
		this.port = port;
	}

	public PhysicalAddress getAddress() {
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

	@Override
	public boolean equals(Object in) {
		if (this == in) return true;
		if (in == null || !( in instanceof TrackerEntry ))
			return false;
		TrackerEntry inObj = (TrackerEntry) in;
		return port == inObj.port &&
				address.equals(inObj.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, port);
	}

	@Override
	public String toString() {
		return this.getAddress().asString()+ " " + this.getPort();
	}
}
