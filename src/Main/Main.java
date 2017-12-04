package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("UserInterface.fxml"));
        primaryStage.setTitle("MorgTrade 1.0.0");
        primaryStage.setScene(new Scene(root, 750, 750));
        primaryStage.show();
    }

    //TODO make custom stylesheet for searchlistview
    //TODO fix modlistview removing items in focus when right clicking anywhere in the list

    public static void main(String[] args) {
        launch(args);
    }
}
