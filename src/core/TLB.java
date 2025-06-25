package core;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class TLB {
	int conjuntos;
	int entradasPorConjunto;
	int hits = 0, misses = 0;

	List<LinkedHashMap<String, Integer>> cacheSets;

	public TLB(int totalEntradas, int conjuntos) {
		this.conjuntos = conjuntos;
		this.entradasPorConjunto = totalEntradas / conjuntos;
		cacheSets = new ArrayList<>();

		for (int i = 0; i < conjuntos; i++) {
			LinkedHashMap<String, Integer> set = new LinkedHashMap<>(16, 0.75f, true) {
				private static final long serialVersionUID = 1L;

				@Override
				protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
					return size() > entradasPorConjunto;
				}
			};
			cacheSets.add(set);
		}
	}

	private int mapearConjunto(String processoId, int pagina) {
		return Math.abs((processoId + ":" + pagina).hashCode()) % conjuntos;
	}

	public boolean acessar(String processoId, int pagina) {
		int setIndex = mapearConjunto(processoId, pagina);
		LinkedHashMap<String, Integer> set = cacheSets.get(setIndex);

		String chave = processoId + ":" + pagina;
		if (set.containsKey(chave)) {
			set.get(chave); // Atualiza ordem
			hits++;
			return true;
		} else {
			misses++;
			return false;
		}
	}

	public void inserir(String processoId, int pagina, int quadro) {
		int setIndex = mapearConjunto(processoId, pagina);
		LinkedHashMap<String, Integer> set = cacheSets.get(setIndex);

		String chave = processoId + ":" + pagina;
		set.put(chave, quadro);
	}
	
	public void limpar() {
	    for (LinkedHashMap<String, Integer> set : cacheSets) {
	        set.clear();
	    }
	}

	public List<Object[]> listarEntradas() {
	    List<Object[]> lista = new ArrayList<>();
	    int i = 0;
	    for (int s = 0; s < cacheSets.size(); s++) {
	        for (Map.Entry<String, Integer> e : cacheSets.get(s).entrySet()) {
	            String[] partes = e.getKey().split(":");
	            String processo = partes[0];
	            String pagina = partes[1];
	            int quadro = e.getValue();
	            lista.add(new Object[]{i++, s, processo, pagina, quadro}); // <-- ordem corrigida
	        }
	    }
	    return lista;
	}
}
