/*
 * Copyright (C) 2021 ctecinf.com.br
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package br.com.ctecinf.database;

import javax.swing.JOptionPane;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public class DatabaseException extends Exception {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }

    public void show() {
        JOptionPane.showMessageDialog(null, this.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public String getMessage() {

        StringBuilder sb = new StringBuilder();
        sb.append("Exception: ").append(super.getMessage()).append("\n");

        for (StackTraceElement stackTrace : getStackTrace()) {
            if (stackTrace.getClassName().contains("ctecinf")) {
                sb.append("Class: ").append(stackTrace.getClassName()).append("\n");
                sb.append("File: ").append(stackTrace.getFileName()).append("\n");
                sb.append("Line: ").append(stackTrace.getLineNumber()).append("\n");
                sb.append("Method: ").append(stackTrace.getMethodName());
                break;
            }
        }

        System.err.println(sb);
        
        return sb.toString();
    }
}
