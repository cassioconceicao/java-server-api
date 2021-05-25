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
import static br.com.ctecinf.print.Daruma.ALIGN_CENTER;
import br.com.ctecinf.text.DateFormatter;
import br.com.ctecinf.text.NumberFormatter;
import java.util.List;

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
     * @param json
     * @throws java.io.IOException
     */
    public static void print(JSONObject json) throws Exception {

        if (crediario == null) {
            throw new Exception("Crediário nulo.");
        }

        if (crediario.getCliente() == null) {
            throw new Exception("Crediário não está vinculado a um cliente.");
        }

        List<Parcela> parcelas = crediario.listOf(Parcela.class);

        if (parcelas.isEmpty()) {
            throw new Exception("Lista de parcelas vazia.");
        }

        Daruma p = new Daruma();

        p.head();
        p.dotLine();

        p.fonteSize(2);
        p.bold("CARNET");
        p.newLine();
        p.newLine();

        p.leftLine("Cliente: " + crediario.getCliente());
        p.leftLine("Data da compra: " + DateFormatter.format().format(crediario.getDataVenda()));
        p.leftLine("Valor total: R$ " + bold(NumberFormatter.format(2).format(crediario.getValorTotal())));
        p.leftLine("Numero de Parcelas: " + parcelas.size());
        p.dotLine();

        for (Parcela parcela : parcelas) {

            p.leftLine("Parcela: " + parcela.getNumeroParcela());
            p.leftLine("Valor: " + NumberFormatter.format(2).format(parcela.getValor()));
            p.leftLine("Data Venc.: " + DateFormatter.format().format(parcela.getDataVencimento()));
            p.leftLine("Data Pgto.: " + (parcela.getDataPagamento() != null ? DateFormatter.format().format(parcela.getDataPagamento()) : "    /    /"));

            p.align(ALIGN_CENTER);
            p.ean13Bar(Utils.leftPad2(parcela.getId(), 12, '0'), 80, false);
            p.centerLine(Utils.leftPad2(parcela.getId(), 12, '0'));
            p.dotLine();
        }

        p.breakLines(5);
    }
}
