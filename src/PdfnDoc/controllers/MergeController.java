package PdfnDoc.controllers;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MergeController {

    private String pdfFolderName = "";
    private List<String> mergeList = new ArrayList<>();

    @FXML
    private JFXListView<Label> pdfListView;
    @FXML
    private JFXListView<Label> mergeListView;

    private String getPdfFolderName() {
        return pdfFolderName;
    }

    private void setPdfFolderName(String pdfFolderName) {
        this.pdfFolderName = pdfFolderName;
    }

    private List<String> getMergeList() {
        return mergeList;
    }

    private void setMergeList(List<String> mergeList) {
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

        // JavaFX bug to fix: https://bugs.openjdk.java.net/browse/JDK-8146919
        pdfListView.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<Label>() {
            @Override
            public void onChanged(Change<? extends Label> c) {

                List<Label> selectedLables = pdfListView.getSelectionModel().getSelectedItems();
                Label selectedLable = pdfListView.getSelectionModel().getSelectedItem();
                List<Integer> selectedIndices = pdfListView.getSelectionModel().getSelectedIndices();

                if (selectedLables.isEmpty()) {
                    List<String> currentList = new ArrayList<>();
                    setMergeList(currentList);
                } else if (selectedLables.size() == 1) {
                    // Only one is selected
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
    private void openPdfFolder(ActionEvent event) {
        File selectedDirectory = Utils.getInstance().showDirectory(event);
        if (selectedDirectory != null) {
            this.listPdfFiles(selectedDirectory);
        } else {
            Stage stage = (Stage) ((Control) event.getSource()).getScene().getWindow();
            Utils.getInstance().showDialog(stage, "Something...not proper", "Did not select a directory");
        }
    }

    private void listPdfFiles(File selectedDirectory) {
        pdfListView.getItems().clear();
        this.setPdfFolderName(selectedDirectory.getPath());

        File[] fileList = selectedDirectory.listFiles();
        List<File> fList = new ArrayList<>();
        for (File f : fileList) {
            if (FilenameUtils.getExtension(f.getPath()).equals("pdf")) {
                fList.add(f);
            }
        }

        for (File f : fList) {
            Label label = new Label(f.getName());
            JFXRippler rippler = new JFXRippler(label);
            pdfListView.getItems().add(label);
        }

    }

    // Event action invoked by merge button
    @FXML
    private void mergePdf(ActionEvent event) {

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

            PdfCopy copy;
            Document document;
            List<String> filePathList;
            // table of contents
            List<HashMap<String, Object>> outlines;

            try {
                document = new Document();
                filePathList = pdfList.stream().map(v -> v.getAbsolutePath()).collect(Collectors.toList());

                outlines = new ArrayList<>();

                // File exist problem seems alright.
                if (filePathList.contains(newFile.getAbsolutePath())) {
                    // New file's name equals to an exciting one
                    copy = new PdfCopy(document, new FileOutputStream(newFile.getAbsolutePath()));
                } else {
                    // File name not exists
                    copy = new PdfCopy(document, new FileOutputStream(newFile.getAbsolutePath()));
                }

                copy.setMergeFields();

                document.open();
                List<PdfReader> readers = new ArrayList<>();

                PdfReader reader;
                for (File pdf : pdfList) {
                    // Build pdf reader from file
                    reader = new PdfReader(pdf.getAbsolutePath());
                    readers.add(reader);
                    // Get start page number of this pdf file
                    int start = copy.getPageNumber();
                    // add bookmark to this pdf file
                    HashMap bookmark = new HashMap();

                    bookmark.put("Title", pdf.getName().split(".pdf")[0]);
                    bookmark.put("Action", "GoTo");
                    bookmark.put("Page", String.format("%d Fit", start));
                    outlines.add(bookmark);
                    // add pdf file to the merge file
                    copy.addDocument(reader);
                }

                copy.setOutlines(outlines);

                copy.close();
                document.close();

                // read need to be closed after copy
                for (PdfReader r : readers) {
                    r.close();
                }

                this.listPdfFiles(new File(this.getPdfFolderName()));
//                Desktop.getDesktop().open(newFile);

            } catch (DocumentException e) {
                Utils.getInstance().showDialog(stage, "Document exceptions", e.getMessage());
            } catch (FileNotFoundException e) {
                Utils.getInstance().showDialog(stage, "File exceptions", e.getMessage());
            } catch (IOException e) {
                Utils.getInstance().showDialog(stage, "IO exceptions", e.getMessage());
            }
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
