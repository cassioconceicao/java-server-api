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

    private String query;

    /**
     * Construtor
     *
     * @param table Nome da tabela
     * @param clauses Claúsulas <i>WHERE</i>
     * @throws DatabaseException
     */
    public Query(String table, Clause... clauses) throws DatabaseException {

        this.table = table.toLowerCase().trim();

        try {
            connection = Connection.open();
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            query = createQuery(table, clauses);
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    /**
     * Tabela alvo
     *
     * @return String
     */
    public String getTable() {
        return table;
    }

    /**
     * Cria a consulta
     *
     * @param table
     * @param clauses
     * @return String
     * @throws DatabaseException
     */
    protected final String createQuery(String table, Clause... clauses) throws DatabaseException {

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

        String q = "SELECT " + Arrays.stream(columns.toArray()).map(String::valueOf).collect(Collectors.joining(", ")) + " FROM " + table + join;

        if (clauses != null && clauses.length > 0) {

            q += " WHERE ";

            for (Clause clause : clauses) {
                if (clause != null) {
                    q += clause;
                }
            }
        }

        return q;
    }

    /**
     * Lista com campos para ordenação
     *
     * @param columnsName
     * @return Query
     */
    public Query orderBy(Object... columnsName) {

        if (columnsName.length > 0) {
            query += " ORDER BY " + table + "." + Arrays.stream(columnsName).map(String::valueOf).collect(Collectors.joining(", " + table + "."));
        }

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

        if (Connection.getURL().toLowerCase().contains("firebird")) {
            query = "SELECT " + LIMIT_FIREBIRD.replace("{offSet}", String.valueOf(offSet)).replace("{maxResults}", String.valueOf(maxResults)) + query.substring(7);
        } else if (Connection.getURL().toLowerCase().contains("derby")) {
            query += LIMIT_DERBY.replace("{offSet}", String.valueOf(offSet)).replace("{maxResults}", String.valueOf(maxResults));
        } else if (Connection.getURL().toLowerCase().contains("postgres")) {
            query += LIMIT_POSTGRES.replace("{offSet}", String.valueOf(offSet)).replace("{maxResults}", String.valueOf(maxResults));
        } else {
            query += LIMIT_MYSQL.replace("{offSet}", String.valueOf(offSet)).replace("{maxResults}", String.valueOf(maxResults));
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
            return st.executeQuery(query);
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    /**
     * Retorna o resultado da consulta em JSONArray
     *
     * @return JSONArray
     * @throws DatabaseException
     */
    public JSONArray getJSON() throws DatabaseException {

        try (ResultSet rs = this.getResultSet()) {

            JSONArray array = new JSONArray();

            while (rs.next()) {

                JSONObject row = new JSONObject();

                for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                    String key = rs.getMetaData().getColumnLabel(i + 1).trim();
                    row.put(key, DataType.getValue(rs, key));
                }

                array.add(row);
            }

            return array;

        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void close() throws DatabaseException {

        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public String toString() {
        return query;
    }
}
