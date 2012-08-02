/**************************************************************
 * Philippe VIENNE, Copyright (C) 2011.  All rights reserved. *
 **************************************************************/
package org.javascool.core;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Définit le mécanisme de compilation en ligne d'un code Java et de création du Jar obtenu.
 *
 * @serial exclude
 * @see <a href="Java2Jar.java.html">code source</a>
 */
public class Java2Jar {
    // @factory
    private Java2Jar() {
    }

    /**
     * Compile dans le système de fichier local, un code source Java.
     * <p>Les fichiers <tt>.class</tt> sont générés dans sur place.</p>
     * <p>Les erreurs de compilation sont affichées dans la console.</p>
     *
     * @param jarFile   La jarre de stockage du résultat.
     * @param mainClass Le nom de la classe qui contient le main.
     * @param javaFile  Le nom du fichier à compiler. Un tableau de noms de fichiers peut être donné.
     * @param allErrors Renvoie toutes les erreur si true, sinon uniquement la première erreur (par défaut).
     * @return La valeur true en cas de succès, false si il y a des erreurs de compilation.
     * @throws RuntimeException Si une erreur d'entrée-sortie s'est produite lors de la compilation.
     */
    public static boolean compile(String jarFile, String mainClass, String javaFile, boolean allErrors) {
        String javaFiles[] = {javaFile};
        return compile(jarFile, mainClass, javaFiles, null, allErrors);
    }

    /**
     * @see #compile(String, String, String, boolean)
     */
    public static boolean compile(String jarFile, String mainClass, String javaFile) {
        return compile(jarFile, mainClass, javaFile, false);
    }

    /**
     * @see #compile(String, String, String, boolean)
     */
    public static boolean compile(String jarFile, String mainClass, String javaFiles[]) {
        return compile(jarFile, mainClass, javaFiles, null, false);
    }

    /**
     * @see #compile(String, String, String, boolean)
     */
    public static boolean compile(String jarFile, String mainClass, String javaFiles[], String dependentsJar[], boolean allErrors) {
        try {
            String buildDir = ".build";
            JarManager.rmDir(new File(buildDir));
            new File(buildDir).mkdirs();
            if (dependentsJar != null) {
                for (String depJar : dependentsJar)
                    JarManager.jarExtract(depJar, buildDir);
            }
            for (int i = 0; i < javaFiles.length; i++) {
                String file = javaFiles[i];
                javaFiles[i] = buildDir + File.separator + file;
                JarManager.copyFiles(file, javaFiles[i]);
            }
            if (!Java2Class.compile(javaFiles, allErrors)) {
                return false;
            }
            String mfFile = buildDir + File.separator + "manifest.jmf";
            Manifest man = new Manifest();
            man.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
            man.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VENDOR, "Java's Cool");
            JarManager.jarCreate(jarFile, man, buildDir);
            JarManager.rmDir(new File(buildDir));
            return true;
        } catch (IOException e) {
            throw new RuntimeException("Erreur d'entrées-sorties à la compilation : " + e);
        }
    }

    /**
     * Lanceur de la conversion Jvs en Java.
     *
     * @param usage <tt>java org.javascool.core.Java2Jar main-file [input-file(s) ..] [output-file]</tt>
     */
    public static void main(String[] usage) {
        // @main
        if (usage.length > 1) {
            String javaFiles[] = new String[usage.length - 1];
            System.arraycopy(usage, 0, javaFiles, 0, javaFiles.length);
            compile(usage[usage.length - 1], new File(usage[0]).getName().replaceAll("\\.java$", ""), javaFiles, null, true);
        }
    }
}

