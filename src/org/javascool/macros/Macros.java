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

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;

/**
 * Cette factory contient des fonctions générales rendues visibles à l'utilisateur de proglets.
 * <p>Elle permet de définir des fonctions plus facile d'utilisation que les appels Java usuels.</p>
 * <p>Elle permet aussi avoir quelques fonctions de base lors de la création de nouvelles proglets.</p>
 *
 * @serial exclude
 * @see <a href="Macros.java.html">code source</a>
 */
public class Macros {
    // @factory

    private Macros() {
    }

    /**
     * Renvoie un nombre entier aléatoire uniformément distribué entre deux valeurs (maximum inclus).
     */
    public static int random(int min, int max) {
        return (int) Math.floor(min + (max - min) * Math.random());
    }

    /**
     * Renvoie true si deux chaînes de caratères sont égales, faux sinon.
     *
     * @param string1 L'une des chaînes à comparer.
     * @param string2 L'autre des chaînes à comparer.
     */
    public static boolean equal(String string1, String string2) {
        return string1.equals(string2);
    }

    /**
     * Renvoie le temps actuel en milli-secondes.
     *
     * @return Renvoie la différence, en millisecondes, entre le temps actuel et celui du 1 Janvier 2000, minuit, en utilisant le temps universel coordonné.
     */
    public static double now() {
        return System.currentTimeMillis() - offset;
    }

    private static long offset;

    static {
        Calendar ref = Calendar.getInstance();
        ref.set(2000, 0, 1, 0, 0, 0);
        offset = ref.getTimeInMillis();
    }

    /**
     * Temporise une durée fixée.
     * Cela permet aussi de mettre à jour l'affichage.
     *
     * @param delay Durée d'attente en milli-secondes.
     */
    public static void sleep(int delay) {
        try {
            if (delay > 0) {
                Thread.sleep(delay);
            } else {
                Thread.sleep(0, 10000);
            }
        } catch (Exception e) {
            throw new RuntimeException("Programme arrêté !");
        }
    }
    /** Excécute une routine à un intervalle régulier.
     * Exemple d'usage (impression de 10 messages à interval d'1 sec. puis arrêt):<pre>
     * sample(1000, new Runnable() { public void run() {
     *    if (count &lt; 10) {
     *      println("Et de "+(++count)+" !");
     *    } else throw new RuntimeException("done!");
     *  }
     *  int count = 0;
     * });</pre>
     * <p> Noter que le runnable doit être interrompu par le jet d'une exception, sinon il tournera sans relâche jusqu'à la fermeture de javascool.
     * @param delay Période d'échantillonage en milli-secondes.
     * @param runnable Le code à exécuter à chaque appel.
     */
//  public static void sample(int delay, Runnable runnable) {
//    Sampler sampler = new Sampler().setDelay(delay).setRunnable(runnable);
//    sampler.start();
//  }

    /**
     * Vérifie une assertion et arrête le code si elle est fausse.
     * Le diagnoctic apparait sous la forme:
     * **<pre>Arrêt du programme :{
     *  L'assertion(«<i>message</i>») est fausse.
     *  Pile d'exécution: {
     *   <i>fonctions appellées et ligne de code correspondant</i>
     *  }
     *  Objet en cause:{
     *     class = «<i>type de l'objet</i>»
     *     value = «<i>valeur de l'objet</i>»
     *  }
     * **}</pre>
     *
     * @param condition Si la condition n'est pas vérifiée, le code JavaScool va s'arrêter.
     * @param message   Un message s'imprime sur la console pour signaler l'erreur.
     * @param object    Si l'objet n'est pas null, donne des renseignements sur l'objet
     */
    public static void assertion(boolean condition, String message, Object object) {
        System.err.println("#" + condition + " : " + message + " ::" + object);
        if (!condition) {
            System.out.println("Arrêt du programme :{\n  L'assertion(«" + message + "») est fausse.\n  Pile d'exécution: {");
            // Sortie de la pile,
            // - moins les deux appels getStackTrace() et assertion() et
            // - moins les trois appels run() du bas de pile qui ont lancés la proglet
            {
                StackTraceElement where[] = Thread.currentThread().getStackTrace();
                for (int i = 2; i < where.length - 3; i++)
                    System.out.println("     " + where[i].toString().replaceAll("JvsToJavaTranslated[0-9]+\\.", "").replaceAll("java:([0-9]+)", "ligne : $1"));
            }
            System.out.println("  }");
            if (object != null) {
                System.out.println("  Objet en cause:{\n    class = «" + object.getClass() + "»\n    value = «" + object + "»\n  }");
            }
            System.out.println("}");
            throw new IllegalStateException("Assert Error, the program has to stop now", new AssertionError(message));
        }
    }

    /**
     * @see #assertion(boolean, String, Object)
     */
    public static void assertion(boolean condition, String message) {
        assertion(condition, message, null);
    }

    /**
     * Renvoie une icone stockée dans le JAR de l'application.
     *
     * @param path Emplacement de l'icone, par exemple <tt>"org/javascool/widget/icons/play.png"</tt>
     * @return L'icone chargée ou null si elle n'existe pas.
     */
    public static ImageIcon getIcon(String path) {
        URL icon = getResourceURL(path);   // Thread.currentThread().getContextClassLoader().getResource(path);
        if (icon == null) {
            System.err.println("Warning : getIcon(" + path + ") not found");
        }
        return icon == null ? null : new ImageIcon(icon);
    }

    /**
     * Ouvre une URL (Universal Resource Location) dans un navigateur extérieur.
     *
     * @param location L'URL à afficher.
     * @throws IllegalArgumentException Si l'URL est mal formée.
     * @throws RuntimeException         Si une erreur d'entrée-sortie s'est produite.
     */
    public static void openURL(String location) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new java.net.URI(location));
                System.err.println("Note: Ouverture de " + location + " dans un navigateur externe");
            } else {
                openURL2(location);
            }
        } catch (Throwable th) {
            openURL2(location);
        }
    }

    // Procédure de secours pour ouvrir une URL
    private static void openURL2(String location) {
        System.err.println("Note: Ouverture de " + location + " dans un browser navigateur (methode de secours)");
        String url = location;
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try {
            if (os.indexOf("win") >= 0) {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (os.indexOf("mac") >= 0) {
                rt.exec("open " + url);
            } else if ((os.indexOf("nix") >= 0) || (os.indexOf("nux") >= 0)) {
                // Do a best guess on unix until we get a platform independent way
                // Build a list of browsers to try, in this order.
                String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx"};
                // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
                StringBuilder cmd = new StringBuilder();
                for (int i = 0; i < browsers.length; i++)
                    cmd.append(i == 0 ? "" : " || ").append(browsers[i]).append(" \"").append(url).append("\" ");
                rt.exec(new String[]{"sh", "-c", cmd.toString()}
                );
            } else {
                throw new RuntimeException("Erreur (pas d'OS détecté) à l'ouverture dans un navigateur de " + location);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur (" + e + ") à l'ouverture dans un navigateur de " + location);
        }
    }

    /**
     * Renvoie une URL (Universal Resource Location) normalisée, dans le cas du système de fichier local ou d'une ressource.
     * <p>La fonction recherche l'existence du fichier:
     * (i) par rapport au répertoire de base qui est donné,
     * (ii) par rapport au dossier de travaul "user.dir",
     * (iii) par rapport à la racine des fichier "user.home",
     * (iv) dans les ressources du CLASSPATH.</p>
     *
     * @param location L'URL à normaliser.
     * @param base     Un répertoire de réference pour la normalisation. Par défaut null.
     * @param reading  Précise si nous sommes en lecture (true) ou écriture (false). Par défaut en lecture.
     * @throws IllegalArgumentException Si l'URL est mal formée.
     */
    public static URL getResourceURL(String location, String base, boolean reading) {
        if (base != null) {
            location = base + "/" + location;
        }
        try {
            // @patch : ceci blinde un bug sur les URL jar
            if (location.matches("jar:[^!]*!.*")) {
                String res = location.replaceFirst("[^!]*!/", "");
                URL url = Thread.currentThread().getContextClassLoader().getResource(res);
                if (url != null) {
                    return url;
                } else {
                    throw new IllegalArgumentException("Unable to find " + res + " from " + location + " as a classpath resource");
                }
            }
            if (location.matches("(ftp|http|https|jar|mailto|stdout):.*")) {
                return new URL(location).toURI().normalize().toURL();
            }
            if (location.startsWith("file:")) {
                location = location.substring(5);
            }
            if (reading) {
                File file = new File(location);
                if (file.exists()) {
                    return new URL("file:" + file.getCanonicalPath());
                }
                file = new File(System.getProperty("user.dir"), location);
                if (file.exists()) {
                    return new URL("file:" + file.getCanonicalPath());
                }
                file = new File(System.getProperty("user.home"), location);
                if (file.exists()) {
                    return new URL("file:" + file.getCanonicalPath());
                }
                URL url = Thread.currentThread().getContextClassLoader().getResource(location);
                if (url != null) {
                    return url;
                }
            }
            return new URL("file:" + location);
        } catch (IOException e) {
            throw new IllegalArgumentException(e + " : " + location + " is a malformed URL");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e + " : " + location + " is a malformed URL");
        }
    }

    /**
     * @see #getResourceURL(String, String, boolean)
     */
    public static URL getResourceURL(String location, String base) {
        return getResourceURL(location, base, true);
    }

    /**
     * @see #getResourceURL(String, String, boolean)
     */
    public static URL getResourceURL(String location, boolean reading) {
        return getResourceURL(location, null, reading);
    }

    /**
     * @see #getResourceURL(String, String, boolean)
     */
    public static URL getResourceURL(String location) {
        return getResourceURL(location, null, true);
    }
}
