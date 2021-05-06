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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class Count implements AutoCloseable {

    private String table;
    private java.sql.Connection connection;
    private Statement st;

    private String query;

    /**
     * Construtor
     *
     * @param table Nome da tabela
     * @throws DatabaseException
     */
    public Count(String table) throws DatabaseException {

        this.table = table.toLowerCase().trim();

        try {
            connection = Connection.open();
            st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
     * Conta total de registros de uma tabela
     *
     * @return int Total de registros
     * @throws DatabaseException
     */
    public int getTotal() throws DatabaseException {

        try (ResultSet rs = st.executeQuery("SELECT COUNT(" + Metadata.getPrimaryKeyName(table) + ") FROM " + table)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }

        return 0;
    }

    /**
     * Maior ID de uma tabela
     *
     * @return int Maior ID
     * @throws DatabaseException
     */
    public int getMaxId() throws DatabaseException {

        try (ResultSet rs = st.executeQuery("SELECT MAX(" + Metadata.getPrimaryKeyName(table) + ") FROM " + table)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }

        return 0;
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
