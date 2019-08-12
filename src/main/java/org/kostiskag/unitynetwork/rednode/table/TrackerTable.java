package org.kostiskag.unitynetwork.rednode.table;

import org.kostiskag.unitynetwork.common.address.PhysicalAddress;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class TrackerTable implements Serializable {

	private static final long serialVersionUID = -4596986766485247624L;
	private Set<TrackerEntry> set;
	
	public TrackerTable() {
		set = new HashSet<>();
	}

	public synchronized void addEntry(String address, int port) throws Exception {
		addEntry(PhysicalAddress.valueOf(address), port);
	}

	public synchronized void addEntry(PhysicalAddress address, int port) throws Exception {
		TrackerEntry t = new TrackerEntry(address, port);
		if (set.contains(t)) {
			throw new Exception("Entry already exists in table");
		} else {
			set.add(t);
		}
	}

	public synchronized TrackerEntry getEntry(String address, int port) throws Exception {
		return getEntry(PhysicalAddress.valueOf(address), port);
	}

	public synchronized TrackerEntry getEntry(PhysicalAddress address, int port) throws Exception {
		var in = new TrackerEntry(address, port);
		var opt = set.stream().filter(t -> t.equals(in)).findFirst();
		if (opt.isPresent()) {
			return opt.get();
		} else {
			throw new Exception("No such entry was found in the table.");
		}
	}

	public synchronized boolean checkIfExisting(String address, int port) throws UnknownHostException {
		return checkIfExisting(PhysicalAddress.valueOf(address), port);
	}

	public synchronized boolean checkIfExisting(PhysicalAddress address, int port) {
		TrackerEntry t = new TrackerEntry(address, port);
		return set.contains(t);
	}

	public synchronized void removeEntry(String address, int port) throws Exception {
		removeEntry(PhysicalAddress.valueOf(address), port);
	}

	public synchronized void removeEntry(PhysicalAddress address, int port) throws Exception {
		TrackerEntry t = new TrackerEntry(address, port);
		if (set.contains(t)) {
			set.remove(t);
		} else {
			throw new Exception("No such entry was found in the table.");
		}
	}

	public synchronized String[][] buildStringTable() {
		return (String[][]) set.stream().map(t -> new String[]{t.getAddress().asString(), t.getPort() + ""}).toArray();
	}
}
