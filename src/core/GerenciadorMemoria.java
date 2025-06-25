package core;

import gui.GerenciadorMemoriaGUI;
import model.Processo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorMemoria {
	Map<String, Processo> processos = new HashMap<>();
	MemoriaFisica memoria;
	TLB tlb;
	int pageFaults = 0;
	private static final int TAMANHO_PAGINA = 1024; // Tamanho da página em bytes

	public GerenciadorMemoria(int quadros, int entradasTLB) {
		memoria = new MemoriaFisica(quadros, new LRU());
		tlb = new TLB(entradasTLB);
	}

	public void executarComando(String linha, GerenciadorMemoriaGUI gui) {
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

			if (!processos.containsKey(pid)) {
				gui.registrarLog("Processo " + pid + " não encontrado.");
				break;
			}

			// Parsing do endereço lógico, removendo parênteses
			String enderecoStr = partes[2].replaceAll("[^\\d]", "");
			int enderecoLogico = Integer.parseInt(enderecoStr);
			int pagina = enderecoLogico / TAMANHO_PAGINA;

			// Captura valor (se for escrita)
			String valor = (comando.equals("W") && partes.length > 3) ? partes[3] : null;

			// Verifica se página está na TLB
			boolean hit = tlb.acessar(pid, pagina);

			// Se não está na MP, causa page fault
			if (!memoria.contem(pid, pagina)) {
				memoria.carregar(pid, pagina);
				pageFaults++;
			}

			// Atualiza TLB
			tlb.inserir(pid, pagina, pagina);

			// Marcar como modificada, se for escrita
			if (comando.equals("W")) {
				processos.get(pid).tabelaPaginas.get(pagina).modificada = true;
			}

			// Construção do log
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
			processos.remove(pid);
			memoria.removerQuadrosDeProcesso(pid);
			gui.registrarLog("Processo " + pid + " finalizado e removido da memória.");
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
