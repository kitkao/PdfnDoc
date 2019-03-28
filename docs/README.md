
# Some issues

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
