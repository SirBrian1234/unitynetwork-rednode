package kostiskag.unitynetwork.rednode.functions;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import kostiskag.unitynetwork.rednode.functions.HashFunctions;

import org.junit.Test;

public class HashFunctionsTest {

	@Test
	public void test() {
		try {
			System.out.println(HashFunctions.SHA256("banana"));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertTrue(false);
		}		
	}

}
