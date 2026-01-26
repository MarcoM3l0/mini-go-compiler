package teste;
import java.util.List;
import analisadorsintatico.Comando;
import parser.Parser;
import scanner.Scanner;
import scanner.Token;

public class Main {
    public static void main(String[] args) {
        String input = """
            var x inteiro = 10;
            se x > 5 {
                imprimir(x);
            } senao {
                x = 0;
            }
            """;

        Scanner scanner = new Scanner(input);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        List<Comando> ast = parser.parsePrograma();

        if (ast != null) {
            System.out.println("Parse realizado com sucesso! " + ast.size() + " comandos identificados.");
            // Aqui você veria os objetos na memória.
            // Para ver bonito, precisaria atualizar o AstPrinter para Comandos.
        }
    }
}