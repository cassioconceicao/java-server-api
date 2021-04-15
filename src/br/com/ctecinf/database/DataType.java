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

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class DataType {

    /**
     * Converte tipo de dado do banco de dados para tipo Java
     *
     * @param type
     * @return Class
     */
    public static Class typeToClass(int type) {

        switch (type) {

            case Types.BIGINT:
                return Long.class;

            case Types.BINARY:
                return Object.class;

            case Types.BIT:
                return Boolean.class;

            case Types.BLOB:
                return Blob.class;

            case Types.BOOLEAN:
                return Boolean.class;

            case Types.CHAR:
                return String.class;

            case Types.DATE:
                return Date.class;

            case Types.DECIMAL:
                return BigDecimal.class;

            case Types.DOUBLE:
                return Double.class;

            case Types.FLOAT:
                return Float.class;

            case Types.INTEGER:
                return Integer.class;

            case Types.LONGNVARCHAR:
                return String.class;

            case Types.LONGVARCHAR:
                return String.class;

            case Types.NCHAR:
                return String.class;

            case Types.NVARCHAR:
                return String.class;

            case Types.SMALLINT:
                return Boolean.class;

            case Types.TIME:
                return Time.class;

            case Types.TIMESTAMP:
                return Timestamp.class;

            case Types.TINYINT:
                return Boolean.class;

            case Types.VARCHAR:
                return String.class;

            default:
                return Object.class;
        }
    }

    /**
     * Converte tipo de dado do banco de dados para tipo literal SQL
     *
     * @param type
     * @return String
     */
    public static String typeToString(int type) {

        switch (type) {

            case Types.BIGINT:
                return "BIGINT";

            case Types.BINARY:
                return "BINARY";

            case Types.BIT:
                return "BIT";

            case Types.BLOB:
                return "BLOB SUB_TYPE 1";

            case Types.BOOLEAN:
                return "BOOLEAN";

            case Types.CHAR:
                return "CHAR";

            case Types.DATE:
                return "DATE";

            case Types.DECIMAL:
                return "DECIMAL";

            case Types.DOUBLE:
                return "DOUBLE";

            case Types.FLOAT:
                return "FLOAT";

            case Types.INTEGER:
                return "INT";

            case Types.LONGNVARCHAR:
                return "VARCHAR";

            case Types.LONGVARCHAR:
                return "TEXT";

            case Types.NCHAR:
                return "VARCHAR";

            case Types.NVARCHAR:
                return "VARCHAR";

            case Types.SMALLINT:
                return "SMALLINT";

            case Types.TIME:
                return "TIME";

            case Types.TIMESTAMP:
                return "TIMESTAMP";

            case Types.TINYINT:
                return "TINYINT";

            case Types.VARCHAR:
                return "VARCHAR";

            default:
                return "VARCHAR";
        }
    }

    /**
     * Converte classe tipo Java para tipo de banco de dados
     *
     * @param cls
     * @return int
     */
    public static int classToType(Class cls) {

        if (cls.isAssignableFrom(Long.class) || cls.isAssignableFrom(long.class)) {
            return Types.BIGINT;
        } else if (cls.isAssignableFrom(Blob.class)) {
            return Types.BLOB;
        } else if (cls.isAssignableFrom(Boolean.class) || cls.isAssignableFrom(boolean.class)) {
            return Types.BOOLEAN;
        } else if (cls.isAssignableFrom(java.util.Date.class) || cls.isAssignableFrom(Date.class)) {
            return Types.DATE;
        } else if (cls.isAssignableFrom(BigDecimal.class)) {
            return Types.DECIMAL;
        } else if (cls.isAssignableFrom(Double.class) || cls.isAssignableFrom(double.class)) {
            return Types.DOUBLE;
        } else if (cls.isAssignableFrom(Float.class) || cls.isAssignableFrom(float.class)) {
            return Types.FLOAT;
        } else if (cls.isAssignableFrom(Integer.class) || cls.isAssignableFrom(int.class)) {
            return Types.INTEGER;
        } else if (cls.isAssignableFrom(Time.class)) {
            return Types.TIME;
        } else if (cls.isAssignableFrom(Timestamp.class)) {
            return Types.TIMESTAMP;
        } else {
            return Types.VARCHAR;
        }
    }

    /**
     * Seta o valor conforme o tipo de dado da coluna no
     * <i>PreparedStatement</i>
     *
     * @param ps
     * @param index
     * @param value
     * @throws DatabaseException
     */
    public static void setValue(PreparedStatement ps, int index, Object value) throws DatabaseException {

        try {

            int type = ps.getMetaData().getColumnType(index);

            if (value == null) {
                ps.setNull(index, type);
            } else {

                String v = value.toString().trim();

                switch (type) {

                    case Types.BIGINT:
                        ps.setLong(index, Long.parseLong(v));
                        break;
                    case Types.BOOLEAN:
                        ps.setBoolean(index, v.equalsIgnoreCase("1") || v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes") || v.equalsIgnoreCase("sim"));
                        break;
                    case Types.DATE:
                        ps.setDate(index, Date.valueOf(v));
                        break;
                    case Types.DECIMAL:
                        ps.setBigDecimal(index, BigDecimal.valueOf(Double.parseDouble(v)));
                        break;
                    case Types.DOUBLE:
                        ps.setDouble(index, Double.parseDouble(v));
                        break;
                    case Types.FLOAT:
                        ps.setFloat(index, Float.parseFloat(v));
                        break;
                    case Types.INTEGER:
                        ps.setInt(index, Integer.parseInt(v));
                        break;
                    case Types.LONGNVARCHAR:
                        ps.setString(index, v);
                        break;
                    case Types.LONGVARCHAR:
                        ps.setString(index, v);
                        break;
                    case Types.NCHAR:
                        ps.setString(index, v);
                        break;
                    case Types.NVARCHAR:
                        ps.setString(index, v);
                        break;
                    case Types.SMALLINT:
                        ps.setInt(index, Integer.parseInt(v));
                        break;
                    case Types.TIME:
                        ps.setTime(index, Time.valueOf(v));
                        break;
                    case Types.TIMESTAMP:
                        ps.setTimestamp(index, Timestamp.valueOf(v));
                        break;
                    case Types.TINYINT:
                        ps.setInt(index, Integer.parseInt(v));
                        break;
                    case Types.VARCHAR:
                        ps.setString(index, v);
                        break;
                    default:
                        ps.setObject(index, value);
                        break;
                }
            }

        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    /**
     * Pega o valor da coluna no <i>ResultSet</i> conforme o tipo de dado da
     * coluna
     *
     * @param rs
     * @param column
     * @return Object
     * @throws DatabaseException
     */
    public static Object getValue(ResultSet rs, String column) throws DatabaseException {

        try {

            Object value = rs.getObject(column);

            if (value != null) {

                int type = rs.getMetaData().getColumnType(rs.findColumn(column));

                switch (type) {

                    case Types.BIGINT:
                        value = rs.getLong(column);
                        break;
                    case Types.BIT:
                        value = rs.getBoolean(column);
                        break;
                    case Types.BLOB:
                        value = rs.getBlob(column);
                        break;
                    case Types.BOOLEAN:
                        value = rs.getBoolean(column);
                        break;
                    case Types.CHAR:
                        value = rs.getString(column).trim();
                        break;
                    case Types.DATE:
                        value = rs.getDate(column);
                        break;
                    case Types.DECIMAL:
                        value = rs.getBigDecimal(column);
                        break;
                    case Types.DOUBLE:
                        value = rs.getDouble(column);
                        break;
                    case Types.FLOAT:
                        value = rs.getFloat(column);
                        break;
                    case Types.INTEGER:
                        value = rs.getInt(column);
                        break;
                    case Types.LONGNVARCHAR:
                        value = rs.getString(column).trim();
                        break;
                    case Types.LONGVARCHAR:
                        value = rs.getString(column).trim();
                        break;
                    case Types.NCHAR:
                        value = rs.getString(column).trim();
                        break;
                    case Types.NVARCHAR:
                        value = rs.getString(column);
                        break;
                    case Types.SMALLINT:
                        value = rs.getBoolean(column);
                        break;
                    case Types.TIME:
                        value = rs.getTime(column);
                        break;
                    case Types.TIMESTAMP:
                        value = rs.getTimestamp(column);
                        break;
                    case Types.TINYINT:
                        value = rs.getBoolean(column);
                        break;
                    case Types.VARCHAR:
                        value = rs.getString(column).trim();
                        break;
                }
            }

            return value;

        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }
}
