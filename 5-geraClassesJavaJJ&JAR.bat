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

PAUSE



