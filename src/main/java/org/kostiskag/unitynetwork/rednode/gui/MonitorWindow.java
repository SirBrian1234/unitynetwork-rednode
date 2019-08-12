package org.kostiskag.unitynetwork.rednode.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.kostiskag.unitynetwork.rednode.App;


/**
 *
 * @author Konstantinos Kagiampakis
 */
final class MonitorWindow extends JFrame {

	private static final long serialVersionUID = 9169713706944195555L;

	private int commandCounter;
	private int upConCounter;
    private int downConCounter;
	private int upCounter;
    private int downCounter;
    private boolean autoScrollDownCommands;
    private boolean autoScrollDownSocketSend;
    private boolean autoScrollDownSocketReceive;
    private boolean autoScrollDownIntRead;
    private boolean autoScrollDownIntWrite;

    //todo private
    public boolean loggedin = false;
    public String command;

    public MonitorWindow() {
        System.out.println("@Started MainWindow at " + Thread.currentThread().getName());
        initComponents();        

        jButton2.setEnabled(false);
        jButton9.setEnabled(false);
        jButton9 = new javax.swing.JButton();
        jButton9.setEnabled(false);
        jButton9.setBounds(0, 71, 103, 25);
        panel_1.add(jButton9);
        
                jButton9.setText("UPING");
                jButton8.setEnabled(false);
                jButton8 = new javax.swing.JButton();
                jButton8.setEnabled(false);
                jButton8.setBounds(110, 71, 117, 25);
                panel_1.add(jButton8);
                
                        jButton8.setText("UREFRESH");
                        jButton7.setEnabled(false);
                        jButton7 = new javax.swing.JButton();
                        jButton7.setEnabled(false);
                        jButton7.setBounds(234, 71, 97, 25);
                        panel_1.add(jButton7);
                        
                                jButton7.setText("Clear");
                                jButton7.addActionListener(new java.awt.event.ActionListener() {
                                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                                        jButton7ActionPerformed(evt);
                                    }
                                });
                        jButton8.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton8ActionPerformed(evt);
                            }
                        });
                jButton9.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        jButton9ActionPerformed(evt);
                    }
                });
        jButton3.setEnabled(false);
        jButton4.setEnabled(false);
        jButton5.setEnabled(false);
        jButton6.setEnabled(false);
        jButton10.setEnabled(false);
        jButton11.setEnabled(false);
    }
    
    private void initComponents() {

        commandw = new javax.swing.JPanel();
        jButton9 = new JButton();
        jButton8 =  new JButton();
        jButton7 = new JButton();
        jButton11 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();

        setTitle("Monitor Window");

        commandw.setBorder(javax.swing.BorderFactory.createTitledBorder("Command Window"));

        jButton11.setText("DIAGNOSTICS");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextArea3.setRows(5);
        jScrollPane3.setViewportView(jTextArea3);

        jButton3.setText("WHOAMI");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton10.setText("PING");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });
        
        btnExit = new JButton("EXIT");
        btnExit.setEnabled(false);
        btnExit.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		exitButtonActionPerformed(arg0);
        	}
        });
        
        chckbxKeepScrolledDown = new JCheckBox("keep scrolled down");
        chckbxKeepScrolledDown.addPropertyChangeListener(new PropertyChangeListener() {
        	public void propertyChange(PropertyChangeEvent evt) {
        		if (chckbxKeepScrolledDown.isSelected()) {
        			autoScrollDownCommands = true;
        		} else {
        			autoScrollDownCommands = false;
        		}
        	}
        });
        chckbxKeepScrolledDown.setSelected(true);

        javax.swing.GroupLayout commandwLayout = new javax.swing.GroupLayout(commandw);
        commandwLayout.setHorizontalGroup(
        	commandwLayout.createParallelGroup(Alignment.LEADING)
        		.addGroup(commandwLayout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(commandwLayout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
        				.addGroup(commandwLayout.createSequentialGroup()
        					.addComponent(jButton3)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(jButton11, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(jButton10)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(btnExit)
        					.addPreferredGap(ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
        					.addComponent(chckbxKeepScrolledDown)))
        			.addContainerGap())
        );
        commandwLayout.setVerticalGroup(
        	commandwLayout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(commandwLayout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(commandwLayout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jButton3)
        				.addComponent(jButton11)
        				.addComponent(jButton10)
        				.addComponent(btnExit)
        				.addComponent(chckbxKeepScrolledDown))
        			.addContainerGap())
        );
        commandw.setLayout(commandwLayout);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Interface"));

        jTextArea5.setColumns(20);
        jTextArea5.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextArea5.setRows(5);
        jScrollPane5.setViewportView(jTextArea5);

        jLabel10.setText("Write");
        
        panel_3 = new JPanel();

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7Layout.setHorizontalGroup(
        	jPanel7Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel7Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel7Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jScrollPane5, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
        				.addGroup(Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
        					.addComponent(jLabel10)
        					.addGap(0, 7, Short.MAX_VALUE))
        				.addComponent(panel_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE))
        			.addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
        	jPanel7Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel7Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jLabel10)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jScrollPane5, GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap())
        );
        panel_3.setLayout(null);
        jLabel12 = new javax.swing.JLabel();
        jLabel12.setBounds(0, 16, 151, 16);
        panel_3.add(jLabel12);
        
                jLabel12.setText("Number of packets written");
                jTextField12 = new javax.swing.JTextField();
                jTextField12.setBounds(156, 13, 65, 22);
                panel_3.add(jTextField12);
                jLabel15 = new javax.swing.JLabel();
                jLabel15.setBounds(239, 16, 75, 16);
                panel_3.add(jLabel15);
                
                        jLabel15.setText("Buffer Queue");
                        jTextField14 = new javax.swing.JTextField();
                        jTextField14.setBounds(319, 13, 97, 22);
                        panel_3.add(jTextField14);
                        jButton14 = new javax.swing.JButton();
                        jButton14.setBounds(281, 48, 135, 25);
                        panel_3.add(jButton14);
                        jButton14.setEnabled(false);
                        
                                jButton14.setText("Clear");
                                
                                checkBox_2 = new JCheckBox("keep scrolled down");
                                checkBox_2.addPropertyChangeListener(new PropertyChangeListener() {
                                	public void propertyChange(PropertyChangeEvent evt) {
                                		if (checkBox_2.isSelected()) {
                                			autoScrollDownIntWrite = true;
                                		} else {
                                			autoScrollDownIntWrite = false;
                                		}
                                	}
                                });
                                checkBox_2.setSelected(true);
                                checkBox_2.setBounds(0, 41, 139, 25);
                                panel_3.add(checkBox_2);
                                jButton14.addActionListener(new java.awt.event.ActionListener() {
                                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                                        jButton14ActionPerformed(evt);
                                    }
                                });
        jPanel7.setLayout(jPanel7Layout);

        jLabel11.setText("Read");

        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextArea4.setRows(5);
        jScrollPane4.setViewportView(jTextArea4);
        
        panel_2 = new JPanel();

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8Layout.setHorizontalGroup(
        	jPanel8Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel8Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel8Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jScrollPane4, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
        				.addGroup(Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
        					.addComponent(jLabel11)
        					.addGap(0, 34, Short.MAX_VALUE))
        				.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE))
        			.addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
        	jPanel8Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel8Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jLabel11)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jScrollPane4, GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE))
        );
        panel_2.setLayout(null);
        jLabel13 = new javax.swing.JLabel();
        jLabel13.setBounds(12, 16, 122, 16);
        panel_2.add(jLabel13);
        
                jLabel13.setText("Number packets read");
                jTextField11 = new javax.swing.JTextField();
                jTextField11.setBounds(139, 13, 65, 22);
                panel_2.add(jTextField11);
                jLabel14 = new javax.swing.JLabel();
                jLabel14.setBounds(232, 13, 75, 16);
                panel_2.add(jLabel14);
                
                        jLabel14.setText("Buffer Queue");
                        jTextField13 = new javax.swing.JTextField();
                        jTextField13.setBounds(312, 10, 99, 22);
                        panel_2.add(jTextField13);
                        jButton15 = new javax.swing.JButton();
                        jButton15.setBounds(290, 44, 121, 25);
                        panel_2.add(jButton15);
                        jButton15.setEnabled(false);
                        
                                jButton15.setText("Clear");
                                
                                checkBox_3 = new JCheckBox("keep scrolled down");
                                checkBox_3.addPropertyChangeListener(new PropertyChangeListener() {
                                	public void propertyChange(PropertyChangeEvent evt) {
                                		if (checkBox_3.isSelected()) {
                                			autoScrollDownIntRead = true;
                                		} else {
                                			autoScrollDownIntRead = false;
                                		}
                                	}
                                });
                                checkBox_3.setSelected(true);
                                checkBox_3.setBounds(12, 44, 139, 25);
                                panel_2.add(checkBox_3);
                                jButton15.addActionListener(new java.awt.event.ActionListener() {
                                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                                        jButton15ActionPerformed(evt);
                                    }
                                });
        jPanel8.setLayout(jPanel8Layout);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5Layout.setHorizontalGroup(
        	jPanel5Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel5Layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(jPanel8, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        				.addComponent(jPanel7, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE))
        			.addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
        	jPanel5Layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(Alignment.LEADING, jPanel5Layout.createSequentialGroup()
        			.addComponent(jPanel7, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jPanel8, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
        );
        jPanel5.setLayout(jPanel5Layout);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Data"));

        jLabel1.setText("Receive");

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);
        
        JPanel panel = new JPanel();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2Layout.setHorizontalGroup(
        	jPanel2Layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(jPanel2Layout.createSequentialGroup()
        			.addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(jPanel2Layout.createSequentialGroup()
        					.addGap(23)
        					.addComponent(jLabel1))
        				.addGroup(jPanel2Layout.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
        				.addGroup(jPanel2Layout.createSequentialGroup()
        					.addContainerGap()
        					.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)))
        			.addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
        	jPanel2Layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(jPanel2Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jLabel1)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 237, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
        );
        panel.setLayout(null);
        jLabel9 = new javax.swing.JLabel();
        jLabel9.setBounds(0, 13, 75, 16);
        panel.add(jLabel9);
        
                jLabel9.setText("Buffer Queue");
                jTextField3 = new javax.swing.JTextField();
                jTextField3.setBounds(80, 10, 65, 22);
                panel.add(jTextField3);
                
                checkBox = new JCheckBox("keep scrolled down");
                checkBox.addPropertyChangeListener(new PropertyChangeListener() {
                	public void propertyChange(PropertyChangeEvent evt) {
                		if (checkBox.isSelected()) {
                			autoScrollDownSocketReceive = true;
                		} else {
                			autoScrollDownSocketReceive = false;
                		}
                	}
                });
                checkBox.setBounds(178, 9, 139, 25);
                panel.add(checkBox);
                checkBox.setSelected(true);
                jButton5 = new javax.swing.JButton();
                jButton5.setBounds(0, 39, 98, 25);
                panel.add(jButton5);
                
                        jButton5.setText("DPING");
                        jButton6 = new javax.swing.JButton();
                        jButton6.setBounds(110, 39, 93, 25);
                        panel.add(jButton6);
                        
                                jButton6.setText("DREFRESH");
                                jButton4 = new javax.swing.JButton();
                                jButton4.setBounds(218, 39, 99, 25);
                                panel.add(jButton4);
                                
                                        jButton4.setText("Clean");
                                        jButton4.addActionListener(new java.awt.event.ActionListener() {
                                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                                jButton4ActionPerformed(evt);
                                            }
                                        });
                                jButton6.addActionListener(new java.awt.event.ActionListener() {
                                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                                        jButton6ActionPerformed(evt);
                                    }
                                });
                        jButton5.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton5ActionPerformed(evt);
                            }
                        });
        jPanel2.setLayout(jPanel2Layout);

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jLabel2.setText("Send");
        
        panel_1 = new JPanel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1Layout.setHorizontalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(jScrollPane2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
        				.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
        				.addComponent(jLabel2, Alignment.LEADING))
        			.addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jLabel2)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))
        );
        panel_1.setLayout(null);
        jLabel16 = new javax.swing.JLabel();
        jLabel16.setBounds(0, 13, 75, 16);
        panel_1.add(jLabel16);
        
                jLabel16.setText("Buffer Queue");
                jTextField15 = new javax.swing.JTextField();
                jTextField15.setBounds(87, 10, 74, 22);
                panel_1.add(jTextField15);
                
                checkBox_1 = new JCheckBox("keep scrolled down");
                checkBox_1.addPropertyChangeListener(new PropertyChangeListener() {
                	public void propertyChange(PropertyChangeEvent evt) {
                		if (checkBox_1.isSelected()) {
                			autoScrollDownSocketSend = true;
                		} else {
                			autoScrollDownSocketSend = false;
                		}
                	}
                });
                checkBox_1.setBounds(192, 9, 139, 25);
                panel_1.add(checkBox_1);
                checkBox_1.setSelected(true);
                jTextField10 = new javax.swing.JTextField();
                jTextField10.setBounds(0, 40, 80, 22);
                panel_1.add(jTextField10);
                jTextField4 = new javax.swing.JTextField();
                jTextField4.setBounds(87, 40, 174, 22);
                panel_1.add(jTextField4);
                jButton2 = new javax.swing.JButton();
                jButton2.setBounds(268, 39, 63, 25);
                panel_1.add(jButton2);
                
                        jButton2.setText("Send");
                        jButton2.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                jButton2ActionPerformed(evt);
                            }
                        });
                
                        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
                            public void keyPressed(java.awt.event.KeyEvent evt) {
                                SendText(evt);
                            }
                            public void keyTyped(java.awt.event.KeyEvent evt) {
                                SendText(evt);
                            }
                        });
        jPanel1.setLayout(jPanel1Layout);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(commandw, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(commandw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        App.login.connection.sendMessage(jTextField10.getText(), jTextField4.getText());
        jTextField4.setText("");
    }

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {
       jTextArea1.setText("");
    }
    
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
    	App.login.connection.giveCommand("WHOAMI");       
    }
    
    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {
        App.login.connection.giveCommand("PING");
    }

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {
        App.login.connection.giveCommand("UPING");
    }
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {
        App.login.connection.giveCommand("DPING");
    }
    
    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {
        App.login.connection.urefresh();
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {
        App.login.connection.drefresh();
    }
    
    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {
        App.login.connection.giveCommand("DIAGNOSTICS");
    }
    
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        App.login.connection.giveCommand("EXIT");
    }

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {
        clearUp();
    }

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {
        jTextArea5.setText("");
    }

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {
        jTextArea4.setText("");
    }

    private void SendText(java.awt.event.KeyEvent evt) {
        if (loggedin == true) {
            int key = evt.getKeyCode();
            if (key == KeyEvent.VK_ENTER) {
                App.login.connection.sendMessage(jTextField10.getText(), jTextField4.getText());
                jTextField4.setText("");
            }
        }
    }

    public void setLogedOut() {       
    	btnExit.setEnabled(false);
        jButton2.setEnabled(false);
        jButton3.setEnabled(false);
        jButton4.setEnabled(false);
        jButton5.setEnabled(false);
        jButton6.setEnabled(false);
        jButton7.setEnabled(false);
        jButton8.setEnabled(false);
        jButton9.setEnabled(false);
        jButton10.setEnabled(false);
        jButton11.setEnabled(false);
        jButton14.setEnabled(false);
        jButton15.setEnabled(false);
    }

    public void setLogedIn() {    
    	btnExit.setEnabled(true);
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
        jButton4.setEnabled(true);
        jButton5.setEnabled(true);
        jButton6.setEnabled(true);
        jButton7.setEnabled(true);
        jButton8.setEnabled(true);
        jButton9.setEnabled(true);
        jButton10.setEnabled(true);
        jButton11.setEnabled(true);
        jButton14.setEnabled(true);
        jButton15.setEnabled(true);
    }

    public void toggleVisible() {
        if (this.isVisible()) {
            this.setVisible(false);
        } else {
            this.setVisible(true);
        }
    }
    
    public void writeToCommands(String info) {
    	jTextArea3.append(info + "\n");
    	commandCounter++;
        if (commandCounter > 1000) {
        	commandCounter = 0;
        	jTextArea3.append("");
        }
        if (autoScrollDownCommands) {
        	jTextArea3.select(jTextArea3.getHeight() + 10000, 0);
        }
    }
    
    public void clearCommands() {
        jTextArea3.setText("");
    }

    public void writeToConnectionUp(String info) {
        jTextArea2.append(info + "\n");
        upConCounter++;
        if (upConCounter > 600) {
        	upConCounter = 0;
            jTextArea2.setText("");
        }
        if (autoScrollDownSocketSend) {
        	jTextArea2.select(jTextArea2.getHeight() + 10000, 0);
        }
    }

    public void clearUp() {
        jTextArea2.setText("");
    }
    
    public void updateConUpBufferQueue(int len) {
    	jTextField3.setText(len+"");
    }

    public void writeToConnectionDown(String info) {
        jTextArea1.append(info + "\n");
        downConCounter++;
        if (downConCounter > 600) {
            downConCounter = 0;
            jTextArea1.setText("");
        }
        if (autoScrollDownSocketReceive) {
        	jTextArea1.select(jTextArea1.getHeight() + 10000, 0);
        }
    }
    
    public void clearDown() {
        jTextArea1.setText("");
    }
    
    public void updateConDownBufferQueue(int len) {
    	jTextField15.setText(len+"");
    }
    
    public void writeToIntRead(String info) {
        jTextArea4.append(info + "\n");
        downCounter++;
        if (downCounter > 600) {
            downCounter = 0;
            jTextArea4.setText("");
        }
        if (autoScrollDownIntRead) {
        	jTextArea4.select(jTextArea4.getHeight() + 10000, 0);
        }
    }
    
    public void clearIntRead() {
        jTextArea4.setText("");
    }
    
    public synchronized void updateIntReadNumber(int num) {
        jTextField11.setText(""+num);
    }
    
    public void clearIntReadNumber() {
        jTextField11.setText("");
    }
    
    public synchronized void updateIntReadBufferNumber(int num) {
        jTextField13.setText(""+num);
    }
    
    public void clearIntReadBufferNumber() {
        jTextField13.setText("");
    }
    
    public synchronized void writeToIntWrite(String info) {
        jTextArea5.append(info + "\n");
        upCounter++;
        if (upCounter > 600) {
        	upCounter = 0;
            jTextArea5.setText("");
        }
        if (autoScrollDownIntWrite) {
        	jTextArea5.select(jTextArea5.getHeight() + 10000, 0);
        }
    }
    
    public void clearIntWrite() {
        jTextArea5.setText("");
    }
    
    public synchronized void updateIntWriteNumber(int num) {
        jTextField12.setText(num+"");
    }
    
    public void clearIntWriteNumber() {
        jTextField12.setText("");
    }
    
    public synchronized void updateIntWriteBufferNumber(int num) {
        jTextField14.setText(num+"");
    }
    
    public void clearIntWriteBufferNumber() {
        jTextField14.setText("");
    }
    
    public void clearWindow() {
        jTextArea2.setText("");
        jTextArea1.setText("");
        jTextArea4.setText("");
        jTextArea5.setText("");                
        jTextField3.setText("");
        jTextField15.setText("");
        jTextField13.setText("");
        jTextField14.setText("");
    }

    private JPanel commandw;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel5;
    private JPanel jPanel6;
    private JPanel jPanel7;
    private JPanel jPanel8;
    private JPanel panel_2;
    private JPanel panel_3;
    private JPanel panel_1;

    private JButton btnExit;
    private JButton jButton10;
    private JButton jButton11;
    private JButton jButton14;
    private JButton jButton15;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;
    private JButton jButton6;
    private JButton jButton7;
    private JButton jButton8;
    private JButton jButton9;

    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel15;
    private JLabel jLabel16;
    private JLabel jLabel2;
    private JLabel jLabel9;

    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JScrollPane jScrollPane4;
    private JScrollPane jScrollPane5;

    private JTextArea jTextArea1;
    private JTextArea jTextArea2;
    private JTextArea jTextArea3;
    private JTextArea jTextArea4;
    private JTextArea jTextArea5;

    private JTextField jTextField10;
    private JTextField jTextField11;
    private JTextField jTextField12;
    private JTextField jTextField13;
    private JTextField jTextField14;
    private JTextField jTextField15;
    private JTextField jTextField3;
    private JTextField jTextField4;

    private JCheckBox chckbxKeepScrolledDown;
    private JCheckBox checkBox;
    private JCheckBox checkBox_1;
    private JCheckBox checkBox_2;
    private JCheckBox checkBox_3;

}
