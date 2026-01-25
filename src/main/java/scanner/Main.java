package scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner("a = b + c && d");
        Token token;
        while ((token = scanner.nextToken()).getType() != TokenType.EOF) {
            System.out.println(token);
        }
    }
}
