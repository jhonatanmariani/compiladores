echo "########################################################################################"
echo "Gerando executável JAR..."
echo "########################################################################################"

cd implementacao-compiladores
CALL mvn clean package

echo ""
echo ""

cd ..\..\..\..\
echo "########################################################################################"
echo "Executando executavel JAR ...
echo "########################################################################################"

cd ..

echo "Executando arquivo jar do trabalho de Compiladores..."

java -jar implementacao-compiladores\target\trabalho-compiladores-1.0-SNAPSHOT-jar-with-dependencies.jar

PAUSE



