package docs2pdf.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Control;
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

}
