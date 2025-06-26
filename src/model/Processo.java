package model;

import java.util.HashMap;
import java.util.Map;

public class Processo {
	String id;
	public Map<Integer, Pagina> tabelaPaginas = new HashMap<>();

	public Processo(String id, int qtdPaginas) {
		this.id = id;
		for (int i = 0; i < qtdPaginas; i++) {
			tabelaPaginas.put(i, new Pagina(i));
		}
	}
}