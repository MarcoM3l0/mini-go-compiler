package teste; // ou o pacote que você estiver usando

import java.util.List;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;
// ATENÇÃO: Importe as classes do seu pacote correto (pelo erro parece ser analisadorsintatico)
import analisadorsintatico.Comando;
import analisadorsintatico.Expressao;
import analisadorsintatico.AstPrinter;

public class TesteParse {
    public static void main(String[] args) {
        String input = "var x inteiro = 10; se (x > 5) { x = 0; }";

        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);

        // Testando parse de PROGRAMA (Lista de Comandos)
        List<Comando> comandos = parser.parsePrograma();

        AstPrinter printer = new AstPrinter();

        System.out.println("--- AST (Comandos) ---");
        for (Comando cmd : comandos) {
            // O jeito certo: passar o 'printer' para o accept, não 'this'
            System.out.println(cmd.accept(printer));

            // OU, se você implementou o método helper 'print' no AstPrinter:
            // System.out.println(printer.print(cmd));
        }

        // Se quiser testar parseExpressao separadamente (agora que é publico):
        /*
        String exprInput = "10 + 20 * 3";
        Parser parserExpr = new Parser(new Scanner(exprInput).scanTokens());
        Expressao expr = parserExpr.parseExpressao();
        System.out.println("--- AST (Expressão) ---");
        System.out.println(expr.accept(printer));
        */
    }
}