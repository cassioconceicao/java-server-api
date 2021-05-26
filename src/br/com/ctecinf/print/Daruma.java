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
package br.com.ctecinf.print;

import br.com.ctecinf.text.MaskFormatter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author Cássio Conceição
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class Daruma {

    public static final int FONT_SIZE_DEFAULT = 1;
    public static final int FONT_SIZE_MEDIUM = 2;
    public static final int FONT_SIZE_LARGER = 3;
    public static final int FONT_SIZE_EXTRA = 4;

    public static final char[] ALIGN_LEFT = {0x1B, 0x6A, 0};
    public static final char[] ALIGN_CENTER = {0x1B, 0x6A, 1};
    public static final char[] ALIGN_RIGHT = {0x1B, 0x6A, 2};

    private static final char[] TAB = {0x09};

    private static final char[] CHAR_SIZE_0 = {0x1B, 0x21, 0x00};
    private static final char[] CHAR_SIZE_1 = {0x1B, 0x21, 0x01};
    private static final char[] CHAR_SIZE_2 = {0x1B, 0x21, 0x30};
    private static final char[] CHAR_SIZE_3 = {0x1B, 0x21, 0x31};

    private static final char[] START_ITALIC = {0x1B, 0x34, 1};
    private static final char[] END_ITALIC = {0x1B, 0x34, 0};

    private static final char[] START_BOLD = {0x1B, 0x45};
    private static final char[] END_BOLD = {0x1B, 0x46};

    private static final char[] START_TEXT_COND = {0x0F};
    private static final char[] END_TEXT_COND = {0x12};

    private static final char[] TEXT_EXPAND = {0x0E};
    private static final char[] TEXT_NORMAL = {0x14};

    private static final char[] GUILHOTINA = {0x6d};

    private OutputStream outputStream;
    private Integer columns;

    public Daruma() throws DarumaException {

        try {

            Properties properties = getProperties();

            outputStream = new FileOutputStream(properties.getProperty("port"));
            columns = Integer.valueOf(properties.getProperty("columns"));

        } catch (FileNotFoundException | DarumaException ex) {
            throw new DarumaException(ex);
        }
    }

    protected final Properties getProperties() throws DarumaException {

        Properties properties = new Properties();

        try {

            File file = new File("config", "daruma.properties");

            if (!file.exists()) {

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                properties.setProperty("port", "/dev/ttyUSB0");
                properties.setProperty("columns", "57");

                properties.setProperty("head_razao_social", "");
                properties.setProperty("head_cnpj", "");
                properties.setProperty("head_insc_estadual", "");
                properties.setProperty("head_logradouro", "");
                properties.setProperty("head_numero", "");
                properties.setProperty("head_complemento", "");
                properties.setProperty("head_bairro", "");
                properties.setProperty("head_municipio", "");
                properties.setProperty("head_uf", "");
                properties.setProperty("head_cep", "");
                properties.setProperty("head_fone", "");

                properties.store(new FileOutputStream(file), "Configuração impressora Daruma");

            } else {
                properties.load(new FileInputStream(file));
            }

        } catch (IOException ex) {
            throw new DarumaException(ex);
        }

        return properties;
    }

    /**
     *
     * @param value
     * @return byte[]
     */
    private byte[] byteArray(int... value) {

        byte[] arr = new byte[value.length];

        for (int i = 0; i < value.length; i++) {
            arr[i] = (byte) value[i];
        }

        return arr;
    }

    /**
     * QrCode
     *
     * @param str
     * @throws DarumaException
     */
    public void qrCode(String str) throws DarumaException {

        int tamanho = str.length() + 3;

        int tamI;
        int tamF;

        if (tamanho > 255) {
            tamI = tamanho % 255;
            tamF = tamanho / 255;
        } else {
            tamI = tamanho;
            tamF = 0;
        }

        try {
            outputStream.write(byteArray(27, 129));
            outputStream.write(byteArray((tamI - 1), tamF, 4, 4));
            outputStream.write(str.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * 12 dígitos de 0 a 9
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void ean13Bar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(1));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * 7 dígitos de 0 a 9
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void ean8Bar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Standard 2 of 5 (Industrial): 0 a 9. Sem dígito de verificação
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void s2of5Bar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(3));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Interleaved 2 of 5: tamanho sempre par. 0 a 9. Sem dígito de verificação
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void i2of5Bar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(4));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Tamanho variável. Todos os caracteres ASCII.
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void code128Bar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(5));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Tamanho variável. 0-9, A-Z, '-', '.', '%', '/', '$', ' ', '+' O caracter
     * '*' de start/stop é inserido automaticamente. Sem dígito de verificação
     * MOD 43
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void code39Bar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(6));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Tamanho variável. 0-9, A-Z, '-', '.', ' ', '$', '/', '+', '%' O caracter
     * '*' de start/stop é inserido automaticamente.
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void code93Bar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(7));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * 11 dígitos de 0 a 9
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void upcABar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(8));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Tamanho variável. 0 - 9, '$', '-', ':', '/', '.', '+' Existem 4
     * diferentes caracteres de start/stop: A, B, C, and D que são usados em
     * pares e não podem aparecer em nenhum outro lugar do código. Sem dígito de
     * verificação
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void codaBar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(9));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Tamanho variável. 0 - 9. 1 dígito de verificação sn ≤ 50
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void msiBar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(10));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Tamanho variável. 0 a 9 Checksum de dois caracteres.
     *
     * @param code
     * @param size 50..200
     * @param showText Exibir número abaixo
     * @throws DarumaException
     */
    public void code11Bar(String code, int size, boolean showText) throws DarumaException {

        try {
            outputStream.write(byteArray(27, 98));
            outputStream.write(byteArray(11));
            outputStream.write(byteArray(2));
            outputStream.write(byteArray(size));
            outputStream.write(showText ? byteArray(1) : byteArray(0));
            outputStream.write(code.getBytes());
            outputStream.write(byteArray(0));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Tamanho da Fonte
     *
     * @param size 1..4
     * @throws DarumaException
     */
    public void fonteSize(int size) throws DarumaException {

        try {

            switch (size) {
                case 2:
                    outputStream.write(new String(CHAR_SIZE_1).getBytes());
                    break;
                case 3:
                    outputStream.write(new String(CHAR_SIZE_2).getBytes());
                    break;
                case 4:
                    outputStream.write(new String(CHAR_SIZE_3).getBytes());
                    break;
                default:
                    outputStream.write(new String(CHAR_SIZE_0).getBytes());
                    break;
            }

        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Saltar linhas
     *
     * @param numRows Número de linhas para saltar
     * @throws DarumaException
     */
    public void breakLines(int numRows) throws DarumaException {
        try {
            for (int i = 0; i < numRows; i++) {
                outputStream.write("\n".getBytes());
            }
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Alinhamento
     *
     * @param align
     * @throws DarumaException
     */
    public void align(char[] align) throws DarumaException {
        try {
            outputStream.write(new String(align).getBytes());
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Tabulação
     *
     * @throws DarumaException
     */
    public void tab() throws DarumaException {
        try {
            outputStream.write(new String(TAB).getBytes());
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Nova linha
     *
     * @throws DarumaException
     */
    public void newLine() throws DarumaException {
        breakLines(1);
    }

    /**
     * Linha de corte
     *
     * @throws DarumaException
     */
    public void dotLine() throws DarumaException {
        newLine();
        align(ALIGN_CENTER);
        startCond();
        for (int i = 0; i < columns; i++) {
            text("-");
        }
        endCond();
        newLine();
    }

    /**
     * Inicia texto condensado
     *
     * @throws DarumaException
     */
    public void startCond() throws DarumaException {
        try {
            outputStream.write(new String(START_TEXT_COND).getBytes());
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Termina texto condensado
     *
     * @throws DarumaException
     */
    public void endCond() throws DarumaException {
        try {
            outputStream.write(new String(END_TEXT_COND).getBytes());
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Escreve texto
     *
     * @param str tag 'b' para texto negrito e tag 'i' para italico são aceita
     * @throws DarumaException
     */
    public void text(String str) throws DarumaException {

        try {

            int indexBold = str.toLowerCase().indexOf("<b>");
            int indexItalic = str.toLowerCase().indexOf("<i>");

            if (indexBold > -1 || indexItalic > -1) {

                int index;
                String tag;

                if (indexBold == -1) {
                    index = indexItalic;
                    tag = "</i>";
                } else if (indexItalic == -1) {
                    index = indexBold;
                    tag = "</b>";
                } else {
                    index = indexBold < indexItalic ? indexBold : indexItalic;
                    tag = indexBold < indexItalic ? "</b>" : "</i>";
                }

                if (index > 0) {
                    outputStream.write(str.substring(0, index).getBytes());
                    text(str.substring(index));
                } else if (index == 0) {

                    int end = str.toLowerCase().indexOf(tag);

                    if (tag.equalsIgnoreCase("</b>")) {
                        bold(str.substring(3, end));
                    } else {
                        italic(str.substring(3, end));
                    }

                    if ((end + 4) < str.length()) {
                        text(str.substring(end + 4));
                    }
                }

            } else {
                outputStream.write(str.getBytes());
            }

        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Excreve texto normal
     *
     * @param str
     * @throws DarumaException
     */
    public void textNormal(String str) throws DarumaException {

        try {
            outputStream.write(new String(TEXT_NORMAL).getBytes());
            text(str);
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Escreve texto expandido
     *
     * @param str
     * @throws DarumaException
     */
    public void textExpand(String str) throws DarumaException {

        try {
            outputStream.write(new String(TEXT_EXPAND).getBytes());
            text(str);
            outputStream.write(new String(TEXT_NORMAL).getBytes());
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Escreve linha de texto condensado
     *
     * @param str
     * @param align
     * @throws DarumaException
     */
    public void lineCond(String str, char[] align) throws DarumaException {
        newLine();
        startCond();
        align(align);
        text(str);
        endCond();
    }

    /**
     * Escreve em negrito
     *
     * @param str
     * @throws DarumaException
     */
    public void bold(String str) throws DarumaException {

        try {
            outputStream.write(new String(START_BOLD).getBytes());
            outputStream.write(str.getBytes());
            outputStream.write(new String(END_BOLD).getBytes());
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Escreve em itálico
     *
     * @param str
     * @throws DarumaException
     */
    public void italic(String str) throws DarumaException {

        try {
            outputStream.write(new String(START_ITALIC).getBytes());
            outputStream.write(str.getBytes());
            outputStream.write(new String(END_ITALIC).getBytes());
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Sinal sonoro
     *
     * @throws DarumaException
     */
    public void beep() throws DarumaException {
        try {
            outputStream.write(byteArray(7));
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Encerra Stream
     *
     * @throws DarumaException
     */
    public void end() throws DarumaException {

        try {
            outputStream.write(byteArray(0));
            outputStream.write(new String(CHAR_SIZE_1).getBytes());
            outputStream.write(byteArray(7));
            outputStream.write(new String(GUILHOTINA).getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new DarumaException(ex);
        }
    }

    /**
     * Imprimi Cabeçalho
     *
     * @throws DarumaException
     */
    public void head() throws DarumaException {

        Properties p = getProperties();

        fonteSize(1);
        startCond();
        align(ALIGN_CENTER);
        bold(p.getProperty("head_razao_social"));

        newLine();
        align(ALIGN_CENTER);
        text("CNPJ " + MaskFormatter.format(p.getProperty("head_cnpj"), "##.###.###/####-##") + "    IE " + p.getProperty("head_insc_estadual"));

        newLine();
        align(ALIGN_CENTER);
        text(p.getProperty("head_logradouro") + ", " + p.getProperty("head_numero") + ", " + p.getProperty("head_complemento"));

        newLine();
        align(ALIGN_CENTER);
        text(p.getProperty("head_bairro") + " - " + p.getProperty("head_municipio") + " - " + p.getProperty("head_uf"));

        newLine();
        align(ALIGN_CENTER);
        text("CEP " + p.getProperty("head_cep") + "  Fone " + MaskFormatter.format(p.getProperty("head_fone"), "(##) ####-####"));

        startCond();
        newLine();
        newLine();
    }
}
