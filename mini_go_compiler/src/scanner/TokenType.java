package scanner;

/**
 * Enumeração que define todos os tipos de tokens reconhecidos pelo analisador léxico.
 * Baseado na gramática BNF do Mini-Go.
 */
public enum TokenType {
	
	// Palavras reservadas
	VAR("var"),
    INTEIRO("inteiro"),
    REAL("real"),
    TEXTO("texto"),
    SE("se"),
    SENAO("senao"),
    PARA("para"),
    IMPRIMIR("imprimir"),
    LER("ler"),

	// Literais
	LITERAL_INTEIRO("literal_inteiro"),
    LITERAL_REAL("literal_real"),
    LITERAL_TEXTO("literal_texto"),
	
	// Identificadores
	IDENTIFICADOR("identificador"),

	// Operadores aritméticos
	MAIS("+"),
    MENOS("-"),
    MULTIPLICACAO("*"),
    DIVISAO("/"),

	// Operadores relacionais
	MENOR("<"),
    MENOR_IGUAL("<="),
    MAIOR(">"),
    MAIOR_IGUAL(">="),

	// Operadores de igualdade
	IGUAL_IGUAL("=="),
    DIFERENTE("!="),
	
	// Operadores lógicos
	E_LOGICO("&&"),
    OU_LOGICO("||"),
    NEGACAO("!"),
	
	// Operador de atribuição
	ATRIBUICAO("="),
	
	// Delimitadores
	PONTO_VIRGULA(";"),
    VIRGULA(","),
    PONTO("."),
	
	// Agrupadores
	ABRE_PARENTESE("("),
    FECHA_PARENTESE(")"),
    ABRE_CHAVE("{"),
    FECHA_CHAVE("}"),
	
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
	public String getLexema() {
		return lexema;
	}
	
	/**
     * Verifica se o tipo de token é uma palavra reservada.
     * @return true se for palavra reservada
    */
	public boolean isPalavraReservada() {
		return this == VAR || this == INTEIRO || this == REAL || this == TEXTO ||
			   this == SE || this == SENAO || this == PARA ||
			   this == IMPRIMIR || this == LER;
	}
	
	/**
     * Verifica se o tipo de token é um operador.
     * @return true se for operador
    */
   public boolean isOperador() {
	   return this == MAIS || this == MENOS || this == MULTIPLICACAO  || this == DIVISAO ||
			  this == MENOR || this == MENOR_IGUAL || this == MAIOR || this == MAIOR_IGUAL ||
			  this == IGUAL_IGUAL || this == DIFERENTE ||
			  this == E_LOGICO || this == OU_LOGICO || this == NEGACAO ||
			  this == ATRIBUICAO;
   }
}
