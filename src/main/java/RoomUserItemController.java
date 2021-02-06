import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


public class RoomUserItemController {

    private TabCategoryController tabCategoryController;
    private HabboPlayer player;

    @FXML
    public ImageView habboHead;

    @FXML
    public Label pseudo;

    public void initController(TabCategoryController tabCategoryController, HabboPlayer player){
        this.tabCategoryController = tabCategoryController;
        this.player = player;
        this.pseudo.setText(player.getName());

        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://www.habbo.fr/habbo-imaging/avatarimage?&head_direction=3&gesture=sml&size=m&headonly=1&figure="+player.getFigure();
                habboHead.setImage(new Image(url));
            }
        }).start();

    }

    public void onChoose(MouseEvent mouseEvent){
        this.tabCategoryController.addFigure(this.player.getFigure(), this.player.getGender());
    }

}
