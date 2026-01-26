package scanner;

/**
 * Representa um token identificado pelo analisador léxico.
 * Contém informações sobre o tipo, lexema e posição no código fonte.
 */
public class Token {
	
    private final TokenType tipo; // tipo do token
    private final String lexema;  // texto original do token
    private final int linha;      // Linha onde o token foi encontrado
    private final int coluna;     // Coluna onde o token começa

	/**
     * Construtor completo do Token.
     * 
     * @param tipo tipo do token
     * @param lexema texto original do token
     * @param linha número da linha (começa em 1)
     * @param coluna número da coluna (começa em 1)
    */
	 public Token(TokenType tipo, String lexema, int linha, int coluna) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linha = linha;
        this.coluna = coluna;
    }
	
	/**
     * Construtor simplificado sem informação de posição.
     * Útil para testes unitários.
     * 
     * @param tipo tipo do token
     * @param lexema texto original do token
    */
    public Token(TokenType tipo, String lexema) {
        this(tipo, lexema, 0, 0);
    }
	
	// Getters
	public TokenType getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }
	
	 /**
     * Verifica se este token é do tipo especificado.
     * 
     * @param tipo tipo a ser verificado
     * @return true se o token for do tipo especificado
     */
	public boolean is(TokenType tipo) {
        return this.tipo == tipo;
    }
	
	 /**
     * Verifica se este token é de algum dos tipos especificados.
     * 
     * @param tipos tipos a serem verificados
     * @return true se o token for de algum dos tipos
     */
	public boolean isOneOf(TokenType... tipos) {
        for (TokenType t : tipos) {
            if (this.tipo == t) {
                return true;
            }
        }
        return false;
    }
	
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
    public String toSimpleString() {
        return String.format("%s('%s')", tipo, lexema);
    }
    
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
