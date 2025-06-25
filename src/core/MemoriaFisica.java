package core;
import model.QuadroMemoria;
import java.util.*;

public class MemoriaFisica {
    int tamanho;
    LinkedList<QuadroMemoria> quadros = new LinkedList<>();
    PoliticaSubstituicao politica;

    public MemoriaFisica(int tamanho, PoliticaSubstituicao politica) {
        this.tamanho = tamanho;
        this.politica = politica;
    }

    public boolean contem(String processoId, int paginaNumero) {
        return quadros.stream().anyMatch(q -> q.processoId.equals(processoId) && q.paginaNumero == paginaNumero);
    }

    public void carregar(String processoId, int paginaNumero) {
    	// Verifica se a página já está na MP
        Optional<QuadroMemoria> existente = quadros.stream()
                .filter(q -> q.processoId.equals(processoId) && q.paginaNumero == paginaNumero)
                .findFirst();

        if (existente.isPresent()) {
            politica.notificarAcesso(existente.get());
            return;
        }
        
        // Verifica se a memória está cheia
        if (quadros.size() >= tamanho) {
            QuadroMemoria substituido = politica.selecionarQuadro(quadros);
            if (substituido.bitModificado) {
                System.out.println("Escrevendo página modificada de volta no disco: " + substituido.processoId + " P" + substituido.paginaNumero);
            }
            quadros.remove(substituido);
        }

        QuadroMemoria novo = new QuadroMemoria(processoId, paginaNumero);
        politica.notificarAcesso(novo);
        quadros.offer(novo);
    }

    public void removerQuadrosDeProcesso(String processoId) {
        quadros.removeIf(q -> q.processoId.equals(processoId));
    }

    public List<Object[]> listarQuadros() {
        List<Object[]> lista = new ArrayList<>();
        int i = 0;
        for (QuadroMemoria q : quadros) {
            lista.add(new Object[] {
                    i++,                         // índice do quadro
                    q.processoId,                // processo
                    q.paginaNumero               // página
            });
        }
        return lista;
    }
}
