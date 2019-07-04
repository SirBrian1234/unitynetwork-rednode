/*
 Copyright 2008, 2009 Wolfgang Ginolas

 This file is part of P2PVPN.

 P2PVPN is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Foobar is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.p2pvpn.tuntap;

import org.kostiskag.unitynetwork.rednode.App;

/**
 * The TunTap class for Windows
 *
 * @author Wolfgang Ginolas
 */
public class TunTapWindows extends TunTap {

    private long cPtr;
    private String dev;

    /**
     * Create a new TunTapWindows.
     *
     * @throws java.lang.Exception
     */
    public TunTapWindows() {
        String osArch = System.getProperty("os.arch");
        int erg;
        try {
            if (osArch.equals("intel64") || osArch.equals("amd64")) {
                loadLib("clib\\libTunTapWindows64.dll");
            } else {
                loadLib("clib\\libTunTapWindows.dll");
            }
            App.login.connection.setLibError(false);
        } catch (Throwable e) {
            App.login.writeInfo("library error, check if clib folder exists in your application directory and if it has valid files");
            erg = -1;
            App.login.connection.setLibError(true);
            e.printStackTrace();
            return;
        }
        erg = openTun();

    }

    public String getDev() {
        return dev;
    }

    private native int openTun();

    public native void close();

    public native void write(byte[] b, int len);

    public native int read(byte[] b);
}
