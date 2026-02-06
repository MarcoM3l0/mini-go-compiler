package codigointermediario;

import analisadorsemantico.AnalisadorSemantico;
import analisadorsintatico.AstPrinter;
import analisadorsintatico.Comando;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;

import java.util.List;

/**
 * Demonstração do compilador Mini-Go com geração de código intermediário.
 * 
 * Pipeline completo:
 * 1. Scanner (Análise Léxica)
 * 2. Parser (Análise Sintática)
 * 3. Analisador Semântico
 * 4. Gerador TAC (Código Intermediário)
 */
public class Programa {
    
    public static void main(String[] args) {
        // Exemplos de demonstração
        exemploSimples();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        exemploSeSenao();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        exemploRepeticao();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        exemploFatorial();
        System.out.println("\n" + "=".repeat(80) + "\n");
        
        exemploProjeto();
    }
    
    /**
     * Compila código fonte completo e exibe todos os passos.
     */
    private static void compilarEExibir(String titulo, String codigo) {
        System.out.println("=".repeat(80));
        System.out.println(titulo);
        System.out.println("=".repeat(80));
        System.out.println("\n CÓDIGO FONTE:\n");
        System.out.println(codigo);
        
        // Etapa 1: Scanner
        System.out.println("\n" + "-".repeat(80));
        System.out.println("ETAPA 1: ANÁLISE LÉXICA (SCANNER)");
        System.out.println("-".repeat(80));
        Scanner scanner = new Scanner(codigo);
        List<Token> tokens = scanner.scanTokens();
        System.out.println(tokens.size() + " tokens identificados");
        
        // Etapa 2: Parser
        System.out.println("\n" + "-".repeat(80));
        System.out.println("ETAPA 2: ANÁLISE SINTÁTICA (PARSER)");
        System.out.println("-".repeat(80));
        Parser parser = new Parser(tokens);
        List<Comando> ast = parser.parsePrograma();
        
        if (ast == null) {
            System.err.println("Erro na análise sintática!");
            return;
        }
        
        System.out.println("AST gerada com " + ast.size() + " comandos");
        
        // Imprime AST
        AstPrinter printer = new AstPrinter();
        System.out.println("\n Árvore Sintática:");
        for (int i = 0; i < ast.size(); i++) {
            System.out.println("  " + (i+1) + ". " + printer.print(ast.get(i)));
        }
        
        // Etapa 3: Análise Semântica
        System.out.println("\n" + "-".repeat(80));
        System.out.println("ETAPA 3: ANÁLISE SEMÂNTICA");
        System.out.println("-".repeat(80));
        AnalisadorSemantico semantico = new AnalisadorSemantico();
        boolean valido = semantico.analisar(ast);
        
        if (!valido) {
            System.err.println("Erros semânticos encontrados!");
            semantico.imprimirErros();
            return;
        }
        
        System.out.println("Programa semanticamente correto!");
        
        // Etapa 4: Geração de Código Intermediário
        System.out.println("\n" + "-".repeat(80));
        System.out.println("ETAPA 4: GERAÇÃO DE CÓDIGO INTERMEDIÁRIO (TAC)");
        System.out.println("-".repeat(80));
        TACGerador tacGen = new TACGerador();
        List<TACInstrucoes> tac = tacGen.gerar(ast);
        
        System.out.println("\n" + tac.size() + " instruções TAC geradas:\n");
        for (int i = 0; i < tac.size(); i++) {
            System.out.printf("  %3d: %s%n", i, tac.get(i));
        }
        
        System.out.println("\n COMPILAÇÃO CONCLUÍDA COM SUCESSO!");
    }
    
    /**
     * Exemplo 1: Expressão aritmética simples.
     */
    private static void exemploSimples() {
        String codigo = """
            var a inteiro = 5;
            var b inteiro = 3;
            var c inteiro = 2;
            var x inteiro = a + b * c;
            imprimir(x);
            """;
        
        compilarEExibir("EXEMPLO 1: Expressão Aritmética com Precedência", codigo);
    }
    
    /**
     * Exemplo 2: Estrutura condicional se-senao.
     */
    private static void exemploSeSenao() {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = 20;
            
            se x > y {
                imprimir("x é maior");
            } senao {
                imprimir("y é maior");
            }
            """;
        
        compilarEExibir("EXEMPLO 2: Estrutura Condicional (SE-SENAO)", codigo);
    }
    
    /**
     * Exemplo 3: Laço de repetição.
     */
    private static void exemploRepeticao() {
        String codigo = """
            var soma inteiro = 0;
            var i inteiro = 1;
            
            para i <= 5 {
                soma = soma + i;
                i = i + 1;
            }
            
            imprimir("Soma:", soma);
            """;
        
        compilarEExibir("EXEMPLO 3: Laço de Repetição (PARA/PARA - WHILE)", codigo);
    }
    
    /**
     * Exemplo 4: Cálculo de fatorial.
     */
    private static void exemploFatorial() {
        String codigo = """
            var n inteiro = 5;
            var fatorial inteiro = 1;
            var i inteiro = 1;
            
            para i <= n {
                fatorial = fatorial * i;
                i = i + 1;
            }
            
            imprimir("Fatorial de", n, "=", fatorial);
            """;
        
        compilarEExibir("EXEMPLO 4: Cálculo de Fatorial", codigo);
    }
    
    /**
     * Exemplo 5: O exemplo da documentação do projeto.
     */
    private static void exemploProjeto() {
        String codigo = """
            var a inteiro = 2;
            var b inteiro = 3;
            var c inteiro = 4;
            var x inteiro = a + b * c;
            """;
        
        System.out.println("=".repeat(80));
        System.out.println("EXEMPLO PROJETO: x = a + b * c");
        System.out.println("=".repeat(80));
        System.out.println("\n Código Fonte:");
        System.out.println("  x = a + b * c\n");
        
        Scanner scanner = new Scanner(codigo);
        Parser parser = new Parser(scanner.scanTokens());
        List<Comando> ast = parser.parsePrograma();
        
        TACGerador tacGen = new TACGerador();
        List<TACInstrucoes> tac = tacGen.gerar(ast);
        
        System.out.println("Código TAC Gerado:");
        for (TACInstrucoes instr : tac) {
            // Mostra apenas as instruções relevantes para o cálculo
            if (instr.getOperador() == TACInstrucoes.TACOperador.ATRIBUICAO &&
                !instr.getResultado().startsWith("t")) {
                continue; // Pula atribuições iniciais de a, b, c
            }
            if (instr.getOperador() == TACInstrucoes.TACOperador.MULTIPLICACAO ||
                instr.getOperador() == TACInstrucoes.TACOperador.SOMA ||
                (instr.getOperador() == TACInstrucoes.TACOperador.ATRIBUICAO && 
                 instr.getResultado().equals("x"))) {
                System.out.println("  " + instr);
            }
        }
        
        System.out.println("\n Explicação:");
        System.out.println("  1. Primeiro multiplica: t1 = b * c");
        System.out.println("  2. Depois soma:         t2 = a + t1");
        System.out.println("  3. Atribui ao resultado: x = t2");
        System.out.println("\n A precedência de operadores foi respeitada!");
    }
    
    /**
     * Exemplo lógico: Operadores lógicos.
     */
    public static void exemploLogico() {
        String codigo = """
            var a inteiro = 10;
            var b inteiro = 20;
            var c inteiro = 30;
            
            se a > 5 && b < 25 && c > 20 {
                imprimir("Todas as condições são verdadeiras");
            }
            """;
        
        compilarEExibir("EXEMPLO LÓGICO: Operadores Lógicos", codigo);
    }
}