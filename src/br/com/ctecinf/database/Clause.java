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

    public Clause(String table) {
        this.table = table;
    }

    public void equal(String column, Object value) {
        clause = table + "." + column + " = " + (value == null ? " NULL " : "'" + value + "'");
    }

    public void notEqual(String column, Object value) {
        clause = table + "." + column + " <> " + (value == null ? " NULL " : "'" + value + "'");
    }

    public void less(String column, Object value) {
        clause = table + "." + column + " < " + (value == null ? " NULL " : "'" + value + "'");
    }

    public void greater(String column, Object value) {
        clause = table + "." + column + " > " + (value == null ? " NULL " : "'" + value + "'");
    }

    public void lessEqual(String column, Object value) {
        clause = table + "." + column + " <= " + (value == null ? " NULL " : "'" + value + "'");
    }

    public void greaterEqual(String column, Object value) {
        clause = table + "." + column + " >= " + (value == null ? " NULL " : "'" + value + "'");
    }

    public void isNull(String column) {
        clause = table + "." + column + " IS NULL";
    }

    public void isNotNull(String column) {
        clause = table + "." + column + " IS NOT NULL";
    }

    public void between(String column, Object value1, Object value2) {
        clause = table + "." + column + " BETWEEN " + (value1 == null ? " NULL " : "'" + value1 + "'") + " AND " + (value2 == null ? " NULL " : "'" + value2 + "'");
    }

    public void in(String column, Object... values) {
        clause = table + "." + column + " IN (" + (values == null ? " NULL " : Arrays.stream(values).map(String::valueOf).collect(Collectors.joining(", "))) + ")";
    }

    public void notIn(String column, Object... values) {
        clause = table + "." + column + " NOT IN (" + (values == null ? " NULL " : Arrays.stream(values).map(String::valueOf).collect(Collectors.joining(", "))) + ")";
    }

    public void like(Object filter) throws DatabaseException {

        List<String> columns = new ArrayList();

        for (String column : Metadata.getColumnsName(table)) {

            columns.add("LOWER (" + table + "." + column + ") LIKE '" + (filter == null ? "" : filter.toString().toLowerCase()) + "%'");

            for (Object referencedTable : Metadata.getReferencedTables(table).values()) {
                Metadata.getColumnsName(referencedTable.toString()).stream().forEach((col) -> {
                    columns.add("LOWER (" + referencedTable + "." + col + ") LIKE '" + (filter == null ? "" : filter.toString().toLowerCase()) + "%'");
                });
            }
        }

        clause = "(" + columns.stream().map(String::valueOf).collect(Collectors.joining(" OR ")) + ")";
    }

    public static Clause equal(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.equal(column, value);

        return c;
    }

    public static Clause notEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.notEqual(column, value);

        return c;
    }

    public static Clause less(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.less(column, value);

        return c;
    }

    public static Clause greater(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greater(column, value);

        return c;
    }

    public static Clause lessEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.lessEqual(column, value);

        return c;
    }

    public static Clause greaterEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greaterEqual(column, value);

        return c;
    }

    public static Clause isNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNull(column);

        return c;
    }

    public static Clause isNotNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNotNull(column);

        return c;
    }

    public static Clause between(String table, String column, Object value1, Object value2) {

        Clause c = new Clause(table);
        c.between(column, value1, value2);

        return c;
    }

    public static Clause in(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.in(column, values);

        return c;
    }

    public static Clause notIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.notIn(column, values);

        return c;
    }

    public static Clause like(String table, Object filter) throws DatabaseException {

        Clause c = new Clause(table);
        c.like(filter);

        return c;
    }

    public static Clause orEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.equal(column, value);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orNotEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.notEqual(column, value);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orLess(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.less(column, value);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orGreater(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greater(column, value);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orLessEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.lessEqual(column, value);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orGreaterEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greaterEqual(column, value);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orIsNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNull(column);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orIsNotNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNotNull(column);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orBetween(String table, String column, Object value1, Object value2) {

        Clause c = new Clause(table);
        c.between(column, value1, value2);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.in(column, values);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orNotIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.notIn(column, values);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause orLike(String table, Object filter) throws DatabaseException {

        Clause c = new Clause(table);
        c.like(filter);
        c.clause = " OR (" + c.clause + ")";

        return c;
    }

    public static Clause andEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.equal(column, value);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andNotEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.notEqual(column, value);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andLess(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.less(column, value);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andGreater(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greater(column, value);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andLessEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.lessEqual(column, value);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andGreaterEqual(String table, String column, Object value) {

        Clause c = new Clause(table);
        c.greaterEqual(column, value);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andIsNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNull(column);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andIsNotNull(String table, String column) {

        Clause c = new Clause(table);
        c.isNotNull(column);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andBetween(String table, String column, Object value1, Object value2) {

        Clause c = new Clause(table);
        c.between(column, value1, value2);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.in(column, values);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andNotIn(String table, String column, Object... values) {

        Clause c = new Clause(table);
        c.notIn(column, values);
        c.clause = " AND " + c.clause;

        return c;
    }

    public static Clause andLike(String table, Object filter) throws DatabaseException {

        Clause c = new Clause(table);
        c.like(filter);
        c.clause = " AND " + c.clause;

        return c;
    }

    @Override
    public String toString() {
        return clause == null ? "" : clause;
    }
}
