package contactus.view;

import contactus.event.EventDispatcher;
import contactus.model.Contact;
import contactus.model.Message;
import contactus.repository.ContactRepository;
import contactus.repository.MessageRepository;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainController {
    @FXML
    protected Pane rootPane;
    @FXML
    protected StackPane content;
    @FXML
    protected ListView<Contact> contactList;
    @FXML
    protected ListView<Message> messageList;

    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;
    private final EventDispatcher eventDispatcher;
    private final AtomicBoolean contactRefreshScheduled = new AtomicBoolean(false);

    public MainController(ContactRepository contactRepository, MessageRepository messageRepository, EventDispatcher eventDispatcher) {
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
        this.eventDispatcher = eventDispatcher;
    }

    @FXML
    protected void initialize() {
        Objects.requireNonNull(contactList);
        Objects.requireNonNull(messageList);

        contactList.setCellFactory(TextFieldListCell.<Contact>forListView(new StringConverter<Contact>() {
            @Override
            public String toString(Contact contact) {
                return contact.getId() + " " + contact.getScreenName();
            }

            @Override
            public Contact fromString(String string) {
                return null;
            }
        }));

        messageList.setCellFactory(TextFieldListCell.<Message>forListView(new StringConverter<Message>() {
            @Override
            public String toString(Message message) {
                return message.getId() + " " + message.getBody();
            }

            @Override
            public Message fromString(String string) {
                return null;
            }
        }));

        contactList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Contact>() {
            @Override
            public void changed(ObservableValue<? extends Contact> observable, Contact oldValue, Contact contact) {
                List<Message> messages = contact != null ? messageRepository.loadAll(contact.getId()) : Collections.emptyList();
                messageList.setItems(FXCollections.observableList(messages));
            }
        });

        eventDispatcher.addListener(Contact.class, event -> {
            boolean schedule = contactRefreshScheduled.compareAndSet(false, true);
            if (schedule) {
                Platform.runLater(this::refreshContacts);
            }
        });
        refreshContacts();
    }

    private void refreshContacts() {
        List<Contact> contacts = contactRepository.loadAll();
        contactList.setItems(FXCollections.observableList(contacts));
        contactRefreshScheduled.set(false);
    }
}
