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
import br.com.ctecinf.UtilsException;
import br.com.ctecinf.text.MaskFormatter;
import br.com.ctecinf.text.NumberFormatter;
import br.com.ctecinf.text.TimestampFormatter;
import br.inf.portalfiscal.nfe.v400.autorizacao.TNFe;
import br.inf.portalfiscal.nfe.v400.autorizacao.TNfeProc;

/**
 *
 * @author Cássio Conceição
 * @since 2021
 * @version 2021
 * @see http://ctecinf.com.br/
 */
public class DarumaDanfe {

    /**
     * Imprimi uma DANFE NFC-e V310
     *
     * @param nfeProc
     * @param cscToken
     * @throws DarumaException
     */
    public static void printNFCe310(br.inf.portalfiscal.nfe.v310.autorizacao.TNfeProc nfeProc, String cscToken) throws DarumaException {

        if (nfeProc.getNFe().getInfNFeSupl() == null || nfeProc.getNFe().getInfNFeSupl().getQrCode() == null) {

            br.inf.portalfiscal.nfe.v310.autorizacao.TNFe.InfNFe inf = nfeProc.getNFe().getInfNFe();

            String cpf = inf.getDest() != null ? inf.getDest().getCPF() : null;
            String dhEmi = inf.getIde().getDhEmi();
            String vTotal = inf.getTotal().getICMSTot().getVNF();
            String chaveAcesso = inf.getId();
            String tpAmb = inf.getIde().getTpAmb();
            String digestValue = new String(nfeProc.getNFe().getSignature().getSignedInfo().getReference().getDigestValue());

            StringBuilder params = new StringBuilder();

            params.append("chNFe=").append(chaveAcesso.replace("NFe", "")).append("&");
            params.append("nVersao=100&");
            params.append("tpAmb=").append(tpAmb).append("&");

            if (cpf != null) {
                params.append("cDest=").append(cpf).append("&");
            }

            params.append("dhEmi=").append(Utils.hexa(dhEmi)).append("&");
            params.append("vNF=").append(vTotal).append("&");
            params.append("vICMS=0.00&");
            params.append("digVal=").append(Utils.hexa(digestValue)).append("&");
            params.append("cIdToken=000001");
            params.append(cscToken);

            String url = null;

            try {
                url = "https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx?" + params.toString().replace(cscToken, "") + "&cHashQRCode=" + Utils.sha1(params.toString()).toLowerCase();
            } catch (UtilsException ex) {
                throw new DarumaException(ex);
            }

            br.inf.portalfiscal.nfe.v310.autorizacao.TNFe.InfNFeSupl infNFeSupl = new br.inf.portalfiscal.nfe.v310.autorizacao.TNFe.InfNFeSupl();
            infNFeSupl.setQrCode(url);
            nfeProc.getNFe().setInfNFeSupl(infNFeSupl);
        }

        Daruma p = new Daruma();

        p.fonteSize(1);
        p.startCond();
        p.align(Daruma.ALIGN_CENTER);

        p.text("CNPJ " + MaskFormatter.format(nfeProc.getNFe().getInfNFe().getEmit().getCNPJ(), "##.###.###/####-##") + "  ");
        p.bold(nfeProc.getNFe().getInfNFe().getEmit().getXNome());

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text(nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getXLgr() + "," + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getNro() + "," + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getXCpl() + "," + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getXBairro() + "," + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getXMun() + "-" + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getUF());

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text("Documento Auxiliar Nota Fiscal de Consumidor Eletronica");

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.bold(Utils.rightPad2("Codigo", 7, ' ') + Utils.rightPad2("Descricao", 23, ' ') + Utils.rightPad2("Qtde", 5, ' ') + Utils.rightPad2("UN", 6, ' ') + Utils.rightPad2("Vl Unit", 8, ' ') + Utils.leftPad2("Vl Total", 8, ' '));

        for (br.inf.portalfiscal.nfe.v310.autorizacao.TNFe.InfNFe.Det det : nfeProc.getNFe().getInfNFe().getDet()) {
            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text(Utils.rightPad2(Utils.leftPad2(det.getProd().getCProd(), 6, '0'), 7, ' ') + Utils.rightPad2(Utils.leftPad2(Utils.limitWords(det.getProd().getXProd(), 3), 22, ' '), 23, ' ') + Utils.rightPad2(Utils.leftPad2(det.getProd().getQCom(), 4, ' '), 5, ' ') + Utils.rightPad2(det.getProd().getUCom(), 4, ' ') + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(det.getProd().getVUnCom())), 9, ' ') + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(det.getProd().getVProd())), 9, ' '));
        }

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Qtde total de itens" + Utils.leftPad2(nfeProc.getNFe().getInfNFe().getDet().size(), 38, ' '));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Valor total R$" + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(nfeProc.getNFe().getInfNFe().getTotal().getICMSTot().getVProd())), 43, ' '));

        if (Double.valueOf(nfeProc.getNFe().getInfNFe().getTotal().getICMSTot().getVDesc()) > 0) {

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text("Desconto R$" + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(nfeProc.getNFe().getInfNFe().getTotal().getICMSTot().getVDesc())), 46, ' '));

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.bold("Valor a Pagar R$" + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(nfeProc.getNFe().getInfNFe().getTotal().getICMSTot().getVNF())), 41, ' '));
        }

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("FORMA PAGAMENTO" + Utils.leftPad2("VALOR PAGO R$", 42, ' '));

        for (br.inf.portalfiscal.nfe.v310.autorizacao.TNFe.InfNFe.Pag pag : nfeProc.getNFe().getInfNFe().getPag()) {

            String tPag;

            switch (pag.getTPag()) {
                case "01":
                    tPag = "Dinheiro";
                    break;
                case "02":
                    tPag = "Cheque";
                    break;
                case "03":
                    tPag = "Catao de Credito";
                    break;
                case "04":
                    tPag = "Cartao de Debito";
                    break;
                case "05":
                    tPag = "Credito Loja";
                    break;
                case "10":
                    tPag = "Vale Alimentacao";
                    break;
                case "11":
                    tPag = "Vale Refeicao";
                    break;
                case "12":
                    tPag = "Vale Presente";
                    break;
                case "13":
                    tPag = "Vale Combustivel";
                    break;
                default:
                    tPag = "Outros";
            }

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text(tPag + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(pag.getVPag())), 57 - tPag.length(), ' '));
        }

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.bold("Consulte pela Chave de Acesso em");

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text("https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx");

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text(MaskFormatter.format(nfeProc.getNFe().getInfNFe().getId().replace("NFe", ""), "#### #### #### #### #### #### #### #### #### #### ####"));

        p.newLine();

        if (nfeProc.getNFe().getInfNFe().getDest() != null) {

            String nome = nfeProc.getNFe().getInfNFe().getDest().getXNome();

            if (nfeProc.getNFe().getInfNFe().getDest().getCPF() != null) {

                String cpf = nfeProc.getNFe().getInfNFe().getDest().getCPF();

                p.newLine();
                p.align(Daruma.ALIGN_CENTER);
                p.bold("CONSUMIDOR - CPF " + MaskFormatter.format(cpf, "###.###.###-##"));

            } else if (nfeProc.getNFe().getInfNFe().getDest().getIdEstrangeiro() != null) {

                String idEstrangeiro = nfeProc.getNFe().getInfNFe().getDest().getCPF();

                p.newLine();
                p.align(Daruma.ALIGN_CENTER);
                p.bold("CONSUMIDOR - ID ESTRANGEIRO " + idEstrangeiro);
            }

            if (!nome.isEmpty()) {
                p.newLine();
                p.align(Daruma.ALIGN_CENTER);
                p.text(nome);
            }

        } else {
            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.bold("CONSUMIDOR NAO IDENTIFICADO");
        }

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);

        try {
            p.bold("NFC-e " + Utils.leftPad2(nfeProc.getNFe().getInfNFe().getIde().getNNF(), 9, '0') + " Serie " + Utils.leftPad2(nfeProc.getNFe().getInfNFe().getIde().getSerie(), 3, '0') + " " + TimestampFormatter.format().format(Utils.dateNFe2Date(nfeProc.getNFe().getInfNFe().getIde().getDhEmi()).getTime()));
        } catch (UtilsException ex) {
            throw new DarumaException(ex);
        }

        if (nfeProc.getProtNFe().getInfProt().getNProt() != null && nfeProc.getProtNFe().getInfProt().getDhRecbto() != null && nfeProc.getNFe().getInfNFeSupl().getQrCode() != null) {

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.bold("Protocolo de autorizacao: ");
            p.text(MaskFormatter.format(nfeProc.getProtNFe().getInfProt().getNProt(), "### ########## ##"));

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.bold("Data de autorizacao ");

            try {
                p.text(TimestampFormatter.format().format(Utils.dateNFe2Date(nfeProc.getProtNFe().getInfProt().getDhRecbto()).getTime()));
            } catch (UtilsException ex) {
                throw new DarumaException(ex);
            }

            p.newLine();

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.qrCode(nfeProc.getNFe().getInfNFeSupl().getQrCode());

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.text(nfeProc.getNFe().getInfNFe().getInfAdic().getInfCpl());

            p.breakLines(7);
        }

        p.end();
    }

    /**
     * Imprimi uma DANFE NFC-e V400
     *
     * @param nfeProc
     * @throws br.com.ctecinf.print.DarumaException
     */
    public static void printNFCe400(TNfeProc nfeProc) throws DarumaException {

        Daruma p = new Daruma();

        double troco = Double.valueOf(nfeProc.getNFe().getInfNFe().getPag().getVTroco());

        p.fonteSize(1);
        p.startCond();
        p.align(Daruma.ALIGN_CENTER);
        p.bold(nfeProc.getNFe().getInfNFe().getEmit().getXNome());

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text("CNPJ " + MaskFormatter.format(nfeProc.getNFe().getInfNFe().getEmit().getCNPJ(), "##.###.###/####-##") + "    IE " + nfeProc.getNFe().getInfNFe().getEmit().getIE());

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text(nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getXLgr() + ", " + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getNro() + ", " + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getXCpl());

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text(nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getXBairro() + " - " + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getXMun() + " - " + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getUF());

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text("CEP " + nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getCEP() + "  Fone " + MaskFormatter.format(nfeProc.getNFe().getInfNFe().getEmit().getEnderEmit().getFone(), "(##) ####-####"));

        p.startCond();
        p.newLine();

        p.startCond();
        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.bold("DANFE NFC-e - Documento Auxiliar");

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.bold("da Nota Fiscal de Consumidor Eletronica");

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.bold(Utils.rightPad2("Codigo", 7, ' ') + Utils.rightPad2("Descricao", 51, ' '));

        p.align(Daruma.ALIGN_RIGHT);
        p.bold(Utils.rightPad2("Qtde", 10, ' ') + Utils.rightPad2("UN", 10, ' ') + " x " + Utils.leftPad2("Vl Unit", 17, ' ') + Utils.leftPad2("Vl Total", 16, ' '));

        for (TNFe.InfNFe.Det det : nfeProc.getNFe().getInfNFe().getDet()) {
            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text(Utils.rightPad2(Utils.leftPad2(det.getProd().getCProd(), 6, '0'), 7, ' ') + Utils.rightPad2(Utils.leftPad2(det.getProd().getXProd(), 50, ' '), 51, ' '));

            p.align(Daruma.ALIGN_RIGHT);
            p.text(Utils.rightPad2(Utils.leftPad2(det.getProd().getQCom(), 6, ' '), 10, ' ') + Utils.rightPad2(det.getProd().getUCom(), 10, ' ') + " x " + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(det.getProd().getVUnCom())), 17, ' ') + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(det.getProd().getVProd())), 16, ' '));

            if (det.getProd().getVDesc() != null && Double.parseDouble(det.getProd().getVDesc()) > 0) {
                p.newLine();
                p.align(Daruma.ALIGN_RIGHT);
                p.text("Desconto " + NumberFormatter.format(2).format(Double.parseDouble(det.getProd().getVDesc())));
            }
        }

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Qtde total de itens" + Utils.leftPad2(nfeProc.getNFe().getInfNFe().getDet().size(), 38, ' '));

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Valor total R$" + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(nfeProc.getNFe().getInfNFe().getTotal().getICMSTot().getVProd())), 43, ' '));

        if (Double.valueOf(nfeProc.getNFe().getInfNFe().getTotal().getICMSTot().getVDesc()) > 0) {

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text("Desconto R$" + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(nfeProc.getNFe().getInfNFe().getTotal().getICMSTot().getVDesc())), 46, ' '));

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.bold("Valor a Pagar R$" + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(nfeProc.getNFe().getInfNFe().getTotal().getICMSTot().getVNF())), 41, ' '));
        }

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("FORMA PAGAMENTO" + Utils.leftPad2("VALOR PAGO R$", 42, ' '));

        for (TNFe.InfNFe.Pag.DetPag det : nfeProc.getNFe().getInfNFe().getPag().getDetPag()) {

            String tPag;

            switch (det.getTPag()) {
                case "01":
                    tPag = "Dinheiro";
                    break;
                case "02":
                    tPag = "Cheque";
                    break;
                case "03":
                    tPag = "Catao de Credito";
                    break;
                case "04":
                    tPag = "Cartao de Debito";
                    break;
                case "05":
                    tPag = "Credito Loja";
                    break;
                case "10":
                    tPag = "Vale Alimentacao";
                    break;
                case "11":
                    tPag = "Vale Refeicao";
                    break;
                case "12":
                    tPag = "Vale Presente";
                    break;
                case "13":
                    tPag = "Vale Combustivel";
                    break;
                default:
                    tPag = "Outros";
            }

            p.newLine();
            p.align(Daruma.ALIGN_LEFT);
            p.text(tPag + Utils.leftPad2(NumberFormatter.format().format(Double.valueOf(det.getVPag())), 57 - tPag.length(), ' '));
        }

        p.newLine();
        p.align(Daruma.ALIGN_LEFT);
        p.text("Troco R$" + Utils.leftPad2(NumberFormatter.format().format(troco), 49, ' '));

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.bold("Consulte pela Chave de Acesso em");

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text(nfeProc.getNFe().getInfNFeSupl().getUrlChave());

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text("CHAVE DE ACESSO");

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);
        p.text(MaskFormatter.format(nfeProc.getNFe().getInfNFe().getId().replace("NFe", ""), "#### #### #### #### #### #### #### #### #### #### ####"));

        p.newLine();

        if (nfeProc.getNFe().getInfNFe().getDest() != null) {

            if (nfeProc.getNFe().getInfNFe().getDest().getCPF() != null) {

                String cpf = nfeProc.getNFe().getInfNFe().getDest().getCPF();

                p.newLine();
                p.align(Daruma.ALIGN_CENTER);
                p.bold("CONSUMIDOR - CPF " + MaskFormatter.format(cpf, "###.###.###-##"));

            } else if (nfeProc.getNFe().getInfNFe().getDest().getIdEstrangeiro() != null) {

                String idEstrangeiro = nfeProc.getNFe().getInfNFe().getDest().getCPF();

                p.newLine();
                p.align(Daruma.ALIGN_CENTER);
                p.bold("CONSUMIDOR - ID ESTRANGEIRO " + idEstrangeiro);
            }

            if (nfeProc.getNFe().getInfNFe().getDest().getXNome() != null) {

                String nome = nfeProc.getNFe().getInfNFe().getDest().getXNome();

                p.newLine();
                p.align(Daruma.ALIGN_CENTER);
                p.text(nome);
            }

        } else {
            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.bold("CONSUMIDOR NAO IDENTIFICADO");
        }

        p.newLine();

        p.newLine();
        p.align(Daruma.ALIGN_CENTER);

        try {
            p.bold("NFC-e " + Utils.leftPad2(nfeProc.getNFe().getInfNFe().getIde().getNNF(), 9, '0') + " Serie " + Utils.leftPad2(nfeProc.getNFe().getInfNFe().getIde().getSerie(), 3, '0') + " " + TimestampFormatter.format().format(Utils.dateNFe2Date(nfeProc.getNFe().getInfNFe().getIde().getDhEmi()).getTime()));
        } catch (UtilsException ex) {
            throw new DarumaException(ex);
        }

        if (nfeProc.getProtNFe().getInfProt().getNProt() != null && nfeProc.getProtNFe().getInfProt().getDhRecbto() != null && nfeProc.getNFe().getInfNFeSupl().getQrCode() != null) {

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.bold("Protocolo de autorizacao: ");
            p.text(MaskFormatter.format(nfeProc.getProtNFe().getInfProt().getNProt(), "### ########## ##"));

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.bold("Data de autorizacao ");

            try {
                p.text(TimestampFormatter.format().format(Utils.dateNFe2Date(nfeProc.getProtNFe().getInfProt().getDhRecbto()).getTime()));
            } catch (UtilsException ex) {
                throw new DarumaException(ex);
            }

            p.newLine();

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.qrCode(nfeProc.getNFe().getInfNFeSupl().getQrCode());

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.text("Documento emitido por ME ou EPP optante pelo");
            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.text("Simples Nacional.");

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            p.text(nfeProc.getNFe().getInfNFe().getInfAdic().getInfCpl().replace("Documento emitido por ME ou EPP optante pelo Simples Nacional.", ""));

            p.newLine();

            p.newLine();
            p.align(Daruma.ALIGN_CENTER);
            //p.bold(Empresa.getSlogan());

            p.breakLines(7);
        }

        p.end();
    }
}
