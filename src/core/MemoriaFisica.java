package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import gui.GerenciadorMemoriaGUI;
import model.Pagina;
import model.Processo;
import model.QuadroMemoria;

public class MemoriaFisica {
	int tamanho;
	QuadroMemoria[] quadros;
	PoliticaSubstituicao politica;
	Map<String, Processo> processos;
	private GerenciadorMemoriaGUI gui;

	public MemoriaFisica(int tamanho, PoliticaSubstituicao politica, Map<String, Processo> processos,
			GerenciadorMemoriaGUI gui) {
		this.tamanho = tamanho;
		this.quadros = new QuadroMemoria[tamanho];
		this.politica = politica;
		this.processos = processos;
		this.gui = gui;
	}

	public boolean contem(String processoId, int paginaNumero) {
		for (QuadroMemoria q : quadros) {
			if (q != null && q.processoId.equals(processoId) && q.paginaNumero == paginaNumero) {
				return true;
			}
		}
		return false;
	}

	public void carregar(String processoId, int paginaNumero) {
		for (int i = 0; i < quadros.length; i++) {
			QuadroMemoria q = quadros[i];
			if (q != null && q.processoId.equals(processoId) && q.paginaNumero == paginaNumero) {
				politica.notificarAcesso(q);
				return;
			}
		}

		for (int i = 0; i < quadros.length; i++) {
			if (quadros[i] == null) {
				QuadroMemoria novo = new QuadroMemoria(processoId, paginaNumero);
				quadros[i] = novo;
				politica.notificarAcesso(novo);
				return;
			}
		}

		QuadroMemoria substituido = politica.selecionarQuadro(Arrays.asList(quadros));
		Processo procSubstituido = processos.get(substituido.processoId);
		if (substituido.bitModificado) {
			gui.registrarLog("Escrevendo página modificada de volta no disco: " + substituido.processoId + " P"
					+ substituido.paginaNumero);
			Pagina pag = procSubstituido.tabelaPaginas.get(substituido.paginaNumero);
			substituido.bitModificado = false;
			if (pag != null)
				pag.modificada = false;
		}

		if (procSubstituido != null) {
			Pagina pag = procSubstituido.tabelaPaginas.get(substituido.paginaNumero);
			if (pag != null)
				pag.presente = false;
		}

		for (int i = 0; i < quadros.length; i++) {
			if (quadros[i] == substituido) {
				QuadroMemoria novo = new QuadroMemoria(processoId, paginaNumero);
				quadros[i] = novo;
				politica.notificarAcesso(novo);
				return;
			}
		}
	}

	public void removerQuadrosDeProcesso(String processoId) {
		for (int i = 0; i < quadros.length; i++) {
			if (quadros[i] != null && quadros[i].processoId.equals(processoId)) {
				quadros[i] = null;
			}
		}
	}

	public List<Object[]> listarQuadros() {
		List<Object[]> lista = new ArrayList<>();
		for (int i = 0; i < quadros.length; i++) {
			QuadroMemoria q = quadros[i];
			if (q == null) {
				lista.add(new Object[] { i, "-", "-" });
			} else {
				lista.add(new Object[] { i, q.processoId, q.paginaNumero });
			}
		}
		return lista;
	}
}
