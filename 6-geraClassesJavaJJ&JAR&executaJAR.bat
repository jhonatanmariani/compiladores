echo "########################################################################################"
echo "Executando o script de geração das classes Java do analisador a partir do arquivo .jj"
echo "########################################################################################"

cd implementacao-compiladores\src\main\java\classes\

CALL javacc linguagem20221.jj

echo ""
echo ""

cd ..\..\..\..\
echo "########################################################################################"
echo "Gerando arquivo executavel JAR ...
echo "########################################################################################"

CALL mvn clean package

echo "Executando arquivo jar do trabalho de Compiladores..."

java -jar target\trabalho-compiladores-1.0-SNAPSHOT-jar-with-dependencies.jar

PAUSE


PAUSE



