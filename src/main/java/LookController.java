import javafx.fxml.FXML;
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.File;

public class LookController {

    @FXML
    public ImageView deleteImage;

    @FXML
    public ImageView lookImage;

    @FXML
    public VBox vbox;

    public Bloom bloom; //Effect hover

    private Wardrobe wardrobe;
    private String figure;
    private String gender;
    private  File lookFile;
    private TabCategoryController tabCategoryController;

    @FXML
    public void onMouseEntered(){
        this.deleteImage.setVisible(true);
        this.lookImage.setEffect(this.bloom);
    }

    @FXML
    public void onMouseExited(){
        this.deleteImage.setVisible(false);
        this.lookImage.setEffect(null);
    }

    @FXML
    public void onChangeLook(MouseEvent event){
        this.wardrobe.changeLook(this.figure, this.gender);
    }

    @FXML
    public void onDelete(MouseEvent event){
        this.lookFile.delete();
        this.tabCategoryController.removeLook(this);
    }


    //Load a look from a file
    public void loadLook(File lookFile, TabCategoryController tabCategoryController, Wardrobe wardrobe){
        this.tabCategoryController = tabCategoryController;
        this.lookFile = lookFile;
        this.wardrobe = wardrobe;
        this.bloom = new Bloom();
        this.bloom.setThreshold(0);
        this.figure = lookFile.getName().split("_")[0];
        this.gender = lookFile.getName().toLowerCase().contains("_m") ? "M" : "F";

        this.deleteImage.setVisible(false);
        lookImage.setImage(new Image(lookFile.toURI().toString()));
    }



}
