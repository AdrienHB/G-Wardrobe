import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class NewLooksController {
    @FXML
    public ImageView ownerAvatar;

    @FXML
    public VBox usersContainer;

    private TabCategoryController tabCategoryController;

    public void initController(TabCategoryController tabCategoryController){
        this.tabCategoryController = tabCategoryController;
        //Display player's avatar
        new Thread(new Runnable() {
            @Override
            public void run() {
                if( Wardrobe.owner != null && Wardrobe.owner.getName() != null) {
                    String url = "https://www.habbo.fr/habbo-imaging/avatarimage?direction=3&head_direction=3&gesture=sml&size=l&action=wav&user=" + Wardrobe.owner.getName();
                    ownerAvatar.setImage(new Image(url));
                }
            }
        }).start();

        //Adding Users in room
        for(HabboPlayer player : RoomUsers.getInstance().getPlayers()){
            if(player != Wardrobe.owner) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RoomUserItem.fxml"));
                    GridPane gridPane = fxmlLoader.load();
                    ((RoomUserItemController) fxmlLoader.getController()).initController(this.tabCategoryController, player);
                    this.usersContainer.getChildren().add(gridPane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Choose to add this look
    public void onChoose(MouseEvent mouseEvent){
        this.tabCategoryController.addFigure(Wardrobe.owner.getFigure(), Wardrobe.owner.getGender());
    }




}
