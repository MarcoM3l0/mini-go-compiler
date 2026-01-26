package analisadorsintatico;

import scanner.Token;

/**
 * Classe base para todas as expressões da linguagem.
 */
public abstract class Expressao {

    // Interface para o padrão Visitor (útil futuramente para interpretar ou gerar código)
    public abstract <R> R accept(Visitor<R> visitor);

    public interface Visitor<R> {
        R visitBinaria(Binaria expressao);
        R visitUnaria(Unaria expressao);
        R visitLiteral(Literal expressao);
        R visitLogica(Logica expressao); // Para && e ||
        R visitAgrupamento(Agrupamento expressao);
        R visitVariavelAcesso(VariavelAcesso expressao);
    }

    // --- Subclasses (Nós da Árvore) ---

    // Ex: a + b, a > b, a == b
    public static class Binaria extends Expressao {
        public final Expressao esquerda;
        public final Token operador;
        public final Expressao direita;

        public Binaria(Expressao esquerda, Token operador, Expressao direita) {
            this.esquerda = esquerda;
            this.operador = operador;
            this.direita = direita;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaria(this);
        }
    }

    // Ex: a && b, a || b
    public static class Logica extends Expressao {
        public final Expressao esquerda;
        public final Token operador;
        public final Expressao direita;

        public Logica(Expressao esquerda, Token operador, Expressao direita) {
            this.esquerda = esquerda;
            this.operador = operador;
            this.direita = direita;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogica(this);
        }
    }

    // Ex: -a, !a
    public static class Unaria extends Expressao {
        public final Token operador;
        public final Expressao direita;

        public Unaria(Token operador, Expressao direita) {
            this.operador = operador;
            this.direita = direita;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaria(this);
        }
    }

    // Ex: 10, 3.14, "texto"
    public static class Literal extends Expressao {
        public final Object valor;

        public Literal(Object valor) {
            this.valor = valor;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteral(this);
        }
    }

    // Ex: ( a + b )
    public static class Agrupamento extends Expressao {
        public final Expressao expressao;

        public Agrupamento(Expressao expressao) {
            this.expressao = expressao;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAgrupamento(this);
        }
    }

    // Ex: x (uso de variável)
    public static class VariavelAcesso extends Expressao {
        public final Token nome;

        public VariavelAcesso(Token nome) {
            this.nome = nome;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariavelAcesso(this);
        }
    }
}