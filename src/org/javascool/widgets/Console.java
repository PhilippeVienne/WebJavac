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

package org.javascool.widgets;

import org.javascool.tools.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

// redirectSystemStreams

/**
 * Définit une zone d'affichage qui permet de recevoir les messages de la console.
 *
 * @author Philippe Vienne
 * @serial exclude
 * @see <a href="Console.java.html">code source</a>
 */
public class Console extends JPanel {
    private static final long serialVersionUID = 1L;

    /**
     * Zone d'affichage
     */
    private JTextArea outputPane;
    /**
     * Barre de menu
     */
    private ToolBar toolbar;
    /**
     * Zone d'affichage du statut.
     */
    private JLabel status;

    // @static-instance

    /**
     * Crée et/ou renvoie l'unique instance de console.
     * <p>Une application ne peut définir qu'une seule console.</p>
     */
    public static Console getInstance() {
        if (console == null) {
            console = new Console();
        }
        return console;
    }

    /**
     * Renvoie true si la console a déjà été instanciée, false sinon.
     */
    public static boolean isInstanced() {
        return console != null;
    }

    private static Console console = null;

    private Console() {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        // Construit la zone d'affichage
        outputPane = new JTextArea();
        outputPane.setEditable(false);
        outputPane.setFont(new Font("Monospaced", Font.PLAIN, 12));
        float[] bg = Color.RGBtoHSB(200, 200, 200, null);
        outputPane.setBackground(Color.getHSBColor(bg[0], bg[1], bg[2]));
        JScrollPane scrolledOutputPane = new JScrollPane(outputPane);
        this.add(scrolledOutputPane, BorderLayout.CENTER);
        // Construit la zone des bouttons
        toolbar = new ToolBar();
        toolbar.addTool("Effacer", "org/javascool/widgets/icons/erase.png", new Runnable() {
            @Override
            public void run() {
                clear();
            }
        }
        );
        toolbar.addTool("Copier tout", "org/javascool/widgets/icons/copyAll.png", new Runnable() {
            @Override
            public void run() {
                copyAll();
            }
        }
        );
        toolbar.addTool("Copier sélection", "org/javascool/widgets/icons/copySelection.png", new Runnable() {
            @Override
            public void run() {
                copySelection();
            }
        }
        );
        toolbar.addSeparator();
        toolbar.addTool("status", status = new JLabel("                                         "));
        this.add(toolbar, BorderLayout.NORTH);
        // Finalise l'objet
        redirectSystemStreams();
    }

    /**
     * Redirige le System.out vers cet affichage
     */
    private void redirectSystemStreams() {
        final OutputStream oldOut = System.out;
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                print(String.valueOf((char) b));
                oldOut.write(b);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                print(new String(b, off, len));
                oldOut.write(b, off, len);
            }

            @Override
            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
                oldOut.write(b);
            }
        };
        System.setOut(new PrintStream(out, true));
    }

    /**
     * Efface le contenu de la console.
     */
    public void clear() {
        outputPane.setText("");
    }

    /**
     * Copie le contenu de la console dans le presse-papier.
     */
    private void copyAll() {
        outputPane.selectAll();
        outputPane.copy();
    }

    /**
     * Copie le contenu de la sélection dans le presse-papier.
     */
    private void copySelection() {
        outputPane.copy();
    }

    /**
     * Affiche une information dans la barre de statut.
     *
     * @param text Texte à afficher.
     */
    public void show(String text) {
        status.setText(text);
    }

    /**
     * Affiche du texte dans la console.
     *
     * @param text Le texte à afficher.
     */
    public void print(String text) {
        for (String p : prefixes)
            if (text.startsWith(p)) {
                return;
            }
        outputPane.append(text);
    }

    // Messages paarasites supprimés à l'affichage
    private static final String prefixes[] = {
            "=== Minim Error ===",
            "=== Likely buffer underrun in AudioOutput.",
            "==== JavaSound Minim Error ===="
    };

    /**
     * Renvoie le contenu actuel de la console.
     *
     * @return Ce qui affiché dans la console.
     */
    public String getText() {
        return outputPane.getText();
    }

    /**
     * Renvoie la barre de menu de la console pour ajouter des éléments.
     *
     * @return La barre de menu de la console.
     */
    public ToolBar getToolBar() {
        return toolbar;
    }

    /**
     * Sauve ce qui est présentement écrit dans la console dans un fichierL.
     *
     * @param location La localisation (chemin du fichier ou localisation internet) où sauver le texte.
     */
    public void saveConsoleOutput(String location) {
        FileManager.save(location, getText());
    }
}
