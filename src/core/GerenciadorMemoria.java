package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gui.GerenciadorMemoriaGUI;
import model.Pagina;
import model.Processo;
import model.QuadroMemoria;

public class GerenciadorMemoria {
	String pidAtual;
	Map<String, Processo> processos = new HashMap<>();
	MemoriaFisica memoria;
	GerenciadorMemoriaGUI gui;
	TLB tlb;
	int pageFaults = 0;
	private static final int TAMANHO_PAGINA = 1024;

	public GerenciadorMemoria(int quadros, int entradasTLB, int conjuntosTLB, GerenciadorMemoriaGUI gui) {
		// Testando Relogio: parece funcionar
		memoria = new MemoriaFisica(quadros, new Relogio(), processos, gui);
		tlb = new TLB(entradasTLB, conjuntosTLB);
		this.gui = gui;
		gui.atualizarMemoria(memoria.listarQuadros());
	}

	public void executarComando(String linha) {
		linha = linha.trim();
		if (linha.isEmpty() || linha.startsWith("//"))
			return;

		String[] partes = linha.split("\\s+");
		String comando = partes[0];

		switch (comando) {
		case "C": {
			String pid = partes[1];
			int tamanhoMB = Integer.parseInt(partes[2]);
			int tamanhoBytes = tamanhoMB * 1048576;
			int paginas = (int) Math.ceil((double) tamanhoBytes / TAMANHO_PAGINA);

			processos.put(pid, new Processo(pid, paginas));
			gui.registrarLog("Processo " + pid + " criado com " + paginas + " páginas (" + tamanhoMB + " MB).");
			break;
		}
		case "P":
		case "R":
		case "W": {
			String pid = partes[1];

			if (!pid.equals(pidAtual)) {
				pidAtual = pid;
				tlb.limpar();
				gui.atualizarProcessoAtual(pidAtual);
			}

			if (!processos.containsKey(pid)) {
				gui.registrarLog("Processo " + pid + " não encontrado.");
				break;
			}

			String enderecoStr = partes[2].replaceAll("[^\\d]", "");
			int enderecoLogico = Integer.parseInt(enderecoStr);
			int pagina = enderecoLogico / TAMANHO_PAGINA;

			String valor = (comando.equals("W") && partes.length > 3) ? partes[3] : null;

			boolean hit = tlb.acessar(pid, pagina);

			if (!memoria.contem(pid, pagina)) {
				memoria.carregar(pid, pagina);
				pageFaults++;
			}
			Pagina paginaRef = processos.get(pid).tabelaPaginas.get(pagina);
			paginaRef.presente = true;
			paginaRef.referenciada = true;

			tlb.inserir(pid, pagina, pagina);

			if (comando.equals("W")) {
				paginaRef.modificada = true;
				for (QuadroMemoria q : memoria.quadros) {
					if (q != null && q.processoId.equals(pid) && q.paginaNumero == pagina) {
						q.bitModificado = true;
						break;
					}
				}
			}

			String acao;
			if (comando.equals("W")) {
				acao = "Escrita: processo " + pid + ", endereço lógico " + enderecoLogico + ", página " + pagina
						+ ", valor escrito: " + valor;
			} else {
				acao = (comando.equals("R") ? "Leitura" : "Execução") + ": processo " + pid + ", endereço lógico "
						+ enderecoLogico + ", página " + pagina;
			}

			acao += hit ? " (TLB HIT)" : " (TLB MISS)";
			gui.registrarLog(acao);

			Processo processoAtual = processos.get(pidAtual);
			List<Object[]> paginasVisiveis = new ArrayList<>();
			for (Map.Entry<Integer, Pagina> entry : processoAtual.tabelaPaginas.entrySet()) {
				Pagina pag = entry.getValue();
				if (pag.referenciada) {
					paginasVisiveis
							.add(new Object[] { pag.numero, pag.presente ? "1" : "0", pag.modificada ? "1" : "0" });
				}
			}
			gui.atualizarPaginas(paginasVisiveis);

			break;
		}
		case "I": {
			String pid = partes[1];
			gui.registrarLog("Processo " + pid + " iniciou operação de E/S...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				gui.registrarLog("E/S interrompida para processo " + pid);
			}
			gui.registrarLog("Processo " + pid + " finalizou operação de E/S.");
			break;
		}
		case "T": {
			String pid = partes[1];

			Processo processoFinalizado = processos.get(pid);
			if (processoFinalizado != null) {
				gui.atualizarProcessoAtual("-");
				tlb.limpar();
				memoria.removerQuadrosDeProcesso(pid);

				for (Pagina pag : processoFinalizado.tabelaPaginas.values()) {
					if (pag.modificada) {
						gui.registrarLog("Escrevendo página modificada de volta no disco: " + pid + " P" + pag.numero);
					}
					pag.presente = false;
					pag.modificada = false;
				}

				gui.registrarLog("Processo " + pid + " finalizado e removido da memória.");

				if (pid.equals(pidAtual)) {
					List<Object[]> paginasVisiveis = new ArrayList<>();
					for (Pagina pag : processoFinalizado.tabelaPaginas.values()) {
						if (pag.referenciada) {
							paginasVisiveis.add(
									new Object[] { pag.numero, pag.presente ? "1" : "0", pag.modificada ? "1" : "0" });
						}
					}
					gui.atualizarPaginas(paginasVisiveis);
				}
				processos.remove(pid);
			} else {
				gui.registrarLog("Processo " + pid + " não encontrado para término.");
			}
			break;
		}
		default:
			gui.registrarLog("Comando desconhecido: " + linha);
		}

		gui.atualizarMemoria(memoria.listarQuadros());
		gui.atualizarTLB(tlb.listarEntradas());
		gui.atualizarMetricas(tlb.hits, tlb.misses, pageFaults);
	}
}
