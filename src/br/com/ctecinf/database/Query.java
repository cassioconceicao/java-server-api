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

import br.com.ctecinf.json.JSONObject;
import br.com.ctecinf.json.JSONArray;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class Query implements AutoCloseable {

    private static final String LIMIT_POSTGRES = " LIMIT {maxResults} OFFSET {offSet}";
    private static final String LIMIT_MYSQL = " LIMIT {offSet}, {maxResults}";
    private static final String LIMIT_FIREBIRD = "FIRST {maxResults} SKIP {offSet} ";
    private static final String LIMIT_DERBY = " OFFSET {offSet} FETCH NEXT {maxResults} ROWS ONLY";

    private String table;
    private java.sql.Connection connection;
    private Statement st;

    private boolean isFirebird;

    private String fullQuery;
    private String query;
    private String limit;
    private Clause clause;
    private List<String> orderBy;

    /**
     * Construtor
     *
     * @param table Nome da tabela
     * @throws DatabaseException
     */
    public Query(String table) throws DatabaseException {
        this(table, null);
    }

    /**
     * Construtor
     *
     * @param table Nome da tabela
     * @param clause Claúsulas <i>WHERE</i>
     * @throws DatabaseException
     */
    public Query(String table, Clause clause) throws DatabaseException {

        this.table = table.toLowerCase().trim();
        this.clause = clause;
        this.isFirebird = Connection.getURL().toLowerCase().contains("firebird");
        this.orderBy = new ArrayList();

        try {
            this.connection = Connection.open();
            this.st = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            this.createQuery(table);
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    /**
     * CRia nova consulta
     *
     * @param table
     * @return Query
     * @throws DatabaseException
     */
    public static Query create(String table) throws DatabaseException {
        return new Query(table);
    }

    /**
     * Tabela alvo
     *
     * @return String
     */
    public String getTable() {
        return this.table;
    }

    /**
     * Cria a consulta
     *
     * @param table
     * @throws DatabaseException
     */
    protected final void createQuery(String table) throws DatabaseException {

        List<String> columns = new ArrayList();

        String join = "";

        Metadata.getColumnsName(table).stream().forEach((column) -> {
            columns.add(table + "." + column + " AS \"" + table + "_" + column + "\"");
        });

        if (Metadata.getReferencedTables(table) != null) {

            for (String column : Metadata.getReferencedTables(table).keySet()) {

                String referencedTable = Metadata.getReferencedTables(table).getStringValue(column);

                if (referencedTable != null && !referencedTable.isEmpty()) {

                    String referencedColumn = Metadata.getPrimaryKeyName(referencedTable);

                    Metadata.getColumnsName(referencedTable).stream().forEach((col) -> {
                        columns.add(referencedTable + "." + col + " AS \"" + referencedTable + "_" + col + "\"");
                    });

                    join += " JOIN " + referencedTable + " ON CASE WHEN " + table + "." + column + " IS NULL THEN (SELECT MIN(" + referencedTable + "." + referencedColumn + ") FROM " + referencedTable + ") ELSE " + table + "." + column + " END = " + referencedTable + "." + referencedColumn;
                    //join += " JOIN " + referenceTable + " ON " + referenceTable + "." + referenceColumn + " = " + table + "." + column;
                }
            }
        }

        this.query = "SELECT " + Arrays.stream(columns.toArray()).map(String::valueOf).collect(Collectors.joining(", ")) + " FROM " + table + join;
    }

    /**
     * Adiciona Claúsula
     *
     * @param clause
     * @return Query
     */
    public Query setClause(Clause clause) {
        this.clause = clause;
        return this;
    }

    /**
     * Lista com campos para ordenação
     *
     * @param columns
     * @return Query
     */
    public Query orderBy(String... columns) {
        this.orderBy = Arrays.asList(columns);
        return this;
    }

    /**
     * Define o número máximo de registros para retornar na consulta
     *
     * @param maxResults
     * @return Query
     * @throws DatabaseException
     */
    public Query setLimit(int maxResults) throws DatabaseException {
        return this.setLimit(0, maxResults);
    }

    /**
     * Define o número máximo de registros para retornar na consulta
     *
     * @param offSet
     * @param maxResults
     * @return Query
     * @throws DatabaseException
     */
    public Query setLimit(int offSet, int maxResults) throws DatabaseException {

        if (this.isFirebird) {
            this.limit = Query.LIMIT_FIREBIRD.replace("{offSet}", String.valueOf(offSet)).replace("{maxResults}", String.valueOf(maxResults));
        } else if (Connection.getURL().toLowerCase().contains("derby")) {
            this.limit = Query.LIMIT_DERBY.replace("{offSet}", String.valueOf(offSet)).replace("{maxResults}", String.valueOf(maxResults));
        } else if (Connection.getURL().toLowerCase().contains("postgres")) {
            this.limit = Query.LIMIT_POSTGRES.replace("{offSet}", String.valueOf(offSet)).replace("{maxResults}", String.valueOf(maxResults));
        } else {
            this.limit = Query.LIMIT_MYSQL.replace("{offSet}", String.valueOf(offSet)).replace("{maxResults}", String.valueOf(maxResults));
        }

        return this;
    }

    /**
     * <i>ResultSet</i> da consulta
     *
     * @return java.sql.ResultSet
     * @throws DatabaseException
     */
    public ResultSet getResultSet() throws DatabaseException {
        try {
            return this.st.executeQuery(this.toString());
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    /**
     * Retorna o resultado da consulta em JSONArray
     *
     * @return JSONArray [ { "label": "<i>to_string</i>",
     * "value":"<i>primary_key</i>", "data": { "column": "value", ...} }, ... ]
     * @throws DatabaseException
     */
    public JSONArray getJSON() throws DatabaseException {

        try (ResultSet rs = this.getResultSet()) {

            JSONArray array = new JSONArray();
            String idKey = Metadata.getPrimaryKeyName(this.table);

            while (rs.next()) {

                JSONObject data = this.getData(this.table, rs);

                String toString = ORM.toString(this.table);
                String idValue = "";

                for (String key : data.keySet()) {

                    String value = data.getStringValue(key);

                    if (value != null && !value.isEmpty()) {
                        toString = toString.replace("{" + key + "}", value);
                    }

                    if (data.containsKey(idKey)) {
                        idValue = data.getStringValue(idKey);
                    }
                }

                JSONObject row = new JSONObject();
                row.put("label", toString);
                row.put("value", idValue);
                row.put("data", data);

                array.add(row);
            }

            return array;

        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    /**
     * Extraí dados da linha do ResultSet
     *
     * @param table
     * @param rs
     * @return JSONObject
     * @throws DatabaseException
     */
    private JSONObject getData(String table, ResultSet rs) throws DatabaseException {

        List<String> columns = Metadata.getColumnsName(table);
        JSONObject reference = Metadata.getReferencedTables(table);

        JSONObject data = new JSONObject();

        for (String column : columns) {

            data.put(column, DataType.getValue(rs, table + "_" + column));

            if (reference.containsKey(column)) {
                try {
                    int index = rs.findColumn(table + "_" + column);
                    if (index > -1) {
                        data.put(reference.getStringValue(column), getData(reference.getStringValue(column), rs));
                    }
                } catch (SQLException ex) {
                }
            }
        }

        return data;
    }

    @Override
    public void close() throws DatabaseException {

        if (this.st != null) {
            try {
                this.st.close();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        }

        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public String toString() {

        if (this.fullQuery == null) {

            this.fullQuery = this.query;

            if (this.clause != null && !this.clause.toString().isEmpty()) {
                this.fullQuery += " WHERE " + this.clause;
            }

            if (!this.orderBy.isEmpty()) {

                this.fullQuery += " ORDER BY ";

                for (int i = 0; i < this.orderBy.size(); i++) {

                    if (this.orderBy.get(i).contains(".")) {
                        this.fullQuery += this.orderBy.get(i);
                    } else {
                        this.fullQuery += this.table + "." + this.orderBy.get(i);
                    }

                    if (i < this.orderBy.size() - 1) {
                        this.fullQuery += ", ";
                    }
                }
            }

            if (this.limit != null) {
                if (this.isFirebird) {
                    this.fullQuery = "SELECT " + this.limit + this.fullQuery.substring(7);
                } else {
                    this.fullQuery += this.limit;
                }
            }
        }
        
        return this.fullQuery;
    }

}
