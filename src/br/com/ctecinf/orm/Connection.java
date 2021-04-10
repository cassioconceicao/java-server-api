/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.orm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public class Connection implements AutoCloseable {

    private java.sql.Connection connection;

    public Connection() throws ORMException {

        File file = new File("config" + File.separator + "database" + File.separator + "connection.properties");

        Properties properties = new Properties();

        if (!file.exists()) {

            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            properties.put("url", "jdbc:firebirdsql:localhost:[path]");
            properties.put("username", "sysdba");
            properties.put("password", "masterkey");
            properties.put("update", "true");
            properties.put("create", "false");

            try {
                properties.store(new FileOutputStream(file), "Dados para conexão com o banco de dados\nEx. de URL: \"jdbc:firebirdsql:localhost:[path]\"\nUPDATE: true = Atualiza estrutura do banco de dados no arquivo 'metadata.json' na primeira conexão.\nCREATE: true = Cria um banco de dados à partir do arquivo 'metadata.json'.");
            } catch (IOException ex) {
                throw new ORMException(ex);
            }
        }

        try {

            properties.load(new FileInputStream(file));

            connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));

            file = new File("config" + File.separator + "database" + File.separator + "metadata.json");

            if (!file.exists() || Boolean.parseBoolean(properties.getProperty("update"))) {

                StringBuilder sb = new StringBuilder("{\n");

                try (ResultSet resultSetTables = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"})) {

                    while (resultSetTables.next()) {

                        String table = resultSetTables.getString(3).toLowerCase().trim();

                        sb.append("  \"").append(table).append("\":{\n");

                        try (ResultSet resultSetPrimaryKey = connection.getMetaData().getPrimaryKeys(null, null, table)) {

                            if (resultSetPrimaryKey.next()) {
                                String primaryKey = resultSetPrimaryKey.getString(4).trim().toLowerCase();
                                sb.append("    \"primary_key\":\"").append(primaryKey).append("\",\n");
                            }
                        }

                        try (ResultSet resultSetColumns = connection.getMetaData().getColumns(null, null, table, "%")) {

                            sb.append("    \"columns\":{\n");
                            while (resultSetColumns.next()) {

                                String column = resultSetColumns.getString(4).trim().toLowerCase();
                                int type = resultSetColumns.getInt(5);

                                sb.append("      \"").append(column).append("\":\"").append(type).append("\"");

                                if (!resultSetColumns.isLast()) {
                                    sb.append(",\n");
                                } else {
                                    sb.append("\n");
                                }
                            }

                            sb.append("    },\n");
                        }

                        try (ResultSet resultSetForeignKey = connection.getMetaData().getImportedKeys(null, null, table)) {

                            sb.append("    \"foreign_key\":{\n");
                            while (resultSetForeignKey.next()) {

                                String column = resultSetForeignKey.getString(8).trim().toLowerCase();
                                String reference = resultSetForeignKey.getString(3).trim().toLowerCase();

                                sb.append("      \"").append(column).append("\":\"").append(reference).append("\"");

                                if (!resultSetForeignKey.isLast()) {
                                    sb.append(",\n");
                                } else {
                                    sb.append("\n");
                                }
                            }

                            sb.append("    }\n");
                        }
                        
                        sb.append("  }");

                        if (!resultSetTables.isLast()) {
                            sb.append(",\n");
                        }
                    }
                }

                sb.append("\n}");

                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                try (PrintWriter p = new PrintWriter(new FileWriter(file, false))) {
                    p.write(sb.toString());
                }
            }

        } catch (IOException | SQLException ex) {
            throw new ORMException(ex);
        }
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Override
    public String toString() {

        try {
            return connection.getMetaData().getURL();
        } catch (SQLException ex) {
            return super.toString();
        }
    }

    public static void main(String[] args) throws ORMException {
        new Connection();
    }
}
