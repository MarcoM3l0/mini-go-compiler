package analisadorsemantico;

import java.util.*;

/**
 * Tabela de Símbolos com suporte a escopos aninhados.
 * 
 * Responsabilidades:
 * - Rastrear variáveis declaradas em cada escopo
 * - Verificar se variável existe antes do uso
 * - Detectar redeclaração de variáveis no mesmo escopo
 * - Gerenciar escopos aninhados (blocos dentro de blocos)
 */
public class TabelaSimbolos {
    // Pilha de escopos: cada escopo é um mapa de nome -> símbolo
    private final Deque<Map<String, Simbolo>> escopos;
    
    /**
     * Construtor inicializa com escopo global.
     */
    public TabelaSimbolos() {
        this.escopos = new ArrayDeque<>();
        entrarEscopo();
    }
    
    /**
     * Entra em um novo escopo (ex: dentro de um bloco { }).
     * Cria um novo nível na pilha de escopos.
     */
    public void entrarEscopo() {
        escopos.push(new HashMap<>());
    }
    
    /**
     * Sai do escopo atual (ex: ao fechar um bloco }).
     * Remove o nível mais interno da pilha.
     */
    public void sairEscopo() {
        if (escopos.size() > 1) { // Mantém pelo menos o escopo global
            escopos.pop();
        }
    }
    
    /**
     * Declara uma nova variável no escopo atual.
     * 
     * @param nome nome da variável
     * @param tipo tipo da variável
     * @param inicializada se a variável foi inicializada
     * @return true se declarou com sucesso, false se já existe no escopo atual
     */
    public boolean declarar(String nome, Tipo tipo, boolean inicializada) {
        Map<String, Simbolo> escopoAtual = escopos.peek();
        
        // Verifica se já existe no escopo ATUAL (não nos pais)
        if (escopoAtual.containsKey(nome)) {
            return false; 
        }
        
        escopoAtual.put(nome, new Simbolo(nome, tipo, inicializada));
        return true;
    }
    
    /**
     * Busca uma variável em todos os escopos (do mais interno ao mais externo).
     * 
     * @param nome nome da variável
     * @return símbolo encontrado ou null se não existir
     */
    public Simbolo buscar(String nome) {
        // Percorre escopos do mais interno para o mais externo
        for (Map<String, Simbolo> escopo : escopos) {
            if (escopo.containsKey(nome)) {
                return escopo.get(nome);
            }
        }
        return null; // Não encontrada
    }
    
    /**
     * Verifica se uma variável existe em algum escopo.
     */
    public boolean existe(String nome) {
        return buscar(nome) != null;
    }
    
    /**
     * Atualiza o status de inicialização de uma variável.
     * Usado após atribuições.
     */
    public void marcarInicializada(String nome) {
        Simbolo simbolo = buscar(nome);
        if (simbolo != null) {
            simbolo.setInicializada(true);
        }
    }
    
    /**
     * Retorna o nível de profundidade do escopo atual.
     * Útil para debug.
     */
    public int getNivelEscopo() {
        return escopos.size();
    }
    
    /**
     * Retorna todas as variáveis do escopo atual (para debug).
     */
    public Set<String> getVariaveisEscopoAtual() {
        return new HashSet<>(escopos.peek().keySet());
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TabelaSimbolos{\n");
        int nivel = escopos.size();
        for (Map<String, Simbolo> escopo : escopos) {
            sb.append("  Escopo ").append(nivel--).append(": ").append(escopo.values()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}