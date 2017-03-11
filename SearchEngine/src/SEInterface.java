/**
 * Created by cjk98 on 3/11/2017.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SEInterface extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
//        final Scene scene = new Scene();
//        primaryStage.setScene(scene);
        primaryStage.setTitle("Search Engine");
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("SEInterface.fxml"));
            primaryStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        final ListView listView = new ListView();
//        listView.setPrefSize(200, 250);
////        listView.setEditable(true);
//        StackPane root = new StackPane();
//        root.getChildren().add(listView);
//        primaryStage.setScene(new Scene(root, 200, 250));
        primaryStage.show();
    }
}
