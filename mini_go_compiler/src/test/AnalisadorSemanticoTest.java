package test;

import analisadorsintatico.Comando;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import analisadorsemantico.*;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o Analisador Semântico.
 * Verifica detecção de erros semânticos comuns.
 */
public class AnalisadorSemanticoTest {
    
    private AnalisadorSemantico analisador;
    
    @BeforeEach
    public void setUp() {
        analisador = new AnalisadorSemantico();
    }
    
    /**
     * Método auxiliar: compila código completo (scanner + parser + semântico).
     */
    private boolean compilar(String codigo) {
        // Scanner
        Scanner scanner = new Scanner(codigo);
        List<Token> tokens = scanner.scanTokens();
        
        // Parser
        Parser parser = new Parser(tokens);
        List<Comando> ast = parser.parsePrograma();
        
        if (ast == null) {
            fail("Erro sintático ao parsear o código");
            return false;
        }
        
        // Análise Semântica
        return analisador.analisar(ast);
    }
    
    /**
     * Método auxiliar: verifica se há erro do tipo específico.
     */
    private boolean temErroTipo(ErroSemantico.TipoErro tipo) {
        return analisador.getErros().stream()
                .anyMatch(e -> e.getTipo() == tipo);
    }
    
    // ========================================================================
    //                    TESTES DE DECLARAÇÃO DE VARIÁVEIS
    // ========================================================================
    
    @Test
    public void testDeclaracaoSimples() {
        String codigo = "var x inteiro;";
        assertTrue(compilar(codigo), "Declaração simples não deveria ter erros");
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testDeclaracaoComInicializacao() {
        String codigo = "var x inteiro = 10;";
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testRedeclaracaoMesmoEscopo() {
        String codigo = """
            var x inteiro;
            var x real;
            """;
        
        assertFalse(compilar(codigo), "Deveria detectar redeclaração");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.VARIAVEL_JA_DECLARADA));
    }
    
    @Test
    public void testDeclaracaoEscoposDiferentes() {
        String codigo = """
            var x inteiro;
            {
                var x real;
            }
            """;
        
        assertTrue(compilar(codigo), "Variáveis em escopos diferentes são permitidas");
        assertEquals(0, analisador.getErros().size());
    }
    
    // ========================================================================
    //                    TESTES DE USO DE VARIÁVEIS
    // ========================================================================
    
    @Test
    public void testVariavelNaoDeclarada() {
        String codigo = "x = 10;";
        
        assertFalse(compilar(codigo), "Deveria detectar variável não declarada");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.VARIAVEL_NAO_DECLARADA));
    }
    
    @Test
    public void testVariavelDeclaradaAntes() {
        String codigo = """
            var x inteiro;
            x = 10;
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testVariavelEmExpressao() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = x + 5;
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    // ========================================================================
    //                    TESTES DE COMPATIBILIDADE DE TIPOS
    // ========================================================================
    
    @Test
    public void testAtribuicaoTiposCompativeis() {
        String codigo = """
            var x inteiro;
            x = 10;
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testAtribuicaoInteiroParaReal() {
        String codigo = """
            var x real;
            x = 10;
            """;
        
        // Inteiro é compatível com Real (promoção de tipo)
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testAtribuicaoTextoParaInteiro() {
        String codigo = """
            var x inteiro;
            x = "texto";
            """;
        
        assertFalse(compilar(codigo), "Não pode atribuir texto a inteiro");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.TIPO_INVALIDO_ATRIBUICAO));
    }
    
    @Test
    public void testDeclaracaoInicializacaoInvalida() {
        String codigo = "var x inteiro = \"texto\";";
        
        assertFalse(compilar(codigo));
        assertTrue(temErroTipo(ErroSemantico.TipoErro.TIPO_INVALIDO_ATRIBUICAO));
    }
    
    // ========================================================================
    //                    TESTES DE OPERAÇÕES ARITMÉTICAS
    // ========================================================================
    
    @Test
    public void testSomaInteiros() {
        String codigo = """
            var a inteiro = 10;
            var b inteiro = 20;
            var c inteiro = a + b;
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testSomaNumeroComTexto() {
        String codigo = """
            var x inteiro = 10;
            var y texto = "abc";
            var z inteiro = x + y;
            """;
        
        assertFalse(compilar(codigo), "Não pode somar número com texto");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO));
    }
    
    @Test
    public void testMultiplicacaoReais() {
        String codigo = """
            var a real = 3.14;
            var b real = 2.0;
            var c real = a * b;
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testOperacaoMista() {
        String codigo = """
            var a inteiro = 10;
            var b real = 3.14;
            var c real = a + b;
            """;
        
        assertTrue(compilar(codigo), "Operação entre inteiro e real é válida");
        assertEquals(0, analisador.getErros().size());
    }
    
    // ========================================================================
    //                    TESTES DE OPERAÇÕES LÓGICAS
    // ========================================================================
    
    @Test
    public void testOperadorLogicoValido() {
        String codigo = """
            var a inteiro = 10;
            var b inteiro = 20;
            var resultado inteiro;
            se a > 5 && b < 30 {
                resultado = 1;
            }
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testOperadorLogicoComNumeros() {
        String codigo = """
            var a inteiro = 10;
            var b inteiro = 20;
            var c inteiro = a && b;
            """;
        
        assertFalse(compilar(codigo), "Operador && requer booleanos");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO));
    }
    
    @Test
    public void testNegacaoBooleana() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro;
            se !(x > 5) {
                y = 0;
            }
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testNegacaoNumero() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = !x;
            """;
        
        assertFalse(compilar(codigo), "Não pode negar um número com !");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO));
    }
    
    // ========================================================================
    //                    TESTES DE CONDICIONAIS
    // ========================================================================
    
    @Test
    public void testCondicionalBooleana() {
        String codigo = """
            var x inteiro = 10;
            se x > 5 {
                imprimir(x);
            }
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testCondicionalNaoBooleana() {
        String codigo = """
            var x inteiro = 10;
            se x {
                imprimir(x);
            }
            """;
        
        assertFalse(compilar(codigo), "Condição deve ser booleana");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.TIPO_INVALIDO_CONDICAO));
    }
    
    @Test
    public void testCondicionalTexto() {
        String codigo = """
            var meuTexto texto = "abc";
            se meuTexto == "abc" {     
                imprimir("ok");
            }
            """;
        
        assertTrue(compilar(codigo));  
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testCondicionalTextoSemComparacao() {
        String codigo = """
            var meuTexto texto = "abc";
            se meuTexto {              
                imprimir("ok");
            }
            """;
        
        assertFalse(compilar(codigo), "Não pode usar texto diretamente");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.TIPO_INVALIDO_CONDICAO));
    }
    
    // ========================================================================
    //                    TESTES DE LAÇOS
    // ========================================================================
    
    @Test
    public void testParaClassico() {
        String codigo = """
            para var i inteiro = 0; i < 10; i = i + 1 {
                imprimir(i);
            }
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testParaEstiloWhile() {
        String codigo = """
            var i inteiro = 0;
            para i < 10 {
                i = i + 1;
            }
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testParaCondicaoInvalida() {
        String codigo = """
            var i inteiro = 0;
            para i {
                i = i + 1;
            }
            """;
        
        assertFalse(compilar(codigo), "Condição do para deve ser booleana");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.TIPO_INVALIDO_CONDICAO));
    }
    
    // ========================================================================
    //                    TESTES DE ESCOPO
    // ========================================================================
    
    @Test
    public void testEscopoBloco() {
        String codigo = """
            var x inteiro = 10;
            {
                var y inteiro = 20;
                imprimir(x, y);
            }
            imprimir(x);
            """;
        
        assertTrue(compilar(codigo), "Variável de escopo externo acessível no interno");
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testVariavelForaDoEscopo() {
        String codigo = """
            {
                var x inteiro = 10;
            }
            imprimir(x);
            """;
        
        assertFalse(compilar(codigo), "Variável não acessível fora do escopo");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.VARIAVEL_NAO_DECLARADA));
    }
    
    @Test
    public void testEscopoFor() {
        String codigo = """
            para var i inteiro = 0; i < 10; i = i + 1 {
                imprimir(i);
            }
            imprimir(i);
            """;
        
        assertFalse(compilar(codigo), "Variável do for não acessível fora");
        assertTrue(temErroTipo(ErroSemantico.TipoErro.VARIAVEL_NAO_DECLARADA));
    }
    
    // ========================================================================
    //                    TESTES DE COMANDOS LER/IMPRIMIR
    // ========================================================================
    
    @Test
    public void testImprimir() {
        String codigo = """
            var x inteiro = 10;
            var nome texto = "João";
            imprimir("Valor:", x, nome);
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testLerVariavelDeclarada() {
        String codigo = """
            var x inteiro;
            ler(x);
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testLerVariavelNaoDeclarada() {
        String codigo = "ler(x);";
        
        assertFalse(compilar(codigo));
        assertTrue(temErroTipo(ErroSemantico.TipoErro.VARIAVEL_NAO_DECLARADA));
    }
    
    // ========================================================================
    //                    TESTES DE PROGRAMAS COMPLETOS
    // ========================================================================
    
    @Test
    public void testProgramaValidoCompleto() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = 20;
            var soma inteiro;
            
            soma = x + y;
            
            se soma > 25 {
                imprimir("Soma maior que 25:", soma);
            } senao {
                imprimir("Soma menor ou igual a 25:", soma);
            }
            
            para var i inteiro = 0; i < soma; i = i + 1 {
                imprimir(i);
            }
            """;
        
        assertTrue(compilar(codigo), "Programa válido não deveria ter erros");
        assertEquals(0, analisador.getErros().size());
    }
    
    @Test
    public void testProgramaComMultiplosErros() {
        String codigo = """
            var x inteiro = "texto";
            y = 10;
            var x real;
            se x {
                z = 20;
            }
            """;
        
        assertFalse(compilar(codigo));
        assertTrue(analisador.getErros().size() >= 3, "Deveria ter pelo menos 3 erros");
    }
    
    @Test
    public void testCalculadora() {
        String codigo = """
            var a real;
            var b real;
            var resultado real;
            
            imprimir("Digite dois números:");
            ler(a, b);
            
            resultado = a + b;
            imprimir("Soma:", resultado);
            
            resultado = a * b;
            imprimir("Multiplicação:", resultado);
            """;
        
        assertTrue(compilar(codigo));
        assertEquals(0, analisador.getErros().size());
    }
}