package docs2pdf.controllers;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Utils {

    private static Utils single_instance = null;


    private Utils()
    {

    }

    public static Utils getInstance()
    {
        if (single_instance == null)
            single_instance = new Utils();
        return single_instance;
    }

    public File showDirectory (ActionEvent event) {
        Stage stage = Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        return directoryChooser.showDialog(stage);
    }

    public void showDialog(Stage stage, String title, String info) {
        JFXAlert alert = new JFXAlert(stage);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(new Label(info));
        JFXButton closeButton = new JFXButton("Okay");
        closeButton.getStyleClass().add("button-raised");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(e -> alert.hideWithAnimation());
        layout.setActions(closeButton);
        alert.setContent(layout);
        alert.show();
    }

}
