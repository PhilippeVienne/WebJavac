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
import org.javascool.tools.JavaGate;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * JS-Java Gate for WebJavac.
 *
 * @author Philippe VIENNE
 */
public class Gateway extends JavaGate {

    /**
     * Compile an JVS Code.
     *
     * @param code What have we to compile
     * @return A compilation's JSon describer :
     *         {success:true,compiledClass:"path to compiled .class"}
     * @see FileManager#load(String)
     */
    public void compile(final String code) throws Exception {
        assertSafeUsage();
        try {
            runInNewThreadWithAllRights(new Runnable() {
                @Override
                public void run() {
                    String javaCode = getJVSTranslator().translate(code), javaClass = getJVSTranslator().getClassName();
                    File tmpDir = FileManager.createTempDir("jvs-compile-" + javaClass);
                    String javaFile = tmpDir.getAbsolutePath() + File.separatorChar + javaClass + ".java";
                    FileManager.save(javaFile, javaCode);
                    String[] java = new String[1], path = new String[1];
                    java[0] = javaFile;
                    path[0] = jar();
                    boolean success = Java2Class.compile(java, false, path);
                    JSONObject r = new JSONObject();
                    r.put("success", success);
                    r.put("compiledClass", javaFile.replace(".java", ".class"));
                    r.put("console", systemOutputController.getResult());
                    jsGate.triggerOff("javascool.compiled", r);
                }
            });

        } catch (Exception e) {
            popException(e);
            throw e;
        }
    }

    public void execInPrivateThread(final String location) {
        assertSafeUsage();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String r = exec(location);
                    if (!r.equals("")) {
                        RuntimeException exception = new RuntimeException(r);
                        popException(exception);
                    }
                } catch (Exception e) {
                    popException(e);
                }
            }
        }, "JVSExecThread");
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

    /**
     * Retrouve le chemin du jar courant.
     *
     * @return Le chemin du jar
     * @throws RuntimeException lorsque l'application n'a pas été démarré depuis un jar
     */
    public static String jar() {
        if (javascoolJar != null) {
            return javascoolJar;
        }
        String url = Gateway.class.getResource("").toString().replaceFirst("jar:file:([^!]*)!.*", "$1");
        System.err.println("Notice: javascool url is " + url);
        if (url.endsWith(".jar")) {
            try {
                String jar = URLDecoder.decode(url, "UTF-8");
                if (new File(jar).exists()) {
                    return javascoolJar = jar;
                }
                // Ici on essaye tous les encodages possibles pour essayer de détecter javascool
                {
                    jar = URLDecoder.decode(url, Charset.defaultCharset().name());
                    if (new File(jar).exists()) {
                        javascoolJarEnc = Charset.defaultCharset().name();
                        return jar;
                    }
                    for (String enc : Charset.availableCharsets().keySet()) {
                        jar = URLDecoder.decode(url, enc);
                        if (new File(jar).exists()) {
                            javascoolJarEnc = enc;
                            System.err.println("Notice: javascool file " + jar + " correct decoding as " + enc);
                            return javascoolJar = jar;
                        } else {
                            System.err.println("Notice: javascool file " + jar + " wrong decoding as " + enc);
                        }
                    }
                    throw new RuntimeException("Il y a un bug d'encoding sur cette plate forme");
                }
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException("Spurious defaultCharset: this is a caveat");
            }
        } else {
            return "";
        }
        // throw new RuntimeException("Java's cool n'a pas été démarré depuis un Jar");
    }

    private static String javascoolJar = null, javascoolJarEnc = null;

}
