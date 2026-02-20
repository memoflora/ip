package flora.ui;

import java.io.IOException;

import flora.Flora;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Flora using FXML.
 */
public class Main extends Application {
    private final Flora flora = new Flora();

    /**
     * Sets up and displays the primary application window.
     *
     * @param stage The primary stage provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("Flora");

            stage.setMinHeight(220);
            stage.setMinWidth(417);

            fxmlLoader.<MainWindow>getController().setFlora(flora);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
