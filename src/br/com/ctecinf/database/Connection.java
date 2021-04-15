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

import br.com.ctecinf.json.JSONArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public class Connection {

    /**
     * Propriedades de conexão com o banco de dados
     *
     * @return Properties
     * @throws DatabaseException
     */
    private static Properties getProperties() throws DatabaseException {

        File file = new File("config" + File.separator + "database" + File.separator + "connection.properties");

        Properties properties = new Properties();

        if (!file.exists()) {

            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            properties.put("url", "jdbc:firebirdsql:localhost:[path]");
            properties.put("username", "sysdba");
            properties.put("password", "masterkey");

            try {
                properties.store(new FileOutputStream(file), "Dados para conexão com o banco de dados\nEx. de URL: \"jdbc:firebirdsql:localhost:[path]\"\n");
            } catch (IOException ex) {
                throw new DatabaseException(ex);
            }
        }

        try {
            properties.load(new FileInputStream(file));
        } catch (IOException ex) {
            throw new DatabaseException(ex);
        }

        return properties;
    }

    /**
     * Abre conexão com banco de dados conforme configuração do arquivo
     * 'config/database/connection.properties'
     *
     *
     * @return java.sql.Connection
     * @throws DatabaseException
     */
    public static java.sql.Connection open() throws DatabaseException {

        java.sql.Connection connection = null;
        Properties properties = Connection.getProperties();

        try {

            connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));

            File file = new File("config" + File.separator + "database" + File.separator + "metadata.json");

            if (!file.exists()) {

                StringBuilder metadata = new StringBuilder("{\n");

                List<String> sequences = new ArrayList();

                if (properties.getProperty("url").toLowerCase().contains("firebird")) {
                    String query = "SELECT RDB$GENERATOR_NAME FROM RDB$GENERATORS";
                    try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(query)) {
                        while (rs.next()) {
                            sequences.add(rs.getString(1).toLowerCase().trim());
                        }
                    }
                }

                try (ResultSet resultSet = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"})) {

                    while (resultSet.next()) {

                        String table = resultSet.getString(3).toLowerCase().trim();

                        metadata.append("  \"").append(table).append("\":{\n");
                        metadata.append("    \"sequence\":\"");

                        if (connection.getMetaData().getURL().toLowerCase().contains("firebird")) {
                            for (String sequence : sequences) {
                                if (sequence.contains(table)) {
                                    metadata.append(sequence);
                                    break;
                                }
                            }
                        }

                        metadata.append("\",\n");

                        try (ResultSet rs = connection.getMetaData().getPrimaryKeys(null, null, table)) {
                            if (rs.next()) {
                                metadata.append("    \"primary_key\":\"").append(rs.getString(4).trim().toLowerCase()).append("\",\n");
                            }
                        }

                        try (ResultSet rs = connection.getMetaData().getColumns(null, null, table, "%")) {

                            metadata.append("    \"columns\":[\n");

                            while (rs.next()) {

                                String column = rs.getString(4).trim().toLowerCase();
                                int type = rs.getInt(5);
                                String typeName = rs.getString(6);
                                int length = rs.getInt(7);
                                boolean notNull = rs.getString(18).toLowerCase().equals("no");

                                metadata.append("        {\n");
                                metadata.append("          \"name\":\"").append(column).append("\",\n");
                                metadata.append("          \"data_type\":\"").append(type).append("\",\n");
                                metadata.append("          \"type_name\":\"").append(typeName).append("\",\n");
                                metadata.append("          \"length\":\"").append(length).append("\",\n");
                                metadata.append("          \"not_null\":\"").append(notNull).append("\"\n");
                                metadata.append("        }");

                                if (!rs.isLast()) {
                                    metadata.append(",\n");
                                } else {
                                    metadata.append("\n");
                                }
                            }

                            metadata.append("    ],\n");
                        }

                        try (ResultSet resultSetForeignKey = connection.getMetaData().getImportedKeys(null, null, table)) {

                            metadata.append("    \"foreign_key\":{\n");

                            while (resultSetForeignKey.next()) {

                                String column = resultSetForeignKey.getString(8).trim().toLowerCase();
                                String reference = resultSetForeignKey.getString(3).trim().toLowerCase();

                                metadata.append("      \"").append(column).append("\":\"").append(reference).append("\"");

                                if (!resultSetForeignKey.isLast()) {
                                    metadata.append(",\n");
                                } else {
                                    metadata.append("\n");
                                }
                            }

                            metadata.append("    }\n");
                        }

                        metadata.append("  }");

                        if (!resultSet.isLast()) {
                            metadata.append(",\n");
                        }
                    }
                }

                metadata.append("\n}");

                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                try (PrintWriter p = new PrintWriter(new FileWriter(file, false))) {
                    p.write(metadata.toString());
                }
            }

            file = new File("config" + File.separator + "orm.json");

            if (!file.exists()) {

                List<String> tables = Metadata.getTables();
                StringBuilder orm = new StringBuilder("{\n");

                for (int i = 0; i < tables.size(); i++) {

                    String table = tables.get(i);

                    orm.append("  \"").append(table).append("\":{\n");
                    orm.append("    \"to_string\":\"").append(table.toUpperCase()).append(" registro ID: {").append(Metadata.getPrimaryKeyName(table)).append("}\",\n");
                    orm.append("    \"labels\":{\n");

                    JSONArray columns = Metadata.getColumns(table);

                    for (int j = 0; j < columns.size(); j++) {

                        String column = columns.get(j).getStringValue("name");

                        orm.append("      \"").append(column).append("\":\"").append(column.replace("_", " ").toUpperCase()).append("\"");

                        if (j < columns.size() - 1) {
                            orm.append(",\n");
                        } else {
                            orm.append("\n");
                        }
                    }

                    orm.append("    }\n");
                    orm.append("  }");

                    if (i < tables.size() - 1) {
                        orm.append(",\n");
                    }
                }

                orm.append("\n}");

                try (PrintWriter p = new PrintWriter(new FileWriter(file, false))) {
                    p.write(orm.toString());
                }
            }

        } catch (IOException | SQLException ex) {
            throw new DatabaseException(ex);
        }

        return connection;
    }

    /**
     * URL de conexão com o banco de dados
     *
     * @return String
     * @throws DatabaseException
     */
    public static String getURL() throws DatabaseException {
        return getProperties().getProperty("url");
    }
}
