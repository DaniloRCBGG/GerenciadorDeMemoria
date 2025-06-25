package core;
import model.QuadroMemoria;
import java.util.*;

public class MemoriaFisica {
    int tamanho;
    QuadroMemoria[] quadros;
    PoliticaSubstituicao politica;

    public MemoriaFisica(int tamanho, PoliticaSubstituicao politica) {
        this.tamanho = tamanho;
        this.quadros = new QuadroMemoria[tamanho];
        this.politica = politica;
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
        // Verifica se a página já está na MP
        for (int i = 0; i < quadros.length; i++) {
            QuadroMemoria q = quadros[i];
            if (q != null && q.processoId.equals(processoId) && q.paginaNumero == paginaNumero) {
                politica.notificarAcesso(q);
                return;
            }
        }

        // Tenta encontrar um quadro livre (null)
        for (int i = 0; i < quadros.length; i++) {
            if (quadros[i] == null) {
                QuadroMemoria novo = new QuadroMemoria(processoId, paginaNumero);
                quadros[i] = novo;
                politica.notificarAcesso(novo);
                return;
            }
        }

        // Memória cheia: aplicar política de substituição
        QuadroMemoria substituido = politica.selecionarQuadro(Arrays.asList(quadros));
        if (substituido.bitModificado) {
            System.out.println("Escrevendo página modificada de volta no disco: " + substituido.processoId + " P" + substituido.paginaNumero);
        }

        // Encontrar índice do substituído
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
                lista.add(new Object[]{i, "-", "-"});
            } else {
                lista.add(new Object[]{i, q.processoId, q.paginaNumero});
            }
        }
        return lista;
    }
}
