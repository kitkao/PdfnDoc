
# Development path & issues

## Changed from C# to JAVA

- Was intended to merge docs directly. But formats differs in each docs.
- Word 2007 could invoke com from :
  - <https://www.microsoft.com/zh-cn/download/details.aspx?id=7>

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
        Word._Document wordDocument = wordApplication.Documents.Open(
                                    fileToMerge1
                                    , ref missing
                                    , ref missing
                                    , ref missing
                                    , ref missing);
        Word.Selection selection = wordApplication.Selection;
        wordDocument.Merge(fileToMerge2, ref missing, ref missing, ref missing, ref missing);
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
        wordDocument = null;
    }
    catch (Exception ex)
    {
        throw ex;
    }
    finally
    {
        wordApplication.Quit(ref missing, ref missing, ref missing);
    }
}
```

## docx4j package trial

docx4j differs in 2.8 and 3.0 version.

```java

    String docPath  = System.getProperty("user.dir") + "/docs/1-1.doc";
    String pdfPath  = System.getProperty("user.dir") + "/docs/1-1n.pdf";


//        XWPFDocument docx = new XWPFDocument(OPCPackage.openOrCreate(new File(docPath)));
//        XWPFWordExtractor wx = new XWPFWordExtractor(docx);
//        String text = wx.getText();
//        System.out.println("text = "+text);


    FileInputStream fin = new FileInputStream(new File(docPath));
    FileOutputStream fout = new FileOutputStream(new File(pdfPath));

    WordprocessingMLPackage wmlPackage = Doc.convert(fin);


    Mapper fontMapper = new IdentityPlusMapper();
    wmlPackage.setFontMapper(fontMapper);

    fontMapper.getFontMappings().put("华文行楷", PhysicalFonts.getPhysicalFonts().get("STXingkai"));
    fontMapper.getFontMappings().put("隶书", PhysicalFonts.getPhysicalFonts().get("LiSu"));
    fontMapper.getFontMappings().put("宋体",PhysicalFonts.getPhysicalFonts().get("SimSun"));
    fontMapper.getFontMappings().put("微软雅黑",PhysicalFonts.getPhysicalFonts().get("Microsoft Yahei"));
    fontMapper.getFontMappings().put("黑体",PhysicalFonts.getPhysicalFonts().get("SimHei"));
    fontMapper.getFontMappings().put("楷体",PhysicalFonts.getPhysicalFonts().get("KaiTi"));
    fontMapper.getFontMappings().put("新宋体",PhysicalFonts.getPhysicalFonts().get("NSimSun"));
    fontMapper.getFontMappings().put("华文仿宋", PhysicalFonts.getPhysicalFonts().get("STFangsong"));
    fontMapper.getFontMappings().put("宋体扩展",PhysicalFonts.getPhysicalFonts().get("simsun-extB"));
    fontMapper.getFontMappings().put("仿宋",PhysicalFonts.getPhysicalFonts().get("FangSong"));
    fontMapper.getFontMappings().put("仿宋_GB2312",PhysicalFonts.getPhysicalFonts().get("FangSong_GB2312"));
    fontMapper.getFontMappings().put("幼圆",PhysicalFonts.getPhysicalFonts().get("YouYuan"));
    fontMapper.getFontMappings().put("华文宋体",PhysicalFonts.getPhysicalFonts().get("STSong"));
    fontMapper.getFontMappings().put("华文中宋",PhysicalFonts.getPhysicalFonts().get("STZhongsong"));

//        DOCX4J 2.8.1
    org.docx4j.convert.out.pdf.PdfConversion c = new org.docx4j.convert.out.pdf.viaXSLFO.Conversion(wmlPackage);
    //清除pdf页码额外调试信息
    Docx4jProperties.getProperties().setProperty("docx4j.Log4j.Configurator.disabled", "true");
    Log4jConfigurator.configure();
    org.docx4j.convert.out.pdf.viaXSLFO.Conversion.log.setLevel(Level.OFF);
    c.output(fout, new PdfSettings());
//        DOCX4J 3.0.1
//        Docx4J.toPDF(wmlPackage, fout);

    fin.close();
    fout.close();
}


private WordprocessingMLPackage getMLPackage(FileInputStream iStream) throws Exception{

    PrintStream originalStdout = System.out;
    System.setOut(new PrintStream(new OutputStream() {
        public void write(int b) {

        }
    }));

    WordprocessingMLPackage mlPackage = Doc.convert(iStream);
    System.setOut(originalStdout);
    return mlPackage;
}

```

## grammar

- T.class.cast 比 (T) 强制转换好
- "CLASS".class 等于 getClass()
- 以下同等，倾向第一个，方便复用
  - 可以在controller中定义@FXML

    ```JAVA
    ((Stage) rootPane.getScene().getWindow()).close();
    ```

  - 在方法中用event获取stage

    ```JAVA
    Stage.class.cast(Control.class.cast(event.getSource()).getScene().getWindow());
    ```

## listview handler choices

Due to JAVAFX Listview bugs, final choice is selectedItems event.

```JAVA
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
