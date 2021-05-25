/*
 * Copyright (C) 2020 ctecinf.com.br
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

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Cássio Conceição
 * @since 05/09/2020
 * @version 2009
 * @see http://ctecinf.com.br/
 */
public class JavaDocument extends DefaultStyledDocument {

    private final String MATCHES = "(\\W)*(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|extends|false|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|native|new|null|package|private|protected|public|return|short|static|strictfp|super|switch|synchronized|this|throw|throws|transient|true|try|void|volatile|while)";
    private final StyleContext STYLE_CONTEXT = StyleContext.getDefaultStyleContext();
    private final AttributeSet ATTR_COMMENT = STYLE_CONTEXT.addAttribute(STYLE_CONTEXT.getEmptySet(), StyleConstants.Foreground, Color.GRAY);
    private final AttributeSet ATTR_STRING = STYLE_CONTEXT.addAttribute(STYLE_CONTEXT.getEmptySet(), StyleConstants.Foreground, Color.RED);
    private final AttributeSet ATTR_RESERVED_WORD = STYLE_CONTEXT.addAttribute(STYLE_CONTEXT.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
    private final AttributeSet ATTR_WORD = STYLE_CONTEXT.addAttribute(STYLE_CONTEXT.getEmptySet(), StyleConstants.Foreground, Color.BLACK);

    private int findLastNonWordChar(String text, int index) {

        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }

        return index;
    }

    private int findFirstNonWordChar(String text, int index) {

        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }

        return index++;
    }

    @Override
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {

        super.insertString(offset, str, a);

        String text = getText(0, getLength());

        int before = findLastNonWordChar(text, offset);

        if (before < 0) {
            before = 0;
        }

        int after = findFirstNonWordChar(text, offset + str.length());

        int wordL = before;
        int wordR = before;

        while (wordR <= after) {

            if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {

                if (text.substring(wordL, wordR).matches(MATCHES)) {
                    setCharacterAttributes(wordL + 1, wordR - wordL, ATTR_RESERVED_WORD, false);
                } else {
                    setCharacterAttributes(wordL, wordR - wordL, ATTR_WORD, false);
                }

                wordL = wordR;
            }

            wordR++;
        }

        Pattern p = Pattern.compile("\\\"(.*?)\\\"");
        Matcher m = p.matcher(text);

        while (m.find()) {
            setCharacterAttributes(m.start(), m.end() - m.start(), ATTR_STRING, false);
        }

        p = Pattern.compile("\\'(.*?)\\'");
        m = p.matcher(text);

        while (m.find()) {
            setCharacterAttributes(m.start(), m.end() - m.start(), ATTR_STRING, false);
        }

        p = Pattern.compile("\\/*(.*?)\\*/");
        m = p.matcher(text);

        while (m.find()) {
            int start = text.substring(0, m.start()).lastIndexOf("/*");
            setCharacterAttributes(start, m.end() - start, ATTR_COMMENT, false);
        }

        p = Pattern.compile("\\//(.*?)\\n");
        m = p.matcher(text);

        while (m.find()) {
            setCharacterAttributes(m.start(), m.end() - m.start(), ATTR_COMMENT, false);
        }
    }

    @Override
    public void remove(int offs, int len) throws BadLocationException {

        super.remove(offs, len);

        String text = getText(0, getLength());

        int before = findLastNonWordChar(text, offs);

        if (before < 0) {
            before = 0;
        }

        int after = findFirstNonWordChar(text, offs);

        if (text.substring(before, after).matches(MATCHES)) {
            setCharacterAttributes(before + 1, after - before, ATTR_RESERVED_WORD, false);
        } else {
            setCharacterAttributes(before, after - before, ATTR_WORD, false);
        }

        Pattern p = Pattern.compile("\\\"(.*?)\\\"");
        Matcher m = p.matcher(text);

        while (m.find()) {
            setCharacterAttributes(m.start(), m.end() - m.start(), ATTR_STRING, false);
        }

        p = Pattern.compile("\\'(.*?)\\'");
        m = p.matcher(text);

        while (m.find()) {
            setCharacterAttributes(m.start(), m.end() - m.start(), ATTR_STRING, false);
        }

        p = Pattern.compile("\\//(.*?)\\n");
        m = p.matcher(text);

        while (m.find()) {
            setCharacterAttributes(m.start(), m.end() - m.start(), ATTR_COMMENT, false);
        }
    }
}
