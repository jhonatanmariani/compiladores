echo "Executando o script de geração das classes Java do analisador a partir do arquivo .jj"
cd implementacao-compiladores\src\main\java\classes\
echo "Diretório que seram geradas/armazenadas as classes Java: " %CD%
javacc linguagem20221.jj
echo "Classes Java geradas!"
pause