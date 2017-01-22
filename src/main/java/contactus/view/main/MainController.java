package contactus.view.main;

import contactus.data.Data;
import contactus.view.View;
import contactus.view.contact.ContactListController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainController {
    @FXML
    protected BorderPane rootPane;

    private final Data data;
    private final View view;

    public MainController(Data data, View view) {
        this.data = data;
        this.view = view;
    }

    @FXML
    protected void initialize() {
        View.ControllerBuilder<ContactListController> contactListBuilder = view.forController(ContactListController.class);
        Node contactListRoot = contactListBuilder.getRoot();
        ContactListController contactListController = contactListBuilder.getController();


        rootPane.setLeft(contactListRoot);
        //rootPane.setCenter();
    }
}
