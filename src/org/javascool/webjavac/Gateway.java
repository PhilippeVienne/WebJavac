/*
 * $file.name
 *     Copyright (C) 2012  Philippe VIENNE
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.javascool.webjavac;

import org.javascool.tools.FileManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.applet.Applet;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * JS-Java Gate for WebJavac.
 *
 * @author Philippe VIENNE
 */
public class Gateway extends Applet {

    /* ================================================================================================================
    *  ================================================================================================================
    *
    *        API Section
    *             All public functions below are accessible from JS
    *
    *  ================================================================================================================
    *  ================================================================================================================
    */

    /**
     * Read a file on a location.
     * Replace the FileReader API for text files (code e.g.)
     *
     * @param location Where have I got to read your file ?
     * @return The file content
     * @see FileManager#load(String)
     */
    public String load(final String location) throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<String>() {
                        public String run() {
                            return FileManager.load(location);
                        }
                    }
            );
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /* ================================================================================================================
    *  ================================================================================================================
    *
    *        Error management Section
    *             Debug functions fo JS and Java
    *
    *  ================================================================================================================
    *  ================================================================================================================
    */

    /**
     * Pop the last Java exception which happened
     *
     * @return The Exception
     */
    public Exception popException() {
        if (lastError != null) {
            Exception e = lastError;
            lastError = null;
            return e;
        }
        return null;
    }

    private Exception lastError;

    private void popException(Exception e) {
        lastError = e;
    }

    /* ================================================================================================================
    *  ================================================================================================================
    *
    *        SECURITY SECTION
    *             All variables and code change below will affect the application's security
    *
    *  ================================================================================================================
    *  ================================================================================================================
    */

    /**
     * Create a File object from the Path.
     *
     * @param path
     * @return
     */
    private File getFile(final String path) {
        return AccessController.doPrivileged(
                new PrivilegedAction<File>() {
                    public File run() {
                        return new File(path);
                    }
                }
        );
    }

    /**
     * Security flag.
     * If the applet have to be locked for security reasons, put this variable to true.
     */
    private boolean appletLocked = false;

    /**
     * Initialize the applet.
     * This function will check if we are in a safe environment.
     * NOTE: You can edit it to adapt to your own environment
     */
    @Override
    public void init() {
        if (!getCodeBase().getProtocol().equals("file")) {
            appletLocked = true;
        }
    }

    /**
     * Message non-spam flag.
     * This flag is used to be sure that we show the security message only one time.
     */
    private boolean showMessage = true;

    /**
     * Perform a security assertion.
     * If the applet is locked, this function will interrupt the current thread.
     *
     * @throws SecurityException
     */
    private void assertSafeUsage() {
        if (appletLocked) {
            if (showMessage) {
                JOptionPane.showMessageDialog(this, "This website (" + getCodeBase().getHost() + ") tried to hack" +
                        " your computer by accessing to the local file system (Attack stopped)", "Error",
                        JOptionPane.ERROR_MESSAGE);
                showMessage = false;
            }
            SecurityException e= new SecurityException("This website is not authorized to use this applet");
            popException(e);
            throw e;
        } else {
        }
    }

}
