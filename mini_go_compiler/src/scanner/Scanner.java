package scanner;

import java.util.*;

/**
 * Analisador Léxico (Scanner) para a linguagem Mini-Go.
 * 
 * Responsável por:
 * - Ler o código fonte caractere por caractere
 * - Agrupar caracteres em tokens significativos
 * - Ignorar espaços em branco e comentários
 * - Identificar erros léxicos
 */
public class Scanner {

	
	// Mapa de palavras reservadas para lookup rápido
    private static final Map<String, TokenType> palavrasReservadas = new HashMap<>();
    
    static {
        palavrasReservadas.put("var", TokenType.VAR);
        palavrasReservadas.put("inteiro", TokenType.INTEIRO);
        palavrasReservadas.put("real", TokenType.REAL);
        palavrasReservadas.put("texto", TokenType.TEXTO);
        palavrasReservadas.put("se", TokenType.SE);
        palavrasReservadas.put("senao", TokenType.SENAO);
        palavrasReservadas.put("para", TokenType.PARA);
        palavrasReservadas.put("imprimir", TokenType.IMPRIMIR);
        palavrasReservadas.put("ler", TokenType.LER);
    }
    
    /**
     * Construtor do Scanner.
     * 
     * @param source código fonte a ser tokenizado
    */
    
    
    /**
     * Realiza a análise léxica completa do código fonte.
     * 
     * @return lista de tokens identificados
    */
    
    
    /**
     * Identifica e processa um único token.
    */
    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // Início de um novo lexema
            start = current;
            scanToken();
        }
        
        // Adiciona token EOF ao final
        tokens.add(new Token(TokenType.EOF, "", linha, coluna));
        return tokens;
    }
    
    /**
     * Identifica e processa um único token.
     */
    private void scanToken() {
        char c = advance();
        
        switch (c) {
            // Ignorar espaços em branco
            case ' ':
            case '\r':
            case '\t':
                break;
            
            // Nova linha: incrementa contador de linhas
            case '\n':
                linha++;
                coluna = 1;
                break;
            
            // Delimitadores simples
            case '(': addToken(TokenType.ABRE_PARENTESE); break;
            case ')': addToken(TokenType.FECHA_PARENTESE); break;
            case '{': addToken(TokenType.ABRE_CHAVE); break;
            case '}': addToken(TokenType.FECHA_CHAVE); break;
            case ';': addToken(TokenType.PONTO_VIRGULA); break;
            case ',': addToken(TokenType.VIRGULA); break;
            case '+': addToken(TokenType.MAIS); break;
            case '-': addToken(TokenType.MENOS); break;
            case '*': addToken(TokenType.MULTIPLICACAO); break;
            
            // Operadores que podem ser compostos
            case '!':
                addToken(match('=') ? TokenType.DIFERENTE : TokenType.NEGACAO);
                break;
            
            case '=':
                addToken(match('=') ? TokenType.IGUAL_IGUAL : TokenType.ATRIBUICAO);
                break;
            
            case '<':
                addToken(match('=') ? TokenType.MENOR_IGUAL : TokenType.MENOR);
                break;
            
            case '>':
                addToken(match('=') ? TokenType.MAIOR_IGUAL : TokenType.MAIOR);
                break;
            
            case '&':
                if (match('&')) {
                    addToken(TokenType.E_LOGICO);
                } else {
                    addError("Esperado '&' após '&'");
                }
                break;
            
            case '|':
                if (match('|')) {
                    addToken(TokenType.OU_LOGICO);
                } else {
                    addError("Esperado '|' após '|'");
                }
                break;
            
            // Comentários e divisão
            case '/':
                if (match('/')) {
                    // Comentário de linha: ignora até o fim da linha
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if (match('*')) {
                    // Comentário de bloco: ignora até encontrar */
                    comentarioBloco();
                } else {
                    addToken(TokenType.DIVISAO);
                }
                break;
            
            // Literais de texto (strings)
            case '"':
                string();
                break;
            
            // Ponto: pode ser parte de número real
            case '.':
                addToken(TokenType.PONTO);
                break;
            
            default:
                // Literais numéricos
                if (isDigit(c)) {
                    number();
                }
                // Identificadores e palavras reservadas
                else if (isAlpha(c)) {
                    identifier();
                }
                // Caractere inválido
                else {
                    addError("Caractere inesperado: '" + c + "'");
                }
                break;
        }
    }
    
    
    /**
     * Processa comentário de bloco /* ... *\/
    */
    
    
    /**
     * Processa identificadores e palavras reservadas.
     * Regra: começa com letra, seguido de letras, dígitos ou underscore.
    */
    
    
    /**
     * Processa números (inteiros e reais).
     * Inteiro: 123
     * Real: 123.456
    */
    
    
    /**
     * Processa literais de texto (strings).
     * Suporta escapes: \" (aspas) e \\ (barra invertida)
    */
    private void string() {
        StringBuilder valor = new StringBuilder();
        
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                linha++;
                coluna = 0;
            }
            
            // Processa caracteres de escape
            if (peek() == '\\') {
                advance(); // consome \
                
                if (isAtEnd()) {
                    addError("String não fechada");
                    return;
                }
                
                char escaped = advance();
                switch (escaped) {
                    case '"':
                        valor.append('"');
                        break;
                    case '\\':
                        valor.append('\\');
                        break;
                    case 'n':
                        valor.append('\n');
                        break;
                    case 't':
                        valor.append('\t');
                        break;
                    default:
                        valor.append('\\').append(escaped);
                        break;
                }
            } else {
                valor.append(advance());
            }
        }
        
        if (isAtEnd()) {
            addError("String não fechada");
            return;
        }
        
        // Consome a aspas de fechamento
        advance();
        
        addToken(TokenType.LITERAL_TEXTO);
    }
    
    
    /**
     * Verifica se o próximo caractere é o esperado e o consome.
     * 
     * @param expected caractere esperado
     * @return true se o caractere foi consumido
    */
    
    
    /**
     * Retorna o caractere atual sem consumir.
     * 
     * @return caractere atual ou '\0' se fim do arquivo
    */
    
    
    /**
     * Retorna o próximo caractere sem consumir.
     * 
     * @return próximo caractere ou '\0' se fim do arquivo
    */
    
    
    /**
     * Verifica se um caractere é uma letra (a-z, A-Z).
    */
    
    
    /**
     * Verifica se um caractere é alfanumérico.
    */
    
    
    /**
     * Verifica se um caractere é um dígito (0-9).
    */
    
    
    /**
     * Verifica se chegou ao fim do código fonte.
    */
    
    /**
     * Consome e retorna o caractere atual.
    */
    
    
    /**
     * Adiciona um token à lista de tokens.
    */
    
    
    /**
     * Adiciona um token de erro.
    */
    
    
    /**
     * Retorna os tokens identificados.
     */
    public List<Token> getTokens() {
        return tokens;
    }
}
