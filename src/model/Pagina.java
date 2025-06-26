package model;

public class Pagina {
	public int numero;
	public boolean presente;
	public boolean modificada;
	public boolean referenciada;

	public Pagina(int numero) {
		this.numero = numero;
		this.presente = false;
		this.modificada = false;
		this.referenciada = false;
	}
}