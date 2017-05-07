package kostiskag.unitynetwork.rednode.gui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import kostiskag.unitynetwork.rednode.App;
import kostiskag.unitynetwork.rednode.connection.ConnectionManager;
import kostiskag.unitynetwork.rednode.functions.HashFunctions;
import kostiskag.unitynetwork.rednode.functions.SocketFunctions;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * @author Konstantinos Kagiampakis
 */
public class LoginWindow extends javax.swing.JFrame {

	// max sizes
	int max_str_size = 128;
	int max_str_port_size = 5; //as 65535
	int max_port_size = 65535;
	// login credentials
	public String hostname;
	private String username;
	private String password;
	// tracker
	private String trackerAddress;
	private int trackerPort;
    // bluenode
	public String blueNodeAddress = null;
	public int blueNodePort = -1;
	// data
	public boolean useStandaloneBN = false;
	public boolean useNetworkSelectedBN = false;
	private int loggedin = 0;
	private boolean validTracker = false;
	// objects
	private AdvancedWindow advanced;
	public MonitorWindow monitor;
	public ConnectionManager connection;
	//about
	About about;

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
		jSeparator1 = new javax.swing.JSeparator();
		jPasswordField1 = new javax.swing.JPasswordField();
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
		
	    tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(new Color(0, 102, 153));
		
		lblHostname = new JLabel();
		lblHostname.setText("Hostname");
		lblHostname.setForeground(Color.WHITE);
		lblHostname.setFont(new Font("Tahoma", Font.BOLD, 11));

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4Layout.setHorizontalGroup(
			jPanel4Layout.createParallelGroup(Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup()
					.addContainerGap()
					.addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING)
						.addGroup(jPanel4Layout.createSequentialGroup()
							.addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING)
								.addGroup(jPanel4Layout.createSequentialGroup()
									.addGap(16)
									.addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(jLabel3)
										.addComponent(jTextField2)
										.addComponent(jLabel4)
										.addComponent(jPasswordField1)
										.addGroup(jPanel4Layout.createSequentialGroup()
											.addComponent(jLabel5)
											.addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING)
												.addComponent(jTextField1, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
												.addComponent(lblHostname, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)))))
								.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addContainerGap())
						.addGroup(jPanel4Layout.createSequentialGroup()
							.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
							.addGap(16))))
		);
		jPanel4Layout.setVerticalGroup(
			jPanel4Layout.createParallelGroup(Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(jLabel3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(jTextField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(jLabel4)
					.addGap(3)
					.addComponent(jPasswordField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGroup(jPanel4Layout.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, jPanel4Layout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(jLabel5)
							.addGap(26)
							.addGap(18)
							.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 7, GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanel4Layout.createSequentialGroup()
							.addGap(6)
							.addComponent(lblHostname)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		
		panel = new JPanel();
		panel.setBackground(new Color(0, 102, 153));
		tabbedPane.addTab("Full Network", null, panel, null);
		panel.setLayout(null);
		
		lblUnityTrackerAddress = new JLabel();
		lblUnityTrackerAddress.setBounds(10, 8, 126, 14);
		lblUnityTrackerAddress.setText("Unity Tracker address");
		lblUnityTrackerAddress.setForeground(Color.WHITE);
		lblUnityTrackerAddress.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(lblUnityTrackerAddress);
		
		label_1 = new JLabel();
		label_1.setBounds(156, 8, 24, 14);
		label_1.setText("port");
		label_1.setForeground(Color.WHITE);
		label_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel.add(label_1);
		
		jTextField3 = new JTextField();
		jTextField3.setBounds(10, 31, 136, 20);
		panel.add(jTextField3);
		
		jTextField4 = new JTextField();
		jTextField4.setBounds(156, 31, 40, 20);
		jTextField4.setColumns(10);
		panel.add(jTextField4);
		
		jButton2 = new JButton();
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton2ActionPerformed(evt);
			}
		});
		jButton2.setBounds(201, 30, 81, 23);
		jButton2.setText("Advanced");
		jButton2.setBackground(Color.BLACK);
		panel.add(jButton2);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(0, 102, 153));
		tabbedPane.addTab("Standalone Blue Node", null, panel_1, null);
		panel_1.setLayout(null);
		
		label_2 = new JLabel();
		label_2.setText("port");
		label_2.setForeground(Color.WHITE);
		label_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_2.setBounds(156, 8, 24, 14);
		panel_1.add(label_2);
		
		lblBlueNodeAddress = new JLabel();
		lblBlueNodeAddress.setText("Blue Node address");
		lblBlueNodeAddress.setForeground(Color.WHITE);
		lblBlueNodeAddress.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblBlueNodeAddress.setBounds(10, 8, 126, 14);
		panel_1.add(lblBlueNodeAddress);
		
		jTextField5 = new JTextField();
		jTextField5.setBounds(10, 31, 136, 20);
		panel_1.add(jTextField5);
		
		jTextField6 = new JTextField();
		jTextField6.setColumns(10);
		jTextField6.setBounds(156, 31, 40, 20);
		panel_1.add(jTextField6);
		jPanel4.setLayout(jPanel4Layout);

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
		jButton3.setBounds(10, 430, 125, 30);

		getContentPane().add(jPanel1);
		jPanel1.setBounds(1, 1, 600, 500);
		
		JButton btnNewButton = new JButton("About");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (about == null) {
					about = new About();
				} else if (!about.isVisible()) {
					about = new About();
				}
			}
		});
		btnNewButton.setBackground(Color.BLACK);
		btnNewButton.setBounds(489, 430, 89, 30);
		jPanel1.add(btnNewButton);

		jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("loginBg.jpg"))); // NOI18N
		getContentPane().add(jLabel1);
		jLabel1.setBounds(-95, -70, 800, 600);

		pack();
	}
	
	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {			
		toggleLogin();
	}
	
	private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {			
		advanced.toggleVisible();
	}

	private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
		monitor.toggleVisible();
	}

	private javax.swing.JTabbedPane tabbedPane;
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton3;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel4;
	public javax.swing.JPasswordField jPasswordField1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JTextArea jTextArea1;
	public javax.swing.JTextField jTextField1;
	public javax.swing.JTextField jTextField2;
	private JPanel panel;
	private JLabel lblUnityTrackerAddress;
	private JLabel label_1;
	private JTextField jTextField3;
	private JTextField jTextField4;
	private JButton jButton2;
	private JLabel label_2;
	private JLabel lblBlueNodeAddress;
	private JTextField jTextField5;
	private JTextField jTextField6;
	private JLabel lblHostname;

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
				if (!useStandaloneBN) {
					if (!useNetworkSelectedBN) {
					validTracker = getRecomendedBlueNode(trackerAddress, trackerPort);
					if (validTracker) {
						connection = new ConnectionManager(username, password, hostname, blueNodeAddress, blueNodePort);
						connection.start();
					} else {
						writeInfo("Terminating connection");
						setLogedOut();
					}
						} else {
							writeInfo("Connecting to Network Blue Node...");
							connection = new ConnectionManager(username, password, hostname, blueNodeAddress, blueNodePort);
							connection.start();
						}					
				} else {
					writeInfo("Connecting to Standalone Blue Node...");
					connection = new ConnectionManager(username, password, hostname, blueNodeAddress, blueNodePort);
					connection.start();
				}
			} else {
				writeInfo("The given data were not valid.");
				setLogedOut();
			}			
		} else if (loggedin == 1) {
			// does logout
			jButton1.setEnabled(false);
			App.login.connection.giveCommand("EXIT");
		} else if  (loggedin == 2) {
			// does retry
			jButton1.setText("Login");
			jPanel4.setVisible(true);
			jScrollPane1.setVisible(false);
			jTextArea1.setText("");
			jTextArea1.setVisible(false);
			loggedin = 0;
		} else {
			//does exit
			System.exit(0);
		}
	}

	public void setLogedOut() {
		//if the interface was used the app has to close
		if (connection != null) {
			if (connection.isInterfaceSet()) {
				loggedin = 3;
				jButton1.setText("Exit");
			} else {
				loggedin = 2;
				jButton1.setText("Retry");
			}
		} else {
			loggedin = 2;
			jButton1.setText("Retry");
		}
		jButton1.setEnabled(true);
	}

	public void setLoggedIn() {
		loggedin = 1;
		jButton1.setText("Logout");
		jButton1.setEnabled(true);
	}

	public void writeInfo(String message) {
		jTextArea1.append("* " + message + "\n");
		monitor.writeToCommands("* " + message);
		System.out.println("* " + message);
	}

	public boolean getInputData() {
		
		//for a full network username password hostname address are needed
		//port may be default
		if (tabbedPane.getSelectedIndex() == 0) {
			useStandaloneBN = false;
			password = new String(jPasswordField1.getPassword());

			if (jTextField1.getText().isEmpty() || jTextField1.getText().length() > max_str_size) {
				writeInfo("Please provide a valid hostname.");
				return false;			
			} else if (jTextField2.getText().isEmpty() || jTextField2.getText().length() > max_str_size) {
				writeInfo("Please provide a valid username.");
				return false;
			} else if (password.isEmpty() || password.length() > max_str_size) {
				writeInfo("Please provide a valid password.");
				return false;	
			} else if (jTextField3.getText().isEmpty() || jTextField3.getText().length() > max_str_size) {
				writeInfo("Please provide a valid tracker address.");
				return false;				    
			} 
			
			if (!jTextField4.getText().isEmpty()) {
				if (jTextField4.getText().length() <= max_str_port_size) {
					try {
						trackerPort = Integer.parseInt(jTextField4.getText());
					} catch (NumberFormatException e) {
						writeInfo("Please provide a valid port number.");
						return false;
					}
					
					if (trackerPort < 0 || trackerPort > max_port_size) {
						writeInfo("Please provide a valid port number.");
						return false;
					}
				} else {
					writeInfo("Please provide a valid port number.");
					return false;
				}
			} else {
				trackerPort = App.defaultTrackerAuthPort;
			}
				
			hostname = jTextField1.getText();	
			username = jTextField2.getText();			
			trackerAddress = jTextField3.getText();	
			return true;
															
		} else {
			useStandaloneBN = true;
			password = new String(jPasswordField1.getPassword());
			if (jTextField1.getText().length() > max_str_size) {
				writeInfo("Please provide a valid hostname.");
				return false;
			} else if (jTextField2.getText().length() > max_str_size) {
				writeInfo("Please provide a valid username.");
				return false;
			} else if (password.length() > max_str_size) {
				writeInfo("Please provide a valid password.");
				return false;	
			} else if (jTextField5.getText().isEmpty() || jTextField5.getText().length() > max_str_size) {
				writeInfo("Please provide a valid blue node address.");
				return false;				    
			} 
			
			if (!jTextField6.getText().isEmpty()) {
				if (jTextField6.getText().length() <= max_str_port_size) {
					try {
						blueNodePort = Integer.parseInt(jTextField6.getText());
					} catch (NumberFormatException e) {
						writeInfo("Please provide a valid port number.");
						return false;
					}
					
					if (blueNodePort < 0 || blueNodePort > max_port_size) {
						writeInfo("Please provide a valid port number.");
						return false;
					}
				} else {
					writeInfo("Please provide a valid port number.");
					return false;
				}
			} else {
				blueNodePort = App.defaultBlueNodeAuthPort;
			}		
			
			if (jTextField1.getText().isEmpty()) {
				//generate a pseudo-random hostname for the guy who just did not want to fill up a hostname!
				byte[] salt = new byte[4];
				SecureRandom ranGen = new SecureRandom();
				ranGen.nextBytes(salt);
				
				try {
					hostname = HashFunctions.MD5(new String(salt));
				} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
					e.printStackTrace();
					hostname = "nohost";
				}
				
			} else {
				hostname = jTextField1.getText();
			}
			
			if (password.isEmpty()) {
				password = "nopass";
			}
			
			if (jTextField2.getText().isEmpty()) {
				username = "nousername";
			} else {
				username = jTextField2.getText();
			}
						
			blueNodeAddress = jTextField5.getText();	
			return true;
		}		
	}

	public boolean getRecomendedBlueNode(String TrackerAddress, int TrackerPort) {
		writeInfo("Getting recomended BlueNode from tracker " + TrackerAddress + ":" + TrackerPort + " ...");

		InetAddress addr = SocketFunctions.getAddress(TrackerAddress);
		
		Socket socket;
		if (addr != null) {
			socket = SocketFunctions.absoluteConnect(addr, TrackerPort);
		} else {
			writeInfo("Tracker connection failed - host not found.");
			return false;
		}
		
		if (socket == null) {
			writeInfo("Tracker connection failed.");
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
