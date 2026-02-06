package codigointermediario;

/**
 * Representa uma instrução de Three-Address Code (TAC).
 * 
 * Formato geral: resultado = operando1 operador operando2
 * 
 * Exemplos:
 * - t1 = a + b       (operação binária)
 * - t2 = -a          (operação unária)
 * - x = t1           (atribuição/cópia)
 * - vai_para R1          (desvio incondicional)
 * - se t1 vai_para R2    (desvio condicional)
 * - R1:              (Rótulo)
 * - imprimir t1         (E/S)
 * - ler x           (E/S)
 */
public class TACInstrucoes {
	
	private final TACOperador operador;
    private final String resultado;      // Variável de destino ou rotulo
    private final String operando1;      // Primeiro operando
    private final String operando2;      // Segundo operando (pode ser null)
    
    /**
     * Tipos de operadores TAC.
     */
    public enum TACOperador {
    	// Operações aritméticas
    	SOMA("+"),             // resultado = op1 + op2
    	SUBTRACAO("-"),        // resultado = op1 - op2
    	MULTIPLICACAO("*"),    // resultado = op1 * op2
    	DIVISAO("/"),          // resultado = op1 / op2
    	INVERSAO("INVERSAO"),  // resultado = -op1
        
        // Operações lógicas/relacionais
    	IGUAL_IGUAL("=="),     // resultado = op1 == op2
    	DIFERENTE("!="),       // resultado = op1 != op2
    	MENOR("<"),            // resultado = op1 < op2
    	MENOR_IGUAL("<="),     // resultado = op1 <= op2
    	MAIOR(">"),            // resultado = op1 > op2
    	MAIOR_IGUAL(">="),     // resultado = op1 >= op2
    	E_LOGICO("&&"),        // resultado = op1 && op2
    	OU_LOGICO("||"),       // resultado = op1 || op2
    	NEGACAO("!"),           // resultado = !op1
        
        // Atribuição e cópia
    	ATRIBUICAO("="),        // resultado = op1
        
        // Controle de fluxo
    	ROTULO("ROTULO"),                 // resultado: (define um rótulo)
    	VAI_PARA("VAI_PARA"),             // vai_para resultado
    	SE_FALSO("SE_FALSO"),             // se_falso op1 vai_para resultado
    	SE_VERDADEIRO("SE_VERDADEIRO"),   // se_verdadeiro op1 vai_para resultado
        
    	// Entrada/Saída
    	IMPRIMIR("IMPRIMIR"),     // imprimir op1
    	LER("LER"),               // ler resultado
    	
    	// Chamadas e parâmetros 
    	PARAMETRO("PARAMETRO"), // parametro op1 
    	CHAMADA("CHAMADA"),     // resultado = chamada op1, op2 
    	RETORNAR("RETORNAR");   // retornar op1

    	
    	private final String simbolo;
        
        TACOperador(String simbolo) {
            this.simbolo = simbolo;
        }
        
        public String getSimbolo() {
            return simbolo;
        }
    	
    }
    
    /**
     * Construtor completo (operação binária).
     */
    public TACInstrucoes(TACOperador operador, String resultado, String operando1, String operando2) {
    	this.operador = operador;
        this.resultado = resultado;
        this.operando1 = operando1;
        this.operando2 = operando2;
    }
    
    /**
     * Construtor para operação unária ou atribuição.
     */
    public TACInstrucoes(TACOperador operador, String resultado, String operando1) {
    	this.operador = operador;
        this.resultado = resultado;
        this.operando1 = operando1;
        this.operando2 = null;   
    }
    
    /**
     * Construtor para rotulos e vai_para.
     */
    public TACInstrucoes(TACOperador operador, String resultado) {
    	this.operador = operador;
        this.resultado = resultado;
        this.operando1 = null;
        this.operando2 = null;
    }
    
    // Getters
    
    public TACOperador getOperador(){
        return this.operador;
    }

    public String getResultado(){
        return this.resultado;
    }

    public String getOperando1(){
        return this.operando1;
    }

    public String getOperando2(){
        return this.operando2;
    }
    
    /**
     * Verifica se a instrução é um rotulo.
     */
    public boolean isRotulo() {
    	return this.operador == TACOperador.ROTULO;
    }
    
    /**
     * Verifica se a instrução é um desvio (vai_para, se_false, se_verdadeiro).
     */
    public boolean isDesvio() {
    	return this.operador == TACOperador.VAI_PARA;
    }
    
    /**
     * Verifica se a instrução é uma operação aritmética.
     */
    public boolean isOperacaoAritmetica() {
    	if (this.operador == TACOperador.SOMA) { 
            return true; 
        } else if (this.operador == TACOperador.SUBTRACAO) { 
            return true; 

        } else if (this.operador == TACOperador.MULTIPLICACAO) { 
            return true; 

        } else if (this.operador == TACOperador.DIVISAO) { 
            return true; 

        } else if (this.operador == TACOperador.INVERSAO) { 
            return true; 

        } else { 
            return false; 

        }
    }
    
    /**
     * Converte a instrução para string legível.
     * 
     * Formatos:
     * - Binária: t1 = a + b
     * - Unária: t2 = -a
     * - Atribuição: x = t1
     * - Rotulo: R1:
     * - Vai_para: vai_para R1
     * - Condicional: se_falso t1 vai_para R2
     */
    @Override
    public String toString() {
        switch (operador) {
            case ROTULO:
                return resultado + ":";
            
            case VAI_PARA:
                return "vai_para " + resultado;
            
            case SE_FALSO:
                return "se_falso " + operando1 + " vai_para " + resultado;
            
            case SE_VERDADEIRO:
                return "se_verdadeiro " + operando1 + " vai_para " + resultado;
            
            case IMPRIMIR:
                return "imprimir " + operando1;
            
            case LER:
                return "ler " + resultado;
            
            case PARAMETRO:
                return "parametro " + operando1;
            
            case CHAMADA:
                if (resultado != null) {
                    return resultado + " = chamada " + operando1 + ", " + operando2;
                }
                return "call " + operando1 + ", " + operando2;
            
            case RETORNAR:
                if (operando1 != null) {
                    return "retornar " + operando1;
                }
                return "retornar";
            
            case ATRIBUICAO:
                return resultado + " = " + operando1;
            
            case INVERSAO:
            case NEGACAO:
                return resultado + " = " + operador.getSimbolo() + " " + operando1;
            
            default:
                // Operações binárias
                if (operando2 != null) {
                    return resultado + " = " + operando1 + " " + operador.getSimbolo() + " " + operando2;
                }
                return resultado + " = " + operando1;
        }
    }
    
    /**
     * Cria uma instrução de operação binária.
     */
    
    public static  TACInstrucoes binaria(TACOperador op, String resul, String op1, String op2) {
    	TACInstrucoes instrucao = new TACInstrucoes(op, resul, op1, op2); 
        return instrucao;
    }
    
    /**
     * Cria uma instrução de operação unária.
     */
    public static TACInstrucoes unario(TACOperador op, String resul, String operando) {
    	TACInstrucoes instrucao = new TACInstrucoes(op, resul, operando); 
        return instrucao;
    }
    
    /**
     * Cria uma instrução de atribuição.
     */
    public static TACInstrucoes  atribuicao(String resul, String fonte) {
    	TACInstrucoes instrucao = new TACInstrucoes(TACOperador.ATRIBUICAO,resul, fonte); 
        return instrucao;
    }
    
    /**
     * Cria uma instrução de rotulo.
     */
    public static TACInstrucoes rotulo(String nomeRotulo) {
    	TACInstrucoes instrucao = new TACInstrucoes(TACOperador.RETORNAR,nomeRotulo); 
        return instrucao;
    }
    
    /**
     * Cria uma instrução vai_para.
     */
    public static TACInstrucoes vaiPara(String nomeRotulo) {
    	TACInstrucoes instrucao = new TACInstrucoes(TACOperador.VAI_PARA, nomeRotulo); 
        return instrucao;
    }
    
    /**
     * Cria uma instrução condicional se_falso.
     */
    public static TACInstrucoes seFalso(String condicao, String nomeRotulo) {
    	TACInstrucoes instrucao = new TACInstrucoes(TACOperador.SE_FALSO, nomeRotulo, condicao); 
        return instrucao;
    }
    
    /**
     * Cria uma instrução condicional se_verdadeiro.
     */
    public static TACInstrucoes seVerdadeiro(String condicao, String nomeRotulo) {
    	TACInstrucoes instrucao = new TACInstrucoes(TACOperador.SE_VERDADEIRO, nomeRotulo, condicao); 
        return instrucao;
    }
    
    /**
     * Cria uma instrução imprimir.
     */
    public static TACInstrucoes imprimir(String valor) {
    	TACInstrucoes instrucao = new TACInstrucoes(TACOperador.IMPRIMIR, null, valor); 
        return instrucao;
    }
    
    /**
     * Cria uma instrução ler.
     */
    public static TACInstrucoes ler(String variavel) {
    	TACInstrucoes instrucao = new TACInstrucoes(TACOperador.LER, variavel); 
        return instrucao;
    }
    
}
