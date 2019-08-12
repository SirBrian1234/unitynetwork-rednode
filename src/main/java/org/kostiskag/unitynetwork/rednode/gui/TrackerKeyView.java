package org.kostiskag.unitynetwork.rednode.gui;

import javax.swing.JFrame;
import javax.swing.JTextField;

import org.kostiskag.unitynetwork.common.utilities.CryptoUtilities;
import org.kostiskag.unitynetwork.rednode.App;
import org.kostiskag.unitynetwork.rednode.connection.TrackerClient;
import org.kostiskag.unitynetwork.rednode.table.TrackerEntry;

import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.UnknownHostException;

public class TrackerKeyView {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextArea textArea;
	private JTextArea textArea_1;
	private JButton btnDownloadTrackersPublic;
	private JButton btnRevokeThisRed;
	private JButton button;
	private TrackerEntry element;
	private String hostname;
	private JLabel lblNewLabel_1;
	private JTextField textField_4;

	/**
	 * Create the application.
	 */
	public TrackerKeyView(String address, int port, String hostname) {
		this.hostname = hostname;
		try {
			element = App.trakerKeyRingTable.getEntry(address, port);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		initialize();
		textField_4.setText(hostname);
		if (element.getPubKey() != null) {
			textArea.setText(CryptoUtilities.bytesToBase64String(element.getPubKey().getEncoded()));
			textField_2.setText("Key is set");
			button.setEnabled(true);
			btnRevokeThisRed.setEnabled(true);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 478, 726);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField(element.getAddress());
		textField.setEditable(false);
		textField.setBounds(114, 44, 180, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField(element.getPort()+"");
		textField_1.setEditable(false);
		textField_1.setBounds(304, 44, 86, 20);
		frame.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Tracker address");
		lblNewLabel.setBounds(10, 47, 94, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblTheFirstTask = new JLabel("<html>The first task at hand is to test the given address and download the tracker's public key</html>");
		lblTheFirstTask.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblTheFirstTask.setBounds(10, 75, 414, 36);
		frame.getContentPane().add(lblTheFirstTask);
		
		btnDownloadTrackersPublic = new JButton("Collect Tracker's Public Key");
		btnDownloadTrackersPublic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				collectTrackerKey();
			}
		});
		btnDownloadTrackersPublic.setBounds(20, 122, 180, 23);
		frame.getContentPane().add(btnDownloadTrackersPublic);
		
		JLabel label = new JLabel("Key status");
		label.setBounds(223, 126, 81, 16);
		frame.getContentPane().add(label);
		
		textField_2 = new JTextField();
		textField_2.setText("Not Set");
		textField_2.setFont(new Font("Tahoma", Font.ITALIC, 13));
		textField_2.setEditable(false);
		textField_2.setColumns(10);
		textField_2.setBounds(314, 121, 112, 22);
		frame.getContentPane().add(textField_2);
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea.setBounds(20, 156, 408, 81);
		frame.getContentPane().add(textArea);
		
		JLabel lbltheSecondTask = new JLabel("<html>The second task is to upload this rednode's public key to the tracker</html>");
		lbltheSecondTask.setFont(new Font("Tahoma", Font.BOLD, 12));
		lbltheSecondTask.setBounds(10, 248, 406, 36);
		frame.getContentPane().add(lbltheSecondTask);
		
		JLabel lblinOrderTo = new JLabel("<html>In order to upload this rednode's public key to the network, you should provide its session ticket generated by the tracker. A tracker's admin should be ble to send your session ticket when requested.</html>");
		lblinOrderTo.setBounds(20, 284, 414, 56);
		frame.getContentPane().add(lblinOrderTo);
		
		JLabel lblPasteYourSession = new JLabel("Paste your session ticket here and click the button");
		lblPasteYourSession.setForeground(new Color(153, 51, 0));
		lblPasteYourSession.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPasteYourSession.setBounds(20, 339, 370, 14);
		frame.getContentPane().add(lblPasteYourSession);
		
		textArea_1 = new JTextArea();
		textArea_1.setLineWrap(true);
		textArea_1.setFont(new Font("Monospaced", Font.PLAIN, 12));
		textArea_1.setBounds(20, 364, 414, 70);
		frame.getContentPane().add(textArea_1);
		
		button = new JButton("Upload Public Key");
		button.setEnabled(false);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadPubKey();
			}
		});
		button.setBounds(301, 445, 133, 23);
		frame.getContentPane().add(button);
		
		JLabel label_3 = new JLabel("<html>If you believe that your private key might be compromised you may revoke this rednode's public key from the server in order to generate a new keypair and upload a new public key. When the public key is revoked the present rednode may not be operational until a new one is set. In order to remove the public key you may click the button below.</html>");
		label_3.setBounds(20, 481, 410, 98);
		frame.getContentPane().add(label_3);
		
		btnRevokeThisRed = new JButton("Revoke this Red Node's Public Key");
		btnRevokeThisRed.setEnabled(false);
		btnRevokeThisRed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				revokePubKey();
			}			
		});
		btnRevokeThisRed.setBackground(new Color(153, 51, 0));
		btnRevokeThisRed.setBounds(205, 592, 229, 25);
		frame.getContentPane().add(btnRevokeThisRed);
		
		JLabel label_4 = new JLabel("Tracker response");
		label_4.setBounds(10, 649, 133, 14);
		frame.getContentPane().add(label_4);
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		textField_3.setBounds(153, 646, 154, 20);
		frame.getContentPane().add(textField_3);
		
		lblNewLabel_1 = new JLabel("This RedNode's hostname is");
		lblNewLabel_1.setBounds(10, 11, 190, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		textField_4 = new JTextField();
		textField_4.setEditable(false);
		textField_4.setBounds(205, 11, 154, 20);
		frame.getContentPane().add(textField_4);
		textField_4.setColumns(10);
	}
	
	public void setVisible() {
		frame.setVisible(true);
	}
	
	private void collectTrackerKey() {
		try {
			TrackerClient.getPubKey(element);
		} catch (IOException e) {

		}
		if (element.getPubKey() != null) {
			textArea.setText(CryptoUtilities.bytesToBase64String(element.getPubKey().getEncoded()));
			textField_2.setText("Key is set");
			btnDownloadTrackersPublic.setEnabled(false);
			button.setEnabled(true);
		}
	}
	
	private void uploadPubKey() {
		if (!textArea_1.getText().isEmpty()) {
			String responce = TrackerClient.offerPubKey(textArea_1.getText(), hostname, element);
			textField_3.setText(responce);
			if (responce.equals("KEY_SET") || responce.equals("KEY_IS_SET")) {
				btnRevokeThisRed.setEnabled(true);
			}
		}
	}
	
	private void revokePubKey() {
		try {
			TrackerClient tr = new TrackerClient(element, hostname);
			String responce;
			if (tr.isConnected()) {
				responce = tr.revokePubKey();
			} else {
				responce = tr.reason;
			}
			textField_3.setText(responce);
		} catch (UnknownHostException e) {
			textField_3.setText("Unknown host address");
		}
	}
}
