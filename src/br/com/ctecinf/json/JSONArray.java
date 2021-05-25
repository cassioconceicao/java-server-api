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
package br.com.ctecinf.json;

import java.util.ArrayList;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class JSONArray extends ArrayList<JSONObject> {

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder("[\n");

        for (int i = 0; i < this.size(); i++) {

            str.append(this.get(i));

            if (i < this.size() - 1) {
                str.append(", ");
            }
        }

        str.append("]");

        return str.toString();
    }
}
