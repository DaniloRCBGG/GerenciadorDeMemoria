package gui;

import core.GerenciadorMemoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.*;
import java.awt.*;
import java.util.List;

public class GerenciadorMemoriaGUI extends JFrame {
	private JTable tabelaMemoria;
	private JTable tabelaTLB;
	private JTextArea log;
	private JLabel labelMetricas;
	private DefaultTableModel modeloMemoria;
	private DefaultTableModel modeloTLB;
	private JButton carregarArquivo;
	// Adicionei
	private List<String> linhasComandos;
	private int indiceAtual = 0;
	private JButton proximaLinha;
	// Adicionei mais
	private JTable tabelaPaginas;
	private DefaultTableModel modeloPaginas;

	private GerenciadorMemoria gm;

	public GerenciadorMemoriaGUI() {
		super("Simulador de Gerência de Memória com Paginação");

		int quadros = Integer.parseInt(JOptionPane.showInputDialog("Quantidade de quadros na memória física:", "8"));
		int entradasTLB = Integer.parseInt(JOptionPane.showInputDialog("Quantidade de entradas na TLB:", "4"));
		int conjuntosTLB = Integer.parseInt(JOptionPane.showInputDialog("Quantidade de conjuntos na TLB:", "2"));

		configurarUI();
		this.gm = new GerenciadorMemoria(quadros, entradasTLB, conjuntosTLB, this);
	}

	private void configurarUI() {
		proximaLinha = new JButton("Próxima Linha");
		proximaLinha.setEnabled(false);
		proximaLinha.addActionListener(e -> executarProximaLinha());

		setLayout(new BorderLayout());

		modeloMemoria = new DefaultTableModel(new Object[] { "Quadro", "Processo", "Página" }, 0);
		tabelaMemoria = new JTable(modeloMemoria);

		modeloTLB = new DefaultTableModel(new Object[] { "Entrada", "Conjunto", "Processo", "Página", "Quadro" }, 0);
		tabelaTLB = new JTable(modeloTLB);

		log = new JTextArea(10, 40);
		log.setEditable(false);
		JScrollPane scrollLog = new JScrollPane(log);

		labelMetricas = new JLabel("Hits: 0 | Misses: 0 | Page Faults: 0");

		carregarArquivo = new JButton("Carregar Arquivo de Comandos");
		carregarArquivo.addActionListener(e -> carregarArquivo());

		modeloPaginas = new DefaultTableModel(new Object[] { "Página", "Presente", "Modificada" }, 0);
		tabelaPaginas = new JTable(modeloPaginas);

		JPanel painelCentro = new JPanel(new GridLayout(1, 3));
		painelCentro.add(new JScrollPane(tabelaMemoria));
		painelCentro.add(new JScrollPane(tabelaTLB));
		painelCentro.add(new JScrollPane(tabelaPaginas));

		JPanel painelSul = new JPanel(new BorderLayout());
		painelSul.add(scrollLog, BorderLayout.CENTER);
		painelSul.add(labelMetricas, BorderLayout.NORTH);
		JPanel botoesPainel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		botoesPainel.add(carregarArquivo);
		botoesPainel.add(proximaLinha);

		painelSul.add(botoesPainel, BorderLayout.SOUTH);

		add(painelCentro, BorderLayout.CENTER);
		add(painelSul, BorderLayout.SOUTH);

		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void executarProximaLinha() {
		if (indiceAtual >= linhasComandos.size()) {
			registrarLog("Todos os comandos já foram executados.");
			proximaLinha.setEnabled(false);
			return;
		}

		String linha = linhasComandos.get(indiceAtual++);
		try {
			gm.executarComando(linha);
		} catch (Exception e) {
			registrarLog("Erro ao executar linha: " + e.getMessage());
		}
	}

	private void carregarArquivo() {
		JFileChooser chooser = new JFileChooser();
		int retorno = chooser.showOpenDialog(this);
		if (retorno == JFileChooser.APPROVE_OPTION) {
			File arquivo = chooser.getSelectedFile();

			new Thread(() -> {
				try (Scanner scanner = new Scanner(arquivo)) {
					linhasComandos = new ArrayList<>();
					while (scanner.hasNextLine()) {
						String linha = scanner.nextLine().trim();
						if (!linha.isEmpty() && !linha.startsWith("//")) {
							linhasComandos.add(linha);
						}
					}
					indiceAtual = 0;

					SwingUtilities.invokeLater(() -> {
						registrarLog("Arquivo carregado com " + linhasComandos.size() + " comandos.");
						proximaLinha.setEnabled(true);
					});

				} catch (Exception ex) {
					SwingUtilities.invokeLater(
							() -> JOptionPane.showMessageDialog(this, "Erro ao ler o arquivo: " + ex.getMessage()));
				}
			}).start();
		}
	}

	public void atualizarMemoria(List<Object[]> quadro) {
		modeloMemoria.setRowCount(0);
		for (Object linha : quadro) {
			modeloMemoria.addRow((Object[]) linha);
		}
	}

	public void atualizarTLB(List<Object[]> entradas) {
		modeloTLB.setRowCount(0);
		for (Object linha : entradas) {
			modeloTLB.addRow((Object[]) linha);
		}
	}

	public void atualizarPaginas(List<Object[]> paginas) {
		modeloPaginas.setRowCount(0);
		for (Object[] linha : paginas) {
			modeloPaginas.addRow(linha);
		}
	}

	public void atualizarMetricas(int hits, int misses, int pageFaults) {
		labelMetricas.setText("Hits: " + hits + " | Misses: " + misses + " | Page Faults: " + pageFaults);
	}

	public void registrarLog(String msg) {
		log.append(msg + "\n");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(GerenciadorMemoriaGUI::new);
	}
}