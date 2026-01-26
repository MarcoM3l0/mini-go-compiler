package analisadorsemantico;

/**
 * Representa os tipos de dados da linguagem Mini-Go.
 */
public enum Tipo {
    INTEIRO,    // Números inteiros 
    REAL,       // Números reais 
    TEXTO,      // Strings 
    BOOLEANO,   // Resultado de comparações e operações lógicas
    NULO,       // Tipo para expressões sem valor
    ERRO;       // Tipo para expressões com erro semântico
    
    /**
     * Verifica se o tipo é numérico (inteiro ou real).
     */
    public boolean isNumerico() {
        return this == INTEIRO || this == REAL;
    }
    
    /**
     * Verifica se dois tipos são compatíveis para operações.
     * Regras:
     * - Inteiro e Real são compatíveis entre si
     * - Outros tipos devem ser exatamente iguais
     */
    public boolean isCompativelCom(Tipo outro) {
        if (this == outro) return true;
        
        // Números são compatíveis entre si
        if (this.isNumerico() && outro.isNumerico()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Retorna o tipo resultante de uma operação entre dois tipos.
     */
    public static Tipo promover(Tipo tipo1, Tipo tipo2) {
        // Se algum for erro, propaga o erro
        if (tipo1 == ERRO || tipo2 == ERRO) return ERRO;
        
        // Se houver REAL, promove para REAL
        if (tipo1 == REAL || tipo2 == REAL) return REAL;
        
        // Senão, mantém o tipo (assumindo compatibilidade)
        return tipo1;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}