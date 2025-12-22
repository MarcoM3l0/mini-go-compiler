# Mini-Go Compiler

Compilador educacional para um subconjunto da linguagem **Go (Mini-Go**, desenvolvido como projeto da disciplina **Linguagens Formais, Autômatos e Compiladores**.

O objetivo do projeto é aplicar, de forma prática, os principais conceitos envolvidos na construção de compiladores, incluindo análise léxica, sintática, semântica e geração de código intermediário.

---

## 📚 Sobre o Projeto

Este projeto consiste na implementação de um compilador acadêmico capaz de processar algoritmos estruturados básicos escritos em uma versão simplificada da linguagem Go, denominada **Mini-Go**.

O compilador **não tem fins comerciais**, sendo um artefato didático voltado para o aprendizado dos fundamentos teóricos e práticos da área de compiladores.

---

## 🎯 Objetivos

- Definir formalmente a gramática da linguagem Mini-Go (EBNF)
- Implementar:
  - Analisador Léxico (Scanner)
  - Analisador Sintático (Parser)
  - Analisador Semântico
  - Gerador de Código Intermediário
- Produzir mensagens de erro claras e informativas
- Aplicar boas práticas de Engenharia de Software
- Utilizar testes unitários para validar cada etapa do compilador

---

## 🧩 Escopo da Linguagem (Mini-Go)

A Mini-Go suporta os seguintes construtos:

### ✔ Variáveis e Tipos de Dados
- Declaração e atribuição de variáveis
- Tipos primitivos:
  - Números (inteiro e/ou ponto flutuante)
  - Texto (strings)

### ✔ Entrada e Saída
- Leitura de dados do teclado
- Escrita de dados na tela

### ✔ Expressões
- Aritméticas: `+`, `-`, `*`, `/`
- Relacionais: `>`, `<`, `>=`, `<=`, `==`, `!=`
- Lógicas: `and`, `or`, `not`
- Respeito à precedência de operadores

### ✔ Controle de Fluxo
- Estruturas condicionais: `if` / `else`
- Estruturas de repetição: `while` ou `for`

### ❌ Fora do Escopo
- Declaração de funções
- Vetores, matrizes ou estruturas
- Ponteiros ou alocação dinâmica manual

