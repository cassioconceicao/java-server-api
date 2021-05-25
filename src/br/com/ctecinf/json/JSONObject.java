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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class JSONObject extends LinkedHashMap<String, Object> {

    private boolean caseSensitive = false;

    /**
     * Caso sensitivo para o nome do campo
     *
     * @return boolean
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Configura caso sensitivo
     *
     * @param caseSensitive TRUE: caso sensitivo | FALSE: não caso sensitivo
     * para o nome do campo
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Verifica se valor do campor é um objeto JSONArray
     *
     * @param key Nome do campo
     * @return boolean
     */
    public boolean isJSONArrayValue(String key) {
        return super.get(key).getClass().isAssignableFrom(JSONArray.class);
    }

    /**
     * Verifica se valor do campor é um objeto JSON
     *
     * @param key Nome do campo
     * @return boolean
     */
    public boolean isJSONObjectValue(String key) {
        return super.get(key).getClass().isAssignableFrom(JSONObject.class);
    }

    /**
     * Recupera o valor <i>Object</i> do campo
     *
     * @param key Nome do campo
     * @return Object
     */
    public Object getValue(String key) {

        if (!caseSensitive) {
            key = key.toLowerCase();
        }

        return super.containsKey(key) ? super.get(key) : null;
    }

    /**
     * Recupera o valor <i>String</i> do campo
     *
     * @param key Nome do campo
     * @return String
     */
    public String getStringValue(String key) {
        return this.getValue(key) == null ? null : this.getValue(key).toString();
    }

    /**
     * Recupera o valor <i>JSON</i> do campo
     *
     * @param key Nome do campo
     * @return String
     */
    public JSONObject getJSONObjectValue(String key) {
        return this.getValue(key) == null ? null : (JSONObject) this.getValue(key);
    }

    /**
     * Recupera o valor <i>JSONArray</i> do campo
     *
     * @param key Nome do campo
     * @return String
     */
    public JSONArray getJSONArrayValue(String key) {
        return this.getValue(key) == null ? null : (JSONArray) this.getValue(key);
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder("{");

        List<String> keys = Arrays.asList(this.keySet().toArray(new String[]{}));

        for (int i = 0; i < keys.size(); i++) {

            String key = keys.get(i);
            Object value = this.get(key);

            if (value!=null && (value.getClass().isAssignableFrom(JSONObject.class) || value.getClass().isAssignableFrom(JSONArray.class))) {
                str.append("\"").append(key).append("\": ").append(value);
            } else {
                str.append("\"").append(key).append("\": \"").append(value).append("\"");
            }

            if (i < keys.size() - 1) {
                str.append(", ");
            }
        }

        str.append("}\n");

        return str.toString();
    }
}
