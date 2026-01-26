package analisadorsemantico;

import scanner.Token;

/**
 * Representa um erro semântico encontrado durante a análise.
 * Armazena informações sobre o tipo de erro e sua localização.
 */
public class ErroSemantico {
    private final Token token;
    private final String mensagem;
    private final TipoErro tipo;
    
    /**
     * Tipos de erros semânticos possíveis.
     */
    public enum TipoErro {
        VARIAVEL_NAO_DECLARADA,      // Uso de variável não declarada
        VARIAVEL_JA_DECLARADA,        // Redeclaração de variável
        VARIAVEL_NAO_INICIALIZADA,    // Uso de variável não inicializada
        INCOMPATIBILIDADE_TIPOS,      // Tipos incompatíveis em operação
        TIPO_INVALIDO_OPERACAO,       // Tipo inválido para operação específica
        TIPO_INVALIDO_CONDICAO,       // Condição não booleana em if/for
        TIPO_INVALIDO_ATRIBUICAO      // Atribuição com tipos incompatíveis
    }
    
    /**
     * Construtor do ErroSemantico.
     */
    public ErroSemantico(Token token, TipoErro tipo, String mensagem) {
        this.token = token;
        this.tipo = tipo;
        this.mensagem = mensagem;
    }
    
    // Getters
    
    public Token getToken() {
        return token;
    }
    
    public String getMensagem() {
        return mensagem;
    }
    
    public TipoErro getTipo() {
        return tipo;
    }
    
    /**
     * Retorna mensagem formatada para exibição.
     */
    public String getMensagemFormatada() {
        return String.format("[Linha %d, Coluna %d] Erro Semântico: %s",
                           token.getLinha(), token.getColuna(), mensagem);
    }
    
    @Override
    public String toString() {
        return getMensagemFormatada();
    }
}