/*
 * Copyright (C) 2019 ctecinf.com.br
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
package br.com.ctecinf.text;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author Cássio Conceição
 * @since 01/11/2019
 * @version 1911
 * @see http://ctecinf.com.br/
 */
public class PasswordFormatter extends JFormattedTextField.AbstractFormatter {

    private String password = "";

    private final DocumentFilter df = new DocumentFilter() {

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            password = password.substring(0, offset) + text;
            super.replace(fb, offset, length, "*", attrs);
        }

        @Override
        public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
            password = password.substring(0, offset);
            super.remove(fb, offset, length);
        }
    };

    private String md5(String value) {

        byte[] messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("MD5").digest(value.getBytes());
        } catch (NoSuchAlgorithmException ex) {
            System.err.println(ex);
        }

        return new BigInteger(1, messageDigest).toString(16);
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        return text.isEmpty() ? null : md5(password);
    }

    @Override
    public String valueToString(Object value) throws ParseException {

        if (value == null) {
            return null;
        }

        password = value.toString();

        String pass = "";

        for (int i = 0; i < value.toString().length(); i++) {
            pass += "*";
        }

        return pass;
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return df;
    }
}
