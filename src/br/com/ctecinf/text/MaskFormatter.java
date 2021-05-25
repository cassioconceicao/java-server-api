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
public class MaskFormatter extends JFormattedTextField.AbstractFormatter {

    public static final String CPF = "###.###.###-##";
    public static final String CEP = "#####-###";
    public static final String CNPJ = "##.###.###/####-##";
    public static final String PHONE = "## #####-####";

    private static final String MASK_PHONE_8 = "## ####-####";
    private static final String MASK_PHONE_9 = "## #####-####";

    private String mask = "";
    private boolean isPhone = false;

    public MaskFormatter(String mask) {

        this.mask = mask;

        if (mask.equalsIgnoreCase(MASK_PHONE_8) || mask.equalsIgnoreCase(MASK_PHONE_9)) {
            isPhone = true;
        }
    }

    private int length(String m) {

        int length = 0;

        for (char c : m.toCharArray()) {
            if (c == '#') {
                length++;
            }
        }

        return length;
    }

    private final DocumentFilter df = new DocumentFilter() {
        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

            if (!Character.isDigit(text.charAt(length))) {
                return;
            }

            if (isPhone) {

                if (offset == 1) {
                    text += " ";
                }

                if (offset == 6) {
                    text += "-";
                }

                if (offset == MASK_PHONE_9.length() - 1) {
                    super.remove(fb, 7, 1);
                    super.insertString(fb, 8, "-", attrs);
                    super.replace(fb, offset, length, text, attrs);
                    getFormattedTextField().transferFocus();
                    return;
                }

                super.replace(fb, offset, length, text, attrs);

            } else {

                if (offset == mask.length() - 1) {
                    getFormattedTextField().transferFocus();
                }

                if (offset >= mask.length()) {
                    return;
                }

                if (text.length() < length || !Character.isDigit(text.charAt(length))) {
                    return;
                }

                if (mask.charAt(offset) != '#') {
                    text = String.valueOf(mask.charAt(offset)) + text;
                }

                super.replace(fb, offset, length, text, attrs);
            }
        }
    };

    @Override
    public Object stringToValue(String text) throws ParseException {

        if (isPhone) {

            if (text == null || text.isEmpty() || text.length() < MASK_PHONE_9.length() - 1) {
                getFormattedTextField().setValue(null);
                return null;
            }

            return text.replace(".", "").replace("-", "").replace("/", "").replace("(", "").replace(")", "").replace(" ", "").trim();

        } else {

            if (text.isEmpty() || text.length() < mask.length()) {
                getFormattedTextField().setValue(null);
                return null;
            }

            String str = "";

            for (int i = 0; i < mask.toCharArray().length; i++) {
                if (mask.charAt(i) == '#') {
                    str += text.charAt(i);
                }
            }

            return str.isEmpty() ? null : str;
        }
    }

    @Override
    public String valueToString(Object value) throws ParseException {

        if (value == null || !value.getClass().isAssignableFrom(String.class)) {
            return "";
        }

        String str = value.toString().replace(".", "").replace("-", "").replace("/", "").replace("(", "").replace(")", "").replace(" ", "").trim();

        if (isPhone) {

            String maskPhone = MASK_PHONE_8;

            if (str.length() == length(MASK_PHONE_9)) {
                maskPhone = MASK_PHONE_9;
            }

            if (str.length() < length(MASK_PHONE_8)) {
                return "";
            } else {
                return MaskFormatter.format(str, maskPhone);
            }

        } else if (str.length() == length(mask)) {
            return MaskFormatter.format(str, mask);
        }

        return "";
    }

    public static String format(String value, String mask) {

        String str = "";
        int index = 0;

        for (char c : mask.toCharArray()) {

            if (c == '#') {
                str += value.charAt(index);
                index++;
            } else {
                str += c;
            }
        }

        return str;
    }

    @Override
    protected DocumentFilter getDocumentFilter() {
        return df;
    }
}
