/**************************************************************
 * Philippe VIENNE, Copyright (C) 2011.  All rights reserved. *
 **************************************************************/
package org.javascool.core;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.regex.Pattern;

/**
 * Définit le mécanisme de compilation en ligne d'un code Java et du chargement de la classe obtenue.
 * <p>Note: utilise un sous ensemble du <tt>tools.jar</tt> de la JDK1.6 de SUN de la JDK appelé ici <tt>javac.jar</tt> qui doit être dans le CLASSPATH.</p>
 *
 * @serial exclude
 * @see <a href="Java2Class.java.html">code source</a>
 */
public class Java2Class {
    // @factory
    private Java2Class() {
    }

    /**
     * Compile dans le système de fichier local, un code source Java.
     * <p>Les fichiers <tt>.class</tt> sont générés sur place.</p>
     * <p>Les erreurs de compilation sont affichées dans la console.</p>
     * <p>Les erreurs les plus courantes sont reformulées en français.</p>
     * <p>Les portions de code de la forme <tt>/*<i>code-jvs</i> @&lt;nojavac*</tt><tt>/<i>code-java-derive</ii>/*@nojavac>*</tt><tt>/</tt> issus de pseudo-code retraduit en Java par un <a href="Translator.html">Translator</a> sont traités pour que seul le <i>code-jvs</i> soit affiché en cas d'erreur de syntaxe.</p>
     *
     * @param javaFile  Le nom du fichier à compiler. Un tableau de noms de fichiers peut être donné.
     * @param allErrors Renvoie toutes les erreur si true, sinon uniquement la première erreur (par défaut).
     * @return La valeur true en cas de succès, false si il y a des erreurs de compilation.
     * @throws RuntimeException Si une erreur d'entrée-sortie s'est produite lors de la compilation.
     */
    public static boolean compile(String javaFile, boolean allErrors) {
        String javaFiles[] = {javaFile};
        return compile(javaFiles, allErrors);
    }

    /**
     * @see #compile(String, boolean)
     */
    public static boolean compile(String javaFile) {
        return compile(javaFile, false);
    }

    /**
     * @see #compile(String, boolean)
     */
    public static boolean compile(String javaFiles[]) {
        return compile(javaFiles, false);
    }

    /**
     * @see #compile(String, boolean)
     */
    public static boolean compile(String javaFiles[], boolean allErrors) {
        if (javaFiles.length == 0) {
            return false;
        }

        // Appel du compilateur SUN par sa méthode main
        int options = 2;
        String args[] = new String[options + javaFiles.length];
        args[0] = "-g";
        args[1] = "-nowarn";
        System.arraycopy(javaFiles, 0, args, options, javaFiles.length);
        StringWriter out = new StringWriter();
        Method javac;
        try {
            javac = Class.forName("com.sun.tools.javac.Main").
                    getDeclaredMethod("compile", Class.forName("[Ljava.lang.String;"), Class.forName("java.io.PrintWriter"));
        } catch (Exception e) {
            throw new IllegalStateException("Impossible d'accéder au compilateur javac : " + e);
        }
        try {
            javac.invoke(null, (Object) args, new PrintWriter(out));
        } catch (Exception e) {
            throw new IllegalStateException("Erreur système lors du lancement du compilateur javac : " + e);
        }
        // Traitement du message de sortie
        {
            String sout = out.toString().trim();
            // Coupure à la première erreur
            if ((sout.indexOf("^") != -1) && !allErrors) {
                sout = sout.substring(0, sout.indexOf("^") + 1);
            }
            if (javaFiles.length > 1) {
                // Remplacement des chemins des sources par leur simple nom
                for (String javaFile : javaFiles)
                    sout = sout.replaceAll(Pattern.quote(new File(javaFile).getParent() + File.separator), "\n");
                // Explicitation du numéro de ligne
                for (String javaFile : javaFiles)
                    sout = sout.replaceAll("(" + Pattern.quote(new File(javaFile).getName()) + "):([0-9]+):", "$1 : erreur de syntaxe ligne $2 :\n ");
            } else {
                sout = sout.replaceAll("(" + Pattern.quote(new File(javaFiles[0]).getPath()) + "):([0-9]+):", "\n Erreur de syntaxe ligne $2 :\n ");
                sout = sout.replaceAll(Pattern.quote(new File(javaFiles[0]).getName().replaceFirst("java$", "")), "");
            }
            // Escape des constructions dérivée d' un Translator
            sout = sout.replaceAll("/\\*(.*)@<nojavac.*@nojavac>\\*/", "$1");
            // Passage en français des principaux diagnostics
            sout = sout.replaceAll("not a statement",
                    "L'instruction n'est pas valide.\n (Il se peut qu'une variable indiquée n'existe pas)");
            sout = sout.replaceAll("';' expected",
                    "Un ';' est attendu (il peut manquer, ou une parenthèse être incorrecte, ..)");
            sout = sout.replaceAll("cannot find symbol\\s*symbol\\s*:\\s*([^\\n]*)[^:]*:\\s*(.*)",
                    "Il y a un symbole non-défini à cette ligne : «$1» (utilisez-vous la bonne proglet ?)");
            sout = sout.replaceAll("illegal start of expression",
                    "($0) L'instruction (ou la précédente) est tronquée ou mal écrite");
            sout = sout.replaceAll("class, interface, or enum expected",
                    "($0) Il y a probablement une erreur dans les accolades (peut-être trop de '}')");
            sout = sout.replaceAll("'.class' expected",
                    "($0) Il manque des accolades ou des parenthèses pour définir l'instruction");
            sout = sout.replaceAll("incompatible\\Wtypes\\W*found\\W*:\\W([A-Za-z\\.]*)\\Wrequired:\\W([A-Za-z\\.]*)",
                    "Vous avez mis une valeur de type $1 alors qu'il faut une valeur de type $2");
            // Elimination des notes de warning de fin de compilation
            if (sout.indexOf("Note:") != -1) {
                sout = sout.substring(0, sout.indexOf("Note:")).trim();
            }
            // Impression du message d'erreur si il existe et retour du statut
            if (sout.length() > 0) {
                System.out.println(sout);
            }
            return sout.length() == 0;
        }
    }

    /**
     * Charge dynamiquement une classe Java qui implémente un Runnable, pour son e×écution au cours d'une session.
     *
     * @param path Le chemin vers la classe Java à charger. La classe ne doit pas appartenir à un package, c'est-à-dire au package "default".
     * @return Une instanciation de cette classe Java.
     * @throws IllegalArgumentException Si la classe n'est pas un Runnable.
     * @throws RuntimeException         Si une erreur d'entrée-sortie s'est produite lors du chargement.
     */
    public static Runnable load(String path) {
        try {
            File javaClass = new File(path).getAbsoluteFile();
            Class<?> j_class = new JVSClassLoader(javaClass.getParent()).loadClass(javaClass.getName().replaceAll("\\.class", ""));
            Object o = j_class.newInstance();
            if (!(o instanceof Runnable)) {
                throw new IllegalArgumentException("Erreur: la classe de " + javaClass + " n'est pas un Runnable");
            }
            return (Runnable) o;
        } catch (Throwable e) {
            throw new RuntimeException("Erreur: impossible de charger la classe de : " + path + e);
        }
    }

    static class JVSClassLoader extends ClassLoader {
        private String location = null;
        private Hashtable classes = new Hashtable();

        public JVSClassLoader(String location) {
            super(ClassLoader.getSystemClassLoader()); //calls the parent class loader's constructor
            this.location = location;
        }

        public Class loadClass(String className) throws ClassNotFoundException {
            return findClass(className);
        }

        public Class findClass(String className) throws ClassNotFoundException {
            byte classByte[];
            Class result = null;

            result = (Class) classes.get(className); //checks in cached classes
            if (result != null) {
                return result;
            }

            try {
                return findSystemClass(className);
            } catch (Exception e) {
            }

            try {
                FileInputStream in = new FileInputStream(location + File.separator + className + ".class");
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                int nextValue = in.read();
                while (-1 != nextValue) {
                    byteStream.write(nextValue);
                    nextValue = in.read();
                }

                classByte = byteStream.toByteArray();
                result = defineClass(className, classByte, 0, classByte.length, null);
                classes.put(className, result);
                return result;
            } catch (Exception e) {
                throw new ClassNotFoundException(e.toString());
            }
            //
        }
    }
}

