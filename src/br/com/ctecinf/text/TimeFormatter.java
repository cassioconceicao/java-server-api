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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;
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
public class TimeFormatter extends JFormattedTextField.AbstractFormatter {

    private int length = 4;
    private String mask = "##:##";
    private static final DateFormat DTF = DateFormat.getTimeInstance(DateFormat.SHORT, new Locale("pt", "BR"));

    private final DocumentFilter df = new DocumentFilter() {
        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

            if (offset == mask.length() - 1) {
                getFormattedTextField().transferFocus();
            }

            if (offset >= mask.length()) {
                return;
            }

            if (!Character.isDigit(text.charAt(length))) {
                return;
            }

            if (mask.charAt(offset) != '#') {
                text = String.valueOf(mask.charAt(offset)) + text;
            }

            super.replace(fb, offset, length, text, attrs);
        }
    };

    @Override
    public Object stringToValue(String text) throws ParseException {

        if (text.isEmpty() || text.length() < mask.length()) {
            getFormattedTextField().setValue(null);
            return null;
        }

        return new java.sql.Time(DTF.parse(text).getTime());
    }

    @Override
    public String valueToString(Object value) throws ParseException {

        if (value instanceof String && value.toString().length() == length) {

            String str = "";
            int index = 0;

            for (char c : mask.toCharArray()) {

                if (c == '#') {
                    str += value.toString().charAt(index);
                    index++;
                } else {
                    str += c;
                }
            }

            return str;
        }

        return value == null ? "" : DTF.format(value);
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return df;
    }

    public static DateFormat format() {
        return DTF;
    }
}
