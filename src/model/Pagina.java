package model;

public class Pagina {
    int numero;
    public boolean presente;
    public boolean modificada;

    public Pagina(int numero) {
        this.numero = numero;
        this.presente = false;
        this.modificada = false;
    }
}