package kostiskag.unitynetwork.rednode;

import java.io.File;
import java.security.KeyPair;

import javax.swing.UIManager;

import kostiskag.unitynetwork.rednode.functions.CryptoMethods;
import kostiskag.unitynetwork.rednode.gui.LoginWindow;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class App {

	public static final int defaultBlueNodeAuthPort = 7000;
	public static final int defaultTrackerAuthPort = 8000;
	public static final int keepAliveTimeSec = 10;
	public static final String SALT = "=UrBN&RLJ=dBshBX3HFn!S^Au?yjqV8MBx7fMyg5p6U8T^%2kp^X-sk9EQeENgVEj%DP$jNnz&JeF?rU-*meW5yFkmAvYW_=mA+E$F$xwKmw=uSxTdznSTbunBKT*-&!";
	public static final String redNodeKeysFileName = "public_private.keypair";
	public static KeyPair rednodeKeys;
	public static LoginWindow login;

	public static void main(String args[]) {
		System.out.println("@Started main at " + Thread.currentThread().getName());

		// rsa key pair
		File keyPairFile = new File(redNodeKeysFileName);
		if (keyPairFile.exists()) {
			// the rednode has key pair in file
			System.out.println("Loading RSA key pair from file...");
			rednodeKeys = (KeyPair) CryptoMethods.fileToObject(keyPairFile);
			System.out.println(
					"Your public key is:\n" + CryptoMethods.bytesToBase64String(rednodeKeys.getPublic().getEncoded()));

		} else {
			// the rednode does not have a public private key pair
			// generating...
			System.out.println("Generating RSA key pair...");
			rednodeKeys = CryptoMethods.generateRSAkeyPair();
			// and storing
			System.out.println("Generating key file...");
			CryptoMethods.objectToFile(rednodeKeys, keyPairFile);
			System.out.println(
					"Your public key is:\n" + CryptoMethods.bytesToBase64String(rednodeKeys.getPublic().getEncoded()));
		}

		// gui
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			} catch (Exception ex) {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
				} catch (Exception ex1) {

				}
			}
		}

		login = new LoginWindow();
		login.setVisible(true);
	}
}
