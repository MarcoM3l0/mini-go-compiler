package codigointermediario;

import analisadorsintatico.Comando;
import analisadorsintatico.Expressao;
import scanner.Token;
import scanner.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Gerador de Código Intermediário (Three-Address Code) para Mini-Go.
 * 
 * Traduz a AST para uma representação linear de instruções TAC.
 * Usa o padrão Visitor para percorrer a árvore.
 */
public class TACGerador implements Expressao.Visitor<String>, Comando.Visitor<Void> {
	
	private final List<TACInstrucoes> instrucoes;  // Lista de instruções geradas
    private int tempCount;                        // Contador de temporários
    private int rotuloCount;                       // Contador de rotulos
    
    /**
     * Construtor do gerador TAC.
     */
    public TACGerador() {
        this.instrucoes = new ArrayList<>();
        this.tempCount = 0;
        this.rotuloCount = 0;
    }
    
    /**
     * Gera código TAC para uma lista de comandos (programa completo).
     * 
     * @param comandos lista de comandos da AST
     * @return lista de instruções TAC geradas
     */
    public List<TACInstrucoes> gerar(List<Comando> comandos) {
    	instrucoes.clear();
        tempCount = 0;
        rotuloCount = 0;
        if (comandos == null) {
            return instrucoes;
        }
        if (comandos.isEmpty()){
            return instrucoes;
        }

        for(int i = 0; i < comandos.size(); i++){
            Comando comando = comandos.get(i);
            comando.accept(this);
        }

        List<TACInstrucoes> listaNova = new ArrayList<TACInstrucoes>();
        for(int i = 0; i< instrucoes.size(); i++){
            listaNova.add(instrucoes.get(i));
        }

        return listaNova;
    }
    
    /**
     * Gera um novo nome de temporário.
     * Ex: t0, t1, t2, ...
     */
    private String novoTemp() {
    	return "t" + (tempCount++);
    }
    
    /**
     * Gera um novo nome de rotulo.
     * Ex: R0, R1, R2, ...
     */
    private String novoRotulo() {
    	return "R" + (rotuloCount++);
    }
    
    /**
     * Adiciona uma instrução à lista.
     */
    private void emitir(TACInstrucoes intrucoe) {
    	instrucoes.add(intrucoe);
    }
    
    /**
     * Retorna as instruções geradas.
     */
    public List<TACInstrucoes> getInstrucoes() {
    	List<TACInstrucoes> lista = new ArrayList<TACInstrucoes>();

        for(int i = 0; i< instrucoes.size(); i++){
            lista.add(instrucoes.get(i));
        }

        return lista;
    }
    
    /**
     * Imprime o código TAC de forma legível.
     */
    public void imprimeCodigo() {
    	System.out.println("=== CÓDIGO INTERMEDIÁRIO (TAC) ===");
        for (int i = 0; i < instrucoes.size(); i++){
            System.out.println(i + ": " + instrucoes.get(i));
        }
        System.out.println("==================================");
    }
    
    // ======================== VISITANTES DE COMANDOS ========================
    
    @Override
    public Void visitBloco(Comando.Bloco bloco) {
        for(int i = 0; i < bloco.comandos.size(); i++){
            Comando comando = bloco.comandos.get(i);
            comando.accept(this);
        }

        return null;
    }
    
    @Override
    public Void visitDeclaracao(Comando.Declaracao decl) {
    	if (decl.inicializador != null){
            String temporario = decl.inicializador.accept(this);
            String nomeVariavel = decl.nome.getLexema();
            TACInstrucoes instrucao = TACInstrucoes.atribuicao(nomeVariavel, temporario);

            emitir(instrucao);
        }

        return null;
    }
    
    @Override
    public Void visitAtribuicao(Comando.Atribuicao atrib) {
    	String temporario = atrib.valor.accept(this);
        String nomeVariavel = atrib.nome.getLexema();
        TACInstrucoes instrucao = TACInstrucoes.atribuicao(nomeVariavel, temporario);

        emitir(instrucao);

        return null;
    }
    
    @Override
    public Void visitSe(Comando.Se comando) {
    	/*
         * Tradução de se-então-senao (if-then-else):
         * 
         *     temp = <condição>
         *     se_falso temp vai_para R_senao
         *     <código do then>
         *     vai_para R_fim
         * R_senao:
         *     <código do senao>
         * R_fim:
         */

        String condicaoTemporaria = comando.condicao.accept(this);

        String rotuloSeNao = novoRotulo();
        String rotuloFim = novoRotulo();

        TACInstrucoes instrucaoSeFalso = TACInstrucoes.seFalso(condicaoTemporaria, rotuloSeNao);

        emitir(instrucaoSeFalso);

        comando.ramoThen.accept(this);

        if (comando.ramoElse != null) {
            TACInstrucoes instrucaoVaiPara = TACInstrucoes.vaiPara(rotuloFim);
            emitir(instrucaoVaiPara);

            TACInstrucoes instrucaoRotuloSeNao = TACInstrucoes.rotulo(rotuloSeNao);
            emitir(instrucaoRotuloSeNao);

            comando.ramoElse.accept(this);

            TACInstrucoes instrucaoRotuloFim = TACInstrucoes.rotulo(rotuloFim);
            emitir(instrucaoRotuloFim);
        } else {
            TACInstrucoes instrucaoRotuloSeNao = TACInstrucoes.rotulo(rotuloSeNao);
            emitir(instrucaoRotuloSeNao);
        }

        return null;
    }
    
    @Override
    public Void visitPara(Comando.Para comando) {
    	/*
         * Tradução de Para clássico:
         * 
         *     <inicialização>
         * R_começar:
         *     temp = <condição>
         *     se_false temp vai_para R_fim
         *     <corpo>
         *     <incremento>
         *     vai_para R_começar
         * R_fim:
         * 
         * Tradução de Para estilo while:
         * 
         * R_começar:
         *     temp = <condição>
         *     se_false temp vai_para R_fim
         *     <corpo>
         *     vai_para R_começar
         * L_end:
         */

        if (comando.inicializacao != null) {
            comando.inicializacao.accept(this);
        }

        String rotuloComecar = novoRotulo();
        String rotuloFim = novoRotulo();

        TACInstrucoes instrucaoRotuloComecar = TACInstrucoes.rotulo(rotuloComecar);
        emitir(instrucaoRotuloComecar);

        if(comando.condicao != null) {
            String condicaoTemporaria = comando.condicao.accept(this);
            TACInstrucoes instrucaoSeFalso = TACInstrucoes.seFalso(condicaoTemporaria, rotuloFim);
            emitir(instrucaoSeFalso);
        }

        comando.corpo.accept(this);

        if(comando.incremento != null){
            comando.incremento.accept(this);
        }

        TACInstrucoes instrucaoVaiPara = TACInstrucoes.vaiPara(rotuloComecar);
        emitir(instrucaoVaiPara);

        TACInstrucoes instrucaoRotuloFim = TACInstrucoes.rotulo(rotuloFim);
        emitir(instrucaoRotuloFim);
    	
        return null;
    }
    
    @Override
    public Void visitImprimir(Comando.Imprimir comando) {
    	for(int i = 0; i < comando.expressoes.size(); i++){
            Expressao expressao = comando.expressoes.get(i);

            String temporario = expressao.accept(this);
            TACInstrucoes intrucaoImprimir = TACInstrucoes.imprimir(temporario);

            emitir(intrucaoImprimir);
        }

        return null;
    }

    @Override
    public Void visitLer(Comando.Ler comando) {
    	for(int i = 0; i < comando.variaveis.size(); i++){
            Token var = comando.variaveis.get(i);

            String nomeVariavel = var.getLexema();

            TACInstrucoes instrucaoLer = TACInstrucoes.ler(nomeVariavel);

            emitir(instrucaoLer);
        }

        return null;
    }
    

    // ======================== VISITANTES DE EXPRESSAO ========================


    @Override
    public String visitBinaria(Expressao.Binaria expressao) {
    	String esquerda = expressao.esquerda.accept(this);
        String direita = expressao.direita.accept(this);

        String temporario = novoTemp();

        TACInstrucoes.TACOperador tacOperador = mapOperador(expressao.operador.getTipo());

        emitir(TACInstrucoes.binaria(tacOperador, temporario, esquerda, direita));

        return temporario;
    }
    
    @Override
    public String visitLogica(Expressao.Logica expressao) {
    	/*
         * Operadores lógicos com avaliação em curto-circuito:
         * 
         * Para E (&&):
         *     temp1 = <esquerda>
         *     se_falso temp1 vai_para R_falso
         *     temp2 = <direita>
         *     se_falso temp2 vai_para R_falso
         *     result = 1
         *     vai_para R_falso
         * R_falso:
         *     result = 0
         * R_fim:
         * 
         * Para OU (||):
         *     temp1 = <esquerda>
         *     se_falso temp1 vai_para R_verdade
         *     temp2 = <direita>
         *     se_falso temp2 vai_para R_verdade
         *     result = 0
         *     vai_para R_fim
         * R_verdade:
         *     result = 1
         * R_fim:
         */
    	
    	String resultado = novoTemp();
        String rotuloVerdadeiro = novoRotulo();
        String rotuloFalso = novoRotulo();
        String rotuloFim = novoRotulo();

        if(expressao.operador.getTipo() == TokenType.E_LOGICO){
            String esquerda = expressao.esquerda.accept(this);
            emitir(TACInstrucoes.seFalso(esquerda, rotuloFalso));

            String direita = expressao.direita.accept(this);
            emitir(TACInstrucoes.seFalso(direita, rotuloFalso));

            emitir(TACInstrucoes.atribuicao(resultado, "1"));
            emitir(TACInstrucoes.vaiPara(rotuloFim));

            emitir(TACInstrucoes.rotulo(rotuloFalso));
            emitir(TACInstrucoes.atribuicao(resultado, "0"));

            emitir(TACInstrucoes.rotulo(rotuloFim));
        } else {
            String esquerda = expressao.esquerda.accept(this);
            emitir(TACInstrucoes.seVerdadeiro(esquerda, rotuloVerdadeiro));

            String direita = expressao.direita.accept(this);
            emitir(TACInstrucoes.seVerdadeiro(direita, rotuloVerdadeiro));

            emitir(TACInstrucoes.atribuicao(resultado, "0"));
            emitir(TACInstrucoes.vaiPara(rotuloFim));

            emitir(TACInstrucoes.rotulo(rotuloVerdadeiro));
            emitir(TACInstrucoes.atribuicao(resultado, "1"));

            emitir(TACInstrucoes.rotulo(rotuloFim));
        }

        return resultado;
    }
    
    @Override
    public String visitUnaria(Expressao.Unaria expressao) {
    	String operando = expressao.direita.accept(this);

        String temporaria = novoTemp();

        TACInstrucoes.TACOperador tacOperador = mapOperador(expressao.operador.getTipo());

        emitir(TACInstrucoes.unario(tacOperador, temporaria, operando));

        return temporaria;
    }
    
    @Override
    public String visitLiteral(Expressao.Literal expressao) {
    	if(expressao.valor == null){
            return "null";
        }

        if (expressao.valor instanceof String){
            return "\"" + expressao.valor + "\"";
        }

        return expressao.valor.toString();
    }
    
    @Override
    public String visitAgrupamento(Expressao.Agrupamento expressao) {
    	return expressao.expressao.accept(this);
    }
    
    @Override
    public String visitVariavelAcesso(Expressao.VariavelAcesso expressao) {
    	return expressao.nome.getLexema();
    }
    
    // =========================== MÉTODOS AUXILIARES =========================
    
    /**
     * Mapeia TokenType para TACOperator.
     */
    private TACInstrucoes.TACOperador mapOperador(TokenType tokenType) {
        switch (tokenType) {
            case MAIS: return TACInstrucoes.TACOperador.SOMA;
            case MENOS: return TACInstrucoes.TACOperador.SUBTRACAO;
            case MULTIPLICACAO: return TACInstrucoes.TACOperador.MULTIPLICACAO;
            case DIVISAO: return TACInstrucoes.TACOperador.DIVISAO;
            case IGUAL_IGUAL: return TACInstrucoes.TACOperador.IGUAL_IGUAL;
            case DIFERENTE: return TACInstrucoes.TACOperador.DIFERENTE;
            case MENOR: return TACInstrucoes.TACOperador.MENOR;
            case MENOR_IGUAL: return TACInstrucoes.TACOperador.MENOR_IGUAL;
            case MAIOR: return TACInstrucoes.TACOperador.MAIOR;
            case MAIOR_IGUAL: return TACInstrucoes.TACOperador.MAIOR_IGUAL;
            case E_LOGICO: return TACInstrucoes.TACOperador.E_LOGICO;
            case OU_LOGICO: return TACInstrucoes.TACOperador.OU_LOGICO;
            case NEGACAO: return TACInstrucoes.TACOperador.NEGACAO;
            default:
                throw new IllegalArgumentException("Operador não suportado: " + tokenType);
        }
    }
    
    /**
     * Reseta o gerador.
     */
    public void reset() {
    	instrucoes.clear();
        tempCount = 0;
        rotuloCount = 0;
    }
}

