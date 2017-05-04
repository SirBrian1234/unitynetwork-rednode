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

import java.util.logging.Level;
import java.util.logging.Logger;

import kostiskag.unitynetwork.rednode.App;

/**
 * The TunTap class for linux.
 * @author Wolfgang  Ginolas
 */
public class TunTapLinux extends TunTap {
    static {
    	String osArch = System.getProperty("os.arch");
    	System.out.println(osArch);
		try {
			if (osArch.equals("intel64") || osArch.equals("amd64")) {
			    loadLib("clib/libTunTapLinux64.so");
			} else {
				loadLib("clib/libTunTapLinux.so");
			}
			App.login.connection.setLibError(false);
		} catch (Throwable e) {
			App.login.connection.setLibError(true);
			Logger.getLogger("").log(Level.SEVERE, "Could not load libTunTapLinux.so", e);			
		}
    }

    private int fd;
    private String dev;    

	/**
	 * Create a new TunTapLinux
	 * @throws java.lang.Exception
	 */
    public TunTapLinux() throws Exception {
        if (1==openTun()) throw new Exception("Could not open '/dev/net/tun!'\n" +
				"Please run this application as root.");
        init();
    }
    
    @Override
    public String getDev() {
        return dev;
    }
    
    private native int openTun();
    
    @Override
    public native void close();
    
    @Override
    public native void write(byte[] b, int len);
    
    @Override
    public native int read(byte[] b);
    
    @Override
    public void setIP(String ip, String subnetmask) {
	super.setIP(ip, subnetmask);
    	try {
    		Process p = Runtime.getRuntime().exec("ifconfig "+dev+" "+ip+" netmask "+subnetmask);
			Logger.getLogger("").log(Level.INFO, "IP set successfully ("+p.waitFor()+")");
    	} catch (Exception e) {
			Logger.getLogger("").log(Level.WARNING, "Could not set IP!", e);
    	}
    }
    
    public void init() {	
    	try {
    		Process p = Runtime.getRuntime().exec("ifconfig "+dev+" up");
			Logger.getLogger("").log(Level.INFO, "Interface Up ("+p.waitFor()+")");
    	} catch (Exception e) {
			Logger.getLogger("").log(Level.WARNING, "Could not open!", e);
    	}
    }
}
