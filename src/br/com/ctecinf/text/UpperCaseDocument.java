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

import java.text.Normalizer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Cássio Conceição
 * @since 01/11/2019
 * @version 1911
 * @see http://ctecinf.com.br/
 */
public class UpperCaseDocument extends PlainDocument {

    private final DocUpperFilter df = new DocUpperFilter();

    @Override
    public DocumentFilter getDocumentFilter() {
        return df;
    }

    public static class DocUpperFilter extends DocumentFilter {

        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            super.replace(fb, offset, length, Normalizer.normalize(text.toUpperCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", ""), attrs);
        }
    }
}
