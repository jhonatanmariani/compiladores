package classes;

import maquinavirtual.*;

import java.util.ArrayList;
import java.util.List;

public class LanguageRules { // AcoesSemanticas
    private String contexto = "";
    private int VT = 0; // contador para número total de constantes e de variáveis
    private int VP = 0; // contador para número de constantes ou variáveis de um determinado tipo;
    private int VI = 0; // contador para o número das variáveis indexadas de um determinado tipo;
    private int TVI = 0; // contador para o tamanho das variáveis indexadas de um determinado tipo;
    private int tipo;   // 1 variável integer ou enumerado, 2 variável float, 3 variável string, 4 variável boolean
                        // 5 constante integer, 6 constante float, 7 constante string;
    private int ponteiro = 1;  // indicador da posição onde será gerada a próxima instrução na área de instruções;
    boolean variavelIndexada = false; // valor lógico que indica ou não ocorrência de uma variável indexada
    private List<Integer> pilhaDeDesvios = new ArrayList<>(); //Lembrar de controlar para trabalhar como LIFO
    private String saida = "";
    private List<Simbolo> tabelaDeSimbolos = new ArrayList<>();
    private static List<InstructionK> instructionList = new ArrayList<>();
    private String identificadorReconhecido;
    private int constanteInteira;
    private List<Object> listaAtributos = new ArrayList<>();
    private static List<String> listaErros = new ArrayList<>();

    public List<InstructionK> getInstructionKList() {
        var tmp =  instructionList;
        instructionList = new ArrayList<>();
        return tmp;
    }

    public List<String> getListaErros() {
        var tmp = listaErros;
        listaErros = new ArrayList<>();
        return tmp;
    }

/////// REGRAS DAS AÇÕES SEMANTICAS

    public void acao1(Token token){
        System.out.println("Acao 1 - reconhecimento do identificador do programa (identificador, 0, -, -)");
        Simbolo simbolo = new Simbolo(token.image, 0);
        tabelaDeSimbolos.add(simbolo);
    }

    public void acao2(){
        System.out.println("Acao 2 - reconhecimento de fim de programa (ponteiro, STP, 0)");
        InstructionK instruction = new InstructionK(InstructionK.Mnemonic.STP, new DataFrameK(DataTypeK.INTEGER, 0));
        instructionList.add(instruction);
        InstructionK.enumerateInstructions(instructionList);
        System.out.println("Lista de Erros: " + listaErros.toString());
        System.out.println("Lista de Instruções:");
        for (InstructionK i : instructionList) {
            System.out.println(i.toString());
        }
    }

    public void acao3(){
        System.out.println("Acao 3 - reconhecimento de identificador do tipo enumerado");
    }

    public void acao4() {
        System.out.println("Acao 4 - reconhecimento de identificador de constante de tipo enumerado");
    }

    public void acao5() {
        System.out.println("Acao 5 - reconhecimento das palavras reservadas as constant");
        this.contexto = "as constant";
        System.out.println("Acao 5 - Setado contexto: as constant");
        this.VI = 0;
        this.TVI = 0;
    }

    public void acao6(){
        System.out.println("Acao 6 - reconhecimento do término da declaração de constantes ou variáveis de um determinado tipo");
        this.VP = this.VP + this.TVI;
        switch (this.tipo){
            case 1: case 5: {
                System.out.println("Acao 6 - Entrou no caso 1 ou 5");
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.ALI, new DataFrameK(DataTypeK.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 2:
            case 6:
            {
                System.out.println("Acao 6 - Entrou no caso 2 ou 6");
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.ALR, new DataFrameK(DataTypeK.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 3: case 7: {
                System.out.println("Acao 6 - Entrou no caso 3 ou 7");
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.ALS, new DataFrameK(DataTypeK.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 4:{
                System.out.println("Acao 6 - Entrou no caso 4");
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.ALB, new DataFrameK(DataTypeK.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
        }
        if(this.tipo == 1 || this.tipo == 2 || this.tipo == 3 || this.tipo == 4){
            System.out.println("Acao 6 - O tipo  da categoria é 1, 2, 3 ou 4");
            this.VP = 0;
            this.VI = 0;
            this.TVI = 0;
        }
    }

    public void acao7(String valor){
        System.out.println("Acao 7 - reconhecimento de valor na declaração de constante");
        switch (this.tipo){
            case 5: {
                System.out.println("Acao 7 - O tipo da categoria é 5");
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.LDI, new DataFrameK(DataTypeK.INTEGER, Integer.parseInt(valor)));
                instructionList.add(instruction);
                break;
            }
            case 6: {
                System.out.println("Acao 7 - O tipo da categoria é 6");
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.LDR, new DataFrameK(DataTypeK.FLOAT, Float.parseFloat(valor)));
                instructionList.add(instruction);
                break;
            }
            case 7: {
                System.out.println("Acao 7 - O tipo da categoria é 7");
                InstructionK instruction = new InstructionK(InstructionK.Mnemonic.LDS, new DataFrameK(DataTypeK.LITERAL, valor));
                instructionList.add(instruction);
                break;
            }
        }
        InstructionK instruction = new InstructionK(InstructionK.Mnemonic.STC, new DataFrameK(DataTypeK.INTEGER, this.VP));
        instructionList.add(instruction);
        this.ponteiro = this.ponteiro + 1;
        this.VP = 0;
    }

    public void acao8(){
        System.out.println("Acao 8 - reconhecimento das palavras reservadas as variable");
        this.contexto = "as variable";
        System.out.println("Acao 8 - Setado contexto: as variable");
    }

    public void acao9(Token token) {
        System.out.println("Acao 9 - reconhecimento de identificador de constante");
        Simbolo exist;
        exist = tabelaDeSimbolos.stream().filter(simb -> token.image.equals(simb.getIdentificador())).findAny().orElse(null);
        if(!(exist == null)){
            this.listaErros.add("9 - Identificador já declarado: '" + token.image + "'  - Linha/Coluna: "+token.beginLine+"/"+token.beginColumn);
        }else{
            this.VT = this.VT + 1;
            this.VP = this.VP + 1;
            Simbolo simbolo = new Simbolo(token.image, this.tipo, this.VT);
            tabelaDeSimbolos.add(simbolo);
        }
    }

    public void acao10(Token token) {
        System.out.println("Acao 10 - reconhecimento de identificador de variável");
        switch (this.contexto){
            case "as variable": {
                System.out.println("Acao 10 - Contexto é: as variable");
                Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> token.image.equals(simb.getIdentificador())).findAny().orElse(null);
                if (!(exist == null)) {
                    this.listaErros.add("10 - Identificador ja declarado: '" + token.image + "' - Linha/Coluna: " + token.beginLine + "/" + token.beginColumn);
                } else {
                    this.variavelIndexada = false;
                    this.identificadorReconhecido = token.image;
                }
                break;
            }
            case "atribuicao": case "entrada dados": {
                System.out.println("Acao 10 - Contexto é: atribuicao ou entrada de dados");
                variavelIndexada = false;
                this.identificadorReconhecido = token.image;
                break;
            }
        }
    }

    public void acao11(Token token) {
        System.out.println("Acao 11 - reconhecimento de identificador de variável e tamanho da variável indexada");
        System.out.println("contexto setado = " + this.contexto);
        switch (this.contexto) {
            case "as variable": {
                System.out.println("Acao 11 - Contexto é: as variable");
                if (!this.variavelIndexada) {
                    System.out.println("Acao 11 - A variavel indexada = falso");
                    this.VT = this.VT + 1;
                    this.VP = this.VP + 1;
                    System.out.println("Identificador reconhecido: " + identificadorReconhecido);
                    System.out.println("tipo do identificador= " + tipo);
                    System.out.println("VT= " + VT);
                    System.out.println("Token : " + token.image);
                    Simbolo simbolo = new Simbolo(this.identificadorReconhecido, this.tipo, this.VT);
                    System.out.println("Simbolo gerado = "+ simbolo.getIdentificador());
                    tabelaDeSimbolos.add(simbolo);
                    System.out.println("Acao 11- tabela de simbolos após inserir: " + tabelaDeSimbolos.toString());
                } else {
                    System.out.println("Acao 11 - A variavel indexada = true");
                    this.VI = this.VI + 1;
                    this.TVI = this.TVI + this.constanteInteira;
                    Simbolo simbolo = new Simbolo(this.identificadorReconhecido, this.tipo, this.VT + 1, this.constanteInteira);
                    tabelaDeSimbolos.add(simbolo);
                    this.VT = this.VT + this.constanteInteira;
                }
                break;
            }
            case "atribuicao": {
                System.out.println("Acao 11 - Contexto é: atribuicao");
                Simbolo simboloo = null;
                if (tabelaDeSimbolos.contains(identificadorReconhecido)) {
                    int index = tabelaDeSimbolos.indexOf(identificadorReconhecido);
                    simboloo = tabelaDeSimbolos.get(index);
                    //int categoria = simbolo.getCategoria();
                    if (simboloo.getAtributo2() == 0) {
                        if (!this.variavelIndexada) {
                            this.listaAtributos.add(simboloo.getAtributo1());
                        } else {
                            this.listaErros.add("11 - Identificador de variavel nao indexada: '" + this.identificadorReconhecido + "' - Linha/Coluna: " + token.beginLine + "/" + token.beginColumn);
                        }
                    } else {
                        if (this.variavelIndexada) {
                            this.listaAtributos.add(simboloo.getAtributo1() + this.constanteInteira - 1);
                        } else {
                            this.listaErros.add("11 - Variavel indexada precisa de index - Linha/Coluna: " + token.beginLine + "/" + token.beginColumn);
                        }
                    }
                } else {
                    this.listaErros.add("11 - Uso de variavel ou constante nao declarada - Linha/Coluna: " + token.beginLine + "/" + token.beginColumn);
                }
                break;
            }
            case "entrada dados": {
                System.out.println("Acao 11 - Contexto é: entrada dados");
                Simbolo simboloo2 = null;
                if (tabelaDeSimbolos.contains(identificadorReconhecido)) {
                    int index = tabelaDeSimbolos.indexOf(identificadorReconhecido);
                    simboloo2 = tabelaDeSimbolos.get(index);
                    int atr1 = simboloo2.getAtributo1();
                    int atr2 = simboloo2.getAtributo2();
                    //int categoria = simbolo.getCategoria();
                    int categ = simboloo2.getCategoria();
                    if (simboloo2.getAtributo2() == 0) {
                        if (!this.variavelIndexada) {
                            instructionList.add(new InstructionK(InstructionK.Mnemonic.REA, new DataFrameK(DataTypeK.get(categ), categ)));
                            this.ponteiro = this.ponteiro + 1;
                            instructionList.add(new InstructionK(InstructionK.Mnemonic.STR, new DataFrameK(DataTypeK.ADDRESS, atr1)));
                            this.ponteiro = this.ponteiro + 1;
                        } else {
                            if (this.variavelIndexada) {
                                instructionList.add(new InstructionK(InstructionK.Mnemonic.REA, new DataFrameK(DataTypeK.get(categ), categ)));
                                this.ponteiro = this.ponteiro + 1;
                                instructionList.add(new InstructionK(InstructionK.Mnemonic.STR, new DataFrameK(DataTypeK.ADDRESS, atr1 + this.constanteInteira - 1)));
                                this.ponteiro = this.ponteiro + 1;
                            } else {
                                this.listaErros.add("11 - Variavel indexada precisa de um index - Linha/Coluna: " + token.beginLine + "/" + token.beginColumn);
                            }
                        }
                    } else {
                        this.listaErros.add("11 - Uso de variavel ou constante nao declarada: '" + this.identificadorReconhecido + "' - Linha/Coluna: " + token.beginLine + "/" + token.beginColumn);
                    }
                    break;
                }
                break;
            }
        }
    }
    public void acao12(Token token){
        System.out.println("Acao 12 - Reconhecimento de constante inteira como tamanho da variável indexada");
        this.constanteInteira = Integer.parseInt(token.image);
        this.variavelIndexada = true;
    }

    public void acao13(){
        System.out.println(": Acao 13 - reconhecimento da palavra reservada integer");
        if(this.contexto.equals("as variable")){
            System.out.println("Acao 13 - O contexto é: as variable");
            this.tipo = 1;
        }else{
            this.tipo = 5;
        }
        System.out.println("Ação 13 - Tipo informado: " + tipo);
    }

    public void acao14(){
        System.out.println("Acao 14 - reconhecimento da palavra reservada float");
        if(this.contexto.equals("as variable")){
            System.out.println("Acao 14 - O contexto é: as variable");
            this.tipo = 2;
        }else{
            this.tipo = 6;
        }
    }

    public void acao15(){
        System.out.println(": Acao 15 - reconhecimento da palavra reservada string");
        if(this.contexto.equals("Acao 15 - as variable")){
            System.out.println("O contexto é: as variable");
            this.tipo = 3;
        }else{
            this.tipo = 7;
        }
    }

    public void acao16(Token token){
        System.out.println("Acao 16 - reconhecimento da palavra reservada boolean"+ token.image);
        if(this.contexto.equals("as variable")){
            System.out.println("Acao 16 - O contexto é: as variable");
            this.tipo = 4;
        }else{
            //Verificar posteriormente se precisa adicionar em um array de erros para prosseguir ou não;
            this.listaErros.add("16 - Tipo invalido para constante - Linha/Coluna: "+token.beginLine+"/"+token.beginColumn);
        }
    }

    public void acao17(Token token){
        System.out.println("Acao 17 - reconhecimento de identificador do tipo enumerado");
        if(this.contexto.equals("as variable")){
            System.out.println("Acao 17 - O contexto é: as variable");
            this.tipo = 1;
        }else{
            this.listaErros.add("17 - Tipo invalido para constante - Linha/Coluna: "+token.beginLine+"/"+token.beginColumn);
        }
    }

    public void acao18(){
        System.out.println("Acao 18 - reconhecimento do início do comando de atribuicao");
        this.contexto = "atribuicao";
        System.out.println("Acao 18 - Setado contexto: atribuicao");
    }

    public void acao19(){
        System.out.println(": Acao 19 - reconhecimento do fim do comando de atribuicao");
        for(int i=0; i< this.listaAtributos.size(); i++){
            instructionList.add(new InstructionK(InstructionK.Mnemonic.STR, new DataFrameK(DataTypeK.ADDRESS, this.listaAtributos.get(i))));
            this.ponteiro = this.ponteiro + 1;
        }
    }

    public void acao20(){
        System.out.println("Acao 20 - reconhecimento do comando de entrada de dados");
        this.contexto = "entrada dados";
        System.out.println("Acao 20 - Setado contexto: entrada dados");

    }

    public void acao21() {
        System.out.println("Acao 21 - reconhecimento das palavras reservadas write all this");
        saida = "write this all";
        System.out.println("Acao 21 - Setado saida: write this all");
    }

    public void acao22() {
        System.out.println("Acao 22 - reconhecimento das palavras reservadas write all");
        saida = "write this";
        System.out.println("Acao 22 - write this");
    }

    public void acao23(){
        System.out.println("Acao 23 - reconhecimento de mensagem em comando de saída de dados");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.WRT, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao24(Token token){
        System.out.println("Acao 24 - reconhecimento de identificador em comando de saída ou em expressão");
        //Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> token.image.equals(simb.getIdentificador())).findAny().orElse(null);
        System.out.println("Acao 24 - Contexto é: atribuicao");
        System.out.println("Identificador reconhecido= " + identificadorReconhecido);
        System.out.println(tabelaDeSimbolos.toString());
        if (tabelaDeSimbolos.contains(identificadorReconhecido)) {
            int index = tabelaDeSimbolos.indexOf(identificadorReconhecido);
            System.out.println("Acao 24 - Identificador existe na tabela de simbolos e identificador é identificador de constante ou de variável");
            this.variavelIndexada = false;
            this.identificadorReconhecido = token.image;
        }else{
            this.listaErros.add("24 - Uso de identificador nao declarado: '"+token.image+"' - Linha/Coluna: "+token.beginLine+"/"+token.beginColumn);
        }

    }

    public void acao25(Token token) {
        System.out.println("Acao 25 - reconhecimento de identificador de constante ou de variável e tamanho de variável indexada em comando de saída");
        Simbolo exist = this.tabelaDeSimbolos.stream().filter(simb -> this.identificadorReconhecido.equals(simb.getIdentificador())).findAny().orElse(null);
        if(!(exist == null) && (exist.getCategoria() == 1 || exist.getCategoria() == 2 || exist.getCategoria() == 3 || exist.getCategoria() == 4)) {
            if (!variavelIndexada) {
                System.out.println("Acao 25 - Variavel indexada = false");
                if (exist.getAtributo2() == 0) {
                    System.out.println("Acao 25 - Atributo 2 = vazio/0");
                    if (saida.equals("write all this")) {
                        System.out.println("Acao 25 - Saida: write all this");
                        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDS, new DataFrameK(DataTypeK.ADDRESS, "identificador=")));
                        this.ponteiro = this.ponteiro + 1;
                        instructionList.add(new InstructionK(InstructionK.Mnemonic.WRT, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1())));
                        this.ponteiro = this.ponteiro + 1;
                    }
                    instructionList.add(new InstructionK(InstructionK.Mnemonic.LDV, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1())));
                    this.ponteiro = this.ponteiro + 1;
                } else {
                    this.listaErros.add("25 - Variavel indexada precisa de um index: '"+token.image+"' - Linha/Coluna: "+token.beginLine+"/"+token.beginColumn);
                }
            } else {
                if(exist.getAtributo2() != 0) {
                    System.out.println("Acao 25 - Atributo 2 é diferente de 0");
                    if(saida.equals("write this all")) {
                        System.out.println("Acao 25 - Saida: write all this");
                        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDS, new DataFrameK(DataTypeK.ADDRESS, "identificador=")));
                        this.ponteiro = this.ponteiro + 1;
                        instructionList.add(new InstructionK(InstructionK.Mnemonic.WRT, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1())));
                        this.ponteiro = this.ponteiro + 1;
                    }
                    instructionList.add(new InstructionK(InstructionK.Mnemonic.LDV, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1() + this.constanteInteira -1)));
                    this.ponteiro = this.ponteiro + 1;
                }else{
                    this.listaErros.add("25 - Identificador de variavel ou constante nao indexada: '"+this.identificadorReconhecido+"' - Linha/Coluna: "+token.beginLine+"/"+token.beginColumn);
                }
            }
        }
    }

    public void acao26(Integer constInt){
        System.out.println("Acao 26 - reconhecimento de constante inteira em comando de saída ou em expressão");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDI, new DataFrameK(DataTypeK.INTEGER, constInt)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao27(Float constReal){
        System.out.println("Acao 27 - reconhecimento de constante real em comando de saída ou em expressão");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDR, new DataFrameK(DataTypeK.FLOAT, constReal)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao28(String constLiteral){
        System.out.println("Acao 28 - reconhecimento de constante literal em comando de saída ou em expressão");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDS, new DataFrameK(DataTypeK.LITERAL, constLiteral)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao29(){
        System.out.println("Acao 29 - reconhecimento de fim de comando de seleção");
        this.pilhaDeDesvios.set(this.pilhaDeDesvios.size()-1, ponteiro);
    }

    public void acao30(){
        System.out.println(" Acao 30 - reconhecimento da palavra reservada true");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMF, new DataFrameK(DataTypeK.NONE, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro -1);
    }

    public void acao31(){
        System.out.println(" Acao 31 - reconhecimento da palavra reservada untrue");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMT, new DataFrameK(DataTypeK.ADDRESS, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro -1);
    }

    public void acao32(){
        System.out.println("Acao 32 - reconhecimento da palavra reservada untrue (ou true)");
        this.pilhaDeDesvios.set(this.pilhaDeDesvios.size()-1, ponteiro+1);
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMP, new DataFrameK(DataTypeK.NONE, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro -1);
    }

    public void acao33(){
        System.out.println("Acao 33 - reconhecimento do início de expressão em comando de repetição");
        this.pilhaDeDesvios.add(this.ponteiro);
    }

    public void acao34(){
        System.out.println("Acao 34 - reconhecimento de expressão em comando de repetição");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMF, new DataFrameK(DataTypeK.INTEGER, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro-1);
    }

    public void acao35(){
        System.out.println("Acao 35 - reconhecimento do fim do comando de repetição");
        Integer desvioAcao34 = this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
        instructionList.get(desvioAcao34).setParameter(new DataFrameK(DataTypeK.ADDRESS, this.ponteiro + 1));
        Integer desvioAcao33 = this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
        instructionList.add(new InstructionK(InstructionK.Mnemonic.JMP, new DataFrameK(DataTypeK.ADDRESS, desvioAcao33)));
        this.ponteiro++;
    }

    public void acao36(){
        System.out.println("Acao 36 - reconhecimento de operação relacional igual");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.EQL, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao37(){
        System.out.println("Acao 37 - reconhecimento de operação relacional diferente");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.DIF, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao38(){
        System.out.println("Acao 38 - reconhecimento de operação relacional menor");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.SMR, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao39(){
        System.out.println("Acao 39 - reconhecimento de operação relacional maior");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.BGR, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao40(){
        System.out.println("Acao 40 - reconhecimento de operação relacional menor igual");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.SME, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao41(){
        System.out.println("Acao 41 - reconhecimento de operação relacional maior igual");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.BGE, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao42(){
        System.out.println("Acao 42 - reconhecimento de operação aritmética adição");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.ADD, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao43(){
        System.out.println("Acao 43 - reconhecimento de operação aritmética subtração");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.SUB, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao44(){
        System.out.println("Acao 44 - reconhecimento de operação lógica OU ( | )");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.OR, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao45(){
        System.out.println("Acao 45 - reconhecimento de operação aritmética multiplicação");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.MUL, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao46(){
        System.out.println("Acao 46 - reconhecimento de operação aritmética divisão real");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.DIV, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao47(){
        System.out.println("Acao 47 - reconhecimento de operação aritmética divisão inteira");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.DVW, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao48(){
        System.out.println("Acao 48 - reconhecimento de operação aritmética resto da divisão inteira");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.MOD, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao49(){
        System.out.println("Acao 49 - reconhecimento de operação lógica E (&)\n");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.AND, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao50(){
        System.out.println("Acao 50 - reconhecimento de operação aritmética potenciação");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.PWR, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = ponteiro + 1;
    }

    public void acao51(Token token){
        System.out.println("Acao 51 - reconhecimento de identificador de constante ou de variável e tamanho de variável indexada em expressão");
        Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> this.identificadorReconhecido.equals(simb.getIdentificador())).findAny().orElse(null);
        if(!this.variavelIndexada){
            System.out.println("Acao 51 - Variavel indexada: false");
            if(exist.getAtributo2() == 0){
                System.out.println("Acao 51 - Atributo 2 = vazio/0");
                instructionList.add(new InstructionK(InstructionK.Mnemonic.LDV, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1())));
                this.ponteiro = this.ponteiro + 1;
            }else{
                this.listaErros.add("51 - Variavel indexada precisa de um index: '"+this.identificadorReconhecido+"' - Linha/Coluna: "+token.beginLine+"/"+token.beginColumn);
            }
        }else{
            if(exist.getAtributo2() != 0){
                System.out.println("Acao 51 - Atributo 2 é diferente de vazio/0");
                instructionList.add(new InstructionK(InstructionK.Mnemonic.LDV, new DataFrameK(DataTypeK.ADDRESS, exist.getAtributo1() + this.constanteInteira -1)));
                this.ponteiro = this.ponteiro + 1;
            }else{
                this.listaErros.add("51 - Identificador de constante ou variavel nao indexada: '"+this.identificadorReconhecido+"' - Linha/Coluna: "+token.beginLine+"/"+token.beginColumn);
            }
        }
    }

    public void acao52(){
        System.out.println("Acao 52 - reconhecimento de constante lógica true");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDB, new DataFrameK(DataTypeK.BOOLEAN, true)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao53(){
        System.out.println("Acao 53 - reconhecimento de constante lógica untrue");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.LDB, new DataFrameK(DataTypeK.BOOLEAN, false)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao54(){
        System.out.println("Acao 54 - reconhecimento de operação lógica não ( ! )");
        instructionList.add(new InstructionK(InstructionK.Mnemonic.NOT, new DataFrameK(DataTypeK.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

}