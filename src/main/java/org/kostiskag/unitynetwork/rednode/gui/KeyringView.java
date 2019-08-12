package org.kostiskag.unitynetwork.rednode.gui;

import java.net.UnknownHostException;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.kostiskag.unitynetwork.common.address.PhysicalAddress;

import org.kostiskag.unitynetwork.rednode.App;


final class KeyringView {

	private JFrame frmNetworkKeyring;
	private JTable table;
	private JLabel lblNewLabel_1;
	private String header[] = new String[] { "Unity Tracker Address", "Port" };
	private DefaultTableModel model = new DefaultTableModel(new Object[][] {}, header);
	private String hostname;

	/**
	 * Create the application.
	 */
	public KeyringView(String hostname) {
		initialize();
		populateJTable();
		this.hostname = hostname;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmNetworkKeyring = new JFrame();
		frmNetworkKeyring.setTitle("Network Keyring");
		frmNetworkKeyring.setBounds(100, 100, 533, 515);
		frmNetworkKeyring.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmNetworkKeyring.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
            	App.storeKeyringObjectToFile();
				frmNetworkKeyring.dispose();
            }
        });
		
		
		frmNetworkKeyring.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("<html>"
				+ "This window allows a rednode to manage all the availlable networks it may connect.<br><br> In order for a rednode to connect to a network, it needs to have fullfiled two tasks.<ul><li>To have a copy of the respective tracker's public key in hold.</li><li>To upload a copy of its public key to the tracker</li></ul>This window will help a user fullfil these tasks. Users can add a new tracker by clicking the add new button and then fill the data in the table by clicking in a field. Users can select a row and then click on the delete entry in order to delete it.</html>");
		lblNewLabel.setBounds(12, 13, 495, 148);
		frmNetworkKeyring.getContentPane().add(lblNewLabel);

		JButton btnDeleteSelected = new JButton("Delete Selected");
		btnDeleteSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// delete selected
				deleteSelected();				
			}
		});
		btnDeleteSelected.setBounds(386, 441, 121, 25);
		frmNetworkKeyring.getContentPane().add(btnDeleteSelected);

		JButton btnAddNew = new JButton("Add new");
		btnAddNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// add new
				addNew();
			}
		});
		btnAddNew.setBounds(12, 441, 112, 25);
		frmNetworkKeyring.getContentPane().add(btnAddNew);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 174, 495, 213);
		frmNetworkKeyring.getContentPane().add(scrollPane);

		table = new JTable();
		table.setModel(model);

		scrollPane.setViewportView(table);

		JButton btnEditSelected = new JButton("Edit Selected");
		btnEditSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// edit selected
				editSelected();
			}
		});
		btnEditSelected.setBounds(200, 442, 124, 23);
		frmNetworkKeyring.getContentPane().add(btnEditSelected);
		
		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setForeground(new Color(153, 51, 0));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblNewLabel_1.setBounds(12, 398, 495, 32);
		frmNetworkKeyring.getContentPane().add(lblNewLabel_1);
	}

	public void setVisible() {
		frmNetworkKeyring.setVisible(true);
	}
	
	private synchronized void addNew() {
		model.addRow(new String[] {"",""});
	}
	
	private synchronized void editSelected() {
		int selected = table.getSelectedRow();
		if (selected > -1) {
			try {
				var strAddr = (String) table.getValueAt(selected, 0);
				if (strAddr.isEmpty()) {
					lblNewLabel_1.setText("Please provide an address");
					return;
				}
				PhysicalAddress address = PhysicalAddress.valueOf(strAddr);

				int port;
				if (((String) table.getValueAt(selected, 1)).isEmpty()) {
					port = 8000;
				} else {
					try {
						port = Integer.parseInt((String) table.getValueAt(selected, 1));
					} catch (NumberFormatException ex) {
						lblNewLabel_1.setText("Please Provide a proper port number from 1 to 65535 or leave it empty for the default port.");
						return;
					}
					if (port <= 0 || port > 65535) {
						lblNewLabel_1.setText("Please Provide a proper port number from 1 to 65535 or leave it empty for the default port.");
						return;
					}
				}

				if (!App.trakerKeyRingTable.checkIfExisting(address, port)) {
					//there is no entry in the table, init please
					try {
						App.trakerKeyRingTable.addEntry(address, port);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//opens a new window to edit the given entry
				new TrackerKeyView(address.asString(), port, hostname).setVisible();
			} catch (UnknownHostException e) {
				System.out.println(e.getLocalizedMessage());
			}
		}
	}
	
	private synchronized void deleteSelected() {
		int selected = table.getSelectedRow();
		if (selected > -1) {
			try {
				var address = PhysicalAddress.valueOf((String) table.getValueAt(selected, 0));
				int port;
				try {
					port = Integer.parseInt((String) table.getValueAt(selected, 1));
				} catch (NumberFormatException ex) {
					port = -1;
				}

				if (App.trakerKeyRingTable.checkIfExisting(address, port)) {
					//delete the table entry
					try {
						App.trakerKeyRingTable.removeEntry(address, port);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				//delete the row
				model.removeRow(selected);
				table.setModel(model);
			} catch (UnknownHostException e) {
				System.out.println("delete failed "+e.getLocalizedMessage());
			}
		}
	}
	
	private void populateJTable() {		
		String[][] data = App.trakerKeyRingTable.buildStringTable();
		model = new DefaultTableModel(data, header);
		table.setModel(model);				
	}
}
