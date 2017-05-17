package kostiskag.unitynetwork.rednode.gui;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;

import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.connection.TrackerClient;
import kostiskag.unitynetwork.rednode.tables.trackerInstance;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JFrame;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class AdvancedWindow extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8836862621884801942L;
	private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private DefaultTableModel bluenodes;
    private String hostname;
    private JLabel lblNewLabel;
    private JTextField textField;
    private JTextField textField_1;

    public AdvancedWindow(String address, String portField, String hostname) {
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	this.hostname = hostname;
        bluenodes = new DefaultTableModel(new String[][]{}, new String[]{"Name", "Physical Address", "Auth Port", "RedNode Load"});
        initComponents();
        textField_1.setText(hostname);
        jTextField1.setText(address);
        jTextField2.setText(portField);
    }

    public void setVisible() {
    	this.setVisible(true);
    }

    public void setLoggedIn() {
        jButton1.setEnabled(false);
        jButton2.setEnabled(false);
    }

    public void setLoggedOut() {
        jButton1.setEnabled(true);
        jButton2.setEnabled(true);
    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jTextField1.setEditable(false);
        jTextField2 = new javax.swing.JTextField();
        jTextField2.setEditable(false);
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jTextField3.setEditable(false);
        jLabel3 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField4.setEditable(false);
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setTitle("Advanced Login Settings");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Get Blue Node list from Tracker"));

        jLabel1.setText("Unity Tracker Address");

        jLabel2.setText("port");
        jLabel2.setToolTipText("");

        jButton1.setText("Get Blue Node List From Tracker");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable1.setModel(new DefaultTableModel(
        	new Object[][] {
        	},
        	new String[] {
        		"Name", "Physical Address", "Auth Port", "RedNode Load"
        	}
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);
        
        lblNewLabel = new JLabel("");
        
        JLabel lblHostname = new JLabel("Your hostname is:");
        
        textField_1 = new JTextField();
        textField_1.setEditable(false);
        textField_1.setColumns(10);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1Layout.setHorizontalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblHostname)
        			.addGap(12)
        			.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(216, Short.MAX_VALUE))
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 196, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jLabel1))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jLabel2)
        				.addGroup(jPanel1Layout.createSequentialGroup()
        					.addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)))
        			.addContainerGap(59, Short.MAX_VALUE))
        		.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 532, Short.MAX_VALUE)
        			.addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(jPanel1Layout.createSequentialGroup()
        					.addGap(3)
        					.addComponent(lblHostname))
        				.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addGap(18)
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel1)
        				.addComponent(jLabel2))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jButton1))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
        			.addGap(10)
        			.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
        );
        jPanel1.setLayout(jPanel1Layout);
        
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        
        jLabel3.setText("Blue Node address");
        
        jLabel4.setText("port");
        
        jButton2.setText("Login");        
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        
        JLabel lblBlueNodeName = new JLabel("Blue Node Name");
        
        textField = new JTextField();
        textField.setEditable(false);
        textField.setColumns(10);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2Layout.setHorizontalGroup(
        	jPanel2Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(lblBlueNodeName, GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
        				.addComponent(textField, GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, 199, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jLabel3))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jLabel4)
        				.addGroup(jPanel2Layout.createSequentialGroup()
        					.addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addComponent(jButton2)))
        			.addGap(42))
        );
        jPanel2Layout.setVerticalGroup(
        	jPanel2Layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(jPanel2Layout.createSequentialGroup()
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        			.addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel3)
        				.addComponent(jLabel4)
        				.addComponent(lblBlueNodeName))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jButton2)
        				.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap())
        );
        jPanel2.setLayout(jPanel2Layout);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(jPanel1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        				.addComponent(jPanel2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE))
        			.addContainerGap())
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap())
        );
        getContentPane().setLayout(layout);

        pack();
    }
    
    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        int num  = jTable1.getSelectedRow();
        if (num > -1) {
            textField.setText((String) jTable1.getValueAt(num, 0));
            jTextField3.setText((String) jTable1.getValueAt(num, 1));            
            jTextField4.setText((String) jTable1.getValueAt(num, 2));
        }
    }      
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (!jTextField1.getText().isEmpty() && jTextField1.getText().length() < App.login.max_str_size && jTextField2.getText().length() < App.login.max_str_port_size) {
	    	int rows = bluenodes.getRowCount();
	        for (int i = 0; i < rows; i++) {
	            bluenodes.removeRow(0);
	        }
	
	        String addr = jTextField1.getText();
	        int port;
	        
	        if (jTextField2.getText().isEmpty()) {
	        	port = App.defaultTrackerAuthPort;
	        } else {
	        	port = Integer.parseInt(jTextField2.getText());
	        }
	
	        if (App.trakerKeyRingTable.checkIfExisting(addr, port)) {
	        	trackerInstance tr;
				try {
					tr = App.trakerKeyRingTable.getEntry(addr, port);
					TrackerClient cl = new TrackerClient(tr, hostname);
					if (cl.isConnected()) {
			        	LinkedList<String[]> list = cl.getBNs();
				        
				        Iterator<String[]> it = list.listIterator();
				        while(it.hasNext()) {
				        	bluenodes.addRow(it.next());
				        }
				        jTable1.setModel(bluenodes);
				    } else {
						lblNewLabel.setText("Could not connect to tracker. Check if your given hosname is the correct one.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	        } else {
	        	lblNewLabel.setText("The given tracker credentials were not registered with the keyring.");
	        }	        
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
    	if (!jTextField3.getText().isEmpty()) {
    		App.login.blueNodeName = textField.getText();
	    	App.login.blueNodeAddress = jTextField3.getText();
	        if (jTextField4.getText().isEmpty()) {
	        	App.login.blueNodePort = App.defaultBlueNodeAuthPort;
	        } else {
	        	App.login.blueNodePort = Integer.parseInt(jTextField4.getText());
	        }        
	        App.login.useNetworkSelectedBN = true;
	        App.login.toggleLogin();
	        dispose();
    	}
    }     
}
