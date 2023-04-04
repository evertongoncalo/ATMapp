package org.ProjectoATM;

public class Pessoa {

    private String nome;
    private String nif;
    private String morada;
    private String email;


    public Pessoa(String nome, String nif, String morada, String email) {
        this.nome = nome;
        this.nif = nif;
        this.morada = morada;
        this.email = email;
        
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getMorada() {
        return morada;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}












}
