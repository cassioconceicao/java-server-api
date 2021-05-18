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
package br.com.ctecinf.server;

import br.com.ctecinf.database.DatabaseException;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public class Server {

    private HttpServer server;
    private final List<Handler> services;

    /**
     * Porta default
     *
     * @throws br.com.ctecinf.server.ServerException
     */
    public Server() throws ServerException {

        try {
            server = HttpServer.create(new InetSocketAddress(Integer.valueOf(getProperties().getProperty("port"))), 0);
        } catch (NumberFormatException | IOException ex) {
            throw new ServerException(ex);
        }

        services = new ArrayList();

        File root = new File("html");

        if (!root.exists()) {
            root.mkdirs();
        }

        File dir = new File(root, "js");

        if (!dir.exists()) {
            dir.mkdirs();
            try (Lib lib = new Lib("default.js")) {
                lib.download(dir);
            }
        }
    }

    /**
     * Propriedades do servidor
     *
     * @return Properties
     * @throws DatabaseException
     */
    private static Properties getProperties() throws ServerException {

        File file = new File("config" + File.separator + "server.properties");

        Properties properties = new Properties();

        if (!file.exists()) {

            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            properties.put("port", "8080");
            properties.put("address", getIpAddress());

            try {
                properties.store(new FileOutputStream(file), "Configuração do servidor");
            } catch (IOException ex) {
                throw new ServerException(ex);
            }
        }

        try {
            properties.load(new FileInputStream(file));
        } catch (IOException ex) {
            throw new ServerException(ex);
        }

        properties.replace("address", Server.getIpAddress());

        try {
            properties.store(new FileOutputStream(file), "Configuração do servidor");
        } catch (IOException ex) {
            throw new ServerException(ex);
        }

        return properties;
    }

    /**
     * Adiciona um serviço
     *
     * @param handler
     * @return Server
     */
    public Server addContext(Handler handler) {
        server.createContext("/" + handler.getName(), handler);
        services.add(handler);
        return this;
    }

    /**
     * Inicia servidor
     */
    public void start() {
        server.setExecutor(null);
        server.start();
    }

    /**
     * Lista de serviços adicionados
     *
     * @return
     */
    public List<Handler> getServices() {
        return services;
    }

    /**
     * Endereço IP que o servidor está rodando
     *
     * @return String IP
     * @throws ServerException
     */
    public static String getIpAddress() throws ServerException {

        try {

            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface netint : Collections.list(nets)) {

                if (!netint.isLoopback() && netint.isUp()) {

                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        if (inetAddress.isSiteLocalAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException ex) {
            throw new ServerException(ex);
        }

        return null;
    }
}
