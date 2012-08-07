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

import org.javascool.widgets.Dialog;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

/**
 * Cette factory contient des fonctions générales rendues visibles à l'utilisateur de proglets.
 * <p>Elle permet de définir des fonctions statiques qui seront utilisées pour faire des programmes élèves.</p>
 * <p>Elle permet aussi avoir quelques fonctions de base lors de la création de nouvelles proglets.</p>
 *
 * @serial exclude
 * @see <a href="Stdin.java.html">code source</a>
 */
public class Stdin {
    // @factory
    private Stdin() {
    }

    /**
     * Lit une chaîne de caractère dans une fenêtre présentée à l'utilisateur.
     *
     * @param question Une invite qui décrit la valeur à entrer (optionel).
     * @return La chaîne lue.
     */
    public static String readString(String question) {
        return readString(question, '\n');
    }

    // Lit une chaîne de caractère jusqu'au '\n' (readLine), ' ' (readWord), ou '\0' (readChar) selon le separator.
    public static String readString(String question, char separator) {
        if (inputBuffer.isPopable()) {
            return inputBuffer.popString(separator);
        }
        inputQuestion = question;
        inputSeparator = separator;
        inputString = (String)JOptionPane.showInputDialog(
                null,
                question,
                "Java's Cool read",
                JOptionPane.PLAIN_MESSAGE);
        /*inputDialog = new Dialog();
        inputDialog.setTitle("Java's Cool read");
        inputDialog.add(new JPanel() {
            {
                add(new JLabel(inputQuestion + " "));
                add(new JTextField(40) {
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                inputString = ((JTextField) e.getSource()).getText();
                                inputDialog.close();
                            }
                        }
                        );
                        addCaretListener(new CaretListener() {
                            @Override
                            public void caretUpdate(CaretEvent e) {
                                inputString = ((JTextField) e.getSource()).getText();
                                if (inputSeparator == '\0') {
                                    inputDialog.close();
                                } else if (inputSeparator == ' ' && Character.isWhitespace(inputString.charAt(inputString.length() - 1))) {
                                    inputString = inputString.substring(0, inputString.length() - 1);
                                    inputDialog.close();
                                }
                            }
                        }
                        );
                    }
                }
                );
            }
        }
        );
        inputDialog.open(true);*/
        return inputString == null ? "" : inputString;
    }

    private static Dialog inputDialog;
    private static char inputSeparator;
    private static String inputQuestion, inputString;

    /**
     * @see #readString(String)
     */
    public static String readString() {
        return readString("Entrez une chaîne :");
    }

    /**
     * Lit une chaîne de caractère sans espace dans une fenêtre présentée à l'utilisateur.
     *
     * @param question Une invite qui décrit la valeur à entrer (optionel).
     * @return La chaîne lue.
     */
    public static String readWord(String question) {
        return readString(question, ' ');
    }

    /**
     * @see #readWord(String)
     */
    public static String readWord() {
        return readWord("Entrez une chaîne sans espace :");
    }

    /**
     * Lit une chaîne de caractère d'un caractère dans une fenêtre présentée à l'utilisateur.
     *
     * @param question Une invite qui décrit la valeur à entrer (optionel).
     * @return La chaîne lue.
     */
    public static char readChar(String question) {
        return readString(question, '\0').charAt(0);
    }

    /**
     * @see #readChar(String)
     */
    public static char readChar() {
        return readChar("Entrez une chaîne sans espace :");
    }

    /**
     * Lit un nombre entier dans une fenêtre présentée à l'utilisateur.
     *
     * @param question Une invite qui décrit la valeur à entrer (optionel).
     * @return La valeur lue.
     */
    public static int readInteger(String question) {
        if (inputBuffer.isPopable()) {
            return inputBuffer.popInteger();
        }
        String s = readString(question, ' ');
        try {
            return Integer.decode(s);
        } catch (Exception e) {
            if (!question.endsWith(" (Merci d'entrer un nombre entier)")) {
                question = question + " (Merci d'entrer un nombre entier)";
            }
            if (s.equals("")) {
                return 0;
            }
            return readInteger(question);
        }
    }

    /**
     * @see #readInteger(String)
     */
    public static int readInteger() {
        return readInteger("Entrez un nombre entier : ");
    }

    /**
     * @see #readInteger(String)
     */
    public static int readInt(String question) {
        return readInteger(question);
    }

    /**
     * @see #readInteger(String)
     */
    public static int readInt() {
        return readInteger();
    }

    /**
     * Lit un nombre décimal dans une fenêtre présentée à l'utilisateur.
     *
     * @param question Une invite qui décrit la valeur à entrer (optionel).
     * @return La valeur lue.
     */
    public static double readDecimal(String question) {
        if (inputBuffer.isPopable()) {
            return inputBuffer.popDecimal();
        }
        String s = readString(question, ' ');
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            if (!question.endsWith(" (Merci d'entrer un nombre)")) {
                question = question + " (Merci d'entrer un nombre)";
            }
            if (s.equals("")) {
                return 0;
            }
            return readDecimal(question);
        }
    }

    /**
     * @see #readDecimal(String)
     */
    public static double readDecimal() {
        return readDecimal("Entrez un nombre décimal : ");
    }

    /**
     * @see #readDecimal(String)
     */
    public static double readDouble(String question) {
        return readDecimal(question);
    }

    /**
     * @see #readDecimal(String)
     */
    public static double readDouble() {
        return readDecimal();
    }

    /**
     * @see #readDecimal(String)
     */
    public static double readFloat(String question) {
        return readDecimal(question);
    }

    /**
     * @see #readDecimal(String)
     */
    public static double readFloat() {
        return readDecimal();
    }

    /**
     * Lit une valeur booléenne dans une fenêtre présentée à l'utilisateur.
     *
     * @param question Une invite qui décrit la valeur à entrer (optionel).
     * @return La valeur lue.
     */
    public static boolean readBoolean(String question) {
        if (inputBuffer.isPopable()) {
            return inputBuffer.popBoolean();
        }
        inputQuestion = question;
        inputString = null;
        int in=JOptionPane.showConfirmDialog(
                null,
                question,
                "Java's Cool read",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        switch (in){
            case JOptionPane.YES_OPTION:
                return true;
            default:
                return false;
        }
        /*inputDialog = new Dialog();
        inputDialog.setTitle("Java's Cool read");
        inputDialog.add(new JPanel() {
            {
                add(new JLabel(inputQuestion + " "));
                add(new JButton("OUI") {
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                inputString = "OUI";
                                inputDialog.close();
                            }
                        }
                        );
                    }
                }
                );
                add(new JButton("NON") {
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                inputString = "NON";
                                inputDialog.close();
                            }
                        }
                        );
                    }
                }
                );
            }
        }
        );
        inputDialog.open(true);
        return "OUI".equals(inputString);*/
    }

    /**
     * @see #readBoolean(String)
     */
    public static boolean readBoolean() {
        return readBoolean("Entrez une valeur booléenne (oui/non) : ");
    }

    /**
     * @see #readBoolean(String)
     */
    public static Boolean readBool(String question) {
        return readBoolean(question);
    }

    /**
     * @see #readBoolean(String)
     */
    public static Boolean readBool() {
        return readBoolean();
    }

    /**
     * Vide le contenu qui sert d'entrée à la console.
     */
    public static void clearConsoleInput() {
        inputBuffer.clear();
    }

    /**
     * Charge une chaine de caractère pour que son contenu serve d'entrée à la console.
     *
     * @param string La chaine de caractère à ajouter.
     */
    public static void addConsoleInput(String string) {
        inputBuffer.add(string);
    }

    /**
     * Charge le contenu d'un fichier pour que son contenu serve d'entrée à la console.
     *
     * @param location La localisation (chemin du fichier ou localisation internet) d'où charger le texte.
     */
    public static void loadConsoleInput(String location) {
        clearConsoleInput();
        addConsoleInput(org.javascool.tools.FileManager.load(location));
    }

    /**
     * Définit une zone tampon qui permet de substituer un fichier aux lectures au clavier.
     */
    private static class InputBuffer {
        String inputs = new String();

        /**
         * Ajoute une chaîne en substitution d'une lecture au clavier.
         *
         * @param string Le texte à ajouter.
         */
        public void add(String string) {
            inputs += string.trim() + "\n";
        }

        /**
         * Vide la zone tampon.
         */
        public void clear() {
            inputs = "";
        }

        /**
         * Teste si il y une chaîne disponible.
         *
         * @return La valeur true si il y une entrée disponible.
         */
        public boolean isPopable() {
            return inputs.length() > 0;
        }

        /**
         * Récupére une chaîne en substitution d'une lecture au clavier.
         *
         * @param separator Lit une chaîne de caractère jusqu'au '\n' (readLine), ' ' (readWord), ou '\0' (readChar) selon le separator.
         * @return Le texte suivant à considérer. Ou la chaîne vide si le tampon est vide.
         */
        public String popString(char separator) {
            Macros.sleep(500);
            int i = separator == '\0' ? 1 : inputs.indexOf('\n');
            if (separator == ' ') {
                int i1 = inputs.indexOf(' ');
                if (i1 != -1 && (i == -1 || i1 < i))
                    i = i1;
            }
            if (i != -1) {
                String input = inputs.substring(0, i);
                inputs = inputs.substring(separator == '\0' ? i : i + 1);
                return input;
            } else {
                return "";
            }
        }

        /**
         * @see #popString(char)
         */
        public String popString() {
            return popString('\n');
        }

        /**
         * @see #popString(char)
         */
        public String popWord() {
            return popString(' ');
        }

        /**
         * @see #popString(char)
         */
        public String popChar() {
            return popString('\0');
        }

        /**
         * @see #popString(char)
         */
        public int popInteger() {
            try {
                return Integer.decode(popString(' '));
            } catch (Exception e) {
                return 0;
            }
        }

        /**
         * @see #popString(char)
         */
        public double popDecimal() {
            try {
                return Double.parseDouble(popString(' '));
            } catch (Exception e) {
                return 0;
            }
        }

        /**
         * @see #popString(char)
         */
        public boolean popBoolean() {
            // Renvoie vrai si [t]rue [y]es [v]rai [o]ui 1
            return popString(' ').toLowerCase().matches("[tyvo1].*");
        }
    }

    private static InputBuffer inputBuffer = new InputBuffer();

    /** Définit une portion de code appelée à chaque entrée d'un caractère au clavier.
     * <p>Les caractères du clavier ne sont détectés que si la souris est sur la fenêtre de la proglet de façon à ce qu'elle est le focus.</p>
     * <p>Les caractères du clavier et quelques touches de contrôle sont gérés.</p>
     * @param runnable La portion de code à appeler, ou null pour annuler l'appel à la portion de code précédent.

    public static void setKeyListener(Runnable runnable) {
    if(keyKeyListener != null) {
    Macros.getProgletPane().removeKeyListener(keyKeyListener);
    }
    if(keyMouseListener != null) {
    Macros.getProgletPane().removeMouseListener(keyMouseListener);
    }
    if(Macros.getProgletPane() != null&& (keyListenerRunnable = runnable) != null) {
    Macros.getProgletPane().addMouseListener(keyMouseListener = new MouseListener() {
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {
    Macros.getProgletPane().requestFocusInWindow();
    }
    public void mouseExited(MouseEvent e) {}
    }
    );
    Macros.getProgletPane().addKeyListener(keyKeyListener = new KeyListener() {
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {
    String s = KeyEvent.getKeyText(e.getKeyCode());
    if((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
    lastKey = "Ctrl+" + s;
    } else {
    int c = e.getKeyChar();
    if((32 <= c) && (c < 127)) {
    lastKey = "" + e.getKeyChar();
    } else {
    if("Shift".equals(s) || "Ctrl".equals(s)) {
    return;
    }
    lastKey = s;
    }
    }
    if(keyListenerRunnable != null) {
    keyListenerRunnable.run();
    }
    }
    public void keyTyped(KeyEvent e) {}
    }
    );
    }
    }*/
    /**
     * Renvoie la dernière touche entrée au clavier ou la chaine vide sinon.
     *
     * @return Renvoie le caractère associée à la touche si il est défini, sinon une chaîne qui représente le caractère de contrôle,
     *         par exemple 'Left', 'Up, 'Right', 'Down' pour les flèches, 'F1, 'F2', .. pour les touches de fonctions, 'Alt', 'Escape', 'Backspace', 'Enter', 'Page Down', 'Page Up', 'Home', 'end' pour les autres touches, 'Ctrl+A' pour la combinaison de la touche 'Control' et 'A', etc.
     */
    public static String getLastKey() {
        return lastKey;
    }

    private static Runnable keyListenerRunnable = null;
    private static KeyListener keyKeyListener = null;
    private static String lastKey = "";
    private static MouseListener keyMouseListener = null;
}

