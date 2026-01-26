package analisadorsintatico;

import java.util.List;
import scanner.Token;

public abstract class Comando {

    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitBloco(Bloco comando);
        R visitDeclaracao(Declaracao comando);
        R visitAtribuicao(Atribuicao comando);
        R visitSe(Se comando);
        R visitPara(Para comando);
        R visitImprimir(Imprimir comando);
        R visitLer(Ler comando);
    }

    // --- Subclasses (Nós da Árvore de Comandos) ---

    // { comando1; comando2; ... }
    public static class Bloco extends Comando {
        public final List<Comando> comandos;

        public Bloco(List<Comando> comandos) {
            this.comandos = comandos;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBloco(this);
        }
    }

    // var x inteiro = 10;
    public static class Declaracao extends Comando {
        public final Token nome;
        public final Token tipo; // INTEIRO, REAL ou TEXTO
        public final Expressao inicializador; // Pode ser null se não tiver = ...

        public Declaracao(Token nome, Token tipo, Expressao inicializador) {
            this.nome = nome;
            this.tipo = tipo;
            this.inicializador = inicializador;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitDeclaracao(this);
        }
    }

    // x = 20;
    public static class Atribuicao extends Comando {
        public final Token nome;
        public final Expressao valor;

        public Atribuicao(Token nome, Expressao valor) {
            this.nome = nome;
            this.valor = valor;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAtribuicao(this);
        }
    }

    // se (cond) { ... } senao { ... }
    public static class Se extends Comando {
        public final Expressao condicao;
        public final Comando ramoThen; // Sempre será um Bloco pela gramática
        public final Comando ramoElse; // Pode ser null

        public Se(Expressao condicao, Comando ramoThen, Comando ramoElse) {
            this.condicao = condicao;
            this.ramoThen = ramoThen;
            this.ramoElse = ramoElse;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSe(this);
        }
    }

    // para (init; cond; inc) { ... }
    // Obs: No estilo 'while', init e inc serão null
    public static class Para extends Comando {
        public final Comando inicializacao; // Pode ser null
        public final Expressao condicao;    // Pode ser null (loop infinito)
        public final Comando incremento;    // Pode ser null
        public final Comando corpo;

        public Para(Comando inicializacao, Expressao condicao, Comando incremento, Comando corpo) {
            this.inicializacao = inicializacao;
            this.condicao = condicao;
            this.incremento = incremento;
            this.corpo = corpo;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPara(this);
        }
    }

    // imprimir(a, b, "texto")
    public static class Imprimir extends Comando {
        public final List<Expressao> expressoes;

        public Imprimir(List<Expressao> expressoes) {
            this.expressoes = expressoes;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitImprimir(this);
        }
    }

    // ler(a, b)
    public static class Ler extends Comando {
        // Guardamos Tokens, pois só podemos ler para variáveis (IDs), não expressões
        public final List<Token> variaveis;

        public Ler(List<Token> variaveis) {
            this.variaveis = variaveis;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLer(this);
        }
    }
}