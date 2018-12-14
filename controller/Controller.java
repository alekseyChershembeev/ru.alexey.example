package controller;



import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import model.Config;
import model.Search;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;




public class Controller  {


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



    /*
       The main program method responsible for building a treeView with the found text.
       it starts after treeView a new thread in which a map is formed with all the files and buffer positions
       in which the text is found.

    */
public void chooseDirectoryButtonAction(javafx.event.ActionEvent actionEvent) {

        chooseDirectoryButton.setOnAction(new EventHandler<ActionEvent>() {

            final TreeView<String> treeViewList = new TreeView<>();

            @Override
            public void handle(ActionEvent event) {

                String userChoice = comboBox.getSelectionModel().getSelectedItem();
                if (userChoice!=null) {
                    Config.ENCODING = userChoice;
                }
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
    /*
        method for finding files in the directory for chooseDirectoryButtonAction
    */
    private TreeItem<String> nodesForDirectory(File directory) {
        TreeItem<String> root = new TreeItem<>(directory.getName());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            System.out.println("Loading " + file.getName());

            if (file.isDirectory()) {
                root.getChildren().add(nodesForDirectory(file));

            } else if (booleanMask(file)) {
                Search.positions.put(new File(file.getAbsolutePath()),text_Field.getText());//добваляем в мапу файлы и пустой лонг


                if (new Search().booleanSearchText(file, text_Field.getText())) {

                    TreeItem<String> currentTreeItem = new TreeItem<>(file.getAbsolutePath());


                    root.getChildren().add(currentTreeItem);
                }
            }

        }

        return root;
    }


    /*
    method for determining the mask of the required files for nodesForDirectory
    */
    private boolean booleanMask(File file)
    {
        return file.getName().substring(file.getName().indexOf('.')).equals(textFieldMask.getText());
    }

    /*
    method for calling text in tabPane (with the first entry of the search text) in new tabs
    */
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
    private int countUpDown = 1;


   /*
   search method Up by HashMap by buffer with found text
   */
    @FXML
    void upButton(ActionEvent event) {
        upButton.setOnAction(event1 -> {

            File file = new File(tabPane.getSelectionModel().getSelectedItem().getText());

            for (Map.Entry<File, ArrayList<Long>> entry : Search.positionsAll.entrySet()) {
                if (entry.getKey().equals(file)) {
                    ArrayList<Long> list = entry.getValue();
                    if (countUpDown < list.size()) {
                        for (int p = countUpDown; p < list.size(); ) {
                            try {

                                byte [] data = new Search().readSomeDataFromFile(file, list.get(p), Config.BUFFER);
                                String text = new String(data);
                                countUpDown++;
                                TextArea textArea = new TextArea(text);



                                upButton.defaultButtonProperty().bind(tabPane.getSelectionModel().getSelectedItem().selectedProperty());
                                tabPane.getSelectionModel().getSelectedItem().setContent(textArea);

                                return;

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }

        });

    }

    /*
    Search method Down by HashMap by buffer with found text
    */
    @FXML
    void downButton(ActionEvent event) {


        downButton.setOnAction(event1 -> {

            File file = new File(tabPane.getSelectionModel().getSelectedItem().getText());

            for (Map.Entry<File, ArrayList<Long>> entry : Search.positionsAll.entrySet()) {
                if (entry.getKey().equals(file)) {
                    ArrayList<Long> list = entry.getValue();
                    if (countUpDown != 0) {
                        for (int p = countUpDown; p < list.size();) {
                            try {
                                byte [] data = new Search().readSomeDataFromFile(file, list.get(p), Config.BUFFER);
                                String text = new String(data);
                                countUpDown--;
                                TextArea textArea = new TextArea(text);
                                downButton.defaultButtonProperty().bind(tabPane.getSelectionModel().getSelectedItem().selectedProperty());
                                tabPane.getSelectionModel().getSelectedItem().setContent(textArea);
                                return;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } );

    }


}

