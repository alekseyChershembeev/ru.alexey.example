package controller;



import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import model.Config;
import model.Search;



import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;




public class Controller  {


    // --Commented out by Inspection (12.12.2018 23:57):private ResourceBundle resources;

    @FXML
    private URL location;


    @FXML
    private   TextField text_Field;


    @FXML
    private ListView<String> listViewForChooseDirectory;
    @FXML
    private Button chooseDirectoryButton;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TextField textFieldMask;

    @FXML
    private Button upButton;
    @FXML
    private Button downButton;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TabPane tabPane;
    @FXML
    private Button interruptButton;



//возможность выбирать файлы напрямую.
//    public void ChooseButtonAction(javafx.event.ActionEvent actionEvent) {
//       final FileChooser fc = new FileChooser();
//
//        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*log files","*.log"));
//        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*txt files","*.txt"));
//        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files","*.*"));
//        File selectedFile = fc.showOpenDialog(null);
//
//
//        if (selectedFile != null){
//            ListviewForChooseFile.getItems().add(selectedFile.getAbsolutePath());
//
//
//        }else {
//            System.out.println("File is not valid");
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText("Could not open File");
//            alert.setContentText("The file is invalid.");
//            alert.showAndWait();
//        }
//
//    }



    public void chooseDirectoryButtonAction(javafx.event.ActionEvent actionEvent) {

        chooseDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {

            final TreeView<String> treeViewList = new TreeView<>();




            @Override
            public void handle(ActionEvent event) {


                String userChoice = comboBox.getSelectionModel().getSelectedItem();
                if (userChoice!=null) Config.ENCODING =userChoice;
                final DirectoryChooser directoryChooser = new DirectoryChooser();
                File file = directoryChooser.showDialog(null);
                if (file != null) {

                    listViewForChooseDirectory.getItems().add(file.getAbsolutePath());
                    treeViewList.setRoot(nodesForDirectory(file));
                    treeView.setRoot(treeViewList.getRoot());


                } else {
                    System.out.println("Folder is not valid");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not open directory");
                    alert.setContentText("The file is invalid.");
                    alert.showAndWait();
                }


                Callable<HashMap<File, ArrayList<Long>>> callable = new Search();
                FutureTask<HashMap<File, ArrayList<Long>>> futureTask = new FutureTask<>(callable);
                new Thread(futureTask).start();
            }

        });


    }

    private TreeItem<String> nodesForDirectory(File directory) {
        TreeItem<String> root = new TreeItem<>(directory.getName());



            for (File file : Objects.requireNonNull(directory.listFiles())) {
                System.out.println("Loading " + file.getName());

                if (file.isDirectory()) {
                    root.getChildren().add(nodesForDirectory(file));

                } else if (booleanMask(file)) {
                    if (new Search().booleanSearchText(file, text_Field.getText())) {

                        TreeItem<String> currentTreeItem = new TreeItem<String>(file.getAbsolutePath());


                        root.getChildren().add(currentTreeItem);
                    }
                }

            }




        return root;
    }

    private boolean booleanMask(File file)
    {


        return file.getName().substring(file.getName().indexOf('.')).equals(textFieldMask.getText());

    }




    public void mouseClick(javafx.scene.input.MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() == 2) {

            Search task = new Search();
            TreeItem<String> item = treeView.getSelectionModel().getSelectedItem();
            System.out.println(item.getValue());
            final Tab tab = new Tab(item.getValue());

            TextArea textArea = new TextArea();
            File currentFile = new File(item.getValue());
            String currentString = text_Field.getText();
            String currentText = task.searchString(currentFile, currentString);
            textArea.appendText(currentText);

            tab.setContent(textArea);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);


        }
    }
    @FXML

    void upButton(ActionEvent event) {

        upButton.setOnAction(event1 -> {


            File currentFile = new File(tabPane.getSelectionModel().getSelectedItem().getText());
            String currentString = text_Field.getText();
            String currentText = new Search().searchStringTextForward(currentFile,currentString);


            TextArea textArea = new TextArea(currentText);
            //if (textArea.equals("")){return;} надо чтобы не пропадал текст, еслинечегов выводить




            upButton.defaultButtonProperty().bind(tabPane.getSelectionModel().getSelectedItem().selectedProperty());
            tabPane.getSelectionModel().getSelectedItem().setContent(textArea);


        });

    }


    @FXML
    void downButton(ActionEvent event) {


        downButton.setOnAction(event1 -> {



            File currentFile = new File(tabPane.getSelectionModel().getSelectedItem().getText());
            String currentString = text_Field.getText();
            String currentText = new Search().searchStringTextBack(currentFile,currentString);

            TextArea textArea = new TextArea(currentText);


            upButton.defaultButtonProperty().bind(tabPane.getSelectionModel().getSelectedItem().selectedProperty());
            tabPane.getSelectionModel().getSelectedItem().setContent(textArea);

        } );

    }

        @FXML
     void interruptButton(ActionEvent actionEvent) {
    }
}



















