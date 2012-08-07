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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class SystemOutputController {
    private String result = "";
    private PrintStream oldOut = null, oldErr = null;
    private boolean listening = false;
    private ArrayList<SystemOutputListener> listeners=new ArrayList<SystemOutputListener>();

    public SystemOutputController() {
    }

    public void startListening() {
        //this.oldErr=System.err;
        this.oldOut = System.out;
        result = "";
        redirectSystemStreams();
        listening = true;
    }

    public void startListening(SystemOutputListener listener) {
        if(listener!=null){
            if(!listeners.contains(listener))
                listeners.add(listener);
        }
        this.oldOut = System.out;
        result = "";
        redirectSystemStreams();
        listening = true;
    }

    public void stopListening() {
        System.setOut(this.oldOut);
        this.oldOut = this.oldErr = null;
        listening = false;
    }

    public String getResult() {
        return result;
    }

    private void updateTextPane(final String text) {
        for(SystemOutputListener l:listeners){
            l.print(text);
        }
        result += text;
    }

    private void redirectSystemStreams() {
        System.setOut(new PrintStream(new SystemOutputStream(SystemOutputStream.OUT), true));
        //System.setErr(new PrintStream(new SystemOutputStream(SystemOutputStream.ERR), true));
    }

    private class SystemOutputStream extends OutputStream {

        final static int OUT = 0;
        final static int ERR = 1;

        private int type = 0;

        public SystemOutputStream(int outType) {
            super();
            type = outType;
        }

        @Override
        public void write(final int b) throws IOException {
            updateTextPane(String.valueOf((char) b));
            switch (type) {
                case OUT:
                    if (oldOut != null) oldOut.write(b);
                case ERR:
                    if (oldErr != null) oldErr.write(b);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            updateTextPane(new String(b, off, len));
            switch (type) {
                case OUT:
                    if (oldOut != null) oldOut.write(b, off, len);
                case ERR:
                    if (oldErr != null) oldErr.write(b, off, len);
            }
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }
    }

    public static interface SystemOutputListener {
         public void print(String out);
    }
}
