package scanner;

/**
 * Representa um token identificado pelo analisador léxico.
 * Contém informações sobre o tipo, lexema e posição no código fonte.
 */
public class Token {
	
	/**
     * Construtor completo do Token.
     * 
     * @param tipo tipo do token
     * @param lexema texto original do token
     * @param linha número da linha (começa em 1)
     * @param coluna número da coluna (começa em 1)
    */
	
	
	/**
     * Construtor simplificado sem informação de posição.
     * Útil para testes unitários.
     * 
     * @param tipo tipo do token
     * @param lexema texto original do token
    */

	
	// Getters
	
	
	 /**
     * Verifica se este token é do tipo especificado.
     * 
     * @param tipo tipo a ser verificado
     * @return true se o token for do tipo especificado
     */
	
	
	 /**
     * Verifica se este token é de algum dos tipos especificados.
     * 
     * @param tipos tipos a serem verificados
     * @return true se o token for de algum dos tipos
     */
	
	
	 /**
     * Representação em string do token para debug.
     * Formato: Token[tipo=VAR, lexema='var', linha=1, coluna=1]
     */
    @Override
    public String toString() {
        return String.format("Token[tipo=%s, lexema='%s', linha=%d, coluna=%d]",
                tipo, lexema, linha, coluna);
    }
    
    /**
     * Representação simplificada para testes.
     * Formato: VAR('var')
     */
    
    
    /**
     * Verifica igualdade entre tokens (compara tipo e lexema).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Token token = (Token) obj;
        return tipo == token.tipo && 
               lexema.equals(token.lexema);
    }
    
    @Override
    public int hashCode() {
        int result = tipo.hashCode();
        result = 31 * result + lexema.hashCode();
        return result;
    }
}
