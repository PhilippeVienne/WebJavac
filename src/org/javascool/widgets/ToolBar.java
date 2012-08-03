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

import org.javascool.macros.Macros;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * Définit une barre d'outils avec intégration de la gestion des actions.
 *
 * @author Philippe Vienne
 * @serial exclude
 * @see <a href="ToolBar.java.html">code source</a>
 */
public class ToolBar extends JToolBar {
    private static final long serialVersionUID = 1L;

    // @bean
    public ToolBar() {
        setFloatable(false);
    }

    /**
     * Table des boutons indexés par leurs noms.
     */
    private HashMap<String, JComponent> buttons = new HashMap<String, JComponent>();
    /**
     * Table des actions associées au bouton.
     */
    private HashMap<AbstractButton, Runnable> actions = new HashMap<AbstractButton, Runnable>();

    /**
     * Initialize la barre de boutons et efface tous les élements.
     */
    @Override
    public void removeAll() {
        left = right = 0;
        setVisible(false);
        revalidate();
        super.removeAll();
        buttons.clear();
        actions.clear();
        setVisible(true);
        revalidate();
    }

    /**
     * Ajoute un bouton à la barre d'outils.
     *
     * @param label  Nom du bouton. Chaque bouton/item/étiquette doit avoir un nom différent.
     * @param icon   Icone du bouton. Si null le bouton est montré sans icone.
     * @param action Action associée au bouton.
     * @return Le bouton ajouté.
     */
    public final JButton addTool(String label, String icon, Runnable action) {
        return addTool(label, icon, action, left++);
    }

    /**
     * @see #addTool(String, String, Runnable)
     */
    public final JButton addTool(String label, Runnable action) {
        return addTool(label, null, action);
    }

    /**
     * Ajoute un pop-up à la barre d'outil.
     *
     * @param label Nom du composant. Chaque bouton/item/étiquette doit avoir un nom différent.
     * @param icon  Icone du bouton. Si null le bouton est montré sans icone.
     * @return Le popup ajouté. Il permet de définir un menu ou d'afficher un composant etc...
     */
    public final JPopupMenu addTool(String label, String icon) {
        PopupMenuRunnable p = new PopupMenuRunnable();
        p.b = addTool(label, icon, p);
        return p.j;
    }

    /**
     * @see #addTool(String, String)
     */
    public final JPopupMenu addTool(String label) {
        return addTool(label, (String) null);
    }

    private class PopupMenuRunnable implements Runnable {
        JPopupMenu j = new JPopupMenu();
        JButton b;

        @Override
        public void run() {
            j.show(b, 0, ToolBar.this.getHeight());
        }
    }

    /**
     * Ajoute un bouton à une position précise de la barre d'outil
     *
     * @see #addTool(String, String, Runnable)
     */
    private JButton addTool(String label, String icon, Runnable action, int where) {
        JButton button = icon == null ? new JButton(label) : new JButton(label, Macros.getIcon(icon));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actions.get((AbstractButton) e.getSource()).run();
            }
        }
        );
        add(button, where);
        if (buttons.containsKey(label)) {
            throw new IllegalArgumentException("Chaque bouton/item/étiquette doit avoir un nom différent, mais le bouton «" + label + "» est en doublon");
        }
        buttons.put(label, button);
        actions.put(button, action);
        revalidate();
        return button;
    }

    /**
     * Ajoute un composant à la barre d'outils.
     *
     * @param label     Nom du composant (ce nom restera invisible). Chaque bouton/item/étiquette doit avoir un nom différent.
     * @param component Le composant à ajouter.
     */
    public void addTool(String label, JComponent component) {
        add(component, left++);
        if (buttons.containsKey(label)) {
            throw new IllegalArgumentException("Chaque bouton/item/étiquette doit avoir un nom différent, mais le bouton «" + label + "» est en doublon");
        }
        buttons.put(label, component);
        revalidate();
    }

    /**
     * Efface un composant de la barre d'outils.
     */
    public void removeTool(String label) {
        if (buttons.containsKey(label)) {
            JComponent c = buttons.get(label);
            remove(c);
            buttons.remove(label);
            if (c instanceof AbstractButton && actions.containsKey((AbstractButton) c)) {
                actions.remove((AbstractButton) c);
            }
            setVisible(false);
            revalidate();
            setVisible(true);
            revalidate();
        }
    }

    /**
     * Teste si un composant est sur la barre d'outils.
     */
    public boolean hasTool(String label) {
        return buttons.containsKey(label);
    }

    /**
     * Ajoute un composant à la droite de la barre d'outil.
     *
     * @param label  Nom du composant. Chaque bouton/item/étiquette doit avoir un nom différent.
     * @param action Action associée au bouton.
     * @return Le bouton ajouté.
     */
    public JButton addRightTool(String label, Runnable action) {
        if (right == 0) {
            add(Box.createHorizontalGlue());
        }
        return addTool(label, null, action, left + (++right));
    }

    /**
     * Ajoute un pop-up à la droite de la barre d'outil.
     *
     * @param label Nom du composant. Chaque bouton/item/étiquette doit avoir un nom différent.
     * @return Le popup ajouté.
     */
    public final JPopupMenu addRightTool(String label) {
        PopupMenuRunnable p = new PopupMenuRunnable();
        p.b = addRightTool(label, p);
        return p.j;
    }
    // @todo a enlever apres la refonte du proglet-builder

    /**
     * Ajoute en permanence un composant à la droite de la barre d'outils.
     *
     * @param component Le composant à ajouter.
     */
    public void addRightTool(JComponent component) {
        if (right == 0) {
            add(Box.createHorizontalGlue());
        }
        add(component, left + (++right));
    }

    private int left = 0, right = 0;
}
