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
     * @param receita
     * @throws java.lang.Exception
     */
    public static void print(Receita receita) throws Exception {

        if (receita == null) {
            throw new Exception("Receita nula.");
        }

        if (receita.getCliente() == null) {
            throw new Exception("Receita não está vinculada a um cliente.");
        }

        Daruma p = new Daruma();

        p.head();
        p.dotLine();

        p.fonteSize(2);
        p.bold("COPIA RECEITA");
        p.newLine();
        p.newLine();

        p.leftLine("Cliente: " + receita.getCliente().getNome());
        p.leftLine("Endereco: " + receita.getCliente().getEndereco());
        p.leftLine("Fone: " + receita.getCliente().getFone() + " Celular: " + receita.getCliente().getCelular());
        p.dotLine();
        p.leftLine("Lente: <b>" + receita.getLente() + "</b>");
        p.leftLine("Armacao: " + receita.getArmacao());
        p.dotLine();
        p.centerLine("LONGE");
        p.leftLine("          Dir.: " + Utils.leftPad2(receita.getLongeDireitoEsferico(), 6, ' ') + "   " + Utils.leftPad2(receita.getLongeDireitoCilindrico(), 6, ' ') + " X " + receita.getLongeDireitoEixo());
        p.leftLine("          Esq.: " + Utils.leftPad2(receita.getLongeEsquerdoEsferico(), 6, ' ') + "   " + Utils.leftPad2(receita.getLongeEsquerdoCilindrico(), 6, ' ') + " X " + receita.getLongeEsquerdoEixo());

        p.centerLine("PERTO");
        p.leftLine("          Dir.: " + Utils.leftPad2(receita.getPertoDireitoEsferico(), 6, ' ') + "   " + Utils.leftPad2(receita.getPertoDireitoCilindrico(), 6, ' ') + " X " + receita.getPertoDireitoEixo());
        p.leftLine("          Esq.: " + Utils.leftPad2(receita.getPertoEsquerdoEsferico(), 6, ' ') + "   " + Utils.leftPad2(receita.getPertoEsquerdoCilindrico(), 6, ' ') + " X " + receita.getPertoEsquerdoEixo());
        p.dotLine();
        p.leftLine("Adicao: <b>" + receita.getAdicao() + "</b>    DNP: <b>" + receita.getDnp() + "</b>   Altura: <b>" + receita.getAltura() + "</b>");
        p.leftLine("Data: " + DateFormatter.format().format(receita.getDataEncomenda()));
        p.leftLine("Medico: " + receita.getMedico());
        p.leftLine("Vendedor: " + receita.getVendedor());

        p.breakLines(5);
    }
}
