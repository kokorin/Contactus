package contactus.view.main;

import contactus.data.ContactListData;
import contactus.view.View;
import contactus.view.contact.ContactListController;
import contactus.view.message.MessagingController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainController {
    @FXML
    protected BorderPane rootPane;

    private final ContactListData data;
    private final View view;

    public MainController(ContactListData data, View view) {
        this.data = data;
        this.view = view;
    }

    @FXML
    protected void initialize() {
        View.ControllerBuilder<ContactListController> contactListBuilder = view.forController(ContactListController.class);
        Node contactListRoot = contactListBuilder.getRoot();
        ContactListController contactListController = contactListBuilder.getController();
        rootPane.setLeft(contactListRoot);

        View.ControllerBuilder<MessagingController> messagingBuilder = view.forController(MessagingController.class);
        Node messagingRoot = messagingBuilder.getRoot();
        MessagingController messagingController = messagingBuilder.getController();
        rootPane.setCenter(messagingRoot);

        messagingController.contactProperty().bind(contactListController.selectedContactProperty());
    }
}
