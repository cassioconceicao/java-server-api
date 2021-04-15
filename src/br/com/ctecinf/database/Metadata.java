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
package br.com.ctecinf.database;

import br.com.ctecinf.json.JSON;
import br.com.ctecinf.json.JSONObject;
import br.com.ctecinf.json.JSONArray;
import br.com.ctecinf.json.JSONException;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class Metadata {

    private static JSONObject json;

    /**
     * JSON
     *
     * @return JSON
     * @throws DatabaseException
     */
    protected static JSONObject get() throws DatabaseException {

        File file = new File("config" + File.separator + "database" + File.separator + "metadata.json");

        if (!file.exists()) {
            throw new DatabaseException("File 'metadata.json' not found.");
        }

        if (json == null) {
            try {
                json = JSON.parse(file).get(JSONObject.class);
            } catch (JSONException ex) {
                throw new DatabaseException(ex);
            }
        }

        return json;
    }

    /**
     * Nome das tabelas
     *
     * @return List
     * @throws DatabaseException
     */
    public static List<String> getTables() throws DatabaseException {
        return new ArrayList(Metadata.get().keySet());
    }

    /**
     * Nome do campo chave primária da tabela
     *
     * @param table
     * @return String
     * @throws DatabaseException
     */
    public static String getPrimaryKeyName(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        String value = Metadata.get().getJSONObjectValue(table.toLowerCase().trim()).getStringValue("primary_key");

        return value.isEmpty() ? null : value.toLowerCase().trim();
    }

    /**
     * Nome do sequenciador da chave primária
     *
     * @param table
     * @return String
     * @throws DatabaseException
     */
    public static String getSequenceName(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        String value = Metadata.get().getJSONObjectValue(table.toLowerCase().trim()).getStringValue("sequence");

        return value.isEmpty() ? null : value.toLowerCase().trim();
    }

    /**
     * Lista das colunas da tabela
     *
     * @param table
     * @return JSONArray
     * @throws DatabaseException
     */
    protected static JSONArray getColumns(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        return Metadata.get().getJSONObjectValue(table.toLowerCase().trim()).getJSONArrayValue("columns");
    }

    /**
     * Lista com nome das colunas da tabela
     *
     * @param table
     * @return List [nome da coluna]
     * @throws DatabaseException
     */
    public static List<String> getColumnsName(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        List<String> columns = new ArrayList();

        for (JSONObject j : Metadata.getColumns(table)) {
            columns.add(j.getStringValue("name"));
        }

        return columns;
    }

    /**
     * Lista com tipo de dados das colunas da tabela
     *
     * @param table
     * @return LinkedHashMap [nome da coluna] keySet() : [data_type]
     * values()<br>
     * Usar java.sql.Types
     * @throws DatabaseException
     */
    public static LinkedHashMap<String, Integer> getColumnsDataType(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        LinkedHashMap<String, Integer> columns = new LinkedHashMap();

        for (JSONObject j : Metadata.getColumns(table)) {
            columns.put(j.getStringValue("name"), Integer.valueOf(j.getStringValue("data_type")));
        }

        return columns;
    }

    /**
     * Lista com tamanho das colunas da tabela
     *
     * @param table
     * @return LinkedHashMap [nome da coluna] keySet() : [length] values()
     * @throws DatabaseException
     */
    public static LinkedHashMap<String, Integer> getColumnsLength(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        LinkedHashMap<String, Integer> columns = new LinkedHashMap();

        for (JSONObject j : Metadata.getColumns(table)) {
            columns.put(j.getStringValue("name"), Integer.valueOf(j.getStringValue("length")));
        }

        return columns;
    }

    /**
     * Lista com valor not null das colunas da tabela
     *
     * @param table
     * @return LinkedHashMap [nome da coluna] keySet() : [not_null] values()
     * @throws DatabaseException
     */
    public static LinkedHashMap<String, Boolean> getColumnsNotNull(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        LinkedHashMap<String, Boolean> columns = new LinkedHashMap();

        for (JSONObject j : Metadata.getColumns(table)) {
            columns.put(j.getStringValue("name"), Boolean.valueOf(j.getStringValue("not_null")));
        }

        return columns;
    }

    /**
     * Lista com JSON das tabelas externas que uma determinada tabela faz
     * referencia
     *
     * @param table
     * @return JSON [nome da coluna] keySet() : [nome da tabela] values()
     * @throws DatabaseException
     */
    public static JSONObject getReferencedTables(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        return Metadata.get().getJSONObjectValue(table.toLowerCase().trim()).getJSONObjectValue("foreign_key");
    }
}
