package gui;

//import classes.Sintatico;
//import classes.ParseEOFException;
import classes.*;
import javafx.scene.control.cell.PropertyValueFactory;
import maquinavirtual.InstructionK;
import util.AlertFactory;
import util.Operation;
//import maquinavirtual.VirtualMachineK;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import classes.Language20221;

public class Controller {
    private List<InstructionK> insList;
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
        filePicker.setInitialDirectory(EditorFile.LAST_CURRENT_WORKING_DIR);
        editorFile = new EditorFile(filePicker.showOpenDialog(new Stage()), false);
        // Error handling
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
        //clearMessageArea();
        messageTextArea.clear();
        hasOpenFile = false;
        this.editorFile.setFile(null);
        updateStageTitle();
        //setStatusMsg("");
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
        System.out.println("Save as called");
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
    public void disableEditOptions(boolean b) {
        cutBtn.setDisable(b);
        cutMenuItem.setDisable(b);
        copyBtn.setDisable(b);
        copyMenuItem.setDisable(b);
        pasteBtn.setDisable(b);
        pasteMenuItem.setDisable(b);
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

    /*public void compileProgram(ActionEvent actionEvent) {
        if (this.inputTextArea.getText().length() == 0) {
            return;
        }
        checkLexical();
        checkSyntax();
    }*/

    public boolean compileProgram(ActionEvent actionEvent) throws ParseException {
        //messageTextArea.clear();
        actionEvent.consume();
        if (inputTextArea.getText().length() == 0) {
            Alert alert = AlertFactory.create
                    (
                            Alert.AlertType.ERROR,
                            "Erro",
                            "Arquivo vazio",
                            "Um arquivo vazio nao pode ser compilado"
                    );
            alert.show();
            return false;
        }
        String[] args = new String[0];
        java.io.InputStream targetStream = new java.io.ByteArrayInputStream(inputTextArea.getText().getBytes());
        Language20221 tokenizer = new Language20221(targetStream);
        String result = tokenizer.analyze(args, inputTextArea.getText());
        //messageTextArea.setText("Qtd Erros Lexicos: " + tokenizer.getContLexicalErrors()
        //    + "\n" + tokenizer.getLexicalErrors()); //result);
        messageTextArea.setText(result);
        System.out.println(result);
        //tokenizer.limpaClasse();

        if (tokenizer.hasAnyErrors()) {
            System.out.println("Has errors!");
            return false;
        } else {
            this.insList = tokenizer.getInstructions();
            displayInstructions(this.insList);
            System.out.println(result);
        }
        return true;
    }

    private void displayInstructions(List<InstructionK> instructions) {
//        instructionNumberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
//        instructionMnemonicCol.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
//        instructionParameterCol.setCellValueFactory(new PropertyValueFactory<>("parameter"));
//        instructionTable.setItems(getObservableListOf(instructions));
    }

    /*
    public void handleRunButton() throws ParseEOFException, ParseException {
        if (handleVMmaybeRunning() == Operation.SUCCESS) {
            if (compileProgram()) {
                this.vm = new VirtualMachineK.java(insList);
                this.isReadingConsole = false;
                runVirtualMachine();
            }
        }
    }
       public Operation handleVMmaybeRunning() {
        if (vm == null || vm.getStatus() == VMStatus.HALTED) {
            return Operation.SUCCESS;
        }
        var confirm = AlertFactory.create(Alert.AlertType.CONFIRMATION, "Confirmation", "", "VM is still running, do you wish to stop the VM and continue this operation?");
        Optional<ButtonType> optional = Optional.empty();
        var op = Operation.CANCELED;
        if (vm.getStatus() == VMStatus.RUNNING || vm.getStatus() == VMStatus.SYSCALL_IO_READ || vm.getStatus() == VMStatus.SYSCALL_IO_WRITE) {
            optional = confirm.showAndWait();
        }
        if (optional.isEmpty()) {
            return Operation.CANCELED;
        }
        var buttonData = optional.get().getButtonData();
        if (buttonData.equals(ButtonType.OK.getButtonData())) {
            vm = null;
            this.messageTextArea.clear();
            setStatusMsg("Forcefully closed VM!");
            return Operation.SUCCESS;
        }
        if (buttonData.equals(ButtonType.CANCEL.getButtonData())) {
            return Operation.CANCELED;
        }
        return op;
    }

    public void runVirtualMachine() {
        try {
            while (vm.getStatus() != VMStatus.HALTED) {
                if (isReadingConsole) {
                    return;
                }
                statusBar.setText("Running Virtual Machine...");
                vm.executeAll();
                switch (vm.getStatus()) {
                    case SYSCALL_IO_READ -> {
                        handleSyscallRead(vm.getSyscallDataType());
                    }
                    case SYSCALL_IO_WRITE -> handleSyscallWrite(vm.getSyscallData());
                }
            }
            statusBar.setText("Virtual Machine halted, program terminated!");
        } catch (Exception e) {
            statusBar.setText("Runtime error when executing VM, aborting!!");
            this.messageTextArea.appendText(String.format("\n== ERROR - VM ==\n%s", e.getMessage()));
            e.printStackTrace();
            this.vm.setStatus(VMStatus.HALTED);
            this.isReadingConsole = false;
            this.consoleInput.setDisable(true);
        }
    }

    private void handleSyscallWrite(Object o) {
        messageTextArea.appendText("\n" + o.toString());
    }

    private void handleSyscallRead(DataType o) {
        consoleInput.setDisable(false);
        isReadingConsole = true;
        statusBar.setText("Waiting for input of " + o.toString());
        consoleInput.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                vm.setSyscallData(consoleInput.getText().trim());
                messageTextArea.appendText("\n--> " + consoleInput.getText().trim());
                consoleInput.setDisable(true);
                consoleInput.clear();
                isReadingConsole = false;
                runVirtualMachine();
            }
        });
    }

    private void displayInstructions(List<InstructionK.java> instructions) {
        instructionNumberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        instructionMnemonicCol.setCellValueFactory(new PropertyValueFactory<>("mnemonic"));
        instructionParameterCol.setCellValueFactory(new PropertyValueFactory<>("parameter"));
        instructionTable.setItems(getObservableListOf(instructions));
    }
    */

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
    private ObservableList<InstructionK> getObservableListOf(List<InstructionK> instructionList) {
        return FXCollections.observableArrayList(instructionList);
    }
}
