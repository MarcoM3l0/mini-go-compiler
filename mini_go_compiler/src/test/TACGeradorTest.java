package test;

import analisadorsintatico.Comando;
import codigointermediario.TACGerador;
import codigointermediario.TACInstrucoes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o Gerador de Código Intermediário (TAC).
 */
public class TACGeradorTest {
    
    private TACGerador gerador;
    
    @BeforeEach
    public void setUp() {
        gerador = new TACGerador();
    }
    
    /**
     * Método auxiliar: compila código e gera TAC.
     */
    private List<TACInstrucoes> gerarTAC(String codigo) {
        Scanner scanner = new Scanner(codigo);
        List<Token> tokens = scanner.scanTokens();
        
        Parser parser = new Parser(tokens);
        List<Comando> ast = parser.parsePrograma();
        
        assertNotNull(ast, "Parser falhou");
        
        return gerador.gerar(ast);
    }
    
    /**
     * Método auxiliar: imprime TAC gerado (para debug).
     */
    private void imprimirTAC(List<TACInstrucoes> tac) {
        System.out.println("\n=== TAC GERADO ===");
        for (int i = 0; i < tac.size(); i++) {
            System.out.printf("%3d: %s%n", i, tac.get(i));
        }
        System.out.println("==================\n");
    }
    
    // ========================================================================
    //                    TESTES DE EXPRESSÕES ARITMÉTICAS
    // ========================================================================
    
    @Test
    public void testAtribuicaoSimples() {
        String codigo = "var x inteiro = 10;";
        List<TACInstrucoes> tac = gerarTAC(codigo);
        
        assertEquals(1, tac.size());
        assertEquals("x = 10", tac.get(0).toString());
    }
    
    @Test
    public void testExpressaoAritmeticaSimples() {
        String codigo = """
            var a inteiro = 10;
            var b inteiro = 20;
            var c inteiro = a + b;
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        
        // Verifica que há instruções
        assertTrue(tac.size() >= 3);
        
        // Última instrução deve ser uma atribuição com SOMA
        boolean temAdicao = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.SOMA);
        assertTrue(temAdicao, "Deveria ter instrução de adição");
    }
    
    @Test
    public void testExpressaoComPrecedencia() {
        // x = a + b * c
        // Deve gerar: t1 = b * c, t2 = a + t1, x = t2
        String codigo = """
            var a inteiro = 5;
            var b inteiro = 3;
            var c inteiro = 2;
            var x inteiro = a + b * c;
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Verifica ordem: MULTIPLICACAO antes de SOMA
        int multiplicacaoIndex = -1, somaIndex = -1;
        for (int i = 0; i < tac.size(); i++) {
            if (tac.get(i).getOperador() == TACInstrucoes.TACOperador.MULTIPLICACAO) {
                multiplicacaoIndex = i;
            }
            if (tac.get(i).getOperador() == TACInstrucoes.TACOperador.SOMA) {
                somaIndex = i;
            }
        }
        
        assertTrue(multiplicacaoIndex >= 0 && somaIndex >= 0, "Deve ter MULTIPLICACAO e SOMA");
        assertTrue(multiplicacaoIndex < somaIndex, "MULTIPLICACAO deve vir antes de SOMA");
    }
    
    @Test
    public void testOperadorUnario() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = -x;
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        
        boolean temInversao = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.SUBTRACAO && 
                          i.getOperando2() == null);
        assertTrue(temInversao, "Deveria ter operador INVERSAO");
    }
    
    // ========================================================================
    //                    TESTES DE EXPRESSÕES RELACIONAIS
    // ========================================================================
    
    @Test
    public void testComparacao() {
        String codigo = """
            var a inteiro = 10;
            var b inteiro = 20;
            se a < b {
                imprimir(a);
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Deve ter comparação MENOR
        boolean temMenor = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.MENOR);
        assertTrue(temMenor, "Deveria ter operador MENOR");
        
        // Deve ter se_falso
        boolean temSeFalso = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.SE_FALSO);
        assertTrue(temSeFalso, "Deveria ter instrução se_falso");
    }
    
    @Test
    public void testIgualdade() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = 10;
            se x == y {
                imprimir(x);
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        
        boolean temIgual = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstruction.TACOperador.IGUAL);
        assertTrue(temIgual, "Deveria ter operador IGUAL");
    }
    
    // ========================================================================
    //                    TESTES DE OPERADORES LÓGICOS
    // ========================================================================
    
    @Test
    public void testOperadorLogico() {
        String codigo = """
            var a inteiro = 10;
            var b inteiro = 20;
            se a > 5 && b < 30 {
                imprimir(a);
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Deve ter rotulos para curto-circuito
        long numRotulo = tac.stream()
            .filter(TACInstrucoes::isRotulo)
            .count();
        assertTrue(numRotulo >= 2, "Operador lógico deve gerar rótulos");
    }
    
    @Test
    public void testNegacaoLogica() {
        String codigo = """
            var x inteiro = 10;
            se !(x > 20) {
                imprimir(x);
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        
        boolean temNegacao = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.NEGACAO);
        assertTrue(temNegacao, "Deveria ter operador NEGACAO");
    }
    
    // ========================================================================
    //                    TESTES DE COMANDOS SE/SENAO
    // ========================================================================
    
    @Test
    public void testSeSimples() {
        String codigo = """
            var x inteiro = 10;
            se x > 5 {
                imprimir(x);
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Deve ter se_falso e rotulos
        boolean temSeFalso = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.SE_FALSO);
        assertTrue(temSeFalso);
        
        long numRotulo = tac.stream().filter(TACInstrucoes::isRotulo).count();
        assertTrue(numRotulo >= 1);
    }
    
    @Test
    public void testSeSenao() {
        String codigo = """
            var x inteiro = 10;
            se x > 5 {
                imprimir("maior");
            } senao {
                imprimir("menor");
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Deve ter vai_para e 2 rotulos (senao e fim)
        boolean temVaiPara = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.VAI_PARA);
        assertTrue(temVaiPara, "If-else deve ter goto");
        
        long numRotulo = tac.stream().filter(TACInstrucoes::isRotulo).count();
        assertTrue(numRotulo >= 2, "Se-senao deve ter pelo menos 2 rotulos");
    }
    
    @Test
    public void testSeAninhado() {
        String codigo = """
            var x inteiro = 10;
            se x > 5 {
                se x < 15 {
                    imprimir("entre 5 e 15");
                }
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Deve ter múltiplos labels
        long numRotulo = tac.stream().filter(TACInstrucoes::isRotulo).count();
        assertTrue(numRotulo >= 2, "Se aninhado deve ter múltiplos rotulos");
    }
    
    // ========================================================================
    //                    TESTES DE LAÇOS
    // ========================================================================
    
    @Test
    public void testParaEstiloWhile() {
        String codigo = """
            var i inteiro = 0;
            para i < 10 {
                i = i + 1;
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Deve ter: rotulo de início, se_falso, corpo, vai_para
        boolean temVaiPara = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.VAI_PARA);
        assertTrue(temVaiPara, "Loop deve ter vai_para");
        
        long numRotulo = tac.stream().filter(TACInstruction::isRotulo).count();
        assertTrue(numRotulo >= 2, "Loop deve ter label de início e fim");
    }
    
    @Test
    public void testParaClassico() {
        String codigo = """
            para var i inteiro = 0; i < 10; i = i + 1 {
                imprimir(i);
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Deve ter inicialização, condição, corpo, incremento
        assertTrue(tac.size() > 5, "Loop clássico deve ter várias instruções");
        
        // Deve ter vai_para para voltar ao início
        boolean temVaiPara = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.VAI_PARA);
        assertTrue(temVaiPara);
    }
    
    @Test
    public void testParaAninhado() {
        String codigo = """
            para var i inteiro = 0; i < 3; i = i + 1 {
                para var j inteiro = 0; j < 3; j = j + 1 {
                    imprimir(i, j);
                }
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Deve ter múltiplos labels
        long numRotulo = tac.stream().filter(TACInstrucoes::isRotulo).count();
        assertTrue(numRotulo >= 4, "Loops aninhados devem ter múltiplos retulos");
    }
    
    // ========================================================================
    //                    TESTES DE I/O
    // ========================================================================
    
    @Test
    public void testImprimir() {
        String codigo = """
            var x inteiro = 10;
            imprimir(x);
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        
        boolean temImprimir = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.IMPRIMIR);
        assertTrue(temImprimir, "Deveria ter instrução imprimir");
    }
    
    @Test
    public void testImprimirMultiplosValores() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = 20;
            imprimir(x, y, "soma:", x + y);
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        long numImprimir = tac.stream()
            .filter(i -> i.getOperador() == TACInstrucoes.TACOperador.IMPRIMIR)
            .count();
        assertEquals(4, numImprimir, "Deveria ter 4 instruções imprimir");
    }
    
    @Test
    public void testLer() {
        String codigo = """
            var x inteiro;
            var y inteiro;
            ler(x, y);
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        
        long numLer = tac.stream()
            .filter(i -> i.getOperador() == TACInstrucoes.TACOperador.LER)
            .count();
        assertEquals(2, numLer, "Deveria ter 2 instruções ler");
    }
    
    // ========================================================================
    //                    TESTES DE PROGRAMAS COMPLETOS
    // ========================================================================
    
    @Test
    public void testProgramaSimples() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = 20;
            var soma inteiro = x + y;
            imprimir(soma);
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        assertFalse(tac.isEmpty());
        assertTrue(tac.size() >= 4);
    }
    
    @Test
    public void testProgramaComSeEPara() {
        String codigo = """
            var n inteiro = 5;
            var i inteiro = 0;
            var soma inteiro = 0;
            
            para i < n {
                soma = soma + i;
                i = i + 1;
            }
            
            se soma > 10 {
                imprimir("Grande:", soma);
            } senao {
                imprimir("Pequeno:", soma);
            }
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        assertTrue(tac.size() > 10, "Programa complexo deve gerar muitas instruções");
    }
    
    @Test
    public void testFatorial() {
        String codigo = """
            var n inteiro = 5;
            var fatorial inteiro = 1;
            var i inteiro = 1;
            
            para i <= n {
                fatorial = fatorial * i;
                i = i + 1;
            }
            
            imprimir("Fatorial:", fatorial);
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Verifica que tem multiplicação
        boolean temMul = tac.stream()
            .anyMatch(i -> i.getOperador() == TACInstrucoes.TACOperador.MULTIPLICACAO);
        assertTrue(temMul, "Cálculo de fatorial deve ter multiplicação");
    }
    
    @Test
    public void testExpressaoComplexaOriginalProjeto() {
        // Exemplo projeto: x = a + b * c
        String codigo = """
            var a inteiro = 2;
            var b inteiro = 3;
            var c inteiro = 4;
            var x inteiro = a + b * c;
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        
        System.out.println("\n=== EXEMPLO PROJETO: x = a + b * c ===");
        System.out.println("Código fonte:");
        System.out.println("  x = a + b * c");
        System.out.println("\nTAC gerado:");
        for (TACInstrucoes instr : tac) {
            if (instr.getOperador() == TACInstrucoes.TACOperaDor.MULTIPLICACAO ||
                instr.getOperador() == TACInstrucoes.TACOperator.SOMA ||
                (instr.getOperador() == TACInstrucoes.TACOperator.ATRIBUICAO && 
                 instr.getResultado().equals("x"))) {
                System.out.println("  " + instr);
            }
        }
        System.out.println("========================================\n");
    }
    
    // ========================================================================
    //                    TESTES DE CONTADORES
    // ========================================================================
    
    @Test
    public void testTemporarios() {
        String codigo = """
            var a inteiro = 1;
            var b inteiro = 2;
            var c inteiro = 3;
            var d inteiro = 4;
            var resultado inteiro = a + b * c - d;
            """;
        
        List<TACInstrucoes> tac = gerarTAC(codigo);
        imprimirTAC(tac);
        
        // Conta quantos temporários foram usados
        long numTemps = tac.stream()
            .flatMap(i -> {
                java.util.stream.Stream.Builder<String> builder = java.util.stream.Stream.builder();
                if (i.getResultado() != null) builder.add(i.getResultado());
                if (i.getOperando1() != null) builder.add(i.getOperando1());
                if (i.getOperando2() != null) builder.add(i.getOperando2());
                return builder.build();
            })
            .filter(s -> s.startsWith("t"))
            .distinct()
            .count();
        
        assertTrue(numTemps >= 2, "Expressão complexa deve usar temporários");
    }
}