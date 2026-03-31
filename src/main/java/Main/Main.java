package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/org/example/sukaldea/fxml/Login-view.fxml"));
        
        Scene scene = new Scene(fxml.load(), 750, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sukaldea");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
