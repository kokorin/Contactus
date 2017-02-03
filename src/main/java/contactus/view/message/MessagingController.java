package contactus.view.message;

import com.google.common.eventbus.EventBus;
import contactus.data.MessageListData;
import contactus.event.MessageEvent;
import contactus.model.Contact;
import contactus.model.Message;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;


public class MessagingController {
    private final EventBus eventBus;
    private final MessageListData messageListData;
    private final ObjectProperty<Contact> contact = new SimpleObjectProperty<>();

    @FXML
    protected ListView<Message> messageListView;
    @FXML
    protected TextArea textArea;
    @FXML
    protected Button sendButton;

    public MessagingController(EventBus eventBus, MessageListData messageListData) {
        this.eventBus = eventBus;
        this.messageListData = messageListData;
    }

    public Contact getContact() {
        return contact.get();
    }

    public ObjectProperty<Contact> contactProperty() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact.set(contact);
    }

    private final ListChangeListener<? super Message> scrollDownListener = c -> {
        ObservableList<Message> messages = messageListView.getItems();
        if (messages != null && !messages.isEmpty()) {
            messageListView.scrollTo(messages.size() - 1);
        }
    };

    @FXML
    protected void initialize() {
        assert textArea != null;
        assert sendButton != null;
        assert messageListView != null;

        messageListView.setCellFactory(list -> new MessageListCell());
        ObservableList<Message> messages = messageListData.getMessages();
        messageListView.setItems(messages);
        messages.addListener(scrollDownListener);

        contact.addListener((observable, oldValue, newValue) -> {
            messageListData.setContactId(newValue == null ? null : newValue.getId());
        });

        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isControlDown()) {
                event.consume();
                sendMessage();
            }
        });

        sendButton.setOnAction(event -> sendMessage());
    }

    private void sendMessage() {
        String text = textArea.getText();
        if (text.isEmpty()) {
            return;
        }

        Message message = new Message();
        message.setFromId(getContact().getId());
        message.setBody(text);

        MessageEvent event = new MessageEvent(MessageEvent.Type.SEND, message);
        eventBus.post(event);

        //TODO wait for send completion
        textArea.setText("");
    }
}
