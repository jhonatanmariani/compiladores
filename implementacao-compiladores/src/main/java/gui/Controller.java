package gui;

import classes.ErrorStruct;
import classes.LanguageParser;
import classes.LanguageParserConstants;
import classes.Token;
import util.AlertFactory;
import util.Operation;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class Controller {
    private EditorFile editorFile = new EditorFile();
    private static boolean hasEditedFile = false;
    private static boolean hasOpenFile = false;
    @FXML
    private Stage stage;
    public CodeArea inputTextArea;
    public TextArea messageTextArea;
    public Label statusBar, lineColLabel;
    // Menu bar items
    public MenuItem saveMenuItem, saveAsMenuItem, exitProgramItem;
    public MenuItem cutMenuItem, copyMenuItem, pasteMenuItem;
    //  Menu toolbar buttons
    public Button newBtn, openBtn, saveBtn;
    public Button copyBtn, cutBtn, pasteBtn;
    public Button buildBtn, runBtn;

    @FXML
    public void openFileDialog(ActionEvent actionEvent) {
        actionEvent.consume();
        if (handleOpenUnsavedFile() != Operation.SUCCESS) {
            return;
        }
        FileChooser filePicker = new FileChooser();
        filePicker.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*." + EditorFile.FILE_EXT));
        filePicker.setInitialDirectory(new File(System.getProperty("user.dir")));
        editorFile = new EditorFile(filePicker.showOpenDialog(new Stage()), false);

        if (!editorFile.isFileStatusOK()) {
            Alert alert = AlertFactory.create
                    (
                            Alert.AlertType.ERROR,
                            "Erro",
                            "Erro",
                            String.format("Falha ao abrir arquivo: %s", editorFile.getFileStatus())
                    );
            alert.showAndWait();
            return;
        }
        fileContentsToCodeArea();
    }

    public void newFileDialog(ActionEvent event) {
        event.consume();
        if (handleOpenUnsavedFile() != Operation.SUCCESS) {
            return;
        }
        inputTextArea.clear();
        clearMessageArea();
        hasOpenFile = false;
        this.editorFile.setFile(null);
        updateStageTitle();
        setStatusMsg("");
    }

    private void fileContentsToCodeArea() {
        hasEditedFile = false;
        hasOpenFile = true;
        inputTextArea.setWrapText(false);
        try {
            inputTextArea.replaceText(editorFile.getFileContents());
        } catch (IOException e) {
            e.printStackTrace();
        }
        setStatusMsg(String.format("Arquivo lido com sucesso %s", editorFile.getFilePath().get()));
        updateStageTitle();
        clearMessageArea();
    }

    private void updateStageTitle() {
        String title = "Compilador";
        if (hasOpenFile) {
            title += String.format(" - [%s]", editorFile.getFilePath().get());
        }
        this.stage.setTitle(title);
    }

    @FXML
    public Operation saveFileDialog(ActionEvent actionEvent) {
        Operation op = Operation.FAILURE;
        actionEvent.consume();
        EditorFile.FileStatus status = editorFile.save(inputTextArea.getText());
        if (status == EditorFile.FileStatus.OK) {
            onSaveSuccess();
            op = Operation.SUCCESS;
        } else {
            AlertFactory.create(Alert.AlertType.ERROR, "Erro", "Falha na operacao",
                    String.format("Falha ao salvar arquivo em '%s'", editorFile.getFilePath().get())).show();
        }
        return op;
    }

    @FXML
    private Operation saveAsDialog(ActionEvent actionEvent) {
        actionEvent.consume();
        Operation op = Operation.CANCELED;
        FileChooser filePicker = new FileChooser();
        filePicker.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text files", "*." + EditorFile.FILE_EXT));
        filePicker.setInitialDirectory(new File(System.getProperty("user.dir")));
        File newFile = filePicker.showSaveDialog(new Stage());
        EditorFile newED = new EditorFile(newFile, false);
        switch (newED.getFileStatus()) {
            case INVALID_EXTENSION -> {
                AlertFactory.create
                        (
                                Alert.AlertType.ERROR,
                                "Erro",
                                "Extensao invalida",
                                "O nome do arquivo deve usar a extensao '.txt'"
                        ).show();
                op = Operation.FAILURE;
            }
            case IO_ERROR -> {
                AlertFactory.create(
                        Alert.AlertType.ERROR,
                        "Erro",
                        "Erro de leitura/escrita de arquivo",
                        "Erro ao ler/escrever arquivo"
                ).show();
                op = Operation.FAILURE;
            }
            case NO_OPEN_FILE -> {
                AlertFactory.create
                        (
                                Alert.AlertType.INFORMATION,
                                "Info",
                                "Operacao cancelada",
                                "Salvar em um novo arquivo cancelado"
                        ).show();
                op = Operation.CANCELED;
            }
            case OK -> {
                editorFile.saveAs(inputTextArea.getText(), newFile);
                System.out.println("Salvo com sucesso");
                onSaveSuccess();
                op = Operation.SUCCESS;
            }
        }
        return op;
    }

    public Operation saveAction() {
        if (hasOpenFile && hasEditedFile) {
            return saveFileDialog(new ActionEvent());
        }
        if (!hasOpenFile && hasEditedFile) {
            return saveAsDialog(new ActionEvent());
        }
        return Operation.SUCCESS;
    }

    private void onSaveSuccess() {
        hasOpenFile = true;
        hasEditedFile = false;
        setStatusMsg("Arquivo salvo");
        updateStageTitle();
        disableSaving(true);
    }

    public void disableSaving(boolean b) {
        saveBtn.setDisable(b);
        saveMenuItem.setDisable(b);
    }

    public void setStatusMsg(String msg) {
        statusBar.setText(String.format("Status: %s", msg));
    }

    public void fileContentChanged() {
        disableSaving(false);
        hasEditedFile = true;
    }

    private void registerLineColUpdater() {
        inputTextArea.caretPositionProperty().addListener((observableValue, integer, t1) -> {
            int line = inputTextArea.getCurrentParagraph();
            int col = inputTextArea.getCaretColumn();
            setLineColLabel(line + 1, col + 1);
        });
    }

    private void setLineColLabel(int line, int col) {
        lineColLabel.setText(String.format("Linha/Coluna: %d:%d", line, col));
    }

    public void registerWindowClose() {
        this.stage.setOnCloseRequest(new ExitButtonListener());
    }

    public void setStage(Stage primaryStage) {
        this.stage = primaryStage;
        inputTextArea.setParagraphGraphicFactory(LineNumberFactory.get(inputTextArea));
        registerWindowClose();
        registerLineColUpdater();
    }

    @FXML
    private Operation handleOpenUnsavedFile() {
        Operation op = Operation.CANCELED;
        Alert alert;
        if (hasEditedFile) {
            alert = AlertFactory.AlertYesNoCancel(Alert.AlertType.CONFIRMATION,
                    "Confirmacao",
                    "Arquivo nao salvo",
                    "Voce editou um arquivo aberto e nao salvou, deseja salva-lo?"
            );
        } else {
            return Operation.SUCCESS;
        }
        Optional<ButtonType> optional = alert.showAndWait();
        if (optional.isEmpty()) {
            return Operation.CANCELED;
        }
        var buttonData = optional.get().getButtonData();
        if (buttonData.equals(ButtonType.YES.getButtonData()) && !hasOpenFile) {
            return saveAsDialog(new ActionEvent());
        }
        if (buttonData.equals(ButtonType.YES.getButtonData())) {
            return saveFileDialog(new ActionEvent());
        }
        if (buttonData.equals(ButtonType.NO.getButtonData())) {
            return Operation.SUCCESS;
        }
        return op;
    }

    private void clearMessageArea() {
        this.messageTextArea.clear();
    }

    public void exitProgram(ActionEvent actionEvent) {
        if (handleOpenUnsavedFile() == Operation.SUCCESS) {
            Platform.exit();
        } else {
            actionEvent.consume();
        }
    }

    public class ExitButtonListener implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent windowEvent) {
            if (handleOpenUnsavedFile() == Operation.SUCCESS) {
                Platform.exit();
            } else {
                windowEvent.consume();
            }
        }
    }

    public void compileProgram(ActionEvent actionEvent) {
        if (this.inputTextArea.getText().length() == 0) {
            return;
        }
        checkLexical();
        checkSyntax();
    }

    private void checkSyntax(){
        ArrayList<ErrorStruct> output = LanguageParser.checkSyntax(this.inputTextArea.getText());
        if (output.size() == 0) {
            this.messageTextArea.appendText("Compilado com sucesso!\n");
            return;
        }
        this.messageTextArea.appendText("\n");
        this.messageTextArea.appendText(output.size() + " Erros sintaticos encontrados :\n");
        for (ErrorStruct err: output){
            this.messageTextArea.appendText(err.getMsg());
            this.messageTextArea.appendText("Esperado(s):" + err.expected());
            this.messageTextArea.appendText("Linha: " + err.getError().currentToken.beginLine);
            this.messageTextArea.appendText("; Coluna: " + err.getError().currentToken.endColumn + "\n");
        }
    }

    private void checkLexical(){
        this.messageTextArea.clear();
        ArrayList<Token> tokens = (ArrayList<Token>) LanguageParser.getTokens(this.inputTextArea.getText());
        int counter = 0;
        if (tokens.size() > 0) {
            this.messageTextArea.appendText("Erro(s) lexicos encontrados: ");
            for (Token token : tokens) {
                if (token.kind == LanguageParserConstants.OTHER || token.kind == LanguageParserConstants.INVALID_IDENTIFIER) {
                    counter++;
                    switch (token.kind) {
                        case 61:
                            this.messageTextArea.appendText("\nSimbolo invalido " + token.beginLine + "; coluna: " + token.endColumn + " " + LanguageParserConstants.tokenImage[token.kind] + " (" + token.kind + ")");
                            break;
                        case 62:
                            this.messageTextArea.appendText("\nIdentificador invalido, linha" + token.beginLine + "; coluna: " + token.endColumn + " " + LanguageParserConstants.tokenImage[token.kind] + " (" + token.kind + ")");
                            break;
                        case 74:
                            this.messageTextArea.appendText("\nString literal invalida, linha" + token.beginLine + "; coluna: " + token.endColumn + " " + LanguageParserConstants.tokenImage[token.kind] + " (" + token.kind + ")");
                            break;
                        case 75:
                            this.messageTextArea.appendText("\nConstante numerica inteira invalida, linha" + token.beginLine + "; coluna: " + token.endColumn + " " + LanguageParserConstants.tokenImage[token.kind] + " (" + token.kind + ")");
                            break;
                        case 76:
                            this.messageTextArea.appendText("\nConstante numerica real invalida, linha" + token.beginLine + "; coluna: " + token.endColumn + " " + LanguageParserConstants.tokenImage[token.kind] + " (" + token.kind + ")");
                            break;
                        case 77:
                            this.messageTextArea.appendText("\nComentario facultativo invalido, linha" + token.beginLine + "; coluna: " + token.endColumn + " " + LanguageParserConstants.tokenImage[token.kind] + " (" + token.kind + ")");
                            break;
                        default:
                            this.messageTextArea.appendText("\nToken invalido, linha " + token.beginLine + "; coluna: " + token.endColumn + " " + LanguageParserConstants.tokenImage[token.kind] + " (" + token.kind + ")");
                            break;
                    }
                } else if (token.kind == 5) {
                    counter++;
                    this.messageTextArea.appendText("\nErro lexico: Comentario de bloco nao encerrado (" + token.kind + ")");
                }
            }
            if (counter == 0){
                this.messageTextArea.appendText("0\n");
            }
        }
        else {
            this.messageTextArea.appendText("\nErros(s) lexicos encontrados 0\n");
        }
    }

    public String copySelection() {
        String selection = inputTextArea.getSelectedText();
        inputTextArea.copy();
        return selection;
    }

    public String cutSelection() {
        String selection = inputTextArea.getSelectedText();
        inputTextArea.cut();
        return selection;
    }

    public void pasteFromClipboard() {
        inputTextArea.paste();
    }
}
