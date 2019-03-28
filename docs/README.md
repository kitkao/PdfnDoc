
# Some issues

## Changed from C# to JAVA
- Was intended to merge docs directly. But formats differs in each docs.
- Word 2007 could invoke com from :
    - https://www.microsoft.com/zh-cn/download/details.aspx?id=7

```cs
using Word = Microsoft.Office.Interop.Word;
using DocumentFormat.OpenXml.Packaging;
using DocumentFormat.OpenXml.Wordprocessing;
using Paragraph = DocumentFormat.OpenXml.Wordprocessing.Paragraph;


private void Merge_Docs()
{
    string fileName1 = @"D:\\docs\test.doc";
    string fileName2 = @"D:\\docs\1-2.doc";
    string testFile = @"D:\\docs\1-1.doc";

    File.Delete(fileName1);
    File.Copy(testFile, fileName1);
    using (WordprocessingDocument myDoc =
        WordprocessingDocument.Open(fileName1, true))
    {
        string altChunkId = "AltChunkId1";
        MainDocumentPart mainPart = myDoc.MainDocumentPart;
        AlternativeFormatImportPart chunk =
            mainPart.AddAlternativeFormatImportPart(
            AlternativeFormatImportPartType.WordprocessingML, altChunkId);
        using (FileStream fileStream = File.Open(fileName2, FileMode.Open))
            chunk.FeedData(fileStream);
        AltChunk altChunk = new AltChunk();
        altChunk.Id = altChunkId;
        mainPart.Document
            .Body
            .InsertAfter(altChunk, mainPart.Document.Body
            .Elements<Paragraph>().Last());
        mainPart.Document.Save();
    }
}

public static void Combine_Word(string fileToMerge1, string fileToMerge2, string outputFilename)
{
    object missing = System.Type.Missing;
    object pageBreak = Word.WdBreakType.wdPageBreak;
    object outputFile = outputFilename;
    // Create  a new Word application
    Word._Application wordApplication = new Word.Application();
    try
    {
        // Create a new file based on our template
        Word._Document wordDocument = wordApplication.Documents.Open(
                                    fileToMerge1
                                    , ref missing
                                    , ref missing
                                    , ref missing
                                    , ref missing);
        // Make a Word selection object.
        Word.Selection selection = wordApplication.Selection;
        wordDocument.Merge(fileToMerge2, ref missing, ref missing, ref missing, ref missing);
        // Save the document to it's output file.
        wordDocument.SaveAs(
                    ref outputFile
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing
                    , ref missing);
        // Clean up!
        wordDocument = null;
    }
    catch (Exception ex)
    {
        //I didn't include a default error handler so i'm just throwing the error
        throw ex;
    }
    finally
    {
        // Finally, Close our Word application
        wordApplication.Quit(ref missing, ref missing, ref missing);
    }
}
```

## grammar
- T.class.cast 比 (T) 强制转换好
 
- "CLASS".class 等于 getClass()
 
- 以下同等，倾向上面的，方便复用
  - 可以在controller中定义@FXML 
    ```
    ((Stage) rootPane.getScene().getWindow()).close();
    ```
  - 在方法中用event获取stage
    ```
    Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
    ```

## listview handler choices

Due to JAVAFX Listview bugs, final choice is selectedItems event.

```
pdfListView.setOnMouseClicked(new EventHandler<MouseEvent>(){
    @Override
    public void handle(MouseEvent arg0) {
        System.out.println("clicked: " + pdfListView.getSelectionModel().getSelectedIndex() +
               pdfListView.getSelectionModel().getSelectedItem());
    }

});


pdfListView.getSelectionModel().selectedItemProperty().addListener(
        new ChangeListener<Label>() {
            @Override
            public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
                List<String> mergeList = getMergeList();
                setMergeList(mergeList);
                if (newValue != null) {
                    if(mergeList.contains(newValue.getText())){
                        mergeList.remove(newValue.getText());
                    } else {
                        mergeList.add(newValue.getText());
                    }
                } 
            }
        }

);

pdfListView.setCellFactory(new Callback<ListView<Label>, ListCell<Label>>() {
    @Override
    public ListCell<Label> call(ListView<Label> param) {
        // Create new inner class for [checkbox + label combo]
        JFXCheckBox checkBox = new JFXCheckBox();
         class SelectCell extends ListCell<Label> {
            @Override
            public void updateItem(Label item, boolean selected) {
                super.updateItem(item, selected);
                if (selected) {
                    setGraphic(null);
                }
                else {
                    checkBox.setText(item.getText());
                    setGraphic(checkBox);
                }
            }
        }
        return new SelectCell();
    }
});
 
```
