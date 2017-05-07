package kostiskag.unitynetwork.rednode;

import javax.swing.UIManager;

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
    public static LoginWindow login;
    
    public static void main(String args[]) {
        System.out.println("@Started main at " + Thread.currentThread().getName());

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
