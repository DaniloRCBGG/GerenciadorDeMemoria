package core;

import model.QuadroMemoria;
import java.util.List;

public class Relogio implements PoliticaSubstituicao {
	private int ponteiro = 0;

	@Override
	public QuadroMemoria selecionarQuadro(List<QuadroMemoria> quadros) {
		while (true) {
			QuadroMemoria q = quadros.get(ponteiro);
			if (!q.bitUso) {
				QuadroMemoria substituto = q;
				ponteiro = (ponteiro + 1) % quadros.size();
				return substituto;
			} else {
				q.bitUso = false;
				ponteiro = (ponteiro + 1) % quadros.size();
			}
		}
	}

	@Override
	public void notificarAcesso(QuadroMemoria quadro) {
		quadro.bitUso = true;
	}
}
