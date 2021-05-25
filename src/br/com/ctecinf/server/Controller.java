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

import br.com.ctecinf.database.Clause;
import br.com.ctecinf.database.Metadata;
import br.com.ctecinf.database.Query;
import java.sql.ResultSet;
import java.util.Map;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public class Controller extends Handler {

    public Controller() {
        super("controller", Handler.TYPE_JSON);
    }

    @Override
    protected byte[] getResponse(Map<String, Object> requestParams) throws Exception {

        String table = (String) requestParams.get(AJAX.PARAM_NAME_TABLE);
        String action = (String) requestParams.get(AJAX.PARAM_NAME_ACTION);
        String term = requestParams.get(AJAX.PARAM_NAME_TERM) == null ? "" : (String) requestParams.get(AJAX.PARAM_NAME_TERM);
        Integer offset = requestParams.get(AJAX.PARAM_NAME_OFFSET) == null ? 0 : Integer.parseInt(requestParams.get(AJAX.PARAM_NAME_OFFSET).toString());
        Integer limit = requestParams.get(AJAX.PARAM_NAME_LIMIT) == null ? 100 : Integer.parseInt(requestParams.get(AJAX.PARAM_NAME_LIMIT).toString());

        switch (action) {

            case AJAX.PARAM_VALUE_QUERY:

                try (Query query = new Query(table, Clause.create(table).like(term)).setLimit(offset, limit); ResultSet rs = query.getResultSet()) {

                    StringBuilder json = new StringBuilder();
                    json.append("{\"primary_key\": \"").append(Metadata.getPrimaryKeyName(table)).append("\", ");
                    json.append("\"columns\": ").append(Metadata.getColumns(table)).append(",");
                    json.append("\"data\": ").append(query.getJSONData()).append("}");

                    return json.toString().getBytes();

                } catch (Exception ex) {

                    StringBuilder sb = new StringBuilder();
                    sb.append("Exception: ").append(ex.getMessage()).append("\\n");

                    for (StackTraceElement stackTrace : ex.getStackTrace()) {
                        if (stackTrace.getClassName().contains("ctecinf")) {
                            sb.append("Class: ").append(stackTrace.getClassName()).append("\\n");
                            sb.append("File: ").append(stackTrace.getFileName()).append("\\n");
                            sb.append("Line: ").append(stackTrace.getLineNumber()).append("\\n");
                            sb.append("Method: ").append(stackTrace.getMethodName());
                            break;
                        }
                    }

                    return ("{\"message\": \"" + sb + "\", \"type\": \"exception\"}").getBytes();
                }

            case AJAX.PARAM_VALUE_SAVE:
                return "{\"message\": \"Registro salvo com sucesso.\", \"type\": \"success\"}".getBytes();

            case AJAX.PARAM_VALUE_DELETE:
                return "{\"message\": \"Registro apagado com sucesso.\", \"type\": \"success\"}".getBytes();

            default:
                return "{\"message\": \"Parâmetro 'action' inválido.\", \"type\": \"error\"}".getBytes();
        }
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.addContext(new Controller());
        server.start();
    }

}
