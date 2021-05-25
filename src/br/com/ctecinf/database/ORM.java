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

import br.com.ctecinf.json.JSON;
import br.com.ctecinf.json.JSONObject;
import br.com.ctecinf.json.JSONException;
import java.io.File;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class ORM {

    private static JSONObject json;

    /**
     * JSON
     *
     * @return JSON
     * @throws DatabaseException
     */
    protected static JSONObject get() throws DatabaseException {

        File file = new File("config", "orm.json");

        if (!file.exists()) {
            throw new DatabaseException("File 'orm.json' not found.");
        }

        if (json == null) {
            try {
                json = JSON.parse(file).get(JSONObject.class);
            } catch (JSONException ex) {
                throw new DatabaseException(ex);
            }
        }

        return json;
    }

    /**
     * String de representação da tabela
     *
     * @param table
     * @return String
     * @throws DatabaseException
     */
    public static String toString(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        String value = ORM.get().getJSONObjectValue(table.toLowerCase().trim()).getStringValue("to_string");

        return value.isEmpty() ? null : value.trim();
    }

    /**
     * Labels das colunas
     *
     * @param table
     * @return JSONObject
     * @throws DatabaseException
     */
    public static JSONObject getLabels(String table) throws DatabaseException {

        if (table == null || table.isEmpty()) {
            return null;
        }

        return ORM.get().getJSONObjectValue(table.toLowerCase().trim()).getJSONObjectValue("labels");
    }
}
