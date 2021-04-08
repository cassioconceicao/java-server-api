/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.orm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import jdk.nashorn.internal.parser.JSONParser;

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

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            properties.put("url", "jdbc:firebirdsql:localhost:[path]");
            properties.put("username", "sysdba");
            properties.put("password", "masterkey");
            properties.put("update", "true");

            try {
                properties.store(new FileOutputStream(file), "Dados para conexão com o banco de dados\nEx. de URL: \"jdbc:firebirdsql:localhost:[path]\"\nUPDATE: true = Atualiza estrutura do banco de dados na primeira conexão.");
            } catch (IOException ex) {
                throw new ORMException(ex);
            }
        }

        try {

            properties.load(new FileInputStream(file));

            connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("username"), properties.getProperty("password"));

            file = new File("config" + File.separator + "database" + File.separator + "tables.properties");

            if (!file.exists() || Boolean.parseBoolean(properties.getProperty("update"))) {

                try (ResultSet resultSetTables = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"})) {

                    while(resultSetTables.next()) {
                        JSON
                    }
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

}
