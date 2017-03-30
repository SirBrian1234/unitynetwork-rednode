package kostiskag.unitynetwork.rednode.GUI;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.Functions.SocketFunctions;
import kostiskag.unitynetwork.rednode.Connection.ConnectionManager;

/**
 *
 * @author kostis
 */
public class LoginWindow extends javax.swing.JFrame {

	// login credentials
	public String hostname;
	private String username;
	private String password;
	private String trackerAddress;
	private int trackerPort;
	// data
	private int loggedin = 0;
	private boolean validTracker = false;
	public boolean DirectBNConnect = false;
	public String blueNodeAddress = null;
	public int blueNodePort = -1;
	// objects
	private AdvancedWindow advanced;
	public MonitorWindow monitor;
	public ConnectionManager connection;

	public LoginWindow() {
		initComponents();
		monitor = new MonitorWindow();
		advanced = new AdvancedWindow();
		jScrollPane1.setVisible(false);
		jTextArea1.setVisible(false);
	}

	private void initComponents() {
		
		jPanel1 = new javax.swing.JPanel();
		jButton1 = new javax.swing.JButton();
		jPanel4 = new javax.swing.JPanel();
		jLabel3 = new javax.swing.JLabel();
		jTextField2 = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jTextField6 = new javax.swing.JTextField();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel8 = new javax.swing.JLabel();
		jPasswordField1 = new javax.swing.JPasswordField();
		jButton2 = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextArea1 = new javax.swing.JTextArea();
		jButton3 = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Red Node");
		setPreferredSize(new java.awt.Dimension(600, 500));
		setResizable(false);
		getContentPane().setLayout(null);

		jPanel1.setMaximumSize(new java.awt.Dimension(600, 500));
		jPanel1.setMinimumSize(new java.awt.Dimension(600, 500));
		jPanel1.setOpaque(false);
		jPanel1.setPreferredSize(new java.awt.Dimension(600, 500));
		jPanel1.setLayout(null);

		jButton1.setBackground(new java.awt.Color(0, 0, 0));
		jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
		jButton1.setText("Login");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});
		jPanel1.add(jButton1);
		jButton1.setBounds(200, 400, 164, 41);

		jPanel4.setOpaque(false);

		jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel3.setForeground(new java.awt.Color(255, 255, 255));
		jLabel3.setText("Username");

		jLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel4.setForeground(new java.awt.Color(255, 255, 255));
		jLabel4.setText("Password");

		jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel5.setForeground(new java.awt.Color(255, 255, 255));

		jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
		jLabel8.setForeground(new java.awt.Color(255, 255, 255));
		jLabel8.setText("Unity Tracker Address");

		jButton2.setBackground(new java.awt.Color(0, 0, 0));
		jButton2.setText("Advanced");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});

		jTextField2.setText("kostis");
		jTextField1.setText("kostis-laptop");
		jPasswordField1.setText("123456");
		jTextField6.setText("127.0.0.1:8000");		

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout
				.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel4Layout.createSequentialGroup().addContainerGap()
								.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(jSeparator1).addComponent(jLabel8)
										.addGroup(jPanel4Layout.createSequentialGroup().addGap(16, 16, 16)
												.addGroup(jPanel4Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(jLabel3).addComponent(jTextField2)
														.addComponent(jLabel5).addComponent(jLabel4)
														.addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE,
																156, Short.MAX_VALUE)
														.addComponent(jPasswordField1)))
										.addGroup(jPanel4Layout.createSequentialGroup()
												.addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 215,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jButton2)))
								.addContainerGap()));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addComponent(jLabel3)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel4)
						.addGap(3, 3, 3)
						.addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel5)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel8)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jButton2))
						.addContainerGap(49, Short.MAX_VALUE)));

		jPanel1.add(jPanel4);
		jPanel4.setBounds(129, 94, 322, 266);

		jTextArea1.setColumns(20);
		jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		jPanel1.add(jScrollPane1);
		jScrollPane1.setBounds(80, 80, 430, 300);

		jButton3.setBackground(new java.awt.Color(0, 0, 0));
		jButton3.setText("Monitor Window");
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton3ActionPerformed(evt);
			}
		});
		jPanel1.add(jButton3);
		jButton3.setBounds(460, 430, 120, 30);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(1, 1, 600, 500);

		jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("Worldwide-Network.jpg"))); // NOI18N
		getContentPane().add(jLabel1);
		jLabel1.setBounds(-95, -70, 800, 600);

		pack();
	}
	
	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {	
		DirectBNConnect = false;
		toggleLogin();
	}

	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {	
		advanced.toggleVisible();
	}

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
		monitor.toggleVisible();
	}

	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel4;
	public javax.swing.JPasswordField jPasswordField1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JTextArea jTextArea1;
	public javax.swing.JTextField jTextField1;
	public javax.swing.JTextField jTextField2;
	public javax.swing.JTextField jTextField6;

	public void toggleLogin() {
		// does login
		if (loggedin == 0) {
			jButton1.setEnabled(false);
			jPanel4.setVisible(false);
			jScrollPane1.setVisible(true);
			jTextArea1.setVisible(true);
			advanced.setVisible(false);
			monitor.clearCommands();

			writeInfo("Getting Data from input...");
			// checks whether the given data are valid
			if (getInputData()) {
				if (!DirectBNConnect) {
					validTracker = getRecomendedBlueNode(trackerAddress, trackerPort);
					if (validTracker) {
						connection = new ConnectionManager(username, password, hostname, blueNodeAddress, blueNodePort);
						connection.start();
					} else {
						writeInfo("Terminating connection");
						setLogedOut();
					}
				} else {
					writeInfo("Skipping Auto BlueNode find...");
					connection = new ConnectionManager(username, password, hostname, advanced.getBlueNodeAddress(),
							advanced.getBlueNodePort());
					connection.start();
				}
			} else {
				writeInfo("The given data were not valid.");
				setLogedOut();
			}
			// does logout
		} else if (loggedin == 1) {
			jButton1.setEnabled(false);
			App.login.connection.giveCommand("EXIT");
		} // does retry
		else {
			jButton1.setText("Login");
			jPanel4.setVisible(true);
			jScrollPane1.setVisible(false);
			jTextArea1.setText("");
			jTextArea1.setVisible(false);
			loggedin = 0;
		}
	}

	public void setLogedOut() {
		loggedin = 2;
		jButton1.setText("Retry");
		jButton1.setEnabled(true);
	}

	public void setLoggedIn() {
		loggedin = 1;
		jButton1.setText("Logout");
		jButton1.setEnabled(true);
	}

	public void writeInfo(String message) {
		jTextArea1.append("* " + message + "\n");
	}

	public boolean getInputData() {		
		password = new String(jPasswordField1.getPassword());
		
		if (!jTextField1.getText().isEmpty() && !jTextField2.getText().isEmpty() && !password.isEmpty() && !jTextField6.getText().isEmpty()) {
			username = jTextField2.getText();
			hostname = jTextField1.getText();
			String fulladdress = jTextField6.getText();
			String[] args = fulladdress.split(":");
			trackerAddress = args[0];
			trackerPort = -1;
			
			if (args.length > 1) {
				try {
					trackerPort = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					return false;
				}

				if (!(trackerPort > 0 && trackerPort <= 65535)) {
					return false;
				}
			} else {
				trackerPort = App.defaultTrackerAuthPort;
			}

			return true;

		} else {
			writeInfo("Please fill in the empty fields.");
			return false;
		}
	}

	public boolean getRecomendedBlueNode(String TrackerAddress, int TrackerPort) {
		writeInfo("Getting recomended BlueNode from tracker " + TrackerAddress + ":" + TrackerPort + " ...");

		InetAddress addr = SocketFunctions.getAddress(TrackerAddress);
		Socket socket = SocketFunctions.absoluteConnect(addr, TrackerPort);
		if (socket == null) {
			writeInfo("Tracker connection failed");
			return false;
		}

		BufferedReader inputReader = SocketFunctions.makeReadWriter(socket);
		PrintWriter writer = SocketFunctions.makeWriteWriter(socket);
		String args[] = SocketFunctions.readData(inputReader);
		args = SocketFunctions.sendData("REDNODE " + App.login.hostname, writer, inputReader);

		if (args[0].equals("OK")) {
			args = SocketFunctions.sendData("GETRBN", writer, inputReader);
			if (!args[0].equals("NONE")) {
				writeInfo("Tracker Gave BN " + args[0] + " " + args[1] + " " + args[2] + " " + args[3]);
				blueNodeAddress = args[1];
				blueNodePort = Integer.parseInt(args[2]);
				return true;
			} else {
				writeInfo(
						"The Tracker is up but does not have any associated BlueNodes.\nIn other words, the network is down as there are no BlueNodes to carry the traffic.\nPlease select either to connect to another Network or define a standalone BlueNode from the Advanced Settings tab.");
				return false;
			}
		}
		SocketFunctions.connectionClose(socket);
		return false;
	}
}
