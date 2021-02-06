import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class TabCategoryController {

    @FXML
    public Tab tab;

    @FXML
    public FlowPane flowPane;
    @FXML
    public Label label;

    private File tabFolder;
    private Wardrobe wardrobe;
    private TextField textField;
    private Stage chooseLookStage;
    private  boolean isDelete;
    private Vector<LookController> items;

    @FXML
    public void onAddFigure(MouseEvent event){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewLooks.fxml"));
            GridPane gridPane = fxmlLoader.load();
            ((NewLooksController)fxmlLoader.getController()).initController(this);

            this.chooseLookStage = new Stage();
            this.chooseLookStage.setTitle("New look");
            this.chooseLookStage.setScene(new Scene(gridPane));
            this.chooseLookStage.setAlwaysOnTop(true);
            this.chooseLookStage.show();

        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showInfo(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Houlala");
        alert.setHeaderText(message);
        alert.show();
    }

    //Add a new look Item
    public void addFigure(String figure, String gender){
        if(this.chooseLookStage != null){
            this.chooseLookStage.close();
            this.chooseLookStage = null;
        }
            if(!isDelete) {
                try {
                    URL url = new URL("https://www.habbo.fr/habbo-imaging/avatarimage?direction=4&head_direction=4&size=m&figure=" + figure);
                    BufferedImage image = ImageIO.read(url);
                    File file = new File(this.tabFolder.getPath(), figure + "_" + gender.toLowerCase() + ".png");
                    if (file.exists()) {
                        showInfo("You already have this in your Wardrobe !");
                    } else {
                        ImageIO.write(image, "png", file);
                        loadLook(file); //load the button
                    }
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
    }

    //Remove look item for this category
    public void removeLook(LookController controller){
        this.flowPane.getChildren().remove(controller.vbox);
        this.items.remove(controller);
    }

    //Load a look item for this category
    private void loadLook(File file){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FigureButton.fxml"));
            VBox pane = loader.load();
            LookController controller = loader.getController();
            this.items.add(controller);
            controller.loadLook(file, this, wardrobe);
            this.flowPane.getChildren().add(this.flowPane.getChildren().size()-1, pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void edit(){
        textField.setText(label.getText());
        tab.setGraphic(textField);
        textField.selectAll();
        textField.requestFocus();
    }

    //Change the name of this category
    public void changeName(){
            if (!this.label.getText().equals(this.textField.getText())) {
                try {
                    String newName = this.textField.getText();
                    Path newPath = Paths.get(Wardrobe.WARDROBEPATH.toString(), newName);
                    if (Files.isDirectory(newPath)) {
                        textField.setText(this.label.getText());
                        showInfo("Oops, you already have this category");
                    } else {
                        Files.move(Paths.get(this.tabFolder.getPath()), newPath);
                        this.tabFolder = newPath.toFile();
                        label.setText(textField.getText());
                    }
                    tab.setGraphic(label);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                tab.setGraphic(label);
            }
    }

    //Delete this category
    public void deleteTab(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete category " + this.label.getText() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            this.isDelete = true;
            this.label.setContextMenu(null);
            try {
                Files.deleteIfExists(this.tabFolder.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            wardrobe.tabs.getTabs().remove(this.tab);
        }
    }

    public void initialize(){
        this.items = new Vector<>();
        this.isDelete = false;
        this.textField = new TextField();
        this.tab.setGraphic(this.label);

        ContextMenu menu =  new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Category");
        TabCategoryController self = this;

        deleteItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(wardrobe.tabs.getTabs().size() == 2){
                    Vector<LookController> items_b = (Vector<LookController>) items.clone();
                    for (LookController controller : items_b){
                        controller.onDelete(null);
                    }
                }else {
                    self.deleteTab();
                }
            }
        });
        menu.getItems().add(deleteItem);
        this.label.setContextMenu(menu);

        /*Handle change category name*/
        this.label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount()==2){
                    edit();
                }
            }
        });

        this.textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
               changeName();
            }
        });

        this.textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if(!newValue && oldValue){
                    changeName();
                }
            }
        });
    }

    //Load this Category
    public void loadTab(File tabFolder, Wardrobe wardrobe){
        this.wardrobe = wardrobe;
        this.label.setText(tabFolder.getName());
        this.tabFolder = tabFolder;


        File[] fList = tabFolder.listFiles();
        if(fList != null){
            for(File file: fList){
                if(file.isFile() && file.getName().endsWith(".png")){
                    loadLook(file);
                }
            }
        }

    }
}
