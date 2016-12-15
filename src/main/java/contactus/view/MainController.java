package contactus.view;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class MainController {
    @FXML
    protected Pane rootPane;
    @FXML
    protected StackPane content;

    private final View view;

    public MainController(View view) {
        this.view = view;
    }

    @FXML
    protected void initialize() {

    }
}
