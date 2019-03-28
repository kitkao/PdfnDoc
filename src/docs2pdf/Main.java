package docs2pdf;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Main.class.getResource("ui/main.fxml"));
        primaryStage.setTitle("Merge docs to booklet");

        Scene mainScene = new Scene(root, 800, 600);

        final ObservableList<String> stylesheets = mainScene.getStylesheets();
        stylesheets.addAll(
//                Main.class.getResource("resources/dark-theme.css").toExternalForm(),
                Main.class.getResource("resources/jfoenix-main.css").toExternalForm()
        );


        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
