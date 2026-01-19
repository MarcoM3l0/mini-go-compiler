package scanner;

/**
 * Enumeração que define todos os tipos de tokens reconhecidos pelo analisador léxico.
 * Baseado na gramática BNF do Mini-Go.
 */
public enum TokenType {
	
	// Palavras reservadas
	
	// Literais
	
	// Identificadores
	
	// Operadores aritméticos
	
	// Operadores relacionais
	
	// Operadores de igualdade
	
	// Operadores lógicos
	
	// Operador de atribuição
	
	// Delimitadores
	
	// Agrupadores
	
	// Especiais
	EOF("EOF"), // End of File
	ERRO("erro"); // Token de erro
	
	private final String lexema;
	
	/**
	 * Construtor do enum TokenType.
	 * @param lexema representação textual do token
	*/
	TokenType(String lexema) {
		this.lexema = lexema;
	}
	
	/**
     * Retorna o lexema associado ao tipo de token.
     * @return lexema do token
    */
	
	
	/**
     * Verifica se o tipo de token é uma palavra reservada.
     * @return true se for palavra reservada
    */
	
	
	/**
     * Verifica se o tipo de token é um operador.
     * @return true se for operador
    */
}
