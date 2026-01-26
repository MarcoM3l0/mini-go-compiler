package analisadorsemantico;

/**
 * Representa uma variável na tabela de símbolos.
 * Armazena informações sobre nome, tipo e se foi inicializada.
 */
public class Simbolo {
    private final String nome;
    private final Tipo tipo;
    private boolean inicializada;
    
    /**
     * Construtor do Símbolo.
     * 
     * @param nome nome da variável
     * @param tipo tipo da variável (INTEIRO, REAL, TEXTO)
     * @param inicializada se a variável foi inicializada na declaração
     */
    public Simbolo(String nome, Tipo tipo, boolean inicializada) {
        this.nome = nome;
        this.tipo = tipo;
        this.inicializada = inicializada;
    }
    
    // Getters
    
    public String getNome() {
        return nome;
    }
    
    public Tipo getTipo() {
        return tipo;
    }
    
    public boolean isInicializada() {
        return inicializada;
    }
    
    /**
     * Marca a variável como inicializada (após atribuição).
     */
    public void setInicializada(boolean inicializada) {
        this.inicializada = inicializada;
    }
    
    @Override
    public String toString() {
        return String.format("Simbolo{nome='%s', tipo=%s, inicializada=%s}", 
                           nome, tipo, inicializada);
    }
}