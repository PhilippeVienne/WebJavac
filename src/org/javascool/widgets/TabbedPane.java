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
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

/**
 * Propose un container d'objets graphiques avec des onglets pouvant posséder une croix de fermeture.
 *
 * @author Philippe Vienne
 */
public class TabbedPane extends JTabbedPane {
    private static final long serialVersionUID = 1L;

    /**
     * Ajoute un composant à ce panneau à onglet.
     *
     * @param title     Le nom du composant.
     * @param icon      Le nom de l'icone de ce composant.
     * @param component Le composant à ajouter.
     * @param tooltip   Un petit titre à afficher quand le curseur passe sur l'onglet du composant.
     * @param closable  Si true indique qu'il y a une croix de fermeture, sinon false (valeur par défaut).
     */
    public void addTab(String title, String icon, Component component, String tooltip, boolean closable) {
        addTab(title, Macros.getIcon(icon), component, tooltip, closable);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    public void addTab(String title, Icon icon, Component component, String tooltip, boolean closable) {
        super.addTab(title, icon, component, tooltip);
        if (closable) {
            setTabComponentAt(indexOfComponent(component), new TabPanel());
        }
        setSelectedComponent(component);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    public void addTab(String title, String icon, Component component, String tooltip) {
        addTab(title, icon, component, tooltip, false);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    @Override
    public void addTab(String title, Icon icon, Component component, String tooltip) {
        addTab(title, icon, component, tooltip, false);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    public void addTab(String title, String icon, Component component) {
        addTab(title, icon, component, null, false);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    @Override
    public void addTab(String title, Icon icon, Component component) {
        addTab(title, icon, component, null, false);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    public void addTab(String title, Component component, String tooltip, boolean closable) {
        addTab(title, (Icon) null, component, tooltip, closable);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    public void addTab(String title, Component component, String tooltip) {
        addTab(title, (Icon) null, component, tooltip, false);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    public void addTab(String title, Component component, boolean closable) {
        addTab(title, (Icon) null, component, null, closable);
    }

    /**
     * @see #addTab(String, String, java.awt.Component, String, boolean)
     */
    @Override
    public void addTab(String title, Component component) {
        addTab(title, (Icon) null, component, null, false);
    }

    /**
     * Procède à la fermeture d'un composant du panneau.
     * <p>Cette routine doit être surchargée pour (i) effectuer les opérations necéssaires à la fermeture et (ii) autoriser ou non cette fermeture. </p>
     * <p>La fermeture est autorisée par défaut.</p>
     *
     * @param index Index du composant dans le panneau.
     * @return La valeur true si on peut procéder à la fermeture, false sinon.
     */
    protected boolean isCloseable(int index) {
        return true;
    }

    @Override
    public void setTitleAt(int index, String title) {
        super.setTitleAt(index, title);
        getTabComponentAt(index).invalidate();
    }

    // Implementation du composant permettant de gérer la fermeture
    private class TabPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        public TabPanel() {
            super(new FlowLayout(FlowLayout.LEFT, 0, 0));
            setOpaque(false);

            JLabel label = new JLabel() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getText() {
                    int i = TabbedPane.this.indexOfTabComponent(TabPanel.this);
                    return i == -1 ? null : TabbedPane.this.getTitleAt(i);
                }
            };
            add(label);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            JButton button = new TabButton();
            add(button);
            setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        }

        private class TabButton extends JButton implements ActionListener {
            private static final long serialVersionUID = 1L;

            public TabButton() {
                int size = 17;
                setPreferredSize(new Dimension(size, size));
                setToolTipText("Fermer cet onglet");
                setUI(new BasicButtonUI());
                setContentAreaFilled(false);
                setFocusable(false);
                setBorder(BorderFactory.createEtchedBorder());
                setBorderPainted(false);
                addMouseListener(buttonMouseListener);
                setRolloverEnabled(true);
                addActionListener(this);
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                int i = TabbedPane.this.indexOfTabComponent(TabPanel.this);
                if ((i != -1) && TabbedPane.this.isCloseable(i)) {
                    TabbedPane.this.remove(i);
                }
            }

            @Override
            public void updateUI() {
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                if (getModel().isPressed()) {
                    g2.translate(1, 1);
                }
                g2.setStroke(new BasicStroke(2));
                g2.setColor(Color.BLACK);
                if (getModel().isRollover()) {
                    g2.setColor(Color.WHITE);
                }
                int delta = 6;
                g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
                g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
                g2.dispose();
            }

            private MouseListener buttonMouseListener = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    Component component = e.getComponent();
                    if (component instanceof AbstractButton) {
                        AbstractButton button = (AbstractButton) component;
                        button.setBorderPainted(false);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    Component component = e.getComponent();
                    if (component instanceof AbstractButton) {
                        AbstractButton button = (AbstractButton) component;
                        button.setBorderPainted(false);
                    }
                }
            };
        }
    }
}
