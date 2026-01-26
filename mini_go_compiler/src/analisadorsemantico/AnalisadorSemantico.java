package analisadorsemantico;

import analisadorsintatico.Comando;
import analisadorsintatico.Expressao;
import scanner.Token;
import scanner.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Analisador Semântico para a linguagem Mini-Go.
 * 
 * Responsabilidades:
 * 1. Verificar se variáveis foram declaradas antes do uso
 * 2. Verificar compatibilidade de tipos em operações
 * 3. Validar tipos em condições (devem ser booleanas)
 * 4. Detectar redeclaração de variáveis
 * 5. Gerenciar escopos de blocos
 * 
 * Implementa o padrão Visitor para percorrer a AST.
 */
public class AnalisadorSemantico implements Expressao.Visitor<Tipo>, Comando.Visitor<Void> {
    
    private final TabelaSimbolos tabela;
    private final List<ErroSemantico> erros;
    
    /**
     * Construtor do analisador semântico.
     */
    public AnalisadorSemantico() {
        this.tabela = new TabelaSimbolos();
        this.erros = new ArrayList<>();
    }
    
    /**
     * Analisa uma lista de comandos (programa completo).
     * 
     * @param comandos lista de comandos da AST
     * @return true se não houver erros semânticos
     */
    public boolean analisar(List<Comando> comandos) {
        erros.clear();
        
        if (comandos == null || comandos.isEmpty()) {
            return true;
        }
        
        for (Comando comando : comandos) {
            try {
                comando.accept(this);
            } catch (Exception e) {
                // Captura erros para não parar a análise
                System.err.println("Erro na análise semântica: " + e.getMessage());
            }
        }
        
        return erros.isEmpty();
    }
    
    /**
     * Retorna a lista de erros semânticos encontrados.
     */
    public List<ErroSemantico> getErros() {
        return new ArrayList<>(erros);
    }
    
    /**
     * Imprime todos os erros encontrados.
     */
    public void imprimirErros() {
        if (erros.isEmpty()) {
            System.out.println("Nenhum erro semântico encontrado.");
            return;
        }
        
        System.err.println("\n Erros Semânticos encontrados (" + erros.size() + "):");
        for (ErroSemantico erro : erros) {
            System.err.println("  " + erro.getMensagemFormatada());
        }
    }
    
    // ======================== VISITANTES DE COMANDOS =========================
    
    @Override
    public Void visitBloco(Comando.Bloco bloco) {
        // Entra em novo escopo
        tabela.entrarEscopo();
        
        // Analisa cada comando do bloco
        for (Comando comando : bloco.comandos) {
            comando.accept(this);
        }
        
        // Sai do escopo
        tabela.sairEscopo();
        return null;
    }
    
    @Override
    public Void visitDeclaracao(Comando.Declaracao decl) {
        // Converte TokenType para Tipo
        Tipo tipo = converterTokenParaTipo(decl.tipo);
        
        // Verifica se já existe no escopo atual
        if (!tabela.declarar(decl.nome.getLexema(), tipo, decl.inicializador != null)) {
            registrarErro(decl.nome, 
                        ErroSemantico.TipoErro.VARIAVEL_JA_DECLARADA,
                        "Variável '" + decl.nome.getLexema() + "' já foi declarada neste escopo.");
        }
        
        // Se tem inicializador, verifica compatibilidade de tipos
        if (decl.inicializador != null) {
            Tipo tipoExpr = decl.inicializador.accept(this);
            
            if (!tipo.isCompativelCom(tipoExpr)) {
                registrarErro(decl.nome,
                            ErroSemantico.TipoErro.TIPO_INVALIDO_ATRIBUICAO,
                            "Não é possível atribuir " + tipoExpr + " a uma variável do tipo " + tipo + ".");
            }
        }
        
        return null;
    }
    
    @Override
    public Void visitAtribuicao(Comando.Atribuicao atrib) {
        // Verifica se a variável foi declarada
        Simbolo simbolo = tabela.buscar(atrib.nome.getLexema());
        
        if (simbolo == null) {
            registrarErro(atrib.nome,
                        ErroSemantico.TipoErro.VARIAVEL_NAO_DECLARADA,
                        "Variável '" + atrib.nome.getLexema() + "' não foi declarada.");
            return null;
        }
        
        // Verifica compatibilidade de tipos
        Tipo tipoValor = atrib.valor.accept(this);
        
        if (!simbolo.getTipo().isCompativelCom(tipoValor)) {
            registrarErro(atrib.nome,
                        ErroSemantico.TipoErro.TIPO_INVALIDO_ATRIBUICAO,
                        "Não é possível atribuir " + tipoValor + " a uma variável do tipo " + simbolo.getTipo() + ".");
        }
        
        // Marca como inicializada
        tabela.marcarInicializada(atrib.nome.getLexema());
        
        return null;
    }
    
    @Override
    public Void visitSe(Comando.Se comando) {
        // Verifica tipo da condição (deve ser booleana)
    	Tipo tipoCondicao = comando.condicao.accept(this);
        
        if (tipoCondicao != Tipo.BOOLEANO && tipoCondicao != Tipo.ERRO) {
            registrarErro(null,
                        ErroSemantico.TipoErro.TIPO_INVALIDO_CONDICAO,
                        "Condição do 'se' deve ser do tipo booleano, mas é " + tipoCondicao + ".");
        }
        
        // Analisa ramo then
        comando.ramoThen.accept(this);
        
        // Analisa ramo else (se existir)
        if (comando.ramoElse != null) {
            comando.ramoElse.accept(this);
        }
        
        return null;
    }
    
    @Override
    public Void visitPara(Comando.Para comando) {
    	
    	// Garante escopo isolado para o laço todo
    	tabela.entrarEscopo();
    	
        // Para clássico: cria escopo para a inicialização
        if (comando.inicializacao != null) {
            comando.inicializacao.accept(this);
        }
        
        // Verifica tipo da condição (deve ser booleana, se existir)
        if (comando.condicao != null) {
            Tipo tipoCondicao = comando.condicao.accept(this);
            
            if (tipoCondicao != Tipo.BOOLEANO && tipoCondicao != Tipo.ERRO) {
                registrarErro(null,
                            ErroSemantico.TipoErro.TIPO_INVALIDO_CONDICAO,
                            "Condição do 'para' deve ser do tipo booleano, mas é " + tipoCondicao + ".");
            }
        }
        
        // Analisa incremento (se existir)
        if (comando.incremento != null) {
            comando.incremento.accept(this);
        }
        
        // Analisa corpo
        comando.corpo.accept(this);
        
        
        // Fecha o escopo
        tabela.sairEscopo();
        
        return null;
    }
    
    @Override
    public Void visitImprimir(Comando.Imprimir comando) {
        // Apenas verifica os tipos das expressões (qualquer tipo pode ser impresso)
        for (Expressao expr : comando.expressoes) {
            expr.accept(this);
        }
        return null;
    }
    
    @Override
    public Void visitLer(Comando.Ler comando) {
        // Verifica se todas as variáveis foram declaradas
        for (Token var : comando.variaveis) {
            Simbolo simbolo = tabela.buscar(var.getLexema());
            
            if (simbolo == null) {
                registrarErro(var,
                            ErroSemantico.TipoErro.VARIAVEL_NAO_DECLARADA,
                            "Variável '" + var.getLexema() + "' não foi declarada.");
            } else {
                // Marca como inicializada (ler inicializa a variável)
                tabela.marcarInicializada(var.getLexema());
            }
        }
        return null;
    }
    
    // ====================== VISITANTES DE EXPRESSÕES ========================
    
    @Override
    public Tipo visitBinaria(Expressao.Binaria expressao) {
        Tipo esquerda = expressao.esquerda.accept(this);
        Tipo direita = expressao.direita.accept(this);
        
        TokenType op = expressao.operador.getTipo();
        
        // Operadores aritméticos: +, -, *, /
        if (op == TokenType.MAIS || op == TokenType.MENOS || 
            op == TokenType.MULTIPLICACAO || op == TokenType.DIVISAO) {
            
            if (!esquerda.isNumerico() || !direita.isNumerico()) {
                registrarErro(expressao.operador,
                            ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO,
                            "Operador '" + expressao.operador.getLexema() + 
                            "' requer operandos numéricos, mas recebeu " + esquerda + " e " + direita + ".");
                return Tipo.ERRO;
            }
            
            // Retorna o tipo promovido (REAL se algum for REAL)
            return Tipo.promover(esquerda, direita);
        }
        
        // Operadores relacionais: <, <=, >, >=
        if (op == TokenType.MENOR || op == TokenType.MENOR_IGUAL || 
            op == TokenType.MAIOR || op == TokenType.MAIOR_IGUAL) {
            
            if (!esquerda.isNumerico() || !direita.isNumerico()) {
                registrarErro(expressao.operador,
                            ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO,
                            "Operador '" + expressao.operador.getLexema() + 
                            "' requer operandos numéricos, mas recebeu " + esquerda + " e " + direita + ".");
                return Tipo.ERRO;
            }
            
            return Tipo.BOOLEANO;
        }
        
        // Operadores de igualdade: ==, !=
        if (op == TokenType.IGUAL_IGUAL || op == TokenType.DIFERENTE) {
            if (!esquerda.isCompativelCom(direita)) {
                registrarErro(expressao.operador,
                            ErroSemantico.TipoErro.INCOMPATIBILIDADE_TIPOS,
                            "Não é possível comparar " + esquerda + " com " + direita + ".");
                return Tipo.ERRO;
            }
            
            return Tipo.BOOLEANO;
        }
        
        return Tipo.ERRO;
    }
    
    @Override
    public Tipo visitLogica(Expressao.Logica expressao) {
        Tipo esquerda = expressao.esquerda.accept(this);
        Tipo direita = expressao.direita.accept(this);
        
        Boolean erro = false;
        
        // Operadores lógicos: &&, ||
        if (esquerda != Tipo.BOOLEANO && esquerda != Tipo.ERRO) {
            registrarErro(expressao.operador,
                        ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO,
                        "Operador '" + expressao.operador.getLexema() + 
                        "' requer operandos booleanos, mas o lado esquerdo é " + esquerda + ".");
            erro = true;
        }
        
        if (direita != Tipo.BOOLEANO && direita != Tipo.ERRO) {
            registrarErro(expressao.operador,
                        ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO,
                        "Operador '" + expressao.operador.getLexema() + 
                        "' requer operandos booleanos, mas o lado direito é " + direita + ".");
            erro = true;
        }
        
        return erro ? Tipo.ERRO : Tipo.BOOLEANO;
    }
    
    @Override
    public Tipo visitUnaria(Expressao.Unaria expressao) {
        Tipo tipo = expressao.direita.accept(this);
        TokenType op = expressao.operador.getTipo();
        
        // Operador de negação lógica: !
        if (op == TokenType.NEGACAO) {
            if (tipo != Tipo.BOOLEANO && tipo != Tipo.ERRO) {
                registrarErro(expressao.operador,
                            ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO,
                            "Operador '!' requer operando booleano, mas recebeu " + tipo + ".");
                return Tipo.ERRO;
            }
            return Tipo.BOOLEANO;
        }
        
        // Operador de negação aritmética: -
        if (op == TokenType.MENOS) {
            if (!tipo.isNumerico() && tipo != Tipo.ERRO) {
                registrarErro(expressao.operador,
                            ErroSemantico.TipoErro.TIPO_INVALIDO_OPERACAO,
                            "Operador '-' (unário) requer operando numérico, mas recebeu " + tipo + ".");
                return Tipo.ERRO;
            }
            return tipo;
        }
        
        return Tipo.ERRO;
    }
    
    @Override
    public Tipo visitLiteral(Expressao.Literal expressao) {
        Object valor = expressao.valor;
        
        if (valor == null) return Tipo.NULO;
        if (valor instanceof Integer) return Tipo.INTEIRO;
        if (valor instanceof Double) return Tipo.REAL;
        if (valor instanceof String) return Tipo.TEXTO;
        if (valor instanceof Boolean) return Tipo.BOOLEANO;
        
        return Tipo.ERRO;
    }
    
    @Override
    public Tipo visitAgrupamento(Expressao.Agrupamento expressao) {
        return expressao.expressao.accept(this);
    }
    
    @Override
    public Tipo visitVariavelAcesso(Expressao.VariavelAcesso expressao) {
        Simbolo simbolo = tabela.buscar(expressao.nome.getLexema());
        
        if (simbolo == null) {
            registrarErro(expressao.nome,
                        ErroSemantico.TipoErro.VARIAVEL_NAO_DECLARADA,
                        "Variável '" + expressao.nome.getLexema() + "' não foi declarada.");
            return Tipo.ERRO;
        }
        
        // Usar variável não inicializada não é erro fatal!
        if (!simbolo.isInicializada()) {
            System.err.println("Aviso: Variável '" + expressao.nome.getLexema() + 
                             "' pode estar sendo usada sem ter sido inicializada.");
        }
        
        return simbolo.getTipo();
    }
    
    // =========================== MÉTODOS AUXILIARES =========================
    
    /**
     * Converte TokenType de tipo para o enum Tipo.
     */
    private Tipo converterTokenParaTipo(Token token) {
        switch (token.getTipo()) {
            case INTEIRO: return Tipo.INTEIRO;
            case REAL: return Tipo.REAL;
            case TEXTO: return Tipo.TEXTO;
            default:
                System.err.println("Tipo desconhecido: " + token.getLexema());
                return Tipo.ERRO;
        }
    }
    
    /**
     * Registra um erro semântico na lista.
     */
    private void registrarErro(Token token, ErroSemantico.TipoErro tipo, String mensagem) {
        // Se token for null, usa um token dummy para evitar NullPointerException
        if (token == null) {
            token = new Token(TokenType.ERRO, "", 0, 0);
        }
        erros.add(new ErroSemantico(token, tipo, mensagem));
    }
    
    /**
     * Retorna a tabela de símbolos (útil para debug).
     */
    public TabelaSimbolos getTabelaSimbolos() {
        return tabela;
    }
}