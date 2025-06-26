package core;

import java.util.List;
import model.QuadroMemoria;

public interface PoliticaSubstituicao {
	QuadroMemoria selecionarQuadro(List<QuadroMemoria> quadros);

	void notificarAcesso(QuadroMemoria quadro);
}
