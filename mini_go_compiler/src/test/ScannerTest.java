package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import scanner.Token;
import scanner.TokenType;
import scanner.Scanner;

import java.util.List;

/**
 * Testes unitários para o Scanner (Analisador Léxico).
 * Verifica a tokenização correta de diversos elementos da linguagem Mini-Go.
 */
public class ScannerTest {
    
    /**
     * Método auxiliar para verificar se os tokens gerados correspondem aos esperados.
     */
    private void assertTokens(String source, TokenType... expectedTypes) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        // Remove o token EOF do final para facilitar a comparação
        tokens = tokens.subList(0, tokens.size() - 1);
        
        assertEquals(expectedTypes.length, tokens.size(), 
            "Número de tokens diferente do esperado");
        
        for (int i = 0; i < expectedTypes.length; i++) {
            assertEquals(expectedTypes[i], tokens.get(i).getTipo(),
                "Token " + i + " deveria ser " + expectedTypes[i] + 
                " mas é " + tokens.get(i).getTipo());
        }
    }
    
    /**
     * Método auxiliar para obter tokens sem EOF.
     */
    private List<Token> getTokensSemEOF(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        return tokens.subList(0, tokens.size() - 1);
    }
    // 
    // ============== TESTES DE PALAVRAS RESERVADAS ==============
    
    @Test
    public void testPalavrasReservadas() {
        String source = "var inteiro real texto se senao para imprimir ler";
        assertTokens(source,
            TokenType.VAR,
            TokenType.INTEIRO,
            TokenType.REAL,
            TokenType.TEXTO,
            TokenType.SE,
            TokenType.SENAO,
            TokenType.PARA,
            TokenType.IMPRIMIR,
            TokenType.LER
        );
    }
    
    @Test
    public void testPalavraReservadaVar() {
        String source = "var";
        assertTokens(source, TokenType.VAR);
    }
    
    @Test
    public void testPalavraReservadaSe() {
        String source = "se";
        assertTokens(source, TokenType.SE);
    }
    
    // ============== TESTES DE IDENTIFICADORES ==============
    
    @Test
    public void testIdentificadorSimples() {
        String source = "idade";
        List<Token> tokens = getTokensSemEOF(source);
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.IDENTIFICADOR, tokens.get(0).getTipo());
        assertEquals("idade", tokens.get(0).getLexema());
    }
    
    @Test
    public void testIdentificadorComNumeros() {
        String source = "var1 teste2 abc123";
        assertTokens(source,
            TokenType.IDENTIFICADOR,
            TokenType.IDENTIFICADOR,
            TokenType.IDENTIFICADOR
        );
    }
    
    @Test
    public void testIdentificadorComUnderscore() {
        String source = "_private meu_nome valor_total";
        assertTokens(source,
            TokenType.IDENTIFICADOR,
            TokenType.IDENTIFICADOR,
            TokenType.IDENTIFICADOR
        );
    }
    
    // ============== TESTES DE LITERAIS NUMÉRICOS ==============
    
    @Test
    public void testLiteralInteiro() {
        String source = "123";
        List<Token> tokens = getTokensSemEOF(source);
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.LITERAL_INTEIRO, tokens.get(0).getTipo());
        assertEquals("123", tokens.get(0).getLexema());
    }
    
    @Test
    public void testLiteralInteiroZero() {
        String source = "0";
        assertTokens(source, TokenType.LITERAL_INTEIRO);
    }
    
    @Test
    public void testLiteralReal() {
        String source = "123.456";
        List<Token> tokens = getTokensSemEOF(source);
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.LITERAL_REAL, tokens.get(0).getTipo());
        assertEquals("123.456", tokens.get(0).getLexema());
    }
    
    @Test
    public void testLiteralRealComZero() {
        String source = "0.5";
        assertTokens(source, TokenType.LITERAL_REAL);
    }
    
    @Test
    public void testVariosNumeros() {
        String source = "10 3.14 0 100.0";
        assertTokens(source,
            TokenType.LITERAL_INTEIRO,
            TokenType.LITERAL_REAL,
            TokenType.LITERAL_INTEIRO,
            TokenType.LITERAL_REAL
        );
    }
    
    // ============== TESTES DE LITERAIS DE TEXTO (STRINGS) ==============
    
    @Test
    public void testStringSimples() {
        String source = "\"Hello World\"";
        List<Token> tokens = getTokensSemEOF(source);
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.LITERAL_TEXTO, tokens.get(0).getTipo());
        assertEquals("\"Hello World\"", tokens.get(0).getLexema());
    }
    
    @Test
    public void testStringVazia() {
        String source = "\"\"";
        assertTokens(source, TokenType.LITERAL_TEXTO);
    }
    
    @Test
    public void testStringComEscapeAspas() {
        String source = "\"Ele disse: \\\"Olá\\\"\"";
        assertTokens(source, TokenType.LITERAL_TEXTO);
    }
    
    @Test
    public void testStringComEscapeBarraInvertida() {
        String source = "\"C:\\\\caminho\\\\arquivo\"";
        assertTokens(source, TokenType.LITERAL_TEXTO);
    }
    
    // ============== TESTES DE OPERADORES ARITMÉTICOS ==============
    
    @Test
    public void testOperadoresAritmeticos() {
        String source = "+ - * /";
        assertTokens(source,
            TokenType.MAIS,
            TokenType.MENOS,
            TokenType.MULTIPLICACAO,
            TokenType.DIVISAO
        );
    }
    
    @Test
    public void testExpressaoAritmetica() {
        String source = "a + b * c - d / e";
        assertTokens(source,
            TokenType.IDENTIFICADOR,
            TokenType.MAIS,
            TokenType.IDENTIFICADOR,
            TokenType.MULTIPLICACAO,
            TokenType.IDENTIFICADOR,
            TokenType.MENOS,
            TokenType.IDENTIFICADOR,
            TokenType.DIVISAO,
            TokenType.IDENTIFICADOR
        );
    }
    
    // ============== TESTES DE OPERADORES RELACIONAIS ==============
    
    @Test
    public void testOperadoresRelacionais() {
        String source = "< <= > >=";
        assertTokens(source,
            TokenType.MENOR,
            TokenType.MENOR_IGUAL,
            TokenType.MAIOR,
            TokenType.MAIOR_IGUAL
        );
    }
    
    @Test
    public void testOperadoresIgualdade() {
        String source = "== !=";
        assertTokens(source,
            TokenType.IGUAL_IGUAL,
            TokenType.DIFERENTE
        );
    }
    
    @Test
    public void testComparacaoCompleta() {
        String source = "x == y";
        assertTokens(source,
            TokenType.IDENTIFICADOR,
            TokenType.IGUAL_IGUAL,
            TokenType.IDENTIFICADOR
        );
    }
    
    // ============== TESTES DE OPERADORES LÓGICOS ==============
    
    @Test
    public void testOperadoresLogicos() {
        String source = "&& || !";
        assertTokens(source,
            TokenType.E_LOGICO,
            TokenType.OU_LOGICO,
            TokenType.NEGACAO
        );
    }
    
    @Test
    public void testExpressaoLogica() {
        String source = "a && b || !c";
        assertTokens(source,
            TokenType.IDENTIFICADOR,
            TokenType.E_LOGICO,
            TokenType.IDENTIFICADOR,
            TokenType.OU_LOGICO,
            TokenType.NEGACAO,
            TokenType.IDENTIFICADOR
        );
    }
    
    // ============== TESTES DE ATRIBUIÇÃO ==============
    
    @Test
    public void testAtribuicao() {
        String source = "x = 10";
        assertTokens(source,
            TokenType.IDENTIFICADOR,
            TokenType.ATRIBUICAO,
            TokenType.LITERAL_INTEIRO
        );
    }
    
    @Test
    public void testAtribuicaoVsIgualdade() {
        String source = "a = b == c";
        assertTokens(source,
            TokenType.IDENTIFICADOR,
            TokenType.ATRIBUICAO,
            TokenType.IDENTIFICADOR,
            TokenType.IGUAL_IGUAL,
            TokenType.IDENTIFICADOR
        );
    }
    
    // ============== TESTES DE DELIMITADORES ==============
    
    @Test
    public void testDelimitadores() {
        String source = "( ) { } ; ,";
        assertTokens(source,
            TokenType.ABRE_PARENTESE,
            TokenType.FECHA_PARENTESE,
            TokenType.ABRE_CHAVE,
            TokenType.FECHA_CHAVE,
            TokenType.PONTO_VIRGULA,
            TokenType.VIRGULA
        );
    }
    
    @Test
    public void testParentesesEmExpressao() {
        String source = "(a + b) * c";
        assertTokens(source,
            TokenType.ABRE_PARENTESE,
            TokenType.IDENTIFICADOR,
            TokenType.MAIS,
            TokenType.IDENTIFICADOR,
            TokenType.FECHA_PARENTESE,
            TokenType.MULTIPLICACAO,
            TokenType.IDENTIFICADOR
        );
    }
    
    // ============== TESTES DE COMENTÁRIOS ==============
    
    @Test
    public void testComentarioLinha() {
        String source = "var x // isso é um comentário\nvar y";
        assertTokens(source,
            TokenType.VAR,
            TokenType.IDENTIFICADOR,
            TokenType.VAR,
            TokenType.IDENTIFICADOR
        );
    }
    
    @Test
    public void testComentarioBloco() {
        String source = "var x /* comentário\nmultilinhas */ var y";
        assertTokens(source,
            TokenType.VAR,
            TokenType.IDENTIFICADOR,
            TokenType.VAR,
            TokenType.IDENTIFICADOR
        );
    }
    
    @Test
    public void testComentarioBlocoAninhado() {
        String source = "inicio /* comentário */ meio /* outro */ fim";
        assertTokens(source,
            TokenType.IDENTIFICADOR,
            TokenType.IDENTIFICADOR,
            TokenType.IDENTIFICADOR
        );
    }
    
    // ============== TESTES DE PROGRAMAS COMPLETOS ==============
    
    @Test
    public void testDeclaracaoSimples() {
        String source = "var idade inteiro;";
        assertTokens(source,
            TokenType.VAR,
            TokenType.IDENTIFICADOR,
            TokenType.INTEIRO,
            TokenType.PONTO_VIRGULA
        );
    }
    
    @Test
    public void testDeclaracaoComInicializacao() {
        String source = "var x inteiro = 10;";
        assertTokens(source,
            TokenType.VAR,
            TokenType.IDENTIFICADOR,
            TokenType.INTEIRO,
            TokenType.ATRIBUICAO,
            TokenType.LITERAL_INTEIRO,
            TokenType.PONTO_VIRGULA
        );
    }
    
    @Test
    public void testComandoIf() {
        String source = "se x > 10 { imprimir(x); }";
        assertTokens(source,
            TokenType.SE,
            TokenType.IDENTIFICADOR,
            TokenType.MAIOR,
            TokenType.LITERAL_INTEIRO,
            TokenType.ABRE_CHAVE,
            TokenType.IMPRIMIR,
            TokenType.ABRE_PARENTESE,
            TokenType.IDENTIFICADOR,
            TokenType.FECHA_PARENTESE,
            TokenType.PONTO_VIRGULA,
            TokenType.FECHA_CHAVE
        );
    }
    
    @Test
    public void testComandoIfElse() {
        String source = "se x { a = 1; } senao { a = 0; }";
        assertTokens(source,
            TokenType.SE,
            TokenType.IDENTIFICADOR,
            TokenType.ABRE_CHAVE,
            TokenType.IDENTIFICADOR,
            TokenType.ATRIBUICAO,
            TokenType.LITERAL_INTEIRO,
            TokenType.PONTO_VIRGULA,
            TokenType.FECHA_CHAVE,
            TokenType.SENAO,
            TokenType.ABRE_CHAVE,
            TokenType.IDENTIFICADOR,
            TokenType.ATRIBUICAO,
            TokenType.LITERAL_INTEIRO,
            TokenType.PONTO_VIRGULA,
            TokenType.FECHA_CHAVE
        );
    }
    
    @Test
    public void testComandoFor() {
        String source = "para i = 0; i < 10; i = i + 1 { imprimir(i); }";
        assertTokens(source,
            TokenType.PARA,
            TokenType.IDENTIFICADOR,
            TokenType.ATRIBUICAO,
            TokenType.LITERAL_INTEIRO,
            TokenType.PONTO_VIRGULA,
            TokenType.IDENTIFICADOR,
            TokenType.MENOR,
            TokenType.LITERAL_INTEIRO,
            TokenType.PONTO_VIRGULA,
            TokenType.IDENTIFICADOR,
            TokenType.ATRIBUICAO,
            TokenType.IDENTIFICADOR,
            TokenType.MAIS,
            TokenType.LITERAL_INTEIRO,
            TokenType.ABRE_CHAVE,
            TokenType.IMPRIMIR,
            TokenType.ABRE_PARENTESE,
            TokenType.IDENTIFICADOR,
            TokenType.FECHA_PARENTESE,
            TokenType.PONTO_VIRGULA,
            TokenType.FECHA_CHAVE
        );
    }
    
    @Test
    public void testComandoImprimir() {
        String source = "imprimir(\"Olá\", nome, idade);";
        assertTokens(source,
            TokenType.IMPRIMIR,
            TokenType.ABRE_PARENTESE,
            TokenType.LITERAL_TEXTO,
            TokenType.VIRGULA,
            TokenType.IDENTIFICADOR,
            TokenType.VIRGULA,
            TokenType.IDENTIFICADOR,
            TokenType.FECHA_PARENTESE,
            TokenType.PONTO_VIRGULA
        );
    }
    
    @Test
    public void testComandoLer() {
        String source = "ler(nome, idade);";
        assertTokens(source,
            TokenType.LER,
            TokenType.ABRE_PARENTESE,
            TokenType.IDENTIFICADOR,
            TokenType.VIRGULA,
            TokenType.IDENTIFICADOR,
            TokenType.FECHA_PARENTESE,
            TokenType.PONTO_VIRGULA
        );
    }
    
    @Test
    public void testProgramaCompleto() {
        String source = """
            var x inteiro = 10;
            var y real = 3.14;
            var nome texto = "João";
            
            se x > 5 {
                imprimir("x é maior que 5");
            } senao {
                imprimir("x é menor ou igual a 5");
            }
            
            para i = 0; i < x; i = i + 1 {
                imprimir(i);
            }
            """;
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        // Verifica que há tokens e termina com EOF
        assertTrue(tokens.size() > 1);
        assertEquals(TokenType.EOF, tokens.get(tokens.size() - 1).getTipo());
    }
    
    // ============== TESTES DE ESPAÇOS E FORMATAÇÃO ==============
    
    @Test
    public void testIgnoraEspacos() {
        String source = "var    x    inteiro   ;";
        assertTokens(source,
            TokenType.VAR,
            TokenType.IDENTIFICADOR,
            TokenType.INTEIRO,
            TokenType.PONTO_VIRGULA
        );
    }
    
    @Test
    public void testIgnoraLinhasVazias() {
        String source = "var x\n\n\ninteiro;";
        assertTokens(source,
            TokenType.VAR,
            TokenType.IDENTIFICADOR,
            TokenType.INTEIRO,
            TokenType.PONTO_VIRGULA
        );
    }
    
    @Test
    public void testIgnoraTabs() {
        String source = "var\tx\tinteiro;";
        assertTokens(source,
            TokenType.VAR,
            TokenType.IDENTIFICADOR,
            TokenType.INTEIRO,
            TokenType.PONTO_VIRGULA
        );
    }
    
    // ============== TESTES DE POSIÇÃO (LINHA E COLUNA) ==============
    
    @Test
    public void testPosicaoTokens() {
        String source = "var x inteiro;\nvar y real;";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        // Primeira linha
        assertEquals(1, tokens.get(0).getLinha()); // var
        assertEquals(1, tokens.get(1).getLinha()); // x
        assertEquals(1, tokens.get(2).getLinha()); // inteiro
        assertEquals(1, tokens.get(3).getLinha()); // ;
        
        // Segunda linha
        assertEquals(2, tokens.get(4).getLinha()); // var
        assertEquals(2, tokens.get(5).getLinha()); // y
        assertEquals(2, tokens.get(6).getLinha()); // real
        assertEquals(2, tokens.get(7).getLinha()); // ;
    }
    
    // ============== TESTES DE EOF ==============
    
    @Test
    public void testEOFAdicionado() {
        String source = "var x";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(TokenType.EOF, tokens.get(tokens.size() - 1).getTipo());
    }
    
    @Test
    public void testEOFEmArquivoVazio() {
        String source = "";
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).getTipo());
    }
}