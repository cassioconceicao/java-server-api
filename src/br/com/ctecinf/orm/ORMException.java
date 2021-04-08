/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.ctecinf.orm;

/**
 *
 * @author Cássio Conceição
 * @version 2021
 * @see http://ctecinf.com.br
 */
public class ORMException extends Exception {

    public ORMException(String message) {
        super(message);
    }

    public ORMException(Throwable cause) {
        super(cause);
    }

}
