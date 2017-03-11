import java.net.URL;
import java.util.*;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


public class SEController implements Initializable {

    @FXML
    private AnchorPane sePane;
    @FXML
    private VBox titleBox;
    @FXML
    private Text seBandName;
    @FXML
    private Text groupName;
    @FXML
    private TextField seTextField;
    @FXML
    private TableView resultTable;
    @FXML
    private TableColumn<Integer, String> urlColumn;
    @FXML
    private ListView resultListView;

    private QueryMatching qmObj;
    private static final ObservableList resultList = FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initializing");
        qmObj = new QueryMatching();
        titleBox.setAlignment(Pos.CENTER_RIGHT);
        seBandName.setText("Search Engine V1.0");
        groupName.setText("IR W17 Grad 48123229, 71169660");
    }

    @FXML
    private void handleClose(ActionEvent event) {
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//        }
        System.out.println("Close Test");
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        ArrayList<String> result = qmObj.search(seTextField.getText());
//        resultList.addAll(result);
////        System.out.println(resultList);
////        resultTable.setItems(resultList);
//        TableColumn testCol = new TableColumn("Test");
//        testCol.setCellValueFactory(ComboBoxListCell.forListView(resultList));
//        resultTable.getColumns().add(testCol);

        for (int i = 0; i < result.size(); i++) {
            resultTable.getItems().add(i);
        }
        urlColumn.setCellValueFactory(cellData -> {
            Integer rowIndex = cellData.getValue();
            return new ReadOnlyStringWrapper(result.get(rowIndex));
        });
    }

}