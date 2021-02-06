import gearth.extensions.ExtensionForm;
import gearth.extensions.ExtensionInfo;
import gearth.protocol.HMessage;
import gearth.protocol.HPacket;
import gearth.ui.GEarthController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtensionInfo(
        Title = "Wardrobe Extension",
        Description = "Wardrobe",
        Version = "1.0",
        Author = "adrien.n"
)
public class Wardrobe extends ExtensionForm {

    @FXML
    public TabPane tabs;

    public static Path WARDROBEPATH;
    public static HabboPlayer  owner;
    public Scene scene;


    @Override
    public ExtensionForm launchForm(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        this.scene = new Scene(root);
        primaryStage.setTitle("Wardrobe");
        primaryStage.setScene(this.scene);
        primaryStage.getScene().getStylesheets().add(GEarthController.class.getResource("/gearth/ui/bootstrap3.css").toExternalForm());
        //primaryStage.setAlwaysOnTop(true);
        return loader.getController();
    }

    public static void main(String[] args) {
        runExtensionForm(args, Wardrobe.class);
    }

    //Load a category from a folder
    public void openCategory(File file, boolean editMode){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TabCategory.fxml"));
            Tab tab = loader.load();
            ((TabCategoryController)loader.getController()).loadTab(file, this);
            tabs.getTabs().add(tabs.getTabs().size()-1,tab);
            if(editMode){
                tabs.getSelectionModel().selectPrevious();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize(){
        try {
            String extFolder = new File(GEarthController.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParent();
            WARDROBEPATH = Paths.get(extFolder, "Wardrobe");
            if(!Files.isDirectory(WARDROBEPATH)){
                Files.createDirectory(WARDROBEPATH);
            }
        } catch (URISyntaxException  | IOException e) {
            e.printStackTrace();
        }

        File[] fList = WARDROBEPATH.toFile().listFiles();
        boolean haveFolder = false;
        if(fList != null){
            for(File file: fList){
                if(file.isDirectory()){
                    openCategory(file, false);
                    haveFolder = true;
                }
            }
        }

        if(!haveFolder){
            try {
                Path dir = Files.createDirectory(Paths.get(WARDROBEPATH.toString(), "My outfits")); //Creating first category
                openCategory(dir.toFile(), false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.tabs.getSelectionModel().clearSelection();
        this.tabs.getSelectionModel().select(0);
    }

    @FXML
    public void newTab(MouseEvent event){
        createNewCategory("New Category");
    }


    //Adding a new category
    public void createNewCategory(String name){
        try {
            Path path = Paths.get(WARDROBEPATH.toString(), name);
            if (!Files.isDirectory(path) ){
                Files.createDirectory(path);
                openCategory(path.toFile(), true);
            }else{
                createNewCategory(name+'_');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void changeLook(String figure, String gender){
        sendToServer(new HPacket("{l}{h:44}{s:\""+gender+"\"}{s:\""+figure+"\"}"));
    }

    @Override
    protected void initExtension() {
        //5 -> UserObject
        intercept(HMessage.Direction.TOCLIENT, 5, hMessage -> {
            hMessage.getPacket().readLong();
            String name = hMessage.getPacket().readString();
            String figure = hMessage.getPacket().readString();
            String gender= hMessage.getPacket().readString();
            owner = new HabboPlayer(-1, name, figure, gender);

        });

        //266 -> UpdateAvatar
        intercept(HMessage.Direction.TOCLIENT, 266, hMessage -> {
            RoomUsers.getInstance().changeLook(hMessage.getPacket());
        });

        //UserLoggedOut
        intercept(HMessage.Direction.TOCLIENT, 29, hMessage -> {
            int index = hMessage.getPacket().readInteger();
            RoomUsers.getInstance().removePlayer(index);
        });

        //UsersInRoom
        intercept(HMessage.Direction.TOCLIENT, 28, hMessage -> {
            RoomUsers.getInstance().parse(hMessage.getPacket());
        } );

    }

}