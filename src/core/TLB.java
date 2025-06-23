package core;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class TLB {
    int tamanho;
    int hits = 0;
    int misses = 0;
    LinkedHashMap<String, Integer> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
            return size() > tamanho;
        }
    };

    public TLB(int tamanho) {
        this.tamanho = tamanho;
    }

    public boolean acessar(String processoId, int pagina) {
        String chave = processoId + ":" + pagina;
        if (cache.containsKey(chave)) {
            hits++;
            return true;
        } else {
            misses++;
            return false;
        }
    }

    public void inserir(String processoId, int pagina, int quadro) {
        String chave = processoId + ":" + pagina;
        cache.put(chave, quadro);
    }

    public List<Object[]> listarEntradas() {
        List<Object[]> lista = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, Integer> e : cache.entrySet()) {
            String[] partes = e.getKey().split(":");
            lista.add(new Object[]{i++, partes[0], partes[1], e.getValue()});
        }
        return lista;
    }

}
