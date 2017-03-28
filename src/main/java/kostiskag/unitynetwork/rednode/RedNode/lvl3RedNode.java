package kostiskag.unitynetwork.rednode.RedNode;

import kostiskag.unitynetwork.rednode.GUI.LoginWindow;
import javax.swing.UIManager;

/**
 *
 * @author kostis
 *
 */
public class lvl3RedNode {

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
