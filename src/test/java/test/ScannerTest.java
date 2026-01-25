package scanner;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int pos = 0;
    private int linha = 1;
    private int coluna = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            char c = advance();

            // Ignora espaços e quebras de linha
            if (Character.isWhitespace(c)) {
                if (c == '\n') { linha++; coluna = 1; }
                continue;
            }

            // Identificadores ou palavras reservadas
            if (Character.isLetter(c) || c == '_') {
                String lexema = readWhile(Character::isLetterOrDigit);
                addToken(resolvePalavraReservada(lexema), lexema);
                continue;
            }

            // Números
            if (Character.isDigit(c)) {
                String lexema = readNumber(c);
                if (lexema.contains(".")) {
                    addToken(TokenType.LITERAL_REAL, lexema);
                } else {
                    addToken(TokenType.LITERAL_INTEIRO, lexema);
                }
                continue;
            }

            // Strings
            if (c == '"') {
                String lexema = readString();
                addToken(TokenType.LITERAL_TEXTO, lexema);
                continue;
            }

            // Operadores e delimitadores (exemplo básico)
            switch (c) {
                case '+': addToken(TokenType.MAIS, "+"); break;
                case '-': addToken(TokenType.MENOS, "-"); break;
                case '*': addToken(TokenType.MULTIPLICACAO, "*"); break;
                case '/': addToken(TokenType.DIVISAO, "/"); break;
                case '=': addToken(TokenType.ATRIBUICAO, "="); break;
                case '(': addToken(TokenType.ABRE_PARENTESE, "("); break;
                case ')': addToken(TokenType.FECHA_PARENTESE, ")"); break;
                case '{': addToken(TokenType.ABRE_CHAVE, "{"); break;
                case '}': addToken(TokenType.FECHA_CHAVE, "}"); break;
                case ';': addToken(TokenType.PONTO_VIRGULA, ";"); break;
                case ',': addToken(TokenType.VIRGULA, ","); break;
                default: addToken(TokenType.IDENTIFICADOR, String.valueOf(c));
            }
        }

        tokens.add(new Token(TokenType.EOF, "", linha, coluna));
        return tokens;
    }

    private boolean isAtEnd() {
        return pos >= source.length();
    }

    private char advance() {
        char c = source.charAt(pos++);
        coluna++;
        return c;
    }

    private void addToken(TokenType tipo, String lexema) {
        tokens.add(new Token(tipo, lexema, linha, coluna));
    }

    // Métodos auxiliares (readWhile, readNumber, readString, resolvePalavraReservada)
    // devem ser implementados para dar suporte completo aos testes.
}
