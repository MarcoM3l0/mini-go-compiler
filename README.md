# Mini-Go Compiler

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![JUnit](https://img.shields.io/badge/JUnit-5-green.svg)](https://junit.org/junit5/)

Compilador educacional para um subconjunto da linguagem **Go (Mini-Go)**, desenvolvido como projeto da disciplina **Linguagens Formais, Autômatos e Compiladores** no Instituto Federal de Sergipe - Campus Itabaiana (2026.2).

O objetivo do projeto é aplicar, de forma prática, os principais conceitos envolvidos na construção de compiladores, incluindo análise léxica, sintática, semântica e geração de código intermediário.

---

## 📚 Sobre o Projeto

Este projeto consiste na implementação de um compilador acadêmico capaz de processar algoritmos estruturados básicos escritos em uma versão simplificada da linguagem Go, denominada **Mini-Go**.

O compilador **não tem fins comerciais**, sendo um artefato didático voltado para o aprendizado dos fundamentos teóricos e práticos da área de compiladores.

### **Pipeline do Compilador**

```
┌─────────────┐    ┌─────────┐    ┌──────────┐    ┌───────────┐    ┌─────────┐
│   Código    │ => │ Scanner │ => │  Parser  │ => │  Análise  │ => │   TAC   │
│   Fonte     │    │  Léxico │    │ Sintático│    │ Semântica │    │   IR    │
└─────────────┘    └─────────┘    └──────────┘    └───────────┘    └─────────┘
```

---

## 🎯 Objetivos

- Definir formalmente a gramática da linguagem Mini-Go (EBNF)
- Implementar:
  - **Analisador Léxico (Scanner)** - Tokenização do código fonte
  - **Analisador Sintático (Parser)** - Geração da AST
  - **Analisador Semântico** - Verificação de tipos e escopo
  - **Gerador de Código Intermediário (TAC)** - Three-Address Code
- Produzir mensagens de erro claras e informativas
- Aplicar boas práticas de Engenharia de Software
- Utilizar testes unitários para validar cada etapa do compilador (100 testes)

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 21
- **Testes:** JUnit 5
- **Paradigma:** Desenvolvimento Orientado a Testes (TDD)
- **Padrões de Projeto:** Visitor Pattern (para navegação da AST)

---

## 🚀 Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/MarcoM3l0/mini-go-compiler.git
cd mini-go-compiler
```

### 2. Compile o projeto

```bash
mvn clean compile
```

### 3. Execute os testes

```bash
mvn test
```

**Saída esperada:**
```
Tests run: 100
Failures: 0
Errors: 0
Success rate: 100%
```

---

## ▶️ Como Executar

### **Executar o Compilador**

O ponto de entrada do compilador é a classe `Programa.java` localizada no pacote `codigointermediario`.

```bash
mvn exec:java -Dexec.mainClass="codigointermediario.Programa"
```

### **Executar Demonstrações**

A classe `Programa` contém exemplos de uso do compilador:

```bash
# Compilar e executar
mvn clean compile
mvn exec:java -Dexec.mainClass="codigointermediario.Programa"
```

**Saída esperada:**

```
================================================================================
EXEMPLO 1: Expressão Aritmética com Precedência
================================================================================

📝 CÓDIGO FONTE:

var a inteiro = 5;
var b inteiro = 3;
var c inteiro = 2;
var x inteiro = a + b * c;
imprimir(x);

--------------------------------------------------------------------------------
⚙️  ETAPA 4: GERAÇÃO DE CÓDIGO INTERMEDIÁRIO (TAC)
--------------------------------------------------------------------------------

✓ 7 instruções TAC geradas:

    0: a = 5
    1: b = 3
    2: c = 2
    3: t0 = b * c
    4: t1 = a + t0
    5: x = t1
    6: print x

✅ COMPILAÇÃO CONCLUÍDA COM SUCESSO!
```

---

## 📖 Linguagem Mini-Go

### **Palavras Reservadas**

```
var  inteiro  real  texto  se  senao  para  imprimir  ler
```

### **Tipos de Dados**

| Tipo | Descrição | Exemplo |
|------|-----------|---------|
| `inteiro` | Números inteiros | `10`, `-5`, `0` |
| `real` | Números de ponto flutuante | `3.14`, `-0.5`, `2.0` |
| `texto` | Strings (entre aspas duplas) | `"Olá"`, `"Mini-Go"` |

### **Operadores**

**Aritméticos:**
```
+  -  *  /
```
Precedência: `*`, `/` > `+`, `-`

**Relacionais:**
```
<  <=  >  >=  ==  !=
```

**Lógicos:**
```
&&  ||  !
```
Precedência: `!` > `&&` > `||`

### **Estruturas de Controle**

#### **Declaração de Variáveis**

```go
var x inteiro;                    // Sem inicialização
var y inteiro = 10;               // Com inicialização
var nome texto = "Marco";          // String
var pi real = 3.14;               // Real
```

#### **Atribuição**

```go
x = 20;
y = x + 10;
```

#### **Condicional (SE/SENAO)**

```go
se x > 10 {
    imprimir("x é maior que 10");
} senao {
    imprimir("x é menor ou igual a 10");
}
```

#### **Laço de Repetição (PARA)**

**Estilo While:**
```go
var i inteiro = 0;
para i < 10 {
    imprimir(i);
    i = i + 1;
}
```

**Estilo For Clássico:**
```go
para var i inteiro = 0; i < 10; i = i + 1 {
    imprimir(i);
}
```

#### **Entrada e Saída**

```go
// Entrada
var nome texto;
var idade inteiro;
ler(nome, idade);

// Saída
imprimir("Nome:", nome, "Idade:", idade);
```

#### **Comentários**

```go
// Comentário de linha

/* 
   Comentário
   de bloco
*/
```

---

## 💡 Exemplos Práticos

### **Exemplo 1: Olá Mundo**

```go
imprimir("Olá, Mini-Go!");
```

---

### **Exemplo 2: Calculadora**

```go
var a inteiro = 15;
var b inteiro = 3;

imprimir("Soma:", a + b);
imprimir("Subtração:", a - b);
imprimir("Multiplicação:", a * b);
imprimir("Divisão:", a / b);
```

**Código TAC Gerado:**
```
a = 15
b = 3
t0 = a + b
print "Soma:"
print t0
t1 = a - b
print "Subtração:"
print t1
t2 = a * b
print "Multiplicação:"
print t2
t3 = a / b
print "Divisão:"
print t3
```

---

### **Exemplo 3: Fatorial**

```go
var n inteiro = 5;
var fatorial inteiro = 1;
var i inteiro = 1;

para i <= n {
    fatorial = fatorial * i;
    i = i + 1;
}

imprimir("Fatorial de", n, "=", fatorial);
```

**Código TAC Gerado:**
```
 0: n = 5
 1: fatorial = 1
 2: i = 1
 3: L0:
 4: t0 = i <= n
 5: if_false t0 goto L1
 6: t1 = fatorial * i
 7: fatorial = t1
 8: t2 = i + 1
 9: i = t2
10: goto L0
11: L1:
12: print "Fatorial de"
13: print n
14: print "="
15: print fatorial
```

---

### **Exemplo 4: Verificar Número Par**

```go
var numero inteiro = 8;
var divisao inteiro = numero / 2;
var multiplicacao inteiro = divisao * 2;

se multiplicacao == numero {
    imprimir(numero, "é par");
} senao {
    imprimir(numero, "é ímpar");
}
```

---

## 🏗️ Estrutura do Projeto

```
mini_go_compiler/
│
├── src/
│   ├── scanner/                    # Análise Léxica
│   │   ├── Token.java             # Representa um token
│   │   ├── TokenType.java         # Enum dos tipos de token
│   │   └── Scanner.java           # Analisador léxico
│   │
│   ├── analisadorsintatico/       # AST
│   │   ├── Expressao.java         # Nós de expressões
│   │   ├── Comando.java           # Nós de comandos
│   │   └── AstPrinter.java        # Visualizador da AST
│   │
│   ├── parser/                    # Análise Sintática
│   │   └── Parser.java            # Analisador sintático
│   │
│   ├── analisadorsemantico/       # Análise Semântica
│   │   ├── Tipo.java              # Sistema de tipos
│   │   ├── Simbolo.java           # Representação de símbolos
│   │   ├── TabelaSimbolos.java    # Gerenciamento de escopo
│   │   ├── ErroSemantico.java     # Erros semânticos
│   │   └── AnalisadorSemantico.java # Verificador semântico
│   │
│   ├── codigointermediario/       # Geração de Código
│   │   ├── TACInstruction.java    # Instruções TAC
│   │   ├── TACGenerator.java      # Gerador TAC
│   │   └── Programa.java          # Ponto de entrada (Main)
│   │
│   ├── bnf/                       # Gramática BNF
│   │   └── Gramatica             # Definição formal da linguagem
│   │
│   └── test/                      # Testes Unitários
│       ├── ScannerTest.java
│       ├── AnalisadorSemanticoTest.java
│       └── TACGeneratorTest.java
│
└── README.md                      # Esta documentação
```

---

## 🧪 Testes

### **Executar Todos os Testes**

```bash
mvn test
```

### **Executar Teste Específico**

```bash
# Testar Scanner
mvn test -Dtest=ScannerTest

# Testar Análise Semântica
mvn test -Dtest=AnalisadorSemanticoTest

# Testar Geração TAC
mvn test -Dtest=TACGeneratorTest
```

### **Cobertura de Testes**

| Módulo | Testes | Descrição |
|--------|--------|-----------|
| **Scanner** | 43 | Tokenização, identificadores, literais, operadores |
| **Análise Semântica** | 35 | Tipos, escopo, declarações, redeclarações |
| **Geração TAC** | 22 | Expressões, estruturas de controle, I/O |
| **TOTAL** | **100** | Cobertura ~95% |

---

## 🔧 Uso Programático

### **Pipeline Completo**

```java
package codigointermediario;

import scanner.Scanner;
import parser.Parser;
import analisadorsemantico.AnalisadorSemantico;
import analisadorsintatico.Comando;
import scanner.Token;

import java.util.List;

public class Programa {
    public static void main(String[] args) {
        String codigo = """
            var x inteiro = 10;
            var y inteiro = 20;
            var soma inteiro = x + y;
            imprimir("Resultado:", soma);
            """;
        
        // 1. Análise Léxica
        Scanner scanner = new Scanner(codigo);
        List<Token> tokens = scanner.scanTokens();
        System.out.println("Tokens: " + tokens.size());
        
        // 2. Análise Sintática
        Parser parser = new Parser(tokens);
        List<Comando> ast = parser.parsePrograma();
        
        if (ast == null) {
            System.err.println("Erro sintático!");
            return;
        }
        System.out.println("AST gerada");
        
        // 3. Análise Semântica
        AnalisadorSemantico semantico = new AnalisadorSemantico();
        boolean valido = semantico.analisar(ast);
        
        if (!valido) {
            System.err.println("Erros semânticos:");
            semantico.imprimirErros();
            return;
        }
        System.out.println("Análise semântica OK");
        
        // 4. Geração TAC
        TACGenerator tacGen = new TACGenerator();
        List<TACInstruction> tac = tacGen.generate(ast);
        
        System.out.println("\n=== CÓDIGO TAC ===");
        tacGen.printCode();
        
        System.out.println("\n Compilação concluída!");
    }
}
```

**Saída:**
```
Tokens: 20
AST gerada
Análise semântica OK

=== CÓDIGO TAC ===
  0: x = 10
  1: y = 20
  2: t0 = x + y
  3: soma = t0
  4: print "Resultado:"
  5: print soma

Compilação concluída!
```

---

## ❌ Tratamento de Erros

### **Erro Léxico**

**Código:**
```go
var x inteiro = 10@;
```

**Saída:**
```
Erro léxico na linha 1, coluna 20: Caractere inesperado: '@'
```

---

### **Erro Sintático**

**Código:**
```go
var x inteiro = 10
var y inteiro = 20;
```

**Saída:**
```
[Linha 1, Coluna 19] Erro em 'var': Esperado ';' após o comando.
```

---

### **Erros Semânticos**

#### **Incompatibilidade de Tipos**

**Código:**
```go
var x inteiro = "texto";
```

**Saída:**
```
[Linha 1, Coluna 17] Erro Semântico: Não é possível atribuir texto a uma 
variável do tipo inteiro.
```

#### **Operação com Tipos Incompatíveis**

**Código:**
```go
var x inteiro = 10;
var y texto = "abc";
var z inteiro = x + y;
```

**Saída:**
```
[Linha 3, Coluna 21] Erro Semântico: Operador '+' requer operandos numéricos,
mas recebeu inteiro e texto.
```

#### **Variável Não Declarada**

**Código:**
```go
x = 10;
```

**Saída:**
```
[Linha 1, Coluna 1] Erro Semântico: Variável 'x' não foi declarada.
```

#### **Redeclaração de Variável**

**Código:**
```go
var x inteiro = 10;
var x real = 3.14;
```

**Saída:**
```
[Linha 2, Coluna 5] Erro Semântico: Variável 'x' já foi declarada neste escopo.
```

---

---

## 🎓 Conceitos de Compiladores Aplicados

### **1. Análise Léxica (Scanner)**
- ✅ Autômatos finitos para reconhecimento de padrões
- ✅ Tokenização de código fonte
- ✅ Tratamento de comentários (`//` e `/* */`)
- ✅ Suporte a escape em strings (`\"`, `\\`, `\n`, `\t`)
- ✅ Rastreamento de linha e coluna para mensagens de erro

### **2. Análise Sintática (Parser)**
- ✅ Gramática LL(1) com Recursive Descent Parsing
- ✅ Geração de Árvore Sintática Abstrata (AST)
- ✅ Precedência e associatividade de operadores
- ✅ Padrão Visitor para navegação da AST

### **3. Análise Semântica**
- ✅ Tabela de símbolos com suporte a escopos aninhados
- ✅ Sistema de tipos (inteiro, real, texto, booleano)
- ✅ Verificação de compatibilidade de tipos
- ✅ Detecção de variáveis não declaradas
- ✅ Detecção de redeclaração de variáveis
- ✅ Verificação de inicialização de variáveis

### **4. Código Intermediário (TAC)**
- ✅ Three-Address Code independente de máquina
- ✅ Geração automática de temporários (`t0`, `t1`, ...)
- ✅ Geração automática de labels (`L0`, `L1`, ...)
- ✅ Avaliação em curto-circuito para `&&` e `||`
- ✅ Linearização da AST
- ✅ Base para futuras otimizações e geração de código nativo

---

## 👥 Colaboradores

Este projeto foi desenvolvido de forma colaborativa pelos seguintes integrantes:

- [**Marco Melo**](https://github.com/MarcoM3l0) 

- [**Alaise Caetano**](https://github.com/alaise-tech)

- [**Raimundo Junio**](https://github.com/RaimundoRJS)

- [**Wesley Souza**](https://github.com/WesleySouza93)

---

## 🏫 Contexto Acadêmico

**Instituição:** Instituto Federal de Sergipe - Campus Itabaiana  
**Disciplina:** Linguagens Formais, Autômatos e Compiladores  
**Semestre:** 2025.2  
**Professor:** Prof. Me. Marlos Tacio Silva  
**Natureza:** Projeto educacional sem fins comerciais
