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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class Update implements AutoCloseable {

    private final String table;
    private final java.sql.Connection connection;
    private StringBuilder sql;

    /**
     * Construtor
     *
     * @param table Nome da tabela
     * @throws DatabaseException
     */
    public Update(String table) throws DatabaseException {
        this.table = table;
        connection = Connection.open();
    }

    /**
     * Cria SQL padrão para inserir dados na tabela
     *
     * @throws DatabaseException
     * @see<br>
     * <code>
     * Derby: id BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL PRIMARY KEY<br>
     * Mysql: id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT<br>
     * Postgres: id SERIAL NOT NULL PRIMARY KEY<br>
     * Firbird: Criar sequenciador: id BIGINT NOT NULL PRIMARY KEY | CREATE
     * SEQUENCE [table_name]_seq
     * </code>
     */
    public void createInsertSQL() throws DatabaseException {

        String columnId = Metadata.getPrimaryKeyName(table);
        String sequence = null;

        try {
            if (connection.getMetaData().getURL().toLowerCase().contains("firebird")) {
                sequence = "GEN_ID(" + Metadata.getSequenceName(table) + ", 1)";
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }

        List<String> columns = Metadata.getColumnsName(table);

        int index = columns.indexOf(columnId);
        if (index > -1) {
            columns.remove(columns.remove(index));
        }

        sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(table.trim());
        sql.append(" (");
        sql.append(columns.stream().map(String::valueOf).collect(Collectors.joining(", ")));
        sql.append(sequence == null ? "" : ", " + columnId);
        sql.append(") VALUES ( :");
        sql.append(columns.stream().map(String::valueOf).collect(Collectors.joining(", :")));
        sql.append(sequence == null ? "" : ", " + sequence);
        sql.append(")");
    }

    /**
     * Cria SQL padrão para alterar dados na tabela
     *
     * @throws DatabaseException
     */
    public void createUpdateSQL() throws DatabaseException {

        String columnId = Metadata.getPrimaryKeyName(table);
        List<String> columns = Metadata.getColumnsName(table);

        int index = columns.indexOf(columnId);
        if (index > -1) {
            columns.remove(columns.remove(index));
        }

        sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(table);
        sql.append(" SET ");

        for (int i = 0; i < columns.size(); i++) {

            sql.append(columns.get(i));
            sql.append(" = :");
            sql.append(columns.get(i));

            if (i < columns.size() - 1) {
                sql.append(", ");
            }
        }

        sql.append(" WHERE ");
        sql.append(columnId);
        sql.append(" = :");
        sql.append(columnId);
    }

    /**
     * Cria SQL padrão para apagar registro na tabela
     *
     * @throws DatabaseException
     */
    public void createDeleteSQL() throws DatabaseException {

        String columnId = Metadata.getPrimaryKeyName(table);

        sql = new StringBuilder();
        sql.append("DELETE FROM ");
        sql.append(table);
        sql.append(" WHERE ");
        sql.append(columnId);
        sql.append(" = :");
        sql.append(columnId);
    }

    /**
     * Executa o SQL no banco de dados
     *
     * @param data Dados
     * @return Long Identificador do registro ou NULL para não encontrado
     * @throws DatabaseException
     */
    public Long execute(LinkedHashMap<String, Object> data) throws DatabaseException {

        String update = sql.toString();
        Long id = null;

        for (Map.Entry<String, Object> entry : data.entrySet()) {

            String key = entry.getKey().toLowerCase().trim();
            Object value = entry.getValue();

            if (value == null) {
                update = update.replace(" :" + key, " null");
            } else {
                update = update.replace(" :" + key, " '" + value + "'");
            }
        }

        for (String column : Metadata.getColumnsName(table)) {
            if (update.contains(" :" + column)) {
                update = update.replace(" :" + column, " null");
            }
        }

        try (Statement st = connection.createStatement()) {

            st.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);

            try (ResultSet rs = st.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getLong(1);
                }
            }

        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }

        return id;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                throw new DatabaseException(ex);
            }
        }
    }
}
