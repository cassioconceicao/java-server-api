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
package br.com.ctecinf.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class JSON {

    private final Object root;

    /**
     * Construtor DEFAULT
     *
     * @param root
     */
    public JSON(Object root) {
        this.root = root;
    }

    /**
     * Verifica se valor do campor é um objeto JSONArray
     *
     * @return boolean
     */
    public boolean isJSONArray() {
        return root.getClass().isAssignableFrom(JSONArray.class);
    }

    /**
     * Verifica se valor do campor é um objeto JSONObject
     *
     * @return boolean
     */
    public boolean isJSONObjectValue() {
        return root.getClass().isAssignableFrom(JSONObject.class);
    }

    /**
     * Pega o objeto raíz que pode ou não ser um array
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T> T get(Class<T> type) {
        return type.cast(root);
    }

    /**
     * Cria objeto <i>JSON</i> à partir de arquivo '.json'
     *
     * @param file
     * @return JSON
     * @throws JSONException
     */
    public static JSON parse(File file) throws JSONException {
        return JSON.parse(file, false);
    }

    /**
     * Cria objeto <i>JSON</i> à partir de arquivo '.json'
     *
     * @param file
     * @param caseSensitive
     * @return JSON
     * @throws JSONException
     */
    public static JSON parse(File file, boolean caseSensitive) throws JSONException {

        if (!file.exists()) {
            throw new JSONException("File not found.");
        }

        StringBuilder str = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                str.append(line);
            }
        } catch (IOException ex) {
            throw new JSONException(ex);
        }

        return str.toString().isEmpty() ? null : JSON.parse(str.toString(), caseSensitive);
    }

    /**
     * Cria objeto <i>JSON</i> à partir de <i>String</i> JSON
     *
     * @param str
     * @return JSON
     * @throws JSONException
     */
    public static JSON parse(String str) throws JSONException {
        return JSON.parse(str, false);
    }

    /**
     * Cria objeto <i>JSON</i> à partir de <i>String</i> JSON
     *
     * @param str
     * @param caseSensitive
     * @return JSON
     * @throws JSONException
     */
    public static JSON parse(String str, boolean caseSensitive) throws JSONException {

        str = str.replace("\\n", "$newline$").replace("\n", "$newline$").replaceAll("[ ]{2,}", " ").trim();

        if (str.startsWith("[")) {

            String value = getContent(str.substring(1), "[", ']');
            value = value.substring(0, value.length() - 1);

            JSONArray list = new JSONArray();

            for (String s : value.split("\\} ?\\, ?\\{")) {
                JSONObject j = new JSONObject();
                j.setCaseSensitive(caseSensitive);
                JSON.stringToJSON(s.replace("{", "").replace("}", "").trim(), j);
                list.add(j);
            }

            return new JSON(list);

        } else {

            JSONObject json = new JSONObject();
            json.setCaseSensitive(caseSensitive);
            JSON.stringToJSON(str, json);

            return new JSON(json);
        }
    }

    /**
     * Passa para objeto JSON a string
     *
     * @param str
     * @param json
     */
    private static void stringToJSON(String str, JSONObject json) throws JSONException {

        int pos = 0;
        String[] keyValue = str.split(":");

        for (int i = 0; i < keyValue[0].length(); i++) {
            if (Character.isLetterOrDigit(keyValue[0].charAt(i))) {
                pos = i;
                break;
            }
        }

        String key = keyValue[0].substring(pos);
        pos += keyValue[0].substring(pos).length();

        for (int i = key.length() - 1; i > 0; i--) {
            if (!Character.isLetterOrDigit(key.charAt(i)) && key.charAt(i) != '_') {
                pos++;
                key = key.substring(0, i);
            }
        }

        if (!json.isCaseSensitive()) {
            key = key.toLowerCase();
        }

        String value;

        if (keyValue.length > 1 && !key.isEmpty()) {

            if (keyValue[1].startsWith(" ")) {
                pos++;
                value = keyValue[1].substring(1);
            } else {
                value = keyValue[1];
            }

            if (value.startsWith("[")) {

                pos++;

                value = getContent(str.substring(pos), "[", ']');

                pos += value.length();

                value = value.substring(0, value.length() - 1);

                JSONArray list = new JSONArray();

                for (String s : value.split("\\} ?\\, ?\\{")) {
                    JSONObject j = new JSONObject();
                    j.setCaseSensitive(json.isCaseSensitive());
                    JSON.stringToJSON(s.replace("{", "").replace("}", "").trim(), j);
                    list.add(j);
                }

                json.put(key, list);

            } else if (value.startsWith("{")) {

                pos++;

                value = getContent(str.substring(pos), "{", '}');

                pos += value.length();

                value = value.substring(0, value.length() - 1);

                JSONObject j = new JSONObject();
                j.setCaseSensitive(json.isCaseSensitive());
                JSON.stringToJSON(value, j);
                json.put(key, j);

            } else if (value.startsWith("\"")) {

                pos++;
                value = str.substring(pos).split("\"")[0].replace("$newline$", "\\n").trim();
                pos += value.length() + 1;
                json.put(key, value);

            } else {
                throw new JSONException("Erro na formatação do arquivo JSON.");
            }

            if (pos < str.length()) {
                JSON.stringToJSON(str.substring(pos), json);
            }
        }
    }

    /**
     * Pega conteúdo de uma <i>String</i> entre parâmetros <i>open</i> e
     * <i>close</i>
     *
     * @param str Linha para extraír conteúdo
     * @param open Exemplos: "[", "{", ...
     * @param close Exemplos: ']', '}', ...
     * @return
     */
    private static String getContent(String str, String open, char close) {

        StringBuilder sb = new StringBuilder();

        for (char c : str.toCharArray()) {

            sb.append(c);

            if (c == close) {
                break;
            }
        }

        if (sb.toString().contains(open)) {
            sb.append(getContent(str.replace(sb.toString(), ""), open, close));
        }

        return sb.toString();
    }
}
