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

package org.javascool.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Invoque une méthode sur un objet Java.
 */
public class Invoke {
    /**
     * Invoke une méthode sans argument sur un objet.
     *
     * @param object L'objet sur lequel on invoque la méthode.
     * @param method La méthode sans argument à invoquer, souvent : <tt>init</tt>, <tt>destroy</tt>, <tt>start</tt>, <tt>stop</tt> ou <tt>run</tt>.
     * @param run    Si true (par défaut) appelle la méthode, si false teste simplement son existence.
     * @return La valeur true si la méthode est invocable, false sinon.
     * @throws RuntimeException si la méthode génère une exception lors de son appel.
     */
    public static boolean run(Object object, String method, boolean run) {
        try {
            Method m = object.getClass().getDeclaredMethod(method);
            if (run) {
                m.invoke(object);
            }
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            return false;
        }
        return true;
    }

    /**
     * @see #run(Object, String, boolean)
     */
    public static boolean run(Object object, String method) {
        return run(object, method, true);
    }
}
