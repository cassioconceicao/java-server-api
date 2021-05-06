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
package br.com.ctecinf.database;

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
public class Clause {

    private final String table;
    private String clause;

    /**
     * Construtor
     *
     * @param table Nome da tabela
     */
    public Clause(String table) {
        this.table = table;
        this.clause = "";
    }

    /**
     * Cria claúsula
     *
     * @param table
     * @return Clause
     */
    public static Clause create(String table) {
        return new Clause(table);
    }

    /**
     * Abre parenteses
     *
     * @return Clause
     */
    public Clause openParentheses() {
        this.clause += "(";
        return this;
    }

    /**
     * Fecha parenteses
     *
     * @return Clause
     */
    public Clause closeParentheses() {
        this.clause += ")";
        return this;
    }

    /**
     * Igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause equal(String column, Object value) {
        this.clause = this.table + "." + column + " = " + (value == null ? " NULL " : "'" + value + "'");
        return this;
    }

    /**
     * Não igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause notEqual(String column, Object value) {
        this.clause = this.table + "." + column + " <> " + (value == null ? " NULL " : "'" + value + "'");
        return this;
    }

    /**
     * Menor
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause less(String column, Object value) {
        this.clause = this.table + "." + column + " < " + (value == null ? " NULL " : "'" + value + "'");
        return this;
    }

    /**
     * Maior
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause greater(String column, Object value) {
        this.clause = this.table + "." + column + " > " + (value == null ? " NULL " : "'" + value + "'");
        return this;
    }

    /**
     * Menor igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause lessEqual(String column, Object value) {
        this.clause = this.table + "." + column + " <= " + (value == null ? " NULL " : "'" + value + "'");
        return this;
    }

    /**
     * Maior igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause greaterEqual(String column, Object value) {
        this.clause = this.table + "." + column + " >= " + (value == null ? " NULL " : "'" + value + "'");
        return this;
    }

    /**
     * Nulo
     *
     * @param column
     * @return Clause
     */
    public Clause isNull(String column) {
        this.clause = this.table + "." + column + " IS NULL";
        return this;
    }

    /**
     * Não nulo
     *
     * @param column
     * @return Clause
     */
    public Clause isNotNull(String column) {
        this.clause = this.table + "." + column + " IS NOT NULL";
        return this;
    }

    /**
     * Entre
     *
     * @param column
     * @param value1
     * @param value2
     * @return Clause
     */
    public Clause between(String column, Object value1, Object value2) {
        this.clause = this.table + "." + column + " BETWEEN " + (value1 == null ? " NULL " : "'" + value1 + "'") + " AND " + (value2 == null ? " NULL " : "'" + value2 + "'");
        return this;
    }

    /**
     * Dentro
     *
     * @param column
     * @param values
     * @return Clause
     */
    public Clause in(String column, Object... values) {
        this.clause = this.table + "." + column + " IN (" + (values == null ? " NULL " : Arrays.stream(values).map(String::valueOf).collect(Collectors.joining(", "))) + ")";
        return this;
    }

    /**
     * Fora
     *
     * @param column
     * @param values
     * @return Clause
     */
    public Clause notIn(String column, Object... values) {
        this.clause = this.table + "." + column + " NOT IN (" + (values == null ? " NULL " : Arrays.stream(values).map(String::valueOf).collect(Collectors.joining(", "))) + ")";
        return this;
    }

    /**
     * Filtro
     *
     * @param filter
     * @return Clause
     * @throws DatabaseException
     */
    public Clause like(Object filter) throws DatabaseException {

        if (filter == null) {
            return this;
        }

        List<String> columns = new ArrayList();

        for (String column : Metadata.getColumnsName(this.table)) {
            if (Connection.getURL().contains("postgres")) {
                columns.add("LOWER (CAST(" + this.table + "." + column + ") AS VARCHAR) LIKE '" + filter.toString().toLowerCase() + "%'");
            } else {
                columns.add("LOWER (" + this.table + "." + column + ") LIKE '" + filter.toString().toLowerCase() + "%'");
            }
        }

        for (Object referencedTable : Metadata.getReferencedTables(this.table).values()) {
            for (String col : Metadata.getColumnsName(referencedTable.toString())) {
                if (Connection.getURL().contains("postgres")) {
                    columns.add("LOWER (CAST(" + referencedTable + "." + col + ") AS VARCHAR) LIKE '" + filter.toString().toLowerCase() + "%'");
                } else {
                    columns.add("LOWER (" + referencedTable + "." + col + ") LIKE '" + filter.toString().toLowerCase() + "%'");
                }
            }
        }

        this.openParentheses();
        this.clause += columns.stream().map(String::valueOf).collect(Collectors.joining(" OR "));
        this.closeParentheses();

        return this;
    }

    /**
     * Igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orEqual(String column, Object value) {
        return this.orEqual(this.table, column, value);
    }

    /**
     * Igual
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.equal(column, value);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Não igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orNotEqual(String column, Object value) {
        return this.orNotEqual(this.table, column, value);
    }

    /**
     * Não igual
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orNotEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.notEqual(column, value);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Menor
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orLess(String column, Object value) {
        return this.orLess(this.table, column, value);
    }

    /**
     * Menor
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orLess(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.less(column, value);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Maior
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orGreater(String column, Object value) {
        return this.orGreater(this.table, column, value);
    }

    /**
     * Maior
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orGreater(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greater(column, value);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Menor igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orLessEqual(String column, Object value) {
        return this.orLessEqual(this.table, column, value);
    }

    /**
     * Menor igual
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orLessEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.lessEqual(column, value);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Maior igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orGreaterEqual(String column, Object value) {
        return this.orGreaterEqual(this.table, column, value);
    }

    /**
     * Maior igual
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause orGreaterEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greaterEqual(column, value);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Nulo
     *
     * @param column
     * @return Clause
     */
    public Clause orIsNull(String column) {
        return this.orIsNotNull(this.table, column);
    }

    /**
     * Nulo
     *
     * @param table
     * @param column
     * @return Clause
     */
    public Clause orIsNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNull(column);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Não nulo
     *
     * @param column
     * @return Clause
     */
    public Clause orIsNotNull(String column) {
        return this.orIsNotNull(this.table, column);
    }

    /**
     * Não nulo
     *
     * @param table
     * @param column
     * @return Clause
     */
    public Clause orIsNotNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNotNull(column);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Entre
     *
     * @param column
     * @param value1
     * @param value2
     * @return Clause
     */
    public Clause orBetween(String column, Object value1, Object value2) {
        return this.orBetween(this.table, column, value1, value2);
    }

    /**
     * Entre
     *
     * @param table
     * @param column
     * @param value1
     * @param value2
     * @return Clause
     */
    public Clause orBetween(String table, String column, Object value1, Object value2) {

        Clause c = new Clause(table);
        c.between(column, value1, value2);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Dentro
     *
     * @param column
     * @param values
     * @return Clause
     */
    public Clause orIn(String column, Object... values) {
        return this.orIn(this.table, column, values);
    }

    /**
     * Dentro
     *
     * @param table
     * @param column
     * @param values
     * @return Clause
     */
    public Clause orIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.in(column, values);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Fora
     *
     * @param column
     * @param values
     * @return Clause
     */
    public Clause orNotIn(String column, Object... values) {
        return this.orNotIn(this.table, column, values);
    }

    /**
     * Fora
     *
     * @param table
     * @param column
     * @param values
     * @return Clause
     */
    public Clause orNotIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.notIn(column, values);
        this.clause += " OR " + c.clause;

        return this;
    }

    /**
     * Igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andEqual(String column, Object value) {
        return this.andEqual(this.table, column, value);
    }

    /**
     * Igual
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.equal(column, value);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Não igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andNotEqual(String column, Object value) {
        return this.andNotEqual(this.table, column, value);
    }

    /**
     * Não igual
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andNotEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.notEqual(column, value);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Menor
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andLess(String column, Object value) {
        return this.andLess(this.table, column, value);
    }

    /**
     * Menor
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andLess(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.less(column, value);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Maior
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andGreater(String column, Object value) {
        return this.andGreater(this.table, column, value);
    }

    /**
     * Maior
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andGreater(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greater(column, value);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Menor igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andLessEqual(String column, Object value) {
        return this.andLessEqual(this.table, column, value);
    }

    /**
     * Menor igual
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andLessEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.lessEqual(column, value);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Maior igual
     *
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andGreaterEqual(String column, Object value) {
        return this.andGreaterEqual(this.table, column, value);
    }

    /**
     * Maior igual
     *
     * @param table
     * @param column
     * @param value
     * @return Clause
     */
    public Clause andGreaterEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greaterEqual(column, value);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Nulo
     *
     * @param column
     * @return Clause
     */
    public Clause andIsNull(String column) {
        return this.andIsNotNull(this.table, column);
    }

    /**
     * Nulo
     *
     * @param table
     * @param column
     * @return Clause
     */
    public Clause andIsNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNull(column);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Não nulo
     *
     * @param column
     * @return Clause
     */
    public Clause andIsNotNull(String column) {
        return this.andIsNotNull(this.table, column);
    }

    /**
     * Não nulo
     *
     * @param table
     * @param column
     * @return Clause
     */
    public Clause andIsNotNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNotNull(column);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Entre
     *
     * @param column
     * @param value1
     * @param value2
     * @return Clause
     */
    public Clause andBetween(String column, Object value1, Object value2) {
        return this.andBetween(this.table, column, value1, value2);
    }

    /**
     * Entre
     *
     * @param table
     * @param column
     * @param value1
     * @param value2
     * @return Clause
     */
    public Clause andBetween(String table, String column, Object value1, Object value2) {

        Clause c = new Clause(table);
        c.between(column, value1, value2);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Dentro
     *
     * @param column
     * @param values
     * @return Clause
     */
    public Clause andIn(String column, Object... values) {
        return this.andIn(this.table, column, values);
    }

    /**
     * Dentro
     *
     * @param table
     * @param column
     * @param values
     * @return Clause
     */
    public Clause andIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.in(column, values);
        this.clause += " AND " + c.clause;

        return this;
    }

    /**
     * Fora
     *
     * @param column
     * @param values
     * @return Clause
     */
    public Clause andNotIn(String column, Object... values) {
        return this.andNotIn(this.table, column, values);
    }

    /**
     * Fora
     *
     * @param table
     * @param column
     * @param values
     * @return Clause
     */
    public Clause andNotIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.notIn(column, values);
        this.clause += " AND " + c.clause;

        return this;
    }

    @Override
    public String toString() {
        return this.clause == null ? "" : this.clause;
    }
}
