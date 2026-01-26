package analisadorsintatico;
import scanner.Token;
import java.util.stream.Collectors;

/**
 * Classe auxiliar para imprimir a árvore de forma legível (estilo Lisp).
 * Exemplo: (+ 1 (* 2 3))
 */

// Agora implementa AMBOS os Visitors: Expressao e Comando
public class AstPrinter implements Expressao.Visitor<String>, Comando.Visitor<String> {

    // Método auxiliar para imprimir qualquer nó (Seja Comando ou Expressao)
    public String print(Object node) {
        if (node instanceof Expressao) {
            return ((Expressao) node).accept(this);
        } else if (node instanceof Comando) {
            return ((Comando) node).accept(this);
        } else {
            return "nulo";
        }
    }

    // ==========================================================
    // VISITANTES DE EXPRESSÃO (Já existiam, mantidos)
    // ==========================================================

    @Override
    public String visitBinaria(Expressao.Binaria expressao) {
        return parenthesize(expressao.operador.getLexema(), expressao.esquerda, expressao.direita);
    }

    @Override
    public String visitLogica(Expressao.Logica expressao) {
        return parenthesize(expressao.operador.getLexema(), expressao.esquerda, expressao.direita);
    }

    @Override
    public String visitUnaria(Expressao.Unaria expressao) {
        return parenthesize(expressao.operador.getLexema(), expressao.direita);
    }

    @Override
    public String visitLiteral(Expressao.Literal expressao) {
        if (expressao.valor == null) return "nulo";
        // Se for string, coloca aspas para visualizar melhor
        if (expressao.valor instanceof String) return "\"" + expressao.valor + "\"";
        return expressao.valor.toString();
    }

    @Override
    public String visitAgrupamento(Expressao.Agrupamento expressao) {
        return parenthesize("group", expressao.expressao);
    }

    @Override
    public String visitVariavelAcesso(Expressao.VariavelAcesso expressao) {
        return expressao.nome.getLexema();
    }

    // ==========================================================
    // VISITANTES DE COMANDO (Novos! Adicione estes)
    // ==========================================================

    @Override
    public String visitBloco(Comando.Bloco bloco) {
        StringBuilder sb = new StringBuilder();
        sb.append("(bloco");
        for (Comando cmd : bloco.comandos) {
            sb.append(" ").append(cmd.accept(this));
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public String visitDeclaracao(Comando.Declaracao decl) {
        String init = (decl.inicializador != null) ? " " + decl.inicializador.accept(this) : "";
        return "(var " + decl.nome.getLexema() + init + ")";
    }

    @Override
    public String visitAtribuicao(Comando.Atribuicao atri) {
        return "(atrib " + atri.nome.getLexema() + " " + atri.valor.accept(this) + ")";
    }

    @Override
    public String visitSe(Comando.Se comando) {
        String elseBranch = (comando.ramoElse != null) ? " " + comando.ramoElse.accept(this) : "";
        return "(se " + comando.condicao.accept(this) + " " + comando.ramoThen.accept(this) + elseBranch + ")";
    }

    @Override
    public String visitPara(Comando.Para comando) {
        // Exibe de forma simplificada: (para condicao corpo) ou (para init cond inc corpo)
        String init = (comando.inicializacao != null) ? comando.inicializacao.accept(this) : "nil";
        String cond = (comando.condicao != null) ? comando.condicao.accept(this) : "true";
        String inc = (comando.incremento != null) ? comando.incremento.accept(this) : "nil";

        return "(para " + init + " ; " + cond + " ; " + inc + " " + comando.corpo.accept(this) + ")";
    }

    @Override
    public String visitImprimir(Comando.Imprimir comando) {
        // Concatena os argumentos
        String args = comando.expressoes.stream()
                .map(e -> e.accept(this))
                .collect(Collectors.joining(" "));
        return "(imprimir " + args + ")";
    }

    @Override
    public String visitLer(Comando.Ler comando) {
        String vars = comando.variaveis.stream()
                .map(t -> t.getLexema())
                .collect(Collectors.joining(" "));
        return "(ler " + vars + ")";
    }

    // ==========================================================
    // HELPER
    // ==========================================================

    private String parenthesize(String name, Expressao... exprs) {
        StringBuilder builder = new StringBuilder();
        builder.append("(").append(name);
        for (Expressao expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");
        return builder.toString();
    }
}