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

import org.javascool.core.Java2Class;
import org.javascool.core.Jvs2Java;
import org.javascool.tools.FileManager;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.applet.Applet;
import java.io.*;
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
     * Compile an JVS Code.
     *
     * @param code What have we to compile
     * @return A compilation's JSon describer :
     *         {success:true,compiledClass:"path to compiled .class"}
     * @see FileManager#load(String)
     */
    public String compile(final String code) throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<String>() {
                        public String run() {
                            //String javaFile=getTmpFile(code,"JvsCode",".jvs")
                            SystemInputOutputController sioc = new SystemInputOutputController();
                            sioc.startListening();
                            String javaCode = getJVSTranslator().translate(code), javaClass = getJVSTranslator().getClassName();
                            File tmpDir = FileManager.createTempDir("jvs-compile-" + javaClass);
                            String javaFile = tmpDir.getAbsolutePath() + File.separatorChar + javaClass + ".java";
                            FileManager.save(javaFile, javaCode);
                            boolean success = Java2Class.compile(javaFile);
                            sioc.stopListening();
                            JSONObject r = new JSONObject();
                            r.put("success", success);
                            r.put("compiledClass", javaFile.replace(".java", ".class"));
                            r.put("console", sioc.getResult());
                            return r.toString();
                        }
                    }
            );
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    /**
     * Exec a compiled Runnable.
     *
     * @param location Which class
     * @return The System.out and System.err threads
     * @see FileManager#load(String)
     */
    public String exec(final String location) throws Exception {
        assertSafeUsage();
        try {
            return AccessController.doPrivileged(
                    new PrivilegedAction<String>() {
                        private String result = "";

                        public String run() {
                            SystemInputOutputController sioc = new SystemInputOutputController();
                            sioc.startListening();
                            Runnable clazz = Java2Class.load(location);
                            clazz.run();
                            sioc.stopListening();
                            return sioc.getResult();
                        }

                    }
            );
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    class SystemInputOutputController {
        private String result = "";
        private PrintStream oldOut = null, oldErr = null;
        private boolean listening = false;

        public void startListening() {
            //this.oldErr=System.err;
            this.oldOut = System.out;
            result = "";
            this.redirectSystemStreams();
            listening = true;
        }

        public void stopListening() {
            System.setOut(this.oldOut);
            //System.setErr(this.oldErr);
            this.oldOut = this.oldErr = null;
            listening = false;
        }

        public String getResult() {
            return result;
        }

        private void updateTextPane(final String text) {
            result += text;
        }

        private void redirectSystemStreams() {
            System.setOut(new PrintStream(new SystemOutputStream(SystemOutputStream.OUT), true));
            //System.setErr(new PrintStream(new SystemOutputStream(SystemOutputStream.ERR), true));
        }

        private class SystemOutputStream extends OutputStream {

            final static int OUT = 0;
            final static int ERR = 1;

            private int type = 0;

            public SystemOutputStream(int outType) {
                super();
                type = outType;
            }

            @Override
            public void write(final int b) throws IOException {
                updateTextPane(String.valueOf((char) b));
                switch (type) {
                    case OUT:
                        if (oldOut != null) oldOut.write(b);
                    case ERR:
                        if (oldErr != null) oldErr.write(b);
                }
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                updateTextPane(new String(b, off, len));
                switch (type) {
                    case OUT:
                        if (oldOut != null) oldOut.write(b, off, len);
                    case ERR:
                        if (oldErr != null) oldErr.write(b, off, len);
                }
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        }
    }

    /**
     * Return the Jvs2Java translator.
     * Call this function only form an AccessController.doPrivileged()
     */
    private Jvs2Java getJVSTranslator() {
        if (translator == null) {
            translator = new Jvs2Java();
        }
        return translator;
    }

    private Jvs2Java translator;

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
     * Create a temporary File object with a content
     *
     * @param content The data to write into
     * @param suffix  The extension ( '.java' e.g.)
     * @param prefix  The prefix for the file
     * @return The path of file
     */
    private String getTmpFile(final String content, final String suffix, final String prefix) {
        return AccessController.doPrivileged(
                new PrivilegedAction<String>() {
                    public String run() {
                        try {
                            // Create temp file.
                            File temp = File.createTempFile("javaTempFile", ".java");

                            // Delete temp file when program exits.
                            temp.deleteOnExit();

                            if (content != null) {
                                // Write to temp file
                                BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                                out.write(content);
                                out.close();
                            }

                            return temp.toString();
                        } catch (IOException e) {
                            throw new IllegalStateException("Can't create a tempory file", e);
                        }
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
            SecurityException e = new SecurityException("This website is not authorized to use this applet");
            popException(e);
            throw e;
        } else {
        }
    }

}
