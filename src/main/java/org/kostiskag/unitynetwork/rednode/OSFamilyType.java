package org.kostiskag.unitynetwork.rednode;

public class OSFamilyType {

    public enum OSType {
        WINDOWS("Windows"),
        UNIX("Unix"),
        MAC("Mac"),
        OTHER("Unknown");

        String value;

        OSType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static OSType getOSFamilyType() {
        if (isWindows()) {
            return OSType.WINDOWS;
        } else if (isMac()) {
            return OSType.MAC;
        } else if (isUnix()) {
            return OSType.UNIX;
        } else {
            return OSType.OTHER;
        }
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.contains("win"));
    }

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.contains("mac"));
    }

    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.contains("nix") || os.contains("nux"));
    }
}
