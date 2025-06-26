package model;

public class QuadroMemoria {
	public String processoId;
	public int paginaNumero;
	public boolean bitUso = false;
	public boolean bitModificado = false;
	public long ultimoAcesso = System.currentTimeMillis();

	public QuadroMemoria(String processoId, int paginaNumero) {
		this.processoId = processoId;
		this.paginaNumero = paginaNumero;
	}
}