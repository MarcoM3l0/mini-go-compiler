package parser;

import java.util.ArrayList;
import java.util.List;

import analisadorsintatico.Comando;
import analisadorsintatico.Expressao;
import scanner.Token;
import scanner.TokenType;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // ========================================================================
    //                                PROGRAMA
    // ========================================================================

    /**
     * Ponto de entrada do Parser.
     * <programa> ::= <lista_comandos>
     */
    public List<Comando> parsePrograma() {
        List<Comando> comandos = new ArrayList<>();

        while (!isAtEnd()) {
            try {
                Comando cmd = parseComando();
                if (cmd != null) {
                    comandos.add(cmd);
                }
            } catch (ParseError e) {
                // Em um compilador real, sincronizaríamos aqui para continuar parseando
                // Por enquanto, paramos no primeiro erro.
                return null;
            }
        }

        return comandos;
    }

    // ========================================================================
    //                                COMANDOS
    // ========================================================================

    /**
     * <comando> ::= <comando_simples> ";" | <comando_se> | <comando_para> | <bloco>
     */
    private Comando parseComando() {
        // 1. Bloco
        if (check(TokenType.ABRE_CHAVE)) {
            return parseBloco();
        }

        // 2. Comandos de Controle de Fluxo (sem ponto e vírgula final)
        if (check(TokenType.SE)) {
            return parseSe();
        }
        if (check(TokenType.PARA)) {
            return parsePara();
        }

        // 3. Comandos Simples (exigem ponto e vírgula)
        Comando cmd = parseComandoSimples();
        consume(TokenType.PONTO_VIRGULA, "Esperado ';' após o comando.");
        return cmd;
    }

    /**
     * Identifica qual comando simples processar.
     */
    private Comando parseComandoSimples() {
        if (check(TokenType.VAR)) {
            return parseDeclaracao();
        }
        if (check(TokenType.IMPRIMIR)) {
            return parseImprimir();
        }
        if (check(TokenType.LER)) {
            return parseLer();
        }
        if (check(TokenType.IDENTIFICADOR)) {
            return parseAtribuicao();
        }

        throw error(peek(), "Comando inválido ou não reconhecido.");
    }

    // <bloco> ::= "{" <lista_comandos> "}"
    private Comando parseBloco() {
        consume(TokenType.ABRE_CHAVE, "Esperado '{' para iniciar o bloco.");
        List<Comando> comandos = new ArrayList<>();

        while (!check(TokenType.FECHA_CHAVE) && !isAtEnd()) {
            comandos.add(parseComando());
        }

        consume(TokenType.FECHA_CHAVE, "Esperado '}' para fechar o bloco.");
        return new Comando.Bloco(comandos);
    }

    // <declaracao> ::= "var" <id> <tipo> ( "=" <expr> )?
    private Comando parseDeclaracao() {
        consume(TokenType.VAR, null);
        Token nome = consume(TokenType.IDENTIFICADOR, "Esperado nome da variável.");

        // Validar tipo (inteiro, real, texto)
        if (!check(TokenType.INTEIRO) && !check(TokenType.REAL) && !check(TokenType.TEXTO)) {
            throw error(peek(), "Tipo da variável esperado (inteiro, real, texto).");
        }
        Token tipo = advance(); // Consome o tipo

        Expressao inicializador = null;
        if (match(TokenType.ATRIBUICAO)) {
            inicializador = parseExpressao();
        }

        return new Comando.Declaracao(nome, tipo, inicializador);
    }

    // <atribuicao> ::= <id> "=" <expr>
    private Comando parseAtribuicao() {
        Token nome = consume(TokenType.IDENTIFICADOR, "Esperado identificador.");
        consume(TokenType.ATRIBUICAO, "Esperado '=' após identificador.");
        Expressao valor = parseExpressao();
        return new Comando.Atribuicao(nome, valor);
    }

    // <comando_se> ::= "se" <expressao> <bloco> ("senao" <bloco>)?
    private Comando parseSe() {
        consume(TokenType.SE, null);
        Expressao condicao = parseExpressao(); // A gramática não obriga parênteses, mas suporta se a expressão tiver

        // Verifica se vem um bloco
        if (!check(TokenType.ABRE_CHAVE)) {
            throw error(peek(), "Esperado '{' após condição do 'se'.");
        }
        Comando thenBranch = parseBloco();

        Comando elseBranch = null;
        if (match(TokenType.SENAO)) {
            if (!check(TokenType.ABRE_CHAVE)) {
                throw error(peek(), "Esperado '{' após 'senao'.");
            }
            elseBranch = parseBloco();
        }

        return new Comando.Se(condicao, thenBranch, elseBranch);
    }

    /**
     * <comando_para> pode ser:
     * 1. While-style: para <expressao> <bloco>
     * 2. Classic:     para <init>; <cond>; <inc> <bloco>
     */
    private Comando parsePara() {
        consume(TokenType.PARA, null);

        // Estratégia: Verificar o que vem a seguir para decidir o tipo de For.
        // Se for 'var' ou ';' ou (ID seguido de '='), é o estilo Clássico.
        // Caso contrário, assumimos que é uma Expressão (estilo While).

        boolean isClassico = false;

        if (check(TokenType.VAR) || check(TokenType.PONTO_VIRGULA)) {
            isClassico = true;
        } else if (check(TokenType.IDENTIFICADOR)) {
            // Lookahead para ver se é atribuição (id = ...)
            if (peekNext().getTipo() == TokenType.ATRIBUICAO) {
                isClassico = true;
            }
        }

        if (isClassico) {
            // Estilo Clássico: para init; cond; inc { }
            Comando inicializacao = null;
            if (!check(TokenType.PONTO_VIRGULA)) {
                // Pode ser declaração ou atribuição
                if (check(TokenType.VAR)) {
                    inicializacao = parseDeclaracao();
                } else {
                    inicializacao = parseAtribuicao();
                }
            }
            consume(TokenType.PONTO_VIRGULA, "Esperado ';' após inicialização do para.");

            Expressao condicao = null;
            if (!check(TokenType.PONTO_VIRGULA)) {
                condicao = parseExpressao();
            }
            consume(TokenType.PONTO_VIRGULA, "Esperado ';' após condição do para.");

            Comando incremento = null;
            if (!check(TokenType.ABRE_CHAVE)) {
                incremento = parseAtribuicao(); // Gramática define incremento como atribuição simples
            }

            Comando corpo = parseBloco();

            return new Comando.Para(inicializacao, condicao, incremento, corpo);

        } else {
            // Estilo While: para condicao { }
            Expressao condicao = parseExpressao();
            Comando corpo = parseBloco();

            // Retorna um Para com init e inc nulos
            return new Comando.Para(null, condicao, null, corpo);
        }
    }

    // <comando_imprimir> ::= "imprimir" "(" <lista_expr> ")"
    private Comando parseImprimir() {
        consume(TokenType.IMPRIMIR, null);
        consume(TokenType.ABRE_PARENTESE, "Esperado '(' após imprimir.");

        List<Expressao> args = new ArrayList<>();
        if (!check(TokenType.FECHA_PARENTESE)) {
            do {
                args.add(parseExpressao());
            } while (match(TokenType.VIRGULA));
        }

        consume(TokenType.FECHA_PARENTESE, "Esperado ')' após argumentos.");
        return new Comando.Imprimir(args);
    }

    // <comando_ler> ::= "ler" "(" <lista_ids> ")"
    private Comando parseLer() {
        consume(TokenType.LER, null);
        consume(TokenType.ABRE_PARENTESE, "Esperado '(' após ler.");

        List<Token> vars = new ArrayList<>();
        if (!check(TokenType.FECHA_PARENTESE)) {
            do {
                vars.add(consume(TokenType.IDENTIFICADOR, "Esperado identificador no ler."));
            } while (match(TokenType.VIRGULA));
        }

        consume(TokenType.FECHA_PARENTESE, "Esperado ')' após variáveis.");
        return new Comando.Ler(vars);
    }

    // ========================================================================
    //                                EXPRESSÕES
    // ========================================================================
    // (Lógica idêntica ao passo anterior, apenas repetida para completude)

    public Expressao parseExpressao() {
        return ouLogico();
    }

    public Expressao ouLogico() {
        Expressao expr = eLogico();
        while (match(TokenType.OU_LOGICO)) {
            Token op = previous();
            Expressao right = eLogico();
            expr = new Expressao.Logica(expr, op, right);
        }
        return expr;
    }

    public Expressao eLogico() {
        Expressao expr = igualdade();
        while (match(TokenType.E_LOGICO)) {
            Token op = previous();
            Expressao right = igualdade();
            expr = new Expressao.Logica(expr, op, right);
        }
        return expr;
    }

    public Expressao igualdade() {
        Expressao expr = relacional();
        while (match(TokenType.DIFERENTE, TokenType.IGUAL_IGUAL)) {
            Token op = previous();
            Expressao right = relacional();
            expr = new Expressao.Binaria(expr, op, right);
        }
        return expr;
    }

    public Expressao relacional() {
        Expressao expr = adicao();
        while (match(TokenType.MAIOR, TokenType.MAIOR_IGUAL, TokenType.MENOR, TokenType.MENOR_IGUAL)) {
            Token op = previous();
            Expressao right = adicao();
            expr = new Expressao.Binaria(expr, op, right);
        }
        return expr;
    }

    public Expressao adicao() {
        Expressao expr = multiplicacao();
        while (match(TokenType.MENOS, TokenType.MAIS)) {
            Token op = previous();
            Expressao right = multiplicacao();
            expr = new Expressao.Binaria(expr, op, right);
        }
        return expr;
    }

    public Expressao multiplicacao() {
        Expressao expr = unario();
        while (match(TokenType.DIVISAO, TokenType.MULTIPLICACAO)) {
            Token op = previous();
            Expressao right = unario();
            expr = new Expressao.Binaria(expr, op, right);
        }
        return expr;
    }

    public Expressao unario() {
        if (match(TokenType.NEGACAO, TokenType.MENOS)) {
            Token op = previous();
            Expressao right = unario();
            return new Expressao.Unaria(op, right);
        }
        return primario();
    }

    public Expressao primario() {
        if (match(TokenType.LITERAL_INTEIRO)) {
            return new Expressao.Literal(Integer.parseInt(previous().getLexema()));
        }
        if (match(TokenType.LITERAL_REAL)) {
            return new Expressao.Literal(Double.parseDouble(previous().getLexema()));
        }
        if (match(TokenType.LITERAL_TEXTO)) {
            String str = previous().getLexema();
            // Remove aspas: "texto" -> texto
            // Se o seu scanner já manda com aspas, é bom remover aqui:
            if (str.startsWith("\"") && str.endsWith("\"")) {
                str = str.substring(1, str.length() - 1);
            }
            return new Expressao.Literal(str);
        }
        if (match(TokenType.IDENTIFICADOR)) {
            return new Expressao.VariavelAcesso(previous());
        }
        if (match(TokenType.ABRE_PARENTESE)) {
            Expressao expr = parseExpressao();
            consume(TokenType.FECHA_PARENTESE, "Esperado ')' após expressão.");
            return new Expressao.Agrupamento(expr);
        }

        throw error(peek(), "Expressão esperada.");
    }

    // ========================================================================
    //                                AUXILIARES
    // ========================================================================

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getTipo() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getTipo() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    // Lookahead + 1 (Espiar o próximo do próximo)
    private Token peekNext() {
        if (current + 1 >= tokens.size()) return tokens.get(tokens.size() - 1); // Return EOF safe
        return tokens.get(current + 1);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        System.err.println("[Linha " + token.getLinha() + ", Coluna " + token.getColuna() + "] Erro em '" + token.getLexema() + "': " + message);
        return new ParseError();
    }

    private static class ParseError extends RuntimeException {}
}