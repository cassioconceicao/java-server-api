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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.JFormattedTextField;

/**
 *
 * @author Cássio Conceição
 * @since 01/11/2019
 * @version 1911
 * @see http://ctecinf.com.br/
 */
public class NumberFormatter extends JFormattedTextField.AbstractFormatter {

    private NumberFormat nf;

    /**
     * Construtor para número fracionário
     *
     * @param fractionDigits
     */
    public NumberFormatter(int fractionDigits) {
        setNumberFormat(fractionDigits);
    }

    private void setNumberFormat(int fractionDigits) {

        nf = DecimalFormat.getNumberInstance(new Locale("pt", "BR"));
        nf.setMinimumFractionDigits(fractionDigits);
        nf.setMaximumFractionDigits(fractionDigits);

        if (fractionDigits == 0) {
            nf.setGroupingUsed(false);
            nf.setParseIntegerOnly(true);
        }
    }

    @Override
    public Number stringToValue(String text) throws ParseException {

        return text == null || text.isEmpty() ? new Number() {

            @Override
            public int intValue() {
                return 0;
            }

            @Override
            public long longValue() {
                return 0l;
            }

            @Override
            public float floatValue() {
                return 0f;
            }

            @Override
            public double doubleValue() {
                return 0.00;
            }
        } : nf.parse(text);
    }

    @Override
    public String valueToString(final Object value) throws ParseException {

        if (value == null) {
            return "";
        }

        Number num;

        if (value instanceof Number) {
            num = (Number) value;
        } else {
            
            num = new Number() {

                @Override
                public int intValue() {
                    return Integer.valueOf(value.toString());
                }

                @Override
                public long longValue() {
                    return Long.valueOf(value.toString());
                }

                @Override
                public float floatValue() {
                    return Float.valueOf(value.toString());
                }

                @Override
                public double doubleValue() {
                    return Double.valueOf(value.toString());
                }
            };
        }

        return nf.format(num);
    }

    public static NumberFormat format(int fractionDigits) {

        NumberFormat numberFormat = DecimalFormat.getNumberInstance(new Locale("pt", "BR"));
        numberFormat.setMinimumFractionDigits(fractionDigits);
        numberFormat.setMaximumFractionDigits(fractionDigits);

        if (fractionDigits == 0) {
            numberFormat.setGroupingUsed(false);
            numberFormat.setParseIntegerOnly(true);
        }

        return numberFormat;
    }

    public static NumberFormat format() {
        return format(2);
    }
}
