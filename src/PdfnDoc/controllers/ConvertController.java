package PdfnDoc.controllers;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertController {

    private ObservableList<DocFile> list = FXCollections.observableArrayList();
    private ObservableList<String> pdfList = FXCollections.observableArrayList();

    @FXML
    private JFXProgressBar convertProgress;
    @FXML
    private JFXListView convertListView;
    @FXML
    private TableView<DocFile> docTableView;
    @FXML
    private TableColumn<DocFile, JFXCheckBox> colTick;
    @FXML
    private TableColumn<DocFile, String> colName;
    @FXML
    private TableColumn<DocFile, String> colPath;
    @FXML
    private TableColumn<DocFile, String> colSize;

    @FXML
    protected void initialize() {
        colTick.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<DocFile, JFXCheckBox>, ObservableValue<JFXCheckBox>>() {
            @Override
            public ObservableValue<JFXCheckBox> call(TableColumn.CellDataFeatures<DocFile, JFXCheckBox> param) {
                DocFile pf = param.getValue();
                JFXCheckBox checkBox = new JFXCheckBox();
                checkBox.selectedProperty().setValue(pf.isTick());
                checkBox.selectedProperty().addListener((ov, old_val, new_val) -> {
                    pf.setTick(new_val);
                });
                return new SimpleObjectProperty<>(checkBox);
            }
        });

        colName.setCellValueFactory(new PropertyValueFactory<DocFile, String>("name"));
        colPath.setCellValueFactory(new PropertyValueFactory<DocFile, String>("path"));
        colSize.setCellValueFactory(new PropertyValueFactory<DocFile, String>("size"));
        docTableView.setItems(list);
        convertListView.setItems(pdfList);
    }


    @FXML
    private void openDocFolder(ActionEvent event) {

        File selectedDirectory = Utils.getInstance().showDirectory(event);
        if (selectedDirectory != null) {
            list.clear();
            File[] fileList = selectedDirectory.listFiles();
            for (File f : fileList) {
                if (FilenameUtils.getExtension(f.getPath()).equals("doc")) {
                    list.add(new DocFile(false, f.getName(), f.getAbsolutePath(),
                            f.length() / 1024 + "kb"));
                }
            }
        } else {
            Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
            Utils.getInstance().showDialog(stage, "Something...not proper", "Did not select a directory");
        }
    }

    @FXML
    private void convertDoc(ActionEvent event) {
        List<DocFile> selectedItems = list.stream().filter(item -> item.isTick()).collect(Collectors.toList());
        if (selectedItems.isEmpty()) {
            Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
            Utils.getInstance().showDialog(stage, "Something...not proper", "Doc files to convert is empty.");
        } else {
            try {
                doc2Pdf(selectedItems);
            } catch (Exception e) {
                Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
                Utils.getInstance().showDialog(stage, "Some exceptions", "e.getMessage()");
            }
        }
    }

    private void doc2Pdf(List<DocFile> doclist) throws Exception {
        pdfList.clear();
        Thread convertThread = new Thread(() -> {
            Platform.runLater(() -> convertProgress.setProgress(0.0));
            // Word Doc format to pdf Macro code is 17
            int doc2PdfMacroCode = 17;
            ActiveXComponent app = new ActiveXComponent("KWPS.Application");
            app.setProperty("Visible", new Variant(false));
            //Some version of wps can't use  macro
            //app.setProperty("AutomationSecurity", new Variant(3));
            Dispatch docs = app.getProperty("Documents").toDispatch();

            Iterator iterator = doclist.iterator();
            int count = 0;
            while (iterator.hasNext()) {
                count++;
                DocFile docfile = (DocFile) iterator.next();
                String inputFile = docfile.getPath();
                String pdfFile = inputFile.substring(0, inputFile.length() - 4) + ".pdf";
                Dispatch doc = Dispatch.call(docs, "Open", inputFile, false, true).toDispatch();
                Dispatch.call(doc, "ExportAsFixedFormat", pdfFile, doc2PdfMacroCode);
                Dispatch.call(doc, "Close", false);
                updateProgress(count, doclist.size());
                String pdfName = docfile.getName().substring(0, docfile.getName().length() - 4) + ".pdf";
                //pdfList is in main FX Thread
                Platform.runLater(() -> pdfList.add(pdfName));
            }
            app.invoke("Quit", 0);

        });
        convertThread.start();
    }

    private void updateProgress(int count, int size) {
        Platform.runLater(() -> convertProgress.setProgress((double) count / (double) size));
    }

    public class DocFile {
        private final SimpleBooleanProperty tick;
        private final SimpleStringProperty name;
        private final SimpleStringProperty path;
        private final SimpleStringProperty size;

        DocFile(Boolean tick, String name, String path, String size) {
            this.tick = new SimpleBooleanProperty(tick);
            this.name = new SimpleStringProperty(name);
            this.path = new SimpleStringProperty(path);
            this.size = new SimpleStringProperty(size);
        }

        public boolean isTick() {
            return tick.get();
        }

        public void setTick(boolean tick) {
            this.tick.set(tick);
        }

        public SimpleBooleanProperty tickProperty() {
            return tick;
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public String getPath() {
            return path.get();
        }

        public void setPath(String path) {
            this.path.set(path);
        }

        public SimpleStringProperty pathProperty() {
            return path;
        }

        public String getSize() {
            return size.get();
        }

        public void setSize(String size) {
            this.size.set(size);
        }

        public SimpleStringProperty sizeProperty() {
            return size;
        }

    }

}
