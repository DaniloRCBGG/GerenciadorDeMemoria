package core;

import model.QuadroMemoria;
import java.util.List;

public class LRU implements PoliticaSubstituicao {
	@Override
	public QuadroMemoria selecionarQuadro(List<QuadroMemoria> quadros) {
		return quadros.stream().min((a, b) -> Long.compare(a.ultimoAcesso, b.ultimoAcesso)).orElse(null);
	}

	@Override
	public void notificarAcesso(QuadroMemoria quadro) {
		quadro.ultimoAcesso = System.currentTimeMillis();
	}
}