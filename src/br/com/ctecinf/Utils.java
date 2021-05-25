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
package br.com.ctecinf;

import br.com.ctecinf.text.DateFormatter;
import br.com.ctecinf.text.NumberFormatter;
import br.com.ctecinf.text.TimeFormatter;
import br.com.ctecinf.text.TimestampFormatter;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;

/**
 *
 * @author Cássio Conceição
 * @since 18/08/2020
 * @version 2008
 * @see http://ctecinf.com.br/
 */
public class Utils {

    /**
     * Converte 'string' para hexadecimal
     *
     * @param value
     * @return String
     */
    public static String hexa(String value) {

        String digVal = "";

        for (int i = 0; i < value.length(); i++) {
            digVal = digVal + Integer.toHexString(value.charAt(i));
        }

        return digVal;
    }

    /**
     * Encriptografa SHA-1
     *
     * @param value
     * @return String
     * @throws br.com.ctecinf.UtilsException
     */
    public static String sha1(String value) throws UtilsException {
        try {
            byte[] messageDigest = MessageDigest.getInstance("SHA-1").digest(value.getBytes());
            return new BigInteger(1, messageDigest).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            throw new UtilsException(ex);
        }
    }

    /**
     * Encriptografa MD5
     *
     * @param value
     * @return String
     * @throws br.com.ctecinf.UtilsException
     */
    public static String md5(String value) throws UtilsException {
        try {
            byte[] messageDigest = MessageDigest.getInstance("MD5").digest(value.getBytes());
            return new BigInteger(1, messageDigest).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            throw new UtilsException(ex);
        }
    }

    /**
     * Calcula módulo 11
     *
     * @param chave
     * @return int
     */
    public static int modulo11(String chave) {

        int total = 0;
        int peso = 2;

        for (int i = 0; i < chave.length(); i++) {

            total += (chave.charAt((chave.length() - 1) - i) - '0') * peso;
            peso++;

            if (peso == 10) {
                peso = 2;
            }
        }

        int resto = total % 11;

        return (resto == 0 || resto == 1) ? 0 : (11 - resto);
    }

    /**
     * Verifica se 'string' é um CPF válido
     *
     * @param cpf
     *
     * @return boolean
     * @throws br.com.ctecinf.UtilsException
     */
    public static boolean isCPF(String cpf) throws UtilsException {

        String[] notValid = {"00000000000", "11111111111", "22222222222", "33333333333", "44444444444", "55555555555", "66666666666", "77777777777", "88888888888", "99999999999"};

        if (cpf == null) {
            return false;
        }

        cpf = cpf.replace(".", "").replace(" ", "").replace("-", "");

        if (Utils.contain(cpf, notValid) > -1 || cpf.length() != 11) {
            return false;
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {

            sm = 0;
            peso = 10;

            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);

            if ((r == 10) || (r == 11)) {
                dig10 = '0';
            } else {
                dig10 = (char) (r + 48);
            }

            sm = 0;
            peso = 11;

            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);

            if ((r == 10) || (r == 11)) {
                dig11 = '0';
            } else {
                dig11 = (char) (r + 48);
            }

            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));

        } catch (InputMismatchException ex) {
            throw new UtilsException(ex);
        }
    }

    /**
     * Converte data para padrão SEFAZ
     *
     * @return String
     * @throws br.com.ctecinf.UtilsException
     */
    public static String date2NFe() throws UtilsException {

        Calendar calendar = dateFromServer();

        String format = "yyyy-MM-dd'T'HH:mm:ss";

        if (calendar.get(Calendar.DST_OFFSET) == 0) {
            format += "'-03:00'";
        } else {
            format += "'-02:00'";
        }

        SimpleDateFormat df = new SimpleDateFormat(format);

        return df.format(calendar.getTime());
    }

    /**
     * Converte 'string' de data da NFe para objeto 'Date' Java
     *
     * @param date
     *
     * @return java.util.Calendar
     * @throws br.com.ctecinf.UtilsException
     */
    public static Calendar dateNFe2Date(String date) throws UtilsException {

        if (date == null || date.isEmpty()) {
            return null;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date dt = null;

        try {
            dt = df.parse(date.replace("-03:00", "").replace("-02:00", ""));
        } catch (ParseException ex) {
            throw new UtilsException(ex);
        }

        Calendar cal = Calendar.getInstance(new Locale("pt", "BR"));
        cal.setTime(dt);

        return cal;
    }

    /**
     * Data e hora do servidor de internet
     *
     * @return java.util.Calendar
     * @throws br.com.ctecinf.UtilsException
     */
    public static Calendar dateFromServer() throws UtilsException {

        Calendar c = Calendar.getInstance(new Locale("pt", "BR"));

        try {

            String server = "http://ctecinf.com.br/current_time_millis.php";

            URL url = new URL(server);

            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(4000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String str = reader.readLine();

            c.setTimeInMillis(Long.parseLong(str));

        } catch (IOException | NumberFormatException ex) {
            throw new br.com.ctecinf.UtilsException(ex);
        }

        return c;
    }

    /**
     * Formata valor percentual
     *
     * @param value
     * @return String
     */
    public static String perc2String(double value) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getPercentInstance(new Locale("pt", "BR"));
        df.setMaximumFractionDigits(2);
        return df.format(value / 100);
    }

    /**
     * Preenche valores a direita
     *
     * @param input
     * @param width
     * @param ch
     * @return String
     */
    public static String rightPad2(Object input, int width, char ch) {

        StringBuilder sb = new StringBuilder(String.valueOf(input).trim());

        while (sb.length() < width) {
            sb.insert(sb.length(), ch);
        }

        String strPad = sb.toString();

        if (strPad.length() > width) {
            strPad = strPad.substring(0, width);
        }

        return strPad;
    }

    /**
     * Preenche valores a esquerda
     *
     * @param input
     * @param width
     * @param ch
     * @return String
     */
    public static String leftPad2(Object input, int width, char ch) {

        StringBuilder sb = new StringBuilder(String.valueOf(input).trim());

        while (sb.length() < width) {
            sb.insert(0, ch);
        }

        String strPad = sb.toString();

        if (strPad.length() > width) {
            strPad = strPad.substring(0, width);
        }

        return strPad;
    }

    /**
     * Transforma objeto (DOMSource | JAXBSource) em (StreamResult | DOMResult)
     *
     * @param <T>
     * @param source
     * @param result DOMResult (Document) | StreamResult (ByteArrayStreamResult)
     * @return javax.xml.transform.Result
     * @throws br.com.ctecinf.UtilsException
     */
    public static <T extends Result> Result transform(Source source, T result) throws UtilsException {

        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            transformer.transform(source, result);

            if (result instanceof DOMResult) {

                DOMResult domResult = (DOMResult) result;

                if (domResult.getNode() instanceof Document) {

                    Document document = (Document) domResult.getNode();

                    if (document.getDocumentElement().hasAttribute("xmlns:ns2")) {
                        document.getDocumentElement().removeAttribute("xmlns:ns2");
                    }
                }
            }

            return result;

        } catch (TransformerConfigurationException ex) {
            throw new UtilsException(ex);
        } catch (TransformerException ex) {
            throw new UtilsException(ex);
        }
    }

    /**
     * Limpa acentos
     *
     * @param text
     * @param upperCase
     * @return String
     */
    public static String ascii(String text, boolean upperCase) {
        return Normalizer.normalize(upperCase ? text.toUpperCase() : text, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Transforma 'inputstream' em texto
     *
     * @param inputStream
     * @return String
     *
     * @throws UtilsException
     */
    public static String inputStream2String(InputStream inputStream) throws UtilsException {

        StringBuilder str = new StringBuilder("");

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1)) {

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                str.append(line).append("\n");
            }

        } catch (IOException ex) {
            throw new UtilsException(ex);
        }

        return str.toString().isEmpty() ? null : str.toString();
    }

    /**
     * Cria um arquivo novo
     *
     * @param path Caminho
     * @param content Conteúdo
     * @return java.io.File
     * @throws br.com.ctecinf.UtilsException
     */
    public static File writeFile(String path, String content) throws UtilsException {
        return writeFile(new File(path), content, false, false);
    }

    /**
     * Cria um arquivo
     *
     * @param path Caminho
     * @param content Conteúdo
     * @param append Concatenar
     * @return java.io.File
     * @throws br.com.ctecinf.UtilsException
     */
    public static File writeFile(String path, String content, boolean append) throws UtilsException {
        return writeFile(new File(path), content, append, false);
    }

    /**
     * Cria um arquivo
     *
     * @param path Caminho
     * @param content Conteúdo
     * @param append Concatenar
     * @param appendIni No início do conteúdo
     * @return java.io.File
     * @throws br.com.ctecinf.UtilsException
     */
    public static File writeFile(String path, String content, boolean append, boolean appendIni) throws UtilsException {
        return writeFile(new File(path), content, append, appendIni);
    }

    /**
     * Cria um arquivo novo
     *
     * @param file Arquivo
     * @param content Conteúdo
     * @return java.io.File
     * @throws br.com.ctecinf.UtilsException
     */
    public static File writeFile(File file, String content) throws UtilsException {
        return writeFile(file, content, false, false);
    }

    /**
     * Cria um arquivo
     *
     * @param file Arquivo
     * @param content Conteúdo
     * @param append Concatenar
     * @return java.io.File
     * @throws br.com.ctecinf.UtilsException
     */
    public static File writeFile(File file, String content, boolean append) throws UtilsException {
        return writeFile(file, content, append, false);
    }

    /**
     * Cria um arquivo
     *
     * @param file Arquivo
     * @param content Conteúdo
     * @param append Concatenar
     * @param appendIni No início do conteúdo
     * @return java.io.File
     * @throws br.com.ctecinf.UtilsException
     */
    public static File writeFile(File file, String content, boolean append, boolean appendIni) throws UtilsException {

        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }

            if (appendIni) {
                String str = readFile(file);
                content += "\n" + str;
                append = false;
            }

            try (PrintWriter p = new PrintWriter(new FileWriter(file, append))) {
                p.write(content);
            }

            return file;

        } catch (IOException | UtilsException ex) {
            throw new UtilsException(ex);
        }
    }

    /**
     * Lê um arquivo
     *
     * @param path Caminho
     * @return String Conteúdo do arquivo
     * @throws br.com.ctecinf.UtilsException
     */
    public static String readFile(String path) throws UtilsException {
        return readFile(new File(path));
    }

    /**
     * Lê um arquivo
     *
     * @param file Arquivo
     * @return String Conteúdo do arquivo
     * @throws br.com.ctecinf.UtilsException
     */
    public static String readFile(File file) throws UtilsException {

        if (file == null || !file.exists()) {
            throw new UtilsException("Arquivo não encontrado.");
        }

        StringBuilder str = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                str.append(line);
                str.append("\n");
            }

        } catch (IOException ex) {
            throw new UtilsException(ex);
        }

        return str.toString().isEmpty() ? null : str.toString().trim();
    }

    /**
     * Converte arquivo em bytes
     *
     * @param path
     * @return byte[]
     * @throws br.com.ctecinf.UtilsException
     */
    public static byte[] fileToByte(String path) throws UtilsException {
        return fileToByte(new File(path));
    }

    /**
     * Converte arquivo em bytes
     *
     * @param file
     * @return
     * @throws br.com.ctecinf.UtilsException
     */
    public static byte[] fileToByte(File file) throws UtilsException {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            throw new UtilsException(ex);
        }
    }

    /**
     * Limita tamanho das palavras no texto
     *
     * @param text
     * @param numCharsWord
     * @return String
     */
    public static String limitWords(String text, int numCharsWord) {

        String[] arr = text.split(" ");

        String str = "";

        for (String s : arr) {
            if (s.length() > numCharsWord) {
                str += s.substring(0, 3) + " ";
            } else {
                str += s + " ";
            }
        }

        return str;
    }

    /**
     * Endereço IP
     *
     * @return String IP
     * @throws br.com.ctecinf.UtilsException
     */
    public static String getMyIP() throws UtilsException {

        try {

            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface netint : Collections.list(nets)) {

                if (!netint.isLoopback() && netint.isUp()) {

                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        if (inetAddress.isSiteLocalAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException ex) {
            throw new UtilsException(ex);
        }

        return null;
    }

    /**
     * Cria um clone de objeto
     *
     * @param obj Objeto Serializable
     * @return Object
     * @throws br.com.ctecinf.UtilsException
     */
    public static Object clone(Object obj) throws UtilsException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException ex) {
            throw new UtilsException(ex);
        }

        byte[] bytes = bos.toByteArray();

        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw new UtilsException(ex);
        }
    }

    /**
     * Cria uma lista <i>String</i>
     *
     * @param glue Separador
     * @param arr Array
     * @return String
     */
    public static String implode(String glue, Object... arr) {
        return implode(glue, Arrays.asList(arr));
    }

    /**
     * Cria uma lista <i>String</i>
     *
     * @param glue Separador
     * @param arr Array
     * @return String
     */
    public static String implode(String glue, List arr) {
        return implode(glue, arr, null);
    }

    /**
     * Cria uma lista <i>String</i>
     *
     * @param glue Separador
     * @param arr Array
     * @param value Valor fixo para substituição
     * @return String
     */
    public static String implode(String glue, List arr, String value) {

        String str = "";

        for (int i = 0; i < arr.size(); i++) {

            Object vl = value == null ? arr.get(i) : value;

            str += arr.size() == (i + 1) ? vl : vl + glue;
        }

        return str;
    }

    /**
     * Cria uma list de valores fixo
     *
     * @param <T>
     * @param size
     * @param value
     * @return
     */
    public static <T> T[] createFixedList(int size, T value) {

        List<T> list = new ArrayList();

        for (int i = 0; i < size; i++) {
            list.add(value);
        }

        return (T[]) list.toArray();
    }

    /**
     * Verifica se contém valor numa determinada lista
     *
     * @param value
     * @param list
     * @return int Index do list e -1 para não contém
     */
    public static int contain(Object value, Object[] list) {

        if (value == null) {
            return -1;
        }

        for (int i = 0; i < list.length; i++) {
            Object obj = list[i];
            if (obj != null && obj.toString().equalsIgnoreCase(value.toString())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Retorna todos arquivo incluido dentro do diretorio e subdiretorios
     *
     * @param path Caminho raiz
     * @param endsWith Extensao do arquivo para filtrar
     *
     * @return java.util.List
     * @throws br.com.ctecinf.UtilsException
     *
     */
    public static final List<File> listFiles(String path, final String endsWith) throws UtilsException {

        final List<File> files = new ArrayList();

        File file = new File("src");

        try {

            Files.walkFileTree(file.toPath(), new SimpleFileVisitor() {

                @Override
                public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {

                    if (attrs.isRegularFile() && file.toString().endsWith(endsWith)) {
                        files.add(new File(file.toString()));
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException ex) {
            throw new UtilsException(ex);
        }

        return files;
    }

    /**
     * Limpa caracteres telefone
     *
     * @param value
     * @return String
     */
    public static String cleanPhone(String value) {

        String str = "";

        if (value == null) {
            return null;
        }

        String[] aux = value.split("/");

        if (aux.length == 1) {
            aux = value.split(" - ");
        }

        for (char c : aux[0].toCharArray()) {
            if (Character.isDigit(c)) {
                str += c;
            }
        }

        if (str.equals("0000000000")) {
            return null;
        }

        if (str.startsWith("051") || str.startsWith("055") || str.startsWith("053") || str.startsWith("054") || str.startsWith("052")) {
            str = str.substring(1);
        }

        if (str.length() == 8 && (str.startsWith("9") || str.startsWith("8"))) {
            str = "519" + str;
        }

        if (str.length() == 8) {
            str = "51" + str;
        }

        if (str.length() == 9) {
            str = "51" + str;
        }

        return str;
    }

    /**
     * Formata valor do objeto para 'string'
     *
     * @param value
     * @return String
     */
    public static String toString(Object value) {

        if (value == null) {
            return "";
        }

        if (value.getClass().isAssignableFrom(Long.class) || value.getClass().isAssignableFrom(Integer.class) || value.getClass().isAssignableFrom(long.class) || value.getClass().isAssignableFrom(int.class)) {
            return NumberFormatter.format(0).format(value);
        } else if (value.getClass().isAssignableFrom(Double.class) || value.getClass().isAssignableFrom(Float.class) || value.getClass().isAssignableFrom(double.class) || value.getClass().isAssignableFrom(float.class) || value.getClass().isAssignableFrom(BigDecimal.class)) {
            return NumberFormatter.format(2).format(value);
        } else if (value.getClass().isAssignableFrom(Boolean.class) || value.getClass().isAssignableFrom(boolean.class)) {
            return ((Boolean) value) ? "Sim" : "Não";
        } else if (value.getClass().isAssignableFrom(java.sql.Date.class) || value.getClass().isAssignableFrom(java.util.Date.class)) {
            return DateFormatter.format().format(value);
        } else if (value.getClass().isAssignableFrom(Timestamp.class)) {
            return TimestampFormatter.format().format(value);
        } else if (value.getClass().isAssignableFrom(Time.class)) {
            return TimeFormatter.format().format(value);
        }

        return value.toString().trim();
    }

    /**
     * Calcula os segundos
     *
     * @param start System.currentTimeMillis() no início do prrocesso long.
     * @return long
     */
    public static long getDif(long start) {
        return (System.currentTimeMillis() - start) / 1000;
    }

    /**
     * Retorna representação em string Base64 de um arquivo
     *
     * @param path Caminho do arquivo
     * @return String
     * @throws br.com.ctecinf.UtilsException
     */
    public static String encodeFileToBase64(String path) throws UtilsException {

        File file = new File(path);

        if (!file.exists()) {
            throw new UtilsException("File not exists.");
        }

        try (FileInputStream fileInputStreamReader = new FileInputStream(file)) {

            byte[] bytes = new byte[(int) file.length()];
            fileInputStreamReader.read(bytes);
            String str = DatatypeConverter.printBase64Binary(bytes);

            Utils.writeFile(file.getAbsolutePath() + "_base64.txt", str);

            return str;

        } catch (IOException ex) {
            throw new UtilsException(ex);
        }
    }

    /**
     * Envia conteudo para a impressora.
     *
     * @param printable
     * @throws UtilsException
     */
    public static void print(Printable printable) throws UtilsException {

        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(printable);

        if (pj.printDialog()) {

            try {
                pj.print();
            } catch (PrinterException ex) {
                throw new UtilsException(ex);
            }
        }
    }

    /**
     * Download de arquivo de um site
     *
     * @param urlFile endereço
     * @throws UtilsException
     */
    public static void downloadFile(String... urlFile) throws UtilsException {

        for (String file : urlFile) {

            try {

                URL url = new URL(file);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                int responseCode = httpConn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    String fileName = "";

                    String disposition = httpConn.getHeaderField("Content-Disposition");
                    Double contentLength = Double.valueOf(httpConn.getHeaderField("Content-Length"));
                    String contentType = httpConn.getHeaderField("Content-Type");

                    if (disposition != null) {
                        int index = disposition.indexOf("filename=");
                        if (index > 0) {
                            fileName = disposition.substring(index + 10, disposition.length() - 1);
                        }
                    } else {
                        fileName = file.substring(file.lastIndexOf("/") + 1, file.length());
                    }

                    NumberFormat df = DecimalFormat.getNumberInstance();
                    df.setGroupingUsed(true);
                    df.setMaximumFractionDigits(0);
                    df.setMinimumFractionDigits(0);

                    System.out.println("Baixando [" + contentType + "]: " + fileName);
                    System.out.println("Tamanho: " + df.format(contentLength) + " bytes");

                    try (InputStream inputStream = httpConn.getInputStream()) {

                        File saveFile = new File("lib", fileName);

                        if (!saveFile.getParentFile().exists()) {
                            saveFile.getParentFile().mkdirs();
                        }

                        try (FileOutputStream outputStream = new FileOutputStream(saveFile)) {

                            int bytesRead;
                            byte[] buffer = new byte[4096];

                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }

                        System.out.println("Download concluído...");
                    }

                    httpConn.disconnect();

                } else {
                    httpConn.disconnect();
                    throw new UtilsException("Sem arquivo [" + file + "]. Server replied HTTP code: " + responseCode);
                }

            } catch (IOException ex) {
                throw new UtilsException(ex);
            }
        }
    }

    private static int LAST_LINE = 0;
    private static int INDEX = -1;
    private static String LAST_METHOD = "";

    /**
     * Recuperar nome da classe que invocou método estático
     *
     * @param <T>
     * @param methodName Nome do método invocado
     * @return
     * @throws br.com.ctecinf.UtilsException
     */
    public static <T> Class<T> getClassInvokedStaticMethod(String methodName) throws UtilsException {

        try {

            StackTraceElement s = Thread.currentThread().getStackTrace()[Thread.currentThread().getStackTrace().length - 1];

            File file = new File(s.getFileName().replace(".java", ".class"));
            file.delete();

            InputStream link = Class.forName(s.getClassName()).getResourceAsStream(s.getFileName().replace(".java", ".class"));

            Files.copy(link, file.getAbsoluteFile().toPath());

            Process p = Runtime.getRuntime().exec("javap -c " + file);

            List<Class<?>> classes = new ArrayList();

            try (BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()))) {

                String line;
                boolean isMethod = false;

                while ((line = bf.readLine()) != null) {

                    if (line.trim().contains(" " + s.getMethodName() + "(")) {
                        isMethod = true;
                    }

                    if (isMethod && line.contains("invokestatic") && line.contains(methodName)) {
                        String[] split = line.trim().split("." + methodName)[0].split(" ");
                        classes.add((Class<?>) Class.forName(split[split.length - 1].trim().replace("/", ".")));
                    }
                }

            } finally {
                file.delete();
            }

            if (methodName.equals(LAST_METHOD)) {

                if (s.getLineNumber() > LAST_LINE) {
                    INDEX++;
                } else if (s.getLineNumber() < LAST_LINE) {
                    INDEX = 0;
                }

            } else {
                INDEX = 0;
            }

            LAST_METHOD = methodName;
            LAST_LINE = s.getLineNumber();

            return (Class<T>) classes.get(INDEX);

        } catch (ClassNotFoundException | IOException ex) {
            throw new UtilsException(ex);
        }
    }
}
