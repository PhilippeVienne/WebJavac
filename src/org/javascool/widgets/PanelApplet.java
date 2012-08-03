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

// Used to encapsulate a proglet

import org.javascool.tools.Invoke;

import javax.swing.*;
import java.awt.*;

/**
 * Définit une applet qui encapsule un objet graphique.
 * <p>Permet de wrapper un objet graphique dans une page HTML avec une construction de la forme
 * <div><tt>&lt;applet code="org.javascool.widgets.PanelApplet" archive="les-classes-java.jar" width="560" height="720"></tt></div>
 * <div><tt>&lt;param name="panel" value="nom-complet-qualifé-de-l-objet-graphique"/></tt></div>
 * <div><tt>&lt;param name="manual-start" value="true-ou-false"/></tt></div>
 * <div><tt>&lt;/applet></tt></div>
 * </p>
 * <ul>
 * <li>L'objet doit être un instance de <tt>java.awt.Component</tt> donc n'importe quel composant «swing» ou «awt».</li>
 * <li>Si l'objet possède des méthodes <tt>init<tt>, , <tt>destroy</tt>, <tt>start</tt>, <tt>stop</tt> elles sont invoquées par les méthodes correspondandes de l'applet.</li>
 * <li>Si l'option <tt>manual-start</tt> est activée les méthodes <tt>start</tt>, <tt>stop</tt> ne sont invoquées par l'applet mais par un bouton utilisateuer.</li>
 * </ul>
 *
 * @serial exclude
 * @see <a href="PanelApplet.java.html">source code</a>
 */
public class PanelApplet extends JApplet {
    private static final long serialVersionUID = 1L;

    /**
     * Definition programmatique des paramètres de l'applet.
     *
     * @param panel       Le nom de la classe Java de l'objet graphique à afficher.
     * @param manualStart Invocations manuelles si true des méthodes <tt>start/stop</tt> (par défaut), sinon elles sont invoquées au lancement.
     * @return Cet objet, permettant de définir la construction <tt>new PanelApplet().reset(..)</tt>.
     */
    public PanelApplet reset(String panel, boolean manualStart) {
        this.panel = panel;
        this.manualStart = manualStart;
        return this;
    }

    /**
     * @see #reset(String, boolean)
     */
    public PanelApplet reset(String panel) {
        return reset(panel, true);
    }

    private String panel = null;
    private boolean manualStart = true;

    @Override
    public void init() {
        if (pane != null) {
            throw new IllegalStateException("Impossible d'instancier deux PanelApplet dans une application");
        }
        try {
            if (panel == null) {
                panel = getParameter("panel");
                manualStart = getParameter("manualStart") == null || getParameter("manualStart").toLowerCase().equals("true");
            }
        } catch (Exception e) {
        }
        try {
            getContentPane().add(pane = (Component) Class.forName(panel).newInstance(), BorderLayout.CENTER);
        } catch (Exception e) {
            System.err.println(e);
            getContentPane().add(new JLabel("Pas d'applet à montrer.", JLabel.CENTER), BorderLayout.CENTER);
            manualStart = false;
        }
        if (manualStart && Invoke.run(pane, "start", false)) {
            getContentPane().add(new ToolBar().addTool("Démo de la proglet", "org/javascool/widgets/icons/play.png", new Runnable() {
                public void run() {
                    (new Thread() {
                        public void run() {
                            Invoke.run(pane, "start");
                        }
                    }
                    ).start();
                }
            }
            ), BorderLayout.NORTH);
        }
        Invoke.run(pane, "init");
    }

    @Override
    public void destroy() {
        Invoke.run(pane, "init");
    }

    @Override
    public void start() {
        if (!manualStart) {
            Invoke.run(pane, "start");
        }
    }

    @Override
    public void stop() {
        if (!manualStart) {
            Invoke.run(pane, "stop");
        }
    }

    /**
     * Renvoie le panneau graphique de la proglet courante.
     *
     * @return Le panneau graphique de la proglet courante ou null si il n'est pas défini.
     */
    public static Component getPane() {
        return pane;
    }

    static {
        MainFrame.setLookAndFeel();
    }

    private static Component pane = null;

    /**
     * Lanceur dans une fenêtre principale d'une objet graphique.
     *
     * @param usage <tt>java org.javascool.widgets.PanelApplet nom-complet-qualifé-de-l-objet-graphique</tt>.
     */
    public static void main(String[] usage) {
        // @main
        if (usage.length > 0) {
            new MainFrame().reset(new PanelApplet().reset(usage[0]));
        }
    }
}
