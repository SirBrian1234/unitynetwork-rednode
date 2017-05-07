package kostiskag.unitynetwork.rednode.gui;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.table.DefaultTableModel;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.functions.SocketFunctions;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 *
 * @author kostis
 */
public class AdvancedWindow extends javax.swing.JFrame {

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

    public AdvancedWindow() {
        bluenodes = new DefaultTableModel(new String[][]{}, new String[]{"Hostname", "Physical Address", "Auth Port", "RedNode Load"});
        initComponents();
    }

    public void toggleVisible() {
        if (this.isVisible()) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
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
        jTextField2 = new javax.swing.JTextField();
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

        jButton1.setText("Get Blue Node List");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable1.setModel(bluenodes);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1Layout.setHorizontalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
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
        					.addComponent(jButton1, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)))
        			.addContainerGap(134, Short.MAX_VALUE))
        		.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel1)
        				.addComponent(jLabel2))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jButton1))
        			.addGap(18)
        			.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE))
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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2Layout.setHorizontalGroup(
        	jPanel2Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel2Layout.createSequentialGroup()
        			.addContainerGap()
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
        			.addContainerGap(227, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
        	jPanel2Layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(jPanel2Layout.createSequentialGroup()
        			.addContainerGap(26, Short.MAX_VALUE)
        			.addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jLabel3)
        				.addComponent(jLabel4))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jTextField3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jTextField4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jButton2))
        			.addContainerGap())
        );
        jPanel2.setLayout(jPanel2Layout);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        		.addComponent(jPanel2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        			.addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE))
        );
        getContentPane().setLayout(layout);

        pack();
    }
    
    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {
        int[] table = jTable1.getSelectedRows();
        if (table.length == 1) {
            int num = table[0];            
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
	
	        InetAddress addr = SocketFunctions.getAddress(jTextField1.getText());
	        int port;
	        if (jTextField2.getText().isEmpty()) {
	        	port = App.defaultTrackerAuthPort;
	        } else {
	        	port = Integer.parseInt(jTextField2.getText());
	        }
	        
	        Socket socket;
	        if (addr != null) {
	        	socket = SocketFunctions.absoluteConnect(addr, port);
	        } else {
	        	return;
	        }
	        
	        if (socket == null) {
	            return;
	        }
	
	        BufferedReader inputReader = SocketFunctions.makeReadWriter(socket);
	        PrintWriter writer = SocketFunctions.makeWriteWriter(socket);
	        String args[] = SocketFunctions.readData(inputReader);
	        
	        App.login.getInputData();
	        args = SocketFunctions.sendData("REDNODE "+App.login.hostname, writer, inputReader);
	
	        if (args[0].equals("OK")) {
	
	            args = SocketFunctions.sendData("GETBNS", writer, inputReader);
	
	            int count = Integer.parseInt(args[1]);
	            for (int i = 0; i < count; i++) {
	                args = SocketFunctions.readData(inputReader);
	                bluenodes.addRow(new Object[]{args[0], args[1], args[2], args[3]});
	            }
	        }
	        SocketFunctions.connectionClose(socket);
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
    	App.login.blueNodeAddress = jTextField3.getText();
        if (jTextField4.getText().isEmpty()) {
        	App.login.blueNodePort = App.defaultBlueNodeAuthPort;
        } else {
        	App.login.blueNodePort = Integer.parseInt(jTextField4.getText());
        }        
        App.login.useNetworkSelectedBN = true;
        App.login.toggleLogin();
        setVisible(false);
    }     
}
