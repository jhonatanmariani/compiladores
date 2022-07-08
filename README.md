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
        </ol>
    <li> Agora, você pode seguir o passo 7 e depois pule para o 9, caso dê erro no 9, execute o passo 8.
    <li> Dentro da pasta do "javacc", crie uma pasta "target" e copie o arquivo "javacc.jar" da pasta "bootstrap" dentro da pasta "target".
    <li> Altere a string 'target' para 'bootstrap' presente nos arquivos da pasta 'scripts' do javaCC </li>
    <li> Para testar se a instalação ocorreu sem erro, execute o comando 'javacc' no Prompt de Comando do Windows, se retornar instruções de execução do comando, a instalação foi concluída com sucesso. </li>
</ol>

## Instalação do Plugin JavaCC no IntelliJ IDEA

<ol>
    <li> Acessar: https://plugins.jetbrains.com/plugin/11431-javacc/ </li>
    <li> Clicar em 'Install to IntelliJ IDEA' </li>
</ol>

## Instalação e configuração do Maven
<ol>
  <li> Descompacte o arquivo onde você gostaria de armazenar os binários, por exemplo:</li>

    Sistemas operacionais baseados em Unix (Linux, Solaris e Mac OS X)
      tar zxvf apache-maven-3.x.y.tar.gz
    Windows
      descompacte o apache-maven-3.x.y.zip

 <li> Um diretório chamado "apache-maven-3.x.y" será criado.</li>

 <li> Adicione o diretório bin ao seu PATH, por exemplo: </li>

    Sistemas operacionais baseados em Unix (Linux, Solaris e Mac OS X)
      export PATH=/usr/local/apache-maven-3.x.y/bin:$PATH
    Windows
      set PATH="c:\program files\apache-maven-3.x.y\bin";%PATH%

 <li> Certifique-se de que JAVA_HOME esteja configurado para o local do seu JDK. Ex.: C:\Program Files\Java\jdk-16.0.1</li>

  <li> Execute "mvn --version" para verificar se está instalado corretamente.</li>

  Para obter a documentação completa, consulte https://maven.apache.org/download.html#Installation
</ol>

## Gerar classes Java do arquivo .jj

- Basta executar o arquivo geraClassesJavaJJ.bat

## Gerar arquivo executável .jar com Maven

<ol>
    <li> Ir na pasta aonde está o arquivo pom.xml </li>
    <li> Abrir o terminal dentro dessa pasta e executar o comando mvn package (isso vai compilar e empacotar o projeto) </li>
    <li> Executar mvn clean (para limpar caso algo já tenha sido criado </li>
    <li> Executar mvn package para criar o arquivo .jar </li>
    <li> Executar mvn clean package para limpar e criar um novo (forma encurtada dos dois comandos acima) </li>
    <li> Ir na pasta target e executar o .jar criado pelo cmd (executar o arquivo com as dependências) </li>
    <ul> Ex: java -jar trabalho-compiladores-1.0-SNAPSHOT-jar-with-dependencies.jar </ul>
</ol>
