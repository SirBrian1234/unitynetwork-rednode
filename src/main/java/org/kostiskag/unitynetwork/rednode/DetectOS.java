package org.kostiskag.unitynetwork.rednode;

public class DetectOS {

    int type = -1;

    public DetectOS() {
        if (isWindows()) {
            type=0;
        } else if (isMac()) {
            type=1;
        } else if (isUnix()) {
            type=2;
        } else {
            type=-1;
        }
    }

    public int getType() {
        return type;
    }
        
    public static boolean isWindows() {

        String os = System.getProperty("os.name").toLowerCase();
        // windows
        return (os.indexOf("win") >= 0);

    }

    public static boolean isMac() {

        String os = System.getProperty("os.name").toLowerCase();
        // Mac
        return (os.indexOf("mac") >= 0);

    }

    public static boolean isUnix() {

        String os = System.getProperty("os.name").toLowerCase();
        // linux or unix
        return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

    }

    public static  String getString() {
        if (isWindows()) {
            return "Windows";
        } else if (isMac()) {
            return "Macintosh";
        } else if (isUnix()) {
            return "Linux";
        } else {
            return "not known";
        }
    }        
}
