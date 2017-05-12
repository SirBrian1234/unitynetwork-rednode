package kostiskag.unitynetwork.rednode.gui;

import javax.swing.JFrame;

public class trackerKeyGUI {

	private JFrame frame;

	/**
	 * Create the application.
	 */
	public trackerKeyGUI(String address, int port) {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void setVisible() {
		frame.setVisible(true);
	}
}
