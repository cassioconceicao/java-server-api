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

/**
 *
 * @author Cássio Conceição
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class DarumaReceita {

    /**
     * Imprimi uma Receita do cliente
     *
     * @param receita{<br>
     * "cliente": {<br>
     * "nome": "",<br>
     * "endereco": "",<br>
     * "fone": "",<br>
     * "celular": ""<br>
     * },<br>
     * "lente": "",<br>
     * "armacao": "",<br>
     * "longe": {<br>
     * "dir_esf": "",<br>
     * "dir_cil": "",<br>
     * "dir_eixo": "",<br>
     * "esq_esf": "",<br>
     * "esq_cil": "",<br>
     * "esq_eixo": ""<br>
     * },<br>
     * "perto": {<br>
     * "dir_esf": "",<br>
     * "dir_cil": "",<br>
     * "dir_eixo": "",<br>
     * "esq_esf": "",<br>
     * "esq_cil": "",<br>
     * "esq_eixo": ""<br>
     * },<br>
     * "adicao": "",<br>
     * "dnp": "",<br>
     * "altura": "",<br>
     * "data_venda": "",<br>
     * "medico": "",<br>
     * "vendedor": ""<br> }
     * @throws java.lang.Exception
     */
    public static void print(JSONObject receita) throws Exception {

        Daruma p = new Daruma();

        p.head();
        p.dotLine();

        p.fonteSize(2);
        p.bold("COPIA RECEITA");
        p.newLine();
        p.newLine();
        p.startCond();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Cliente: " + receita.getJSONObjectValue("cliente").getStringValue("nome"));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Endereco: " + receita.getJSONObjectValue("cliente").getStringValue("endereco"));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Fone: " + receita.getJSONObjectValue("cliente").getStringValue("fone") + " Celular: " + receita.getJSONObjectValue("cliente").getStringValue("celular"));

        p.dotLine();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Lente: <b>" + receita.getStringValue("lente") + "</b>");

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Armacao: " + receita.getStringValue("armacao"));

        p.dotLine();

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text("LONGE");

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("          Dir.: " + Utils.leftPad2(receita.getJSONObjectValue("longe").getStringValue("dir_esf"), 6, ' ') + "   " + Utils.leftPad2(receita.getJSONObjectValue("longe").getStringValue("dir_cil"), 6, ' ') + " X " + receita.getJSONObjectValue("longe").getStringValue("dir_eixo"));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("          Esq.: " + Utils.leftPad2(receita.getJSONObjectValue("longe").getStringValue("esq_esf"), 6, ' ') + "   " + Utils.leftPad2(receita.getJSONObjectValue("longe").getStringValue("esq_cil"), 6, ' ') + " X " + receita.getJSONObjectValue("longe").getStringValue("esq_eixo"));

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text("PERTO");

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("          Dir.: " + Utils.leftPad2(receita.getJSONObjectValue("perto").getStringValue("dir_esf"), 6, ' ') + "   " + Utils.leftPad2(receita.getJSONObjectValue("perto").getStringValue("dir_cil"), 6, ' ') + " X " + receita.getJSONObjectValue("perto").getStringValue("dir_eixo"));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("          Esq.: " + Utils.leftPad2(receita.getJSONObjectValue("perto").getStringValue("esq_esf"), 6, ' ') + "   " + Utils.leftPad2(receita.getJSONObjectValue("perto").getStringValue("esq_cil"), 6, ' ') + " X " + receita.getJSONObjectValue("perto").getStringValue("esq_eixo"));

        p.dotLine();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Adicao: <b>" + receita.getStringValue("adicao") + "</b>    DNP: <b>" + receita.getStringValue("dnp") + "</b>   Altura: <b>" + receita.getStringValue("altura") + "</b>");

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Data: " + DateFormatter.format().format(receita.getDateValue("data_venda")));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Medico: " + receita.getStringValue("medico"));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Vendedor: " + receita.getStringValue("vendedor"));

        p.breakLines(5);
    }
}
