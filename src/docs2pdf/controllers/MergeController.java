package docs2pdf.controllers;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRippler;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MergeController {


    Utils su = Utils.getInstance();

    private String pdfFolderName = "";
    private List<String> currentList = new ArrayList<>();
    private List<String> mergeList = new ArrayList<>();


    @FXML
    private JFXListView<Label> pdfListView;
    @FXML
    private JFXListView<Label> mergeListView;


    public String getPdfFolderName() {
        return pdfFolderName;
    }

    public void setPdfFolderName(String pdfFolderName) {
        this.pdfFolderName = pdfFolderName;
    }

    public List<String> getCurrentList() {
        return currentList;
    }

    public void setCurrentList(List<String> currentList) {
        this.currentList = currentList;
    }

    public List<String> getMergeList() {
        return mergeList;
    }

    public void setMergeList(List<String> mergeList) {
        this.mergeList = mergeList;
    }


    @FXML
    protected void initialize() {

        this.setMergeList(new ArrayList<String>());
        pdfListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (!this.pdfListView.isExpanded()) {
            pdfListView.setExpanded(true);
        }
        mergeListView.getSelectionModel().setSelectionMode((SelectionMode.SINGLE));

        // JAVAFX BUG to fix: https://bugs.openjdk.java.net/browse/JDK-8146919
        pdfListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Label>() {
            @Override
            public void onChanged(Change<? extends Label> c) {

                List<Label> selectedLables = pdfListView.getSelectionModel().getSelectedItems();
                Label selectedLable = pdfListView.getSelectionModel().getSelectedItem();
                List<Integer> selectedIndices = pdfListView.getSelectionModel().getSelectedIndices();
                int selectedSize = selectedIndices.size();

                if (selectedLables.isEmpty()) {
                    List<String> currentList = new ArrayList<>();
                    setMergeList(currentList);
                } else if (selectedLables.size() == 1) {
                    //只有一个文件被选中
                    List<String> currentList = new ArrayList<>();
                    currentList.add(selectedLables.get(0).getText());
                    setMergeList(currentList);
                } else if (mergeList.contains(selectedLable.getText())) {
                    if (selectedIndices.size() == mergeList.size() - 1) {
                        List<String> mergeList = getMergeList();
                        mergeList.remove(selectedLable.getText());
                        setMergeList(mergeList);
                    } else {
                        List<String> currentList = new ArrayList<>();
                        for (Label sl : selectedLables) {
                            currentList.add(sl.getText());
                        }
                        setMergeList(currentList);
                    }
                } else {
                    if (selectedIndices.size() == mergeList.size() + 1) {
                        List<String> mergeList = getMergeList();
                        mergeList.add(selectedLable.getText());
                        setMergeList(mergeList);
                    } else {
                        List<String> currentList = new ArrayList<>();
                        for (Label sl : selectedLables) {
                            currentList.add(sl.getText());
                        }
                        setMergeList(currentList);
                    }

                }
                mergeListView.getItems().clear();
                for (String m : mergeList) {
                    Label label = new Label(m);
                    mergeListView.getItems().add(label);
                }
            }
        });


    }


    @FXML
    public void openPdfFolder(ActionEvent event) throws IOException {
        File selectedDirectory = su.showDirectory(event);

        if (selectedDirectory != null) {
            this.listPdfFiles(selectedDirectory);
        } else {
            Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
            Utils.getInstance().showDialog(stage, "Something...not proper", "Did not select a directory");
        }
    }

    public void listPdfFiles(File selectedDirectory) {

        pdfListView.getItems().clear();
        this.setPdfFolderName(selectedDirectory.getPath());

        File[] fileList = selectedDirectory.listFiles();
        List<File> flist = new ArrayList<>();
        for (File f : fileList) {
            if (FilenameUtils.getExtension(f.getPath()).equals("pdf")) {
                flist.add(f);
            }
        }

        for (File f : flist) {
            Label label = new Label(f.getName());
            JFXRippler rippler = new JFXRippler(label);
            pdfListView.getItems().add(label);
        }

    }

    @FXML
    public void mergePdf(ActionEvent event) throws IOException, DocumentException {

        Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
        if (this.getMergeList().isEmpty()) {
            String title = "Something...not proper";
            String info = "Files list to merge:" + " is empty";
            Utils.getInstance().showDialog(stage, title, info);
            return;
        }

        // If selected some files, then generate the merge file
        List<File> pdfList = new ArrayList<>();
        for (String pdfName : this.getMergeList()) {
            File pdfFile = new File(this.getPdfFolderName() + "\\" + pdfName);
            pdfList.add(pdfFile);
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);
        File newFile = fileChooser.showSaveDialog(stage);

        if (newFile != null) {
            Document document = new Document();
            PdfCopy copy;
            List<String> filePathList = pdfList.stream().map(v -> v.getAbsolutePath()).collect(Collectors.toList());
            // If the new file's name is the same to an existing one
            if (filePathList.contains(newFile.getAbsolutePath())) {
                String newFilePath = newFile.getAbsolutePath()
                        .substring(0, newFile.getAbsolutePath().length() - 4) + "-new.pdf";
                copy = new PdfCopy(document, new FileOutputStream(newFile.getAbsolutePath()));
            } else {
                // File name not exists
                copy = new PdfCopy(document, new FileOutputStream(newFile.getAbsolutePath()));
            }
            copy.setMergeFields();
            document.open();
            List<PdfReader> readers = new ArrayList<>();

            for (File pdf : pdfList) {
                readers.add(new PdfReader(pdf.getAbsolutePath()));
            }

            for (PdfReader reader : readers) {
                copy.addDocument(reader);
            }

            document.close();
            copy.close();

            for (PdfReader reader : readers) {
                reader.close();
            }

            Desktop.getDesktop().open(newFile);
            this.listPdfFiles(new File(this.getPdfFolderName()));
        } else {
            Utils.getInstance().showDialog(stage, "Cancel merging", "New file not selected.");
        }


    }

    // getIndices return array in increment order : [2, 5, 9 ,10, 13]
    boolean areConsecutive(int[] arr, int n) {
        if (n < 1)
            return false;
        int min = arr[0];
        int max = arr[arr.length - 1];
        if (max - min + 1 == n) {
            boolean[] visited = new boolean[n];
            int i;
            for (i = 0; i < n; i++) {
                if (visited[arr[i] - min] != false)
                    return false;
                visited[arr[i] - min] = true;
            }
            return true;
        }
        return false;
    }
}
