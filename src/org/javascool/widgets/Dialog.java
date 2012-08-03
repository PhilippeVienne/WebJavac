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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Définit un dialogue en popup qui gère l'aspect modal/non-modal.
 * <p>Si le dialog est appelé de l'AWT il est non-modal (donc ne bloque pas l'AWT) sinon il est modal (il blqieu jusqu'à complétion du dialogue).
 * <p>Son utilisation typique se fait à travers une construction de la forme:<pre>
 * dialog = new NonModalDialog();
 * dialog.add(.. le composant du dialogue ..);
 * dialog.open(true);</pre></p>
 * <p>Lorsque le composant du dialogue reçoit la réponse il ferme le dialogue, par exemple:<pre>
 *  public void actionPerformed(ActionEvent e) {
 *    ... report de la valeur fournie par l'utilisateur ...
 *    messageDialog.close();
 * }</pre></p>
 */
public class Dialog extends JDialog {
    // @bean
    public Dialog() {
        super(MainFrame.getFrame());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pending = false;
            }
        }
        );
    }

    /**
     * Ouvre le dialogue et entre en attente d'un retour de l'utilisateur.
     *
     * @param modal Si true le dialogue est bloquant et attend la réponse de l'utilisateur, si false il est non-modal et renvoie la main AVANT que le dialogue soit complété.
     *              <p>Il ne faut pas appelé le dialogue en mode modal directement d'un gestionnaire d'événement (bouton, etc..) mais utiliser un Thread.</p>
     */
    public void open(boolean modal) {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        if (MainFrame.getFrame() != null) {
            setLocation((MainFrame.getFrame().getWidth() - getWidth()) / 2, (MainFrame.getFrame().getHeight() - getHeight()) / 2);
        }
        setVisible(true);
        if (modal && SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Impossible d'utiliser un dialogue modal directement d'un événement de l'interface: créer un thread");
        }
        pending = modal;
        while (pending) {
            Macros.sleep(100);
        }
    }

    /**
     * Routine à appeler quand le dialogue à été achevé pour continuer le programme.
     */
    public void close() {
        dispose();
        pending = false;
    }

    /**
     * Teste si le dialogue est en cours ou achevé.
     * return La valeur true si le dialogue est en cours, sinon false.
     */
    public boolean isOpen() {
        return pending;
    }

    private boolean pending = false;
}
