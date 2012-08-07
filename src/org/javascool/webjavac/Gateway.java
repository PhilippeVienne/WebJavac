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

import netscape.javascript.JSObject;
import org.javascool.core.Java2Class;
import org.javascool.core.Jvs2Java;
import org.javascool.tools.FileManager;
import org.javascool.tools.SystemOutputController;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.applet.Applet;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * JS-Java Gate for WebJavac.
 *
 * @author Philippe VIENNE
 */
public class Gateway extends Applet implements SystemOutputController.SystemOutputListener {

    SystemOutputController systemOutputController = new SystemOutputController();

    /** Send a print out to browser.
     * @param out The data to print into console
     */
    @Override
    public void print(String out) {
        JSONObject r = new JSONObject();
        r.put("print", out);
        JSObject window=JSObject.getWindow(this);
        /*try{
            window.eval("webconsole.print("+r.toJSONString()+");");
        }catch (Throwable e){ */
            try{

                window.eval("webconsole.print("+r.toJSONString()+");");
            }catch(Throwable e2){}
        //}
    }

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


                            String javaCode = getJVSTranslator().translate(code), javaClass = getJVSTranslator().getClassName();
                            File tmpDir = FileManager.createTempDir("jvs-compile-" + javaClass);
                            String javaFile = tmpDir.getAbsolutePath() + File.separatorChar + javaClass + ".java";
                            FileManager.save(javaFile, javaCode);
                            String[] java=new String[1], path=new String[1];
                            java[0]=javaFile;
                            path[0]=jar();
                            boolean success = Java2Class.compile(java,false,path);

                            JSONObject r = new JSONObject();
                            r.put("success", success);
                            r.put("compiledClass", javaFile.replace(".java", ".class"));
                            r.put("console", systemOutputController.getResult());
                            return r.toString();
                        }
                    }
            );
        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    public void execInPrivateThread(final String location){
        assertSafeUsage();
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String r=exec(location);
                    if(!r.equals("")){
                        RuntimeException exception=new RuntimeException(r);
                        popException(exception);
                    }
                } catch (Exception e) {
                    popException(e);
                }
            }
        },"JVSExecThread");
        t.start();
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
                            Runnable clazz = Java2Class.load(location);
                            try {
                                clazz.run();
                            } catch (Throwable e) {
                                return e.toString();
                            }
                            return "";
                        }

                    }
            );
        } catch (Exception e) {
            popException(e);
            throw e;
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

    /** Retrouve le chemin du jar courant.
     * @return Le chemin du jar
     * @throws RuntimeException lorsque l'application n'a pas été démarré depuis un jar
     */
    public static String jar() {
        if(javascoolJar != null) {
            return javascoolJar;
        }
        String url = Gateway.class.getResource("").toString().replaceFirst("jar:file:([^!]*)!.*", "$1");
        System.err.println("Notice: javascool url is " + url);
        if(url.endsWith(".jar")) {
            try {
                String jar = URLDecoder.decode(url, "UTF-8");
                if(new File(jar).exists()) {
                    return javascoolJar = jar;
                }
                // Ici on essaye tous les encodages possibles pour essayer de détecter javascool
                {
                    jar = URLDecoder.decode(url, Charset.defaultCharset().name());
                    if(new File(jar).exists()) {
                        javascoolJarEnc = Charset.defaultCharset().name();
                        return jar;
                    }
                    for(String enc : Charset.availableCharsets().keySet()) {
                        jar = URLDecoder.decode(url, enc);
                        if(new File(jar).exists()) {
                            javascoolJarEnc = enc;
                            System.err.println("Notice: javascool file " + jar + " correct decoding as " + enc);
                            return javascoolJar = jar;
                        } else {
                            System.err.println("Notice: javascool file " + jar + " wrong decoding as " + enc);
                        }
                    } throw new RuntimeException("Il y a un bug d'encoding sur cette plate forme");
                }
            } catch(UnsupportedEncodingException ex) { throw new RuntimeException("Spurious defaultCharset: this is a caveat");
            }
        } else { return "";
        }
        // throw new RuntimeException("Java's cool n'a pas été démarré depuis un Jar");
    }
    private static String javascoolJar = null, javascoolJarEnc = null;

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
        systemOutputController.startListening(this);
    }

    @Override
    public void stop(){
        systemOutputController.stopListening();
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
