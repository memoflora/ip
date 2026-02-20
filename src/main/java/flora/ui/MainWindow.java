package flora.ui;

import flora.Flora;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Flora flora;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/User.png"));
    private final Image floraImage = new Image(this.getClass().getResourceAsStream("/images/Flora.png"));

    /**
     * Initializes the controller, binding the scroll pane to always scroll to the bottom.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Flora instance and displays the welcome message.
     *
     * @param flora The Flora instance to use for handling user input.
     */
    public void setFlora(Flora flora) {
        this.flora = flora;
        dialogContainer.getChildren().add(
                DialogBox.getFloraDialog(this.flora.getWelcomeMessage(), floraImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Flora's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = flora.getResponse(input);
        boolean isError = response.startsWith("Error: ");
        DialogBox floraBox = isError
                ? DialogBox.getFloraErrorDialog(response, floraImage)
                : DialogBox.getFloraDialog(response, floraImage);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                floraBox
        );
        userInput.clear();
        if (flora.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
