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
package br.com.ctecinf.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public abstract class Handler implements HttpHandler {

    public static final String TYPE_PLAIN = "text/plain";
    public static final String TYPE_HTML = "text/html";
    public static final String TYPE_JSON = "application/json";
    public static final String TYPE_PDF = "application/pdf";

    private final String name;
    private final String contentType;

    public Handler() {
        this.name = "";
        this.contentType = Handler.TYPE_PLAIN;
    }

    public Handler(String name) {
        this.name = name;
        this.contentType = Handler.TYPE_PLAIN;
    }

    public Handler(String name, String contentType) {
        this.name = name;
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    /**
     *
     * @param requestParams Parâmetros da requisição
     * @return byte[] response Resposta do servidor para requisição
     * @throws Exception
     */
    protected abstract byte[] getResponse(Map<String, Object> requestParams) throws Exception;

    @Override
    public void handle(HttpExchange he) throws IOException {

        Map<String, Object> params = null;
        String query = null;

        if (he.getRequestMethod().equalsIgnoreCase("get")) {

            String get = he.getRequestURI().getQuery();

            if (get != null && !get.isEmpty()) {
                query = URLDecoder.decode(get, "UTF-8").trim();
            }

        } else if (he.getRequestMethod().equalsIgnoreCase("post")) {

            String post = "";

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(he.getRequestBody()), 1)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    post += line + "\n";
                }
            }

            if (!post.isEmpty()) {
                query = URLDecoder.decode(post, "UTF-8").trim();
            }
        }
        
        if (query != null && !query.isEmpty()) {
            params = parseParams(query);
        }

        try {

            byte[] response = getResponse(params);

            he.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            he.getResponseHeaders().set("Content-Type", getContentType() + "; charset=UTF-8");
            he.sendResponseHeaders(200, response.length);

            try (OutputStream os = he.getResponseBody()) {
                os.write(response, 0, response.length);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex, "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Map<String, Object> parseParams(String query) {

        String[] split = query.split("&");

        Map<String, Object> map = new HashMap();

        for (String sp : split) {

            if (sp != null) {

                int pos = sp.indexOf("=");

                String key = sp.substring(0, pos).trim();
                Object value = sp.substring(pos + 1).trim();

                if (value != null) {

                    SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));

                    try {
                        java.util.Date dt = date.parse(value.toString());
                        value = new java.sql.Date(dt.getTime());
                    } catch (ParseException ex) {
                    }

                    NumberFormat number = NumberFormat.getInstance(new Locale("pt", "BR"));

                    try {
                        Number num = number.parse(value.toString());
                        value = num;
                    } catch (ParseException ex) {
                    }

                    if (value != null) {

                        String vl = value.toString();

                        if (vl.equalsIgnoreCase("yes") || vl.equalsIgnoreCase("on") || vl.equalsIgnoreCase("true")) {
                            value = Boolean.TRUE;
                        }
                    }

                    if (value != null && value.toString().isEmpty()) {
                        value = null;
                    }
                }

                String regex = "\\G[^\\[']*(?:'[^']*'[^\\['*]*)*(\\[[^]']*(?:'[^']*'[^]']*)*\\])";

                if (key.matches(regex)) {

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(key);

                    if (matcher.find()) {

                        key = key.replace(matcher.group(1), "");

                        String reg = "\\[(\\w+)\\]";

                        Pattern pattern1 = Pattern.compile(reg);
                        Matcher matcher1 = pattern1.matcher(matcher.group(1));

                        if (matcher1.find()) {

                            if (!map.containsKey(key)) {
                                map.put(key, new HashMap());
                            }

                            ((Map) map.get(key)).put(matcher1.group(1), value);

                        } else {

                            if (!map.containsKey(key)) {
                                map.put(key, new ArrayList());
                            }

                            ((List) map.get(key)).add(value);
                        }
                    }

                } else {
                    map.put(key, value);
                }
            }
        }

        return map;
    }
}
