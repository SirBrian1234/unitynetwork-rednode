/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kostiskag.unitynetwork.rednode.functions;

import org.kostiskag.unitynetwork.common.utilities.CryptoUtilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class CryptoMethods {

	/**
	 * Generates a random question
	 * 
	 * @return
	 */
	public static String generateQuestion() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(1024, random).toString(32);
	}

	/**
	 * Generates an AES 256 session key
	 * 
	 * @return
	 */
	public static SecretKey generateAESSessionkey() {
		try {
			KeyGenerator AES_keygen = KeyGenerator.getInstance("AES");
			AES_keygen.init(128, new SecureRandom());
			return AES_keygen.generateKey();
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public static byte[] aesEncrypt(String message, SecretKey key) {
		Cipher AesCipher;
		try {
			AesCipher = Cipher.getInstance("AES");
			AesCipher.init(Cipher.ENCRYPT_MODE, key);
	        return AesCipher.doFinal(message.getBytes());
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String aesDecrypt(byte[] chiphered, SecretKey key) {
		Cipher AesCipher;
		try {
			AesCipher = Cipher.getInstance("AES");
			AesCipher.init(Cipher.DECRYPT_MODE, key);
	        return new String(AesCipher.doFinal(chiphered));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Generates a 2048 RSA key pair
	 * 
	 * @return keypair
	 */
	public static KeyPair generateRSAkeyPair() {
		KeyPairGenerator kpg = null;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048, new SecureRandom());
			return kpg.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encryptWithPublic(String text, PublicKey key) {
		byte[] cipherText = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	public static String decryptWithPrivate(byte[] text, PrivateKey key) {
		byte[] dectyptedText = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedText = cipher.doFinal(text);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new String(dectyptedText);
	}
	
	public static byte[] objectToBytes(Object obj) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(b);
			out.writeObject(obj);
			byte[] array = b.toByteArray();
			out.close();
			b.close();
			return array;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * remember to typecast to the object of your preference ex. PrivateKey
	 * private = (PrivateKey) bytesToObject(bytes);
	 * 
	 * @param bytes
	 * @return
	 */
	public static Object bytesToObject(byte[] bytes) {
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
			ObjectInputStream in = new ObjectInputStream(bin);
			Object obj = in.readObject();
			in.close();
			bin.close();
			return obj;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String objectToBase64StringRepresentation(Object obj) {
		byte[] serial = objectToBytes(obj);
		if (serial != null) {
			return CryptoUtilities.bytesToBase64String(serial);
		}
		return null;
	}
	
	public static Object base64StringRepresentationToObject(String base64Str) {
		try {
			return CryptoUtilities.base64StringRepresentationToObject(base64Str);
		} catch (GeneralSecurityException | IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String bytesToBase64String(byte[] b) {
		return CryptoUtilities.bytesToBase64String(b);
	}
	
	public static byte[] base64StringTobytes(String base64Str) {
		return CryptoUtilities.base64StringTobytes(base64Str);
	}

	public static void objectToFile(Object obj, File file) {
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(obj);
			out.close();
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				fileOut.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static Object fileToObject(File file) {
		FileInputStream fileIn;
		try {
			fileIn = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fileIn);
	        Object obj = in.readObject();
	        in.close();
	        fileIn.close();
			return obj;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        
		return null;
	}
}
