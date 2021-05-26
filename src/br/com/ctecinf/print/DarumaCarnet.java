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
package br.com.ctecinf.print;

import br.com.ctecinf.Utils;
import br.com.ctecinf.json.JSONArray;
import br.com.ctecinf.json.JSONObject;
import br.com.ctecinf.text.DateFormatter;
import br.com.ctecinf.text.NumberFormatter;

/**
 *
 * @author Cássio Conceição
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class DarumaCarnet {

    /**
     * Imprimi um Carnet crediario
     *
     * @param crediario {<br>
     * "cliente": "",<br>
     * "data_venda": "",<br>
     * "valor_total": "",<br>
     * "parcelas": [<br>
     * {"id": "", "numero_parcela": "", "valor": "",
     * "data_vencimento": "", "data_pagamento": ""}, ...<br>
     * ]<br> }
     * @throws br.com.ctecinf.print.DarumaException
     */
    public static void print(JSONObject crediario) throws DarumaException {

        JSONArray parcelas = crediario.getJSONArrayValue("parcelas");

        if (parcelas.isEmpty()) {
            throw new DarumaException("Lista de parcelas vazia.");
        }

        Daruma p = new Daruma();

        p.head();
        p.dotLine();

        p.fonteSize(2);
        p.bold("CARNET");
        p.newLine();
        p.newLine();

        p.newLine();
        p.startCond();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Cliente: " + crediario.getStringValue("cliente"));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Data da compra: " + DateFormatter.format().format(crediario.getDateValue("data_venda")));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Valor total: R$ ");
        p.bold(NumberFormatter.format(2).format(crediario.getNumberValue("valor_total")));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Numero de Parcelas: " + parcelas.size());

        p.dotLine();

        for (JSONObject parcela : parcelas) {

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text("Parcela: " + parcela.getStringValue("numero_parcela"));

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text("Valor: " + NumberFormatter.format(2).format(parcela.getNumberValue("valor")));

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text("Data Venc.: " + DateFormatter.format().format(parcela.getDateValue("data_vencimento")));

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text("Data Pgto.: " + (parcela.getDateValue("data_pagamento") != null ? DateFormatter.format().format(parcela.getDateValue("data_pagamento")) : "    /    /"));

            p.align(Daruma.ALIGN_CENTER);
            p.ean13Bar(Utils.leftPad2(parcela.getStringValue("id"), 12, '0'), 80, false);

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.text(Utils.leftPad2(parcela.getStringValue("id"), 12, '0'));

            p.dotLine();
        }

        p.breakLines(5);
    }
}
