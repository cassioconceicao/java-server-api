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
import br.com.ctecinf.json.JSONObject;
import br.com.ctecinf.text.DateFormatter;
import br.com.ctecinf.text.NumberFormatter;
import java.util.Date;

/**
 *
 * @author Cássio Conceição
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class DarumaRecibo {

    /**
     * Imprimi um recibo de pagamento
     *
     * @param json {"nome_cliente":"[nome]", "valor_recebido":"[valor]",
     * "referente":"[descricao]", "numero":"[numero recibo]",
     * "via_consumidor":"[true|false]", "slogan":"[slogan empresa]"}
     * @throws DarumaException
     */
    public static void print(JSONObject json) throws DarumaException {

        Daruma p = new Daruma();

        p.head();

        p.align(Daruma.ALIGN_CENTER);
        p.fonteSize(4);
        p.bold("RECIBO");
        p.newLine();

        p.newLine();
        p.fonteSize(1);
        p.align(Daruma.ALIGN_LEFT);
        p.tab();

        p.text("Recebemos de ");
        p.bold(json.getStringValue("nome_cliente"));
        p.text(", a quantia de ");
        p.bold(NumberFormatter.format(2).format(json.get("valor_recebido")));
        p.text(", correspondente a: ");
        p.bold(json.getStringValue("referente"));
        p.text(" do que passamos o presente recibo.");

        p.newLine();
        p.newLine();

        p.align(Daruma.ALIGN_CENTER);
        p.bold("SEM VALOR FISCAL");
        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text("Num.:" + Utils.leftPad2(json.getStringValue("numero"), 6, '0') + " Emissao:" + DateFormatter.format().format(new Date()));
        p.newLine();

        p.align(Daruma.ALIGN_CENTER);
        p.text("Via");
        p.newLine();

        p.align(Daruma.ALIGN_CENTER);
        p.text(Boolean.valueOf(json.getStringValue("via_consumidor")) ? "Consumidor" : "Estabelecimento");

        p.breakLines(5);

        p.dotLine();

        p.align(Daruma.ALIGN_CENTER);
        p.text("Autenticacao");

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.bold(json.getStringValue("slogan"));

        p.breakLines(6);

        p.end();
    }
}
