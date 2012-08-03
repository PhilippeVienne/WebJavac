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

package org.javascool.macros;

/**
 * Cette factory contient des fonctions générales rendues visibles à l'utilisateur de proglets.
 * <p>Elle permet de définir des fonctions statiques qui seront utilisées pour faire des programmes élèves.</p>
 * <p>Elle permet aussi avoir quelques fonctions de base lors de la création de nouvelles proglets.</p>
 *
 * @serial exclude
 * @see <a href="Stdout.java.html">code source</a>
 */
public class Stdout {
    // @factory
    private Stdout() {
    }

    /**
     * Affiche dans la console une chaîne de caractères ou la représentation textuelle d'un objet sur la console.
     * - Cette fonction ne change pas le focus de javascool.
     *
     * @param string La chaine ou l'objet à afficher sous sa représentation textuelle.
     * @see #println(String)
     */
    public static void echo(String string) {
        System.out.print(string + "\n");
    }

    /**
     * @see #echo(String)
     */
    public static void echo(int string) {
        echo("" + string);
    }

    /**
     * @see #echo(String)
     */
    public static void echo(char string) {
        echo("" + string);
    }

    /**
     * @see #echo(String)
     */
    public static void echo(double string) {
        echo("" + string);
    }

    /**
     * @see #echo(String)
     */
    public static void echo(boolean string) {
        echo("" + string);
    }

    /**
     * @see #echo(String)
     */
    public static void echo(Object string) {
        echo("" + string);
    }

    /**
     * Affiche dans la console une chaîne de caractères ou la représentation textuelle d'un objet sur la console.
     * - Cette fonction ramène le focus de javascool sur la console.
     *
     * @param string La chaine ou l'objet à afficher sous sa représentation textuelle.
     * @see #echo(String)
     */
    public static void println(String string) {
        //Desktop.getInstance().focusOnConsolePanel();
        //System.err.println("printing : \"" + string + "\"");
        System.out.print(string + "\n");
    }

    /**
     * @see #echo(String)
     */
    public static void println(int i) {
        println("" + i);
    }

    /**
     * @see #echo(String)
     */
    public static void println(char i) {
        println("" + i);
    }

    /**
     * @see #echo(String)
     */
    public static void println(double d) {
        println("" + d);
    }

    /**
     * @see #echo(String)
     */
    public static void println(boolean b) {
        println("" + b);
    }

    /**
     * @see #echo(String)
     */
    public static void println(Object o) {
        println("" + o);
    }

    /**
     * Affiche dans la console une chaîne de caractères ou la représentation textuelle d'un objet sur la console sans retour à la ligne.
     *
     * @param string La chaine ou l'objet à afficher sous sa représentation textuelle.
     */
    public static void print(String string) {
        System.out.print(string);
        System.out.flush();
    }

    /**
     * @see #print(String)
     */
    public static void print(int i) {
        print("" + i);
    }

    /**
     * @see #print(String)
     */
    public static void print(char i) {
        print("" + i);
    }

    /**
     * @see #print(String)
     */
    public static void print(double d) {
        print("" + d);
    }

    /**
     * @see #print(String)
     */
    public static void print(boolean b) {
        print("" + b);
    }

    /**
     * @see #print(String)
     */
    public static void print(Object o) {
        print("" + o);
    }

    /**
     * Efface tout ce qui est écrit dans la console.
     */
    public static void clear() {
        //Console.getInstance().clear();
    }

    /**
     * Sauve ce qui est présentement écrit dans la console dans un fichier.
     *
     * @param location La localisation (chemin du fichier ou localisation internet) où sauver le texte.
     */
    public static void saveConsoleOutput(String location) {
        //Console.getInstance().saveConsoleOutput(location);
    }
}
