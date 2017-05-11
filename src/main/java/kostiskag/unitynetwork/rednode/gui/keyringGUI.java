package kostiskag.unitynetwork.rednode.gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class keyringGUI {

	private JFrame frmNetworkKeyring;
	private JTable table;

	/**
	 * Create the application.
	 */
	public keyringGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmNetworkKeyring = new JFrame();
		frmNetworkKeyring.setTitle("Network Keyring");
		frmNetworkKeyring.setBounds(100, 100, 981, 515);
		frmNetworkKeyring.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmNetworkKeyring.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel(
				"<html>"
				+ "This window allows a rednode to manage all the availlable networks it may connect.<br><br> In order for a rednode to connect to a network, it needs to have fullfiled two tasks.<ul><li>To have a copy of the respective tracker's public key in hold.</li><li>To upload a copy of its public key to the tracker</li></ul>This window will help a user fullfil these tasks. Users can add a new tracker by clicking the add new button and then fill the data in the table by clicking in a field. Users can select a row and then click on the delete entry in order to delete it.</html>");
		lblNewLabel.setBounds(12, 13, 560, 148);
		frmNetworkKeyring.getContentPane().add(lblNewLabel);
		
		JButton btnDeleteSelected = new JButton("Delete Selected");
		btnDeleteSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//delete selected
			}
		});
		btnDeleteSelected.setBounds(12, 430, 121, 25);
		frmNetworkKeyring.getContentPane().add(btnDeleteSelected);
		
		JButton btnAddNew = new JButton("Add new");
		btnAddNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//add new
			}
		});
		btnAddNew.setBounds(839, 430, 112, 25);
		frmNetworkKeyring.getContentPane().add(btnAddNew);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 174, 939, 239);
		frmNetworkKeyring.getContentPane().add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Unity Tracker Address", "Port", "Tracker PubKey Status", "RedNode PubKey Status"
			}
		));
		
		scrollPane.setViewportView(table);			    
		
		JButton btnNewButton = new JButton("Download Selected Tracker PubKey");
		btnNewButton.setBounds(212, 430, 272, 25);
		frmNetworkKeyring.getContentPane().add(btnNewButton);
		
		JButton btnUploadRednodePublic = new JButton("Upload Selected RedNode Public Key");
		btnUploadRednodePublic.setBounds(496, 430, 241, 25);
		frmNetworkKeyring.getContentPane().add(btnUploadRednodePublic);
	}

	public void setVisible() {
		frmNetworkKeyring.setVisible(true);
	}
}
