package docs2pdf.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;

public class MainController {

    // Inject tab content.
    @FXML
    private TabPane convertTab;
    // Inject controller
    @FXML
    private ConvertController convertController;
    // Inject tab content.
    @FXML
    private TabPane mergeTab;
    // Inject controller
    @FXML
    private MergeController mergeController;

}
