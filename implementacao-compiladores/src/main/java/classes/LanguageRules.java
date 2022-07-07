package classes;

import maquinavirtual.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LanguageRules { // AcoesSemanticas
    private String contexto = "";
    private int VT = 0; // contador para número total de constantes e de variáveis
    private int VP = 0; // contador para número de constantes ou variáveis de um determinado tipo;
    private int VIT = 0; // contador para o tamanho das variáveis indexadas de um determinado tipo;
    private int tipo; // 1 variável natural, 2 variável real, 3 variável char, 4 variável boolean, 5 constante natural, 6 constante real, 7 constante char;
    private int ponteiro = 1;  // indicador da posição onde será gerada a próxima instrução na área de instruções;
    boolean variavelIndexada = false;
    private List<Integer> pilhaDeDesvios = new ArrayList<>(); //Lembrar de controlar para trabalhar como LIFO
    private List<Simbolo> tabelaDeSimbolos = new ArrayList<>();
    private static List<Instruction> instructionList = new ArrayList<>();
    private String identificadorReconhecido;
    private int constanteInteira;
    private List<Object> listaAtributos = new ArrayList<>();
    private static List<String> listaErros = new ArrayList<>();

    public List<Instruction> getInstructionList() {
        // Ugly hack: figure out why do is this list blank after the .JJ has run.
        var tmp =  instructionList;
        instructionList = new ArrayList<>();
        return tmp;
    }

    public List<String> getListaErros() {
        // Ugly hack: figure out why do is this list blank after the .JJ has run.
        var tmp = listaErros;
        listaErros = new ArrayList<>();
        return tmp;
    }

    // OK
    public void acao2(){
        System.out.println("gerar instrução: (ponteiro, STP, 0)");
        Instruction instruction = new Instruction(Instruction.Mnemonic.STP, new DataFrame(DataType.INTEGER, 0));
        instructionList.add(instruction);
        Instruction.enumerateInstructions(instructionList);
        System.out.println("Lista de Erros: " + listaErros.toString());
        System.out.println("Lista de Instruções:");
        for (Instruction i : instructionList) {
            System.out.println(i.toString());
        }
    }

    // OK
    public void acao1(Token token){
        System.out.println("inserir na tabela de símbolos a tupla (identificador, 0, -, -)");
        Simbolo simbolo = new Simbolo(token.image, 0);
        tabelaDeSimbolos.add(simbolo);
    }

//    public void acao3(){ // TODO implementar
//        System.out.println("reconhecimento da palavra reservada not variable");
//        this.contexto = "constante";
//        this.VIT = 0;
//    }

    //OK
    public void acao6(){
        System.out.println("reconhecimento do término da declaração de constantes ou variáveis de um determinado tipo");
        this.VP = this.VP + this.VIT;
        switch (this.tipo){
            case 1: case 5: {
                Instruction instruction = new Instruction(Instruction.Mnemonic.ALI, new DataFrame(DataType.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 2:
            case 6:
            {
                Instruction instruction = new Instruction(Instruction.Mnemonic.ALR, new DataFrame(DataType.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 3: case 7: {
                Instruction instruction = new Instruction(Instruction.Mnemonic.ALS, new DataFrame(DataType.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
            case 4:{
                Instruction instruction = new Instruction(Instruction.Mnemonic.ALB, new DataFrame(DataType.INTEGER, this.VP));
                instructionList.add(instruction);
                break;
            }
        }
        if(this.tipo == 1 || this.tipo == 2 || this.tipo == 3 || this.tipo == 4){
            this.VP = 0;
            this.VIT = 0;
        }
    }

    // OK
    public void acao7(String valor){
        System.out.println("reconhecimento de valor na declaração de constante");
        switch (this.tipo){
            case 5: {
                Instruction instruction = new Instruction(Instruction.Mnemonic.LDI, new DataFrame(DataType.INTEGER, Integer.parseInt(valor)));
                instructionList.add(instruction);
                break;
            }
            case 6: {
                Instruction instruction = new Instruction(Instruction.Mnemonic.LDR, new DataFrame(DataType.FLOAT, Float.parseFloat(valor)));
                instructionList.add(instruction);
                break;
            }
            case 7: {
                Instruction instruction = new Instruction(Instruction.Mnemonic.LDS, new DataFrame(DataType.LITERAL, valor));
                instructionList.add(instruction);
                break;
            }
        }
        Instruction instruction = new Instruction(Instruction.Mnemonic.STC, new DataFrame(DataType.INTEGER, this.VP));
        instructionList.add(instruction);
        this.ponteiro = this.ponteiro + 1;
        this.VP = 0;
    }

    //OK
    public void acao8(){
        System.out.println("reconhecimento da palavra reservada variable");
        this.contexto = "variável";
    }

    //OK
    public void acao13(){
        System.out.println(": reconhecimento da palavra reservada natural");
        if(this.contexto.equals("variável")){
            this.tipo = 1;
        }else{
            this.tipo = 5;
        }
    }

    //OK
    public void acao14(){
        System.out.println("reconhecimento da palavra reservada real");
        if(this.contexto.equals("variável")){
            this.tipo = 2;
        }else{
            this.tipo = 6;
        }
    }

    //OK
    public void acao15(){
        System.out.println(": reconhecimento da palavra reservada char");
        if(this.contexto.equals("variável")){
            this.tipo = 3;
        }else{
            this.tipo = 7;
        }
    }

    //OK
    public void acao16(Token token){
        System.out.println(" reconhecimento da palavra reservada boolean"+ token.image);
        if(this.contexto.equals("variável")){
            this.tipo = 4;
        }else{
            //Verificar posteriormente se precisa adicionar em um array de erros para prosseguir ou não;
            this.listaErros.add("10 - Invalid type for constant - Line/Column: "+token.beginLine+"/"+token.beginColumn);
        }
    }

    //OK
    public void acao11(Token token){ // TODO implementar depois
//        System.out.println("reconhecimento de identificador de constante");
//        Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> token.image.equals(simb.getIdentificador())).findAny().orElse(null);
//        if(!(exist == null)){
//            this.listaErros.add("11 - Identifier already declared: '" + token.image + "'  - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//        }else{
//            this.VT = this.VT + 1;
//            this.VP = this.VP + 1;
//            Simbolo simbolo = new Simbolo(token.image, this.tipo, this.VT);
//            tabelaDeSimbolos.add(simbolo);
//        }
    }

    //OK
    public void acao12(Token token){  // TODO implementar depois
//        System.out.println("reconhecimento de identificador de variável");
//        if(this.contexto.equals("variável")){
//            Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> token.image.equals(simb.getIdentificador())).findAny().orElse(null);
//            if(!(exist == null)){
//                this.listaErros.add("12 - Identifier already declared: '" + token.image + "' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//            }else{
//                this.variavelIndexada = false;
//                this.identificadorReconhecido = token.image;
//            }
//        }else{
//            this.tipo = 7;
//            this.identificadorReconhecido = token.image;
//        }
    }

    //OK
    public void acao13(Token token){ // TODO implementar depois
//        System.out.println("reconhecimento de identificador de variável e tamanho da variável indexada");
//        switch (this.contexto){
//            case "variável": {
//                if(!this.variavelIndexada){
//                    this.VT= this.VT + 1;
//                    this.VP= this.VP + 1;
//                    Simbolo simbolo = new Simbolo(this.identificadorReconhecido, this.tipo, this.VT);
//                    tabelaDeSimbolos.add(simbolo);
//                }else{
//                    this.VIT = this.VIT + this.constanteInteira;
//                    Simbolo simbolo = new Simbolo(this.identificadorReconhecido, this.tipo, this.VT+1, this.constanteInteira);
//                    tabelaDeSimbolos.add(simbolo);
//                    this.VT = this.VT + this.constanteInteira;
//                }
//                break;
//            }
//            case "atribuição": {
//                Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> this.identificadorReconhecido.equals(simb.getIdentificador())).findAny().orElse(null);
//                if(!(exist == null) && (exist.getCategoria() == 1 || exist.getCategoria() == 2 || exist.getCategoria() == 3 || exist.getCategoria() == 4)) {
//                    if(exist.getAtributo2() == 0){
//                        if(!this.variavelIndexada){
//                            this.listaAtributos.add(exist.getAtributo1());
//                        }else{
//                            this.listaErros.add("13 - Identifier of non-indexed variable: '" + this.identificadorReconhecido + "' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                        }
//                    } else{
//                        if(this.variavelIndexada){
//                            this.listaAtributos.add(exist.getAtributo1() + this.constanteInteira -1);
//                        }else{
//                            this.listaErros.add("13 - Indexed variables require an index - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                        }
//                    }
//                }else{
//                    this.listaErros.add("13 - Use of undeclared variable or constant - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                }
//                break;
//            }
//            case "entrada dados": {
//                Simbolo exist = this.tabelaDeSimbolos.stream().filter(simb -> this.identificadorReconhecido.equals(simb.getIdentificador())).findAny().orElse(null);
//                if(!(exist == null) && (exist.getCategoria() == 1 || exist.getCategoria() == 2 || exist.getCategoria() == 3 || exist.getCategoria() == 4)) {
//                    if(exist.getAtributo2() == 0){
//                        if(!this.variavelIndexada){
//                            instructionList.add(new Instruction(Instruction.Mnemonic.REA, new DataFrame(DataType.get(exist.getCategoria()), exist.getCategoria())));
//                            this.ponteiro = this.ponteiro + 1;
//                            instructionList.add(new Instruction(Instruction.Mnemonic.STR, new DataFrame(DataType.ADDRESS, exist.getAtributo1())));
//                            this.ponteiro = this.ponteiro + 1;
//                        }else{
//                            this.listaErros.add("13 - Identifier of non-indexed variable - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                        }
//                    } else{
//                        if(this.variavelIndexada){
//                            instructionList.add(new Instruction(Instruction.Mnemonic.REA, new DataFrame(DataType.get(exist.getCategoria()), exist.getCategoria())));
//                            this.ponteiro = this.ponteiro + 1;
//                            instructionList.add(new Instruction(Instruction.Mnemonic.STR, new DataFrame(DataType.ADDRESS, exist.getAtributo1() + this.constanteInteira -1)));
//                            this.ponteiro = this.ponteiro + 1;
//                        }else{
//                            this.listaErros.add("13 - Indexed variables requires an index - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                        }
//                    }
//                }else{
//                    this.listaErros.add("13 - Use of undeclared variable or constant: '"+this.identificadorReconhecido+"' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
//                }
//                break;
//            }
//        }
    }

    //OK
    public void acao12(Token token){
        System.out.println(" reconhecimento de constante inteira como tamanho da variável indexada ou como índice");
        this.constanteInteira = Integer.parseInt(token.image);
        this.variavelIndexada = true;
    }

    //OK
    public void acao18(){
        System.out.println("reconhecimento do início do comando de atribuição");
        this.contexto = "atribuição";
    }

    //OK
    public void acao19(){
        System.out.println(": reconhecimento do fim do comando de atribuição");
        for(int i=0; i< this.listaAtributos.size(); i++){
            instructionList.add(new Instruction(Instruction.Mnemonic.STR, new DataFrame(DataType.ADDRESS, this.listaAtributos.get(i))));
            this.ponteiro = this.ponteiro + 1;
        }
    }

    //OK
//    public void acao17(){ // TODO implementar depois
//        System.out.println("reconhecimento do comando de entrada de dados");
//        this.contexto = "entrada dados";
//    }

    //OK
    public void acao23(){
        System.out.println("reconhecimento de mensagem em comando de saída de dados");
        instructionList.add(new Instruction(Instruction.Mnemonic.WRT, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao24(Token token){
        System.out.println("reconhecimento de identificador em comando de saída ou em expressão");
        Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> token.image.equals(simb.getIdentificador())).findAny().orElse(null);
        if(!(exist == null)){
            this.variavelIndexada = false;
            this.identificadorReconhecido = token.image;
        }else{
            this.listaErros.add("19 - Use of non-declared identifier: '"+token.image+"' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
        }
    }

    //OK
    public void acao51(Token token){
        System.out.println(" reconhecimento de índice de variável indexada em comando de saída");
        Simbolo exist = tabelaDeSimbolos.stream().filter(simb -> this.identificadorReconhecido.equals(simb.getIdentificador())).findAny().orElse(null);
        if(!this.variavelIndexada){
            if(exist.getAtributo2() == 0){
                instructionList.add(new Instruction(Instruction.Mnemonic.LDV, new DataFrame(DataType.ADDRESS, exist.getAtributo1())));
                this.ponteiro = this.ponteiro + 1;
            }else{
                this.listaErros.add("20 - Indexed variables requires an index: '"+this.identificadorReconhecido+"' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
            }
        }else{
            if(exist.getAtributo2() != 0){
                instructionList.add(new Instruction(Instruction.Mnemonic.LDV, new DataFrame(DataType.ADDRESS, exist.getAtributo1() + this.constanteInteira -1)));
                this.ponteiro = this.ponteiro + 1;
            }else{
                this.listaErros.add("20 - Identifier of non-indexed constant or variable: '"+this.identificadorReconhecido+"' - Line/Column: "+token.beginLine+"/"+token.beginColumn);
            }
        }
    }

    //OK
    public void acao26(Integer constInt){
        System.out.println("reconhecimento de constante inteira em comando de saída ou em expressão");
        instructionList.add(new Instruction(Instruction.Mnemonic.LDI, new DataFrame(DataType.INTEGER, constInt)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao27(Float constReal){
        System.out.println("reconhecimento de constante real em comando de saída ou em expressão");
        instructionList.add(new Instruction(Instruction.Mnemonic.LDR, new DataFrame(DataType.FLOAT, constReal)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao28(String constLiteral){
        System.out.println("reconhecimento de constante literal em comando de saída ou em expressão");
        instructionList.add(new Instruction(Instruction.Mnemonic.LDS, new DataFrame(DataType.LITERAL, constLiteral)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao29(){
        //ACHO que é isso
        System.out.println("reconhecimento de fim de comando de seleção");
        this.pilhaDeDesvios.set(this.pilhaDeDesvios.size()-1, ponteiro);
    }

    //OK
    public void acao30(){
        System.out.println(" reconhecimento da palavra reservada true");
        instructionList.add(new Instruction(Instruction.Mnemonic.JMF, new DataFrame(DataType.NONE, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro -1);
    }

    //OK
//    public void acao26(){ // TODO implementar depois
//        System.out.println(" reconhecimento da palavra reservada false");
//        instructionList.add(new Instruction(Instruction.Mnemonic.JMT, new DataFrame(DataType.ADDRESS, '?')));
//        this.ponteiro = this.ponteiro + 1;
//        this.pilhaDeDesvios.add(this.ponteiro -1);
//    }
//
//    //OK
//    public void acao27(){ // TODO implementar depois
//        System.out.println("reconhecimento da palavra reservada false (ou true)");
//        this.pilhaDeDesvios.set(this.pilhaDeDesvios.size()-1, ponteiro+1);
//        instructionList.add(new Instruction(Instruction.Mnemonic.JMP, new DataFrame(DataType.NONE, '?')));
//        this.ponteiro = this.ponteiro + 1;
//        this.pilhaDeDesvios.add(this.ponteiro -1);
//    }
//
//    //OK
//    public void acao28(){ // TODO implementar depois
//        System.out.println("reconhecimento do comando de repetição");
//        this.pilhaDeDesvios.add(this.ponteiro);
//    }
//
//    //OK
//    public void acao29(){ // TODO implementar depois
//        System.out.println("reconhecimento do fim do comando de repetição");
//        Integer p = this.pilhaDeDesvios.get(this.pilhaDeDesvios.size()-1);
//        this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
//        instructionList.add(new Instruction(Instruction.Mnemonic.JMT, new DataFrame(DataType.ADDRESS, p)));
//        this.ponteiro = this.ponteiro + 1;
//    }

    //OK
    public void acao33(){
        System.out.println("reconhecimento do início de expressão em comando de repetição");
        this.pilhaDeDesvios.add(this.ponteiro);
    }

    //OK
    public void acao34(){
        System.out.println("reconhecimento de expressão em comando de repetição");
        instructionList.add(new Instruction(Instruction.Mnemonic.JMF, new DataFrame(DataType.INTEGER, '?')));
        this.ponteiro = this.ponteiro + 1;
        this.pilhaDeDesvios.add(this.ponteiro-1);
    }

//    //rever - Não entendi bem o que é para fazer nessa função;
//    public void acao35(){
//        System.out.println("reconhecimento do fim do comando de repetição");
//        Integer p = this.pilhaDeDesvios.get(this.pilhaDeDesvios.size()-1);
//        this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
//        p = ponteiro + 1;
//        this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
//        instructionList.add(new Instruction(Instruction.Mnemonic.JMP, new DataFrame(DataType.INTEGER, p)));
//    }

// essa aqui deve ser a correta
    public void acao35(){
        System.out.println("reconhecimento do fim do comando de repetição");
        Integer desvioAcao31 = this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
        instructionList.get(desvioAcao31).setParameter(new DataFrame(DataType.ADDRESS, this.ponteiro + 1));
        Integer desvioAcao30 = this.pilhaDeDesvios.remove(this.pilhaDeDesvios.size()-1);
        instructionList.add(new Instruction(Instruction.Mnemonic.JMP, new DataFrame(DataType.ADDRESS, desvioAcao30)));
        this.ponteiro++;
    }

    //OK
    public void acao36(){
        System.out.println("reconhecimento de operação relacional igual");
        instructionList.add(new Instruction(Instruction.Mnemonic.EQL, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao37(){
        System.out.println("reconhecimento de operação relacional diferente");
        instructionList.add(new Instruction(Instruction.Mnemonic.DIF, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao38(){
        System.out.println("reconhecimento de operação relacional menor");
        instructionList.add(new Instruction(Instruction.Mnemonic.SMR, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao39(){
        System.out.println("reconhecimento de operação relacional maior");
        instructionList.add(new Instruction(Instruction.Mnemonic.BGR, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao40(){
        System.out.println("reconhecimento de operação relacional menor igual");
        instructionList.add(new Instruction(Instruction.Mnemonic.SME, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao41(){
        System.out.println("reconhecimento de operação relacional maior igual");
        instructionList.add(new Instruction(Instruction.Mnemonic.BGE, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao42(){
        System.out.println("reconhecimento de operação aritmética adição");
        instructionList.add(new Instruction(Instruction.Mnemonic.ADD, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao43(){
        System.out.println("reconhecimento de operação aritmética subtração");
        instructionList.add(new Instruction(Instruction.Mnemonic.SUB, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao44(){
        System.out.println("reconhecimento de operação lógica OU ( | )");
        instructionList.add(new Instruction(Instruction.Mnemonic.OR, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao45(){
        System.out.println("reconhecimento de operação aritmética multiplicação");
        instructionList.add(new Instruction(Instruction.Mnemonic.MUL, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao46(){
        System.out.println("reconhecimento de operação aritmética divisão real");
        instructionList.add(new Instruction(Instruction.Mnemonic.DIV, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao47(){
        System.out.println("reconhecimento de operação aritmética divisão inteira");
        instructionList.add(new Instruction(Instruction.Mnemonic.DVW, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao48(){
        System.out.println("reconhecimento de operação aritmética resto da divisão inteira");
        instructionList.add(new Instruction(Instruction.Mnemonic.MOD, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao49(){
        System.out.println("reconhecimento de operação lógica E (&)\n");
        instructionList.add(new Instruction(Instruction.Mnemonic.AND, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }

    public void acao50(){
        System.out.println(" reconhecimento de operação aritmética potenciação");
        instructionList.add(new Instruction(Instruction.Mnemonic.PWR, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = ponteiro + 1;
    }

    //OK
    public void acao52(){
        System.out.println("reconhecimento de constante lógica true");
        instructionList.add(new Instruction(Instruction.Mnemonic.LDB, new DataFrame(DataType.BOOLEAN, true)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao53(){
        System.out.println("reconhecimento de constante lógica false");
        instructionList.add(new Instruction(Instruction.Mnemonic.LDB, new DataFrame(DataType.BOOLEAN, false)));
        this.ponteiro = this.ponteiro + 1;
    }

    //OK
    public void acao54(){
        System.out.println("reconhecimento de operação lógica NÃO ( ! )");
        instructionList.add(new Instruction(Instruction.Mnemonic.NOT, new DataFrame(DataType.INTEGER, 0)));
        this.ponteiro = this.ponteiro + 1;
    }




//////////////////////////
//////////////////////////
//////////////////////////
//////////////////////////
//////////////////////////
//////////////////////////
//////////////////////////
//////////////////////////
// TODO REMOVER DEPOIS

//    private String contexto;
//    private String verificarAtribuicao;
//    private Integer VT; //Número de constantes e de variável
//    private Integer VP; //Número de constantes ou variáveis de um determinado tipo;
//    private Integer VIT; //Tamanho das variáveis indexadas de um determinado tipo;
//    private Integer tipo; //Iindica um determinado tipo de constante ou variável. (variável 1-NATURAL 2-REAL 3-CHAR 4-BOOLEAN) (constante 5-NATURAL 6-REAL 7-CHAR)
//    private Integer ponteiro; //Indicador da posição onde será gerada a próxima instrução na área de instruções
//    private Boolean variavel_indexada;//Valor lógico que indica ou não a ocorrência de uma variável indexada;
//    private List<String> pilha_de_desvios; //Pilha de endereços para resolução de desvios com operandos inicialmente desconhecidos, quando da análise dos comandos de seleção e de repetição;
//    private List<SymbolTable> pilha_de_simbolos;
//    private List<Instruction> pilha_de_instrucoes;
//    private List<String> error;
//    private String identificadorAcao10;
//    private String identificadorAcao18;
//    private Integer constante_inteiraAcao13;
//    private List<SymbolTable> list_atributos;
//
//    public LanguageRules() {
//        pilha_de_simbolos = new ArrayList<>();
//        pilha_de_instrucoes = new ArrayList<>();
//        pilha_de_desvios = new ArrayList<>();
//        error = new ArrayList<>();
//        list_atributos = new ArrayList<>();
//        identificadorAcao10 = "";
//        identificadorAcao18 = "";
//        constante_inteiraAcao13 = 0;
//        ponteiro = 1;
//        VT = 0;
//        VP = 0;
//        VIT = 0;
//        tipo = 0;
//        variavel_indexada = false;
//        contexto = "";
//    }
//
//    public List<String> getError() {
//        return error;
//    }
//
//    public List<SymbolTable> getPilha_de_simbolos() {
//        return pilha_de_simbolos;
//    }
//
//    public List<Instruction> getPilha_de_instrucoes() {
//        return pilha_de_instrucoes;
//    }
//
//    private boolean VerificarIdentificadorExistenteTabelaSimbolo(String identificador){
//        boolean retorno = false;
//        for (SymbolTable tabelaSimbolos : pilha_de_simbolos) {
//            if(tabelaSimbolos.getIdentificador().toUpperCase().equals(identificador.toUpperCase())){
//                retorno = true;
//                break;
//            }
//        }
//        return retorno;
//    }
//
//
//    private SymbolTable RecuperarSinbolo(String identificador){
//        for (SymbolTable tabelaSimbolos : pilha_de_simbolos) {
//            if(tabelaSimbolos.getIdentificador().toUpperCase().equals(identificador.toUpperCase())){
//                return tabelaSimbolos;
//            }
//        }
//        return null;
//    }
//
//    // REGRAS PRONTAS DAQUI PRA BAIXO
//
//    public void regra1(Token token){
//        System.out.println("inserir na tabela de símbolos a tupla (identificador, 0, -, -)");
//        SymbolTable simbolo = new SymbolTable(token.image, 0);
//        pilha_de_simbolos.add(simbolo);
//    }
//
//    public void regra2(String identificador){
//        pilha_de_simbolos.add(new SymbolTable(identificador, 0, "-", "-"));
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "STP", "0"));
//    }
//
//    public void regra3() {
//        // TODO implementar
//    }
//
//    public void regra4() {
//        // TODO implementar
//    }
//
//    public void regra5() {
//        // TODO implementar
//    }
//
//
//    public void regra6(){
//        VP = VP + VIT;
//        switch(tipo.toString()){
//            case "1":
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALI", VP.toString()));
//                ponteiro = ponteiro + 1;
//                break;
//            case "2":
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALR", VP.toString()));
//                ponteiro = ponteiro + 1;
//                break;
//            case "3":
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALS", VP.toString()));
//                ponteiro = ponteiro + 1;
//                break;
//            case "4":
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALB", VP.toString()));
//                ponteiro = ponteiro + 1;
//                break;
//            case "5":
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALI", VP.toString()));
//                ponteiro = ponteiro + 1;
//                break;
//            case "6":
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALR", VP.toString()));
//                ponteiro = ponteiro + 1;
//                break;
//            case "7":
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "ALS", VP.toString()));
//                ponteiro = ponteiro + 1;
//                break;
//            default :
//                break;
//        }
//
//        if (tipo.toString().equals("1") || tipo.toString().equals("2") || tipo.toString().equals("3") || tipo.toString().equals("4")){
//            VP = 0;
//            VIT = 0;
//        }
//    }
//
//    public void regra7(String valor){
//        switch(tipo.toString()){
//            case "5":
//                Pattern roma = Pattern.compile("[0-9]{1,3}");
//                if(!roma.matcher(valor).matches()){
//                    error.add("O valor (" + valor + ") é um inteiro inválido");
//                }
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDI", valor));
//                ponteiro = ponteiro + 1;
//                break;
//            case "6":
//                Pattern romafloat = Pattern.compile("[0-9]{1,5}.[0-9]{1,2}");
//                if(!romafloat.matcher(valor).matches()){
//                    error.add("O valor (" + valor + ") é um float inválido");
//                }
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDR", valor));
//                ponteiro = ponteiro + 1;
//                break;
//            case "7":
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDS", valor));
//                ponteiro = ponteiro + 1;
//            default :
//                break;
//        }
//
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "STC", VP.toString()));
//        ponteiro = ponteiro + 1;
//        VP = 0;
//    }
//
//    public void regra8(){
//        contexto = "VARIAVEL";
//    }
//
//    public void regra9(String identificador){
//        if(VerificarIdentificadorExistenteTabelaSimbolo(identificador)){
//            error.add("Identificador (" + identificador + ") já declarado");
//        }else{
//            VT = VT + 1;
//            VP = VP + 1;
//            pilha_de_simbolos.add(new SymbolTable(identificador, tipo, VT.toString(), "-"));
//        }
//    }
//
//    public void regra10(String identificador){
//        if(contexto.toUpperCase().equals("VARIAVEL")){
//            if(VerificarIdentificadorExistenteTabelaSimbolo(identificador)){
//                error.add("Identificador (" + identificador + ") já declarado");
//            }else{
//                variavel_indexada = false;
//                identificadorAcao10 = identificador;
//            }
//        }else{
//            variavel_indexada = false;
//            identificadorAcao10 = identificador;
//        }
//    }
//
//    public void regra11(){
//        Integer auxVT;
//        Integer auxAtributo;
//        switch(contexto){
//            case "VARIAVEL":
//                if(!variavel_indexada){
//                    VT = VT + 1;
//                    VP = VP + 1;
//                    pilha_de_simbolos.add(new SymbolTable(identificadorAcao10, tipo, VT.toString(), "-"));
//                }else{
//                    VIT = VIT + constante_inteiraAcao13;
//                    auxVT = VT +1;
//                    pilha_de_simbolos.add(new SymbolTable(identificadorAcao10, tipo, auxVT.toString(), constante_inteiraAcao13.toString()));
//                    VT = VT + constante_inteiraAcao13;
//                }
//                break;
//            case "ATRIBUICAO":
//                SymbolTable tabelaSimbolo;
//                tabelaSimbolo = RecuperarSinbolo(identificadorAcao10);
//                if(VerificarIdentificadorExistenteTabelaSimbolo(identificadorAcao10) && (tabelaSimbolo.getCategoria() == 1
//                        || tabelaSimbolo.getCategoria() == 2 || tabelaSimbolo.getCategoria() == 3 || tabelaSimbolo.getCategoria() == 4)){
//                    if(tabelaSimbolo.getAtributo2().equals("-")){
//                        if(!variavel_indexada){
//                            list_atributos.add(tabelaSimbolo);
//                        }else{
//                            error.add("Identificador (" + identificadorAcao10 + ") de variável não indexada");
//                        }
//                    }else{
//                        if(variavel_indexada){
//                            SymbolTable newSimbolo = tabelaSimbolo;
//                            auxAtributo = Integer.parseInt(tabelaSimbolo.getAtributo1())+constante_inteiraAcao13-1;
//                            newSimbolo.setAtributo1(auxAtributo.toString());
//                            list_atributos.add(newSimbolo);
//                        }else{
//                            error.add("Identificador (" + identificadorAcao10 + ") de variável indexada exige índice");
//                        }
//                    }
//                }else{
//                    error.add("Identificador (" + identificadorAcao10 + ") não declarado ou de constante");
//                }
//                break;
//            case "ENTRADA DADOS":
//                tabelaSimbolo = RecuperarSinbolo(identificadorAcao10);
//                if(VerificarIdentificadorExistenteTabelaSimbolo(identificadorAcao10) && (tabelaSimbolo.getCategoria() == 1
//                        || tabelaSimbolo.getCategoria() == 2 || tabelaSimbolo.getCategoria() == 3 || tabelaSimbolo.getCategoria() == 4)){
//                    if(tabelaSimbolo.getAtributo2().equals("-")){
//                        if(!variavel_indexada){
//                            pilha_de_instrucoes.add(new Instruction(ponteiro, "REA", tabelaSimbolo.getCategoria().toString()));
//                            ponteiro = ponteiro + 1;
//                            pilha_de_instrucoes.add(new Instruction(ponteiro, "STR", tabelaSimbolo.getAtributo1()));
//                            ponteiro = ponteiro + 1;
//                        }else{
//                            error.add("Identificador (" + identificadorAcao10 + ") de variável não indexada");
//                        }
//                    }else{
//                        if(variavel_indexada){
//                            pilha_de_instrucoes.add(new Instruction(ponteiro, "REA", tabelaSimbolo.getCategoria().toString()));
//                            ponteiro = ponteiro + 1;
//                            auxAtributo = Integer.parseInt(tabelaSimbolo.getAtributo1()) + constante_inteiraAcao13-1;
//                            pilha_de_instrucoes.add(new Instruction(ponteiro, "STR", auxAtributo.toString()));
//                            ponteiro = ponteiro + 1;
//                        }else{
//                            error.add("Identificador (" + identificadorAcao10 + ") de variável indexada exige índice");
//                        }
//                    }
//                }else{
//                    error.add("Identificador (" + identificadorAcao10 + ") não declarado ou de constante");
//                }
//                break;
//            default :
//                break;
//        }
//    }
//
//    public void regra12(Integer constante_inteira){
//        constante_inteiraAcao13 = constante_inteira;
//        variavel_indexada = true;
//    }
//
//    public void regra13(){
//        if(contexto.toUpperCase().equals("VARIAVEL")){
//            tipo = 1;
//        }else{
//            tipo = 5;
//        }
//    }
//
//    public void regra14(){
//        if(contexto.toUpperCase().equals("VARIAVEL")){
//            tipo = 2;
//        }else{
//            tipo = 6;
//        }
//    }
//
//    public void regra15(){
//        if(contexto.toUpperCase().equals("VARIAVEL")){
//            tipo = 3;
//        }else{
//            tipo = 7;
//        }
//    }
//
//    public void regra16(){
//        if(contexto.toUpperCase().equals("VARIAVEL")){
//            tipo = 4;
//        }else{
//            error.add("Tipo inválido para constante. Não é possível criar constante booleana");
//        }
//    }
//
//    public void regra17() {
//        // TODO falta implementar
//    }
//
//    public void regra18(){
//        contexto = "ATRIBUICAO";
//    }
//
//    public void regra19() {
//        for(int i=0; i< this.list_atributos.size(); i++){
////            pilha_de_instrucoes.add(new Instruction(Instruction.Mnemonic.STR, new DataFrame(DataType.ADDRESS, this.listaAtributos.get(i))));
//            pilha_de_instrucoes.add(new InstructionK(InstructionK.Mnemonic.STR, new DataFrameK(DataTypeK.ADDRESS, this.list_atributos.get(i))));
//            this.ponteiro = this.ponteiro + 1;
//        }
//    }
//
//    public void regra20(){
//        contexto = "ENTRADA DADOS";
//    }
//
//    public void regra23(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "WRT", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra24(String identificador){
//        SymbolTable tabelaSimbolo;
//        tabelaSimbolo = RecuperarSinbolo(identificador);
//        if(VerificarIdentificadorExistenteTabelaSimbolo(identificador) && (tabelaSimbolo.getCategoria() != 0)){
//            variavel_indexada = false;
//            identificadorAcao18 = identificador;
//        }else{
//            error.add("Identificador (" + identificador + ") não declarado");
//        }
//    }
//
//    public void regra25(){
//        SymbolTable tabelaSimbolo;
//        tabelaSimbolo = RecuperarSinbolo(identificadorAcao18);
//        Integer auxAtributo;
//        if(!variavel_indexada){
//            if(tabelaSimbolo.getAtributo2().equals("-")){
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDV", tabelaSimbolo.getAtributo1()));
//                ponteiro = ponteiro + 1;
//            }else{
//                error.add("Identificador (" + identificadorAcao18 + ") de variável indexada exige índice");
//            }
//        }else{
//            if(!tabelaSimbolo.getAtributo2().equals("-")){
//                auxAtributo = Integer.parseInt(tabelaSimbolo.getAtributo1()) + constante_inteiraAcao13-1;
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "LDV", auxAtributo.toString()));
//                ponteiro = ponteiro + 1;
//            }else{
//                error.add("Identificador (" + identificadorAcao18 + ") de constante ou de variável não indexada");
//            }
//        }
//    }
//
//    public void regra26(Integer constante_inteira){
//        verificarAtribuicao = constante_inteira.toString();
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDI", constante_inteira.toString()));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra27(Float constante_real){
//        verificarAtribuicao = constante_real.toString();
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDR", constante_real.toString()));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra28(String constante_literal){
//        verificarAtribuicao = constante_literal;
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDS", constante_literal));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra30(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMF", "?"));
//        pilha_de_desvios.add(ponteiro.toString());
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra31(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMT", "?"));
//        pilha_de_desvios.add(ponteiro.toString());
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra32(){
//        //to_do
//        Integer auxPonteiro;
//        String topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
//        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
//        auxPonteiro = ponteiro + 1;
//        for(int i = 0; i < pilha_de_instrucoes.size(); i ++){
//            if(pilha_de_instrucoes.get(i).getPonteiro() == Integer.parseInt(topoPilhaDesvio)){
//                pilha_de_instrucoes.get(i).setEndereco(auxPonteiro.toString());
//                break;
//            }
//        }
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMP", "?"));
//        pilha_de_desvios.add(ponteiro.toString());
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra33(){
//        pilha_de_desvios.add(ponteiro.toString());
//    }
//
//
//    public void regra29(){ // todo ta repetida com a 33
//        pilha_de_desvios.add(ponteiro.toString());
//    }
//
//
//    public void regra35(){
//        Integer auxPonteiro;
//        String topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
//        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
//        auxPonteiro = ponteiro + 1;
//        for(int i = 0; i < pilha_de_instrucoes.size(); i ++){
//            if(pilha_de_instrucoes.get(i).getPonteiro() == Integer.parseInt(topoPilhaDesvio)){
//                pilha_de_instrucoes.get(i).setEndereco(auxPonteiro.toString());
//                break;
//            }
//        }
//        topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
//        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMP", topoPilhaDesvio.toString()));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra36(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "EQL", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra42(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "ADD", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra44(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "OR", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra49(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "AND", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra52(){
//        verificarAtribuicao = "TRUE";
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDB", "TRUE"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra53(){
//        verificarAtribuicao = "FALSE";
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "LDB", "FALSE"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra54(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "NOT", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    ////////
//    ////////
//    //////// DAQUI PRA CIMA TA FINALIZADO
//    ////////
//    ////////
//
//
//    public void regra2(){
//        contexto = "CONSTANTE";
//        VIT = 0;
//    }
//
//    private boolean regexInt(String teste){
//        Pattern roma = Pattern.compile("[0-9]{1,3}");
//        if(!roma.matcher(teste).matches()){
//            return true;
//        }
//        return false;
//    }
//
//    private boolean regexFloat(String teste){
//        Pattern roma = Pattern.compile("[0-9]{1,5}.[0-9]{1,2}");
//        if(!roma.matcher(teste).matches()){
//            return true;
//        }
//        return false;
//    }
//
//    public void regra15(){
//        for (SymbolTable simbolo : list_atributos) {
////            if((verificarAtribuicao.contains(".") && simbolo.getCategoria() == 1) || (simbolo.getCategoria() == 1 && regexInt(verificarAtribuicao))){
////                error.add("O valor (" + verificarAtribuicao + ") é diferente do tipo da variável inteira");
////            } else if((!verificarAtribuicao.contains(".") && simbolo.getCategoria() == 2) || (simbolo.getCategoria() == 2 && regexFloat(verificarAtribuicao))){
////                error.add("O valor (" + verificarAtribuicao + ") é diferente do tipo da variável float");
////            } else if(simbolo.getCategoria() == 4 && !((verificarAtribuicao.equals("TRUE") || verificarAtribuicao.equals("FALSE")))){
////                error.add("O valor (" + verificarAtribuicao + ") é diferente do tipo da variável boolean");
////            }
//            if(simbolo.getCategoria() == 4){
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "STR", verificarAtribuicao));
//            } else {
//                pilha_de_instrucoes.add(new Instruction(ponteiro, "STR", simbolo.getAtributo1()));
//            }
//
//            ponteiro = ponteiro + 1;
//        }
//        list_atributos = new ArrayList<>();
//    }
//
//
//    public void regra23(){
//        String topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
//        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
//        for(int i = 0; i < pilha_de_instrucoes.size(); i ++){
//            if(pilha_de_instrucoes.get(i).getPonteiro() == Integer.parseInt(topoPilhaDesvio)){
//                pilha_de_instrucoes.get(i).setEndereco(ponteiro.toString());
//                break;
//            }
//        }
//    }
//
//
//    public void regra28(){
//        String topoPilhaDesvio = pilha_de_desvios.get(pilha_de_desvios.size()-1);
//        pilha_de_desvios.remove(pilha_de_desvios.size()-1);
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMT", topoPilhaDesvio));
//        ponteiro = ponteiro + 1;
//    }
//
//
//    public void regra30(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "JMF", "?"));
//        pilha_de_desvios.add(ponteiro.toString());
//        ponteiro = ponteiro + 1;
//    }
//
//
//    public void regra33(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "DIF", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra34(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "SMR", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra35(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "BGR", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra36(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "SME", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra37(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "BGE", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//
//    public void regra39(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "SUB", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//
//    public void regra41(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "MUL", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra42(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "DIV", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra43(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "DVI", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//    public void regra44(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "MOD", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//
//    public void regra46(){
//        pilha_de_instrucoes.add(new Instruction(ponteiro, "POT", "0"));
//        ponteiro = ponteiro + 1;
//    }
//
//



}