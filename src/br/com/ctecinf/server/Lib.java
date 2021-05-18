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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public class Lib implements AutoCloseable {

    private static final String SERVER = "https://ctecinf.com.br/lib/";

    private HttpURLConnection connection;
    private String fileName;
    private String disposition;
    private Double contentLength;
    private String contentType;

    /**
     * Construtor
     *
     * @param file Nome do arquivo para download do servidor
     * @throws ServerException
     */
    public Lib(String file) throws ServerException {
        try {
            this.connection = (HttpURLConnection) new URL(SERVER + file).openConnection();
        } catch (IOException ex) {
            throw new ServerException(ex);
        }
    }

    /**
     * Arquivo baixado
     *
     * @param dir Nome/Caminho do diretório
     * @return File Arquivo recebido
     * @throws ServerException
     */
    public File download(String dir) throws ServerException {
        return download(new File(dir));
    }

    /**
     * Arquivo baixado
     *
     * @param dir Arquivo do diretório
     * @return File Arquivo recebido no diretório informado
     * @throws ServerException
     */
    public File download(File dir) throws ServerException {

        try {

            int responseCode = this.connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                this.disposition = this.connection.getHeaderField("Content-Disposition");
                this.contentLength = Double.valueOf(this.connection.getHeaderField("Content-Length"));
                this.contentType = this.connection.getHeaderField("Content-Type");

                if (this.disposition != null) {
                    int index = this.disposition.indexOf("filename=");
                    if (index > 0) {
                        this.fileName = this.disposition.substring(index + 10, this.disposition.length() - 1);
                    }
                } else {
                    String url = this.connection.getURL().toString();
                    this.fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
                }

                try (InputStream inputStream = this.connection.getInputStream()) {

                    File file = new File(dir, this.fileName);

                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }

                    try (FileOutputStream outputStream = new FileOutputStream(file)) {
                        int bytesRead;
                        byte[] buffer = new byte[4096];
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }

                    return file;
                }

            } else {
                throw new ServerException("Arquivo não encontrado. Server replied HTTP code: " + responseCode);
            }

        } catch (IOException ex) {
            throw new ServerException(ex);
        }
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDisposition() {
        return disposition;
    }

    public Double getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    @Override
    public void close() throws ServerException {
        if (this.connection != null) {
            this.connection.disconnect();
        }
    }

}
