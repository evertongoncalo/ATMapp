package org.ProjectoATM;

import java.util.Random;
public class Cliente {
    private int numeroConta;
    private Pessoa pessoa;
    private String pin;
    private Double saldo = 0.0;

    public Cliente(int numeroConta, Pessoa pessoa, String pin) {
        this.numeroConta = numeroConta;
        this.pessoa = pessoa;
        this.pin = pin;
    }


    public void setNumeroConta(int numeroConta) {
        this.numeroConta = numeroConta;
    }

    public int getNumeroConta() {
        return numeroConta;
    }

    public Pessoa getCliente() {
        return pessoa;
    }

    public void setCliente(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Double getSaldo() {
        return saldo;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }



    public static int gerarNumeroAleatorio(int minimo, int maximo) {
        Random gerador = new Random();
        return gerador.nextInt(maximo - minimo + 1) + minimo;
    }


}
