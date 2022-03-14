# Compiladores

<ul>
    <li> Esse trabalho é baseado na análise da [linguagem 2022.1](https://github.com/jhonatanmariani/compiladores/blob/master/linguagem/Especificacao-linguagem2022.2.pdf). </li>
    <li> A IDE utilizada foi a IntelliJ IDEA. </li>
    <li> A versão do JDK utilizada foi a 16.0.1. </li>
    <li> O arquivo .jj é onde possui a análise léxica e sintática da linguagem 2022.1 . </li>
</ul>

## Instalação do JavaCC no Windows

OBSERVAÇÃO: A Versão do JAVACC utilizada nesse trabalho foi a 7.0.10

<ol>
    <li> Acessar: https://javacc.github.io/javacc/ </li>
    <li> Clicar em Download. </li>
    <li> Para baixar a versão 7.0.10, acesse: https://github.com/javacc/javacc/archive/javacc-7.0.10.zip que o download será iniciado automaticamente. </li>
    <li> Extraia a pasta javacc-javacc-7.0.10 para 'C:\Program Files\Java' com o nome 'javacc-7.0.10'</li>
    <li> Abrir as Variáveis do Sistema do Windows:</li>
        <ol>
            <li> Em Variáveis do Sistema clicar na variável "Path" e depois ir em "Editar"  </li> 
            <li> Clicar em "Novo" e adicionar o caminho da pasta 'scripts' do JavaCC, exemplo: 'C:\Program Files\Java\javacc-7.0.10\scripts'
            <li> Caso não tenha em Path o diretório da pasta JDK da versão utilizada para programar, adicione também apontando para a pasta 'bin', exemplo: 'C:\Program Files\Java\jdk-16.0.1\bin' </li>
            <li> Caso não tenha em Path o diretório da pasta JRE da versão utilizada para programar, adicione também apontando para a pasta 'bin', exemplo: 'C:\Program Files\Java\jre1.8.0_301\bin'  </li>
            <li> Clicar em OK </li>
            <li> Pronto! </li>
        </ol>
</ol>

## Instalação do Plugin JavaCC no IntelliJ IDEA

<ol>
    <li> Acessar: https://plugins.jetbrains.com/plugin/11431-javacc/ </li>
    <li> Clicar em 'Install to IntelliJ IDEA' </li>
</ol>