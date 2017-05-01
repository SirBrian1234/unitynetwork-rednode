package kostiskag.unitynetwork.rednode.Routing.data;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import kostiskag.unitynetwork.rednode.Routing.data.ReverseARPTable;

public class ReverseArpTableTest {

	@Test
	public void testLease() {
		ReverseARPTable table = null;
		try {
			table = new ReverseARPTable(InetAddress.getByName("10.0.0.1"));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			table.lease(InetAddress.getByName("10.0.0.2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//get .2
		try {
			table.getByIP(InetAddress.getByName("10.0.0.2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//and not .3
		try {
			table.getByIP(InetAddress.getByName("10.0.0.3"));
		} catch (Exception e) {
			assertTrue(true);
		}

	}
	
	@Test
	public void testRelease() {
		ReverseARPTable table = null;
		try {
			table = new ReverseARPTable(InetAddress.getByName("10.0.0.1"));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			table.lease(InetAddress.getByName("10.0.0.2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//get .2
		try {
			table.getByIP(InetAddress.getByName("10.0.0.2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		//and not .3
		try {
			table.getByIP(InetAddress.getByName("10.0.0.3"));
		} catch (Exception e) {
			assertTrue(true);
		}
		
		try {
			table.release(InetAddress.getByName("10.0.0.2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		try {
			table.release(InetAddress.getByName("10.0.0.2"));
		} catch (Exception e) {
			assertTrue(true);
		}
		
		assertEquals(table.getLength(),0);
	}
	
	@Test
	public void testGetByIP() {
		ReverseARPTable table = null;
		try {
			table = new ReverseARPTable(InetAddress.getByName("10.0.0.1"));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		try {
			table.lease(InetAddress.getByName("10.0.0.2"));
			table.lease(InetAddress.getByName("10.0.0.3"));
			table.lease(InetAddress.getByName("10.0.2.2"));
			table.lease(InetAddress.getByName("10.1.0.2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		try {
			System.out.println(table.getByIP(InetAddress.getByName("10.0.0.2")).getMac().toString());
			System.out.println(table.getByIP(InetAddress.getByName("10.0.0.3")).getMac().toString());
			System.out.println(table.getByIP(InetAddress.getByName("10.0.2.2")).getMac().toString());
			System.out.println(table.getByIP(InetAddress.getByName("10.1.0.2")).getMac().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
