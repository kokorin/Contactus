package contactus.view.message;

import contactus.model.Message;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;


public class MessagingController {
    @FXML
    protected ListView<Message> messageListView;
    @FXML
    protected TextArea textArea;
    @FXML
    protected Button sendButton;

   /* private final Data data;
    private final LoadMessageHistoryService loadMessageHistoryService;
    private final SendMessageService sendMessageService;

    private final ObjectProperty<Contact> contact = new SimpleObjectProperty<>();
    private final ListChangeListener<? super Message> scrollDownListener = c -> {
        ObservableList<Message> messages = messageListView.getItems();
        if (messages != null && !messages.isEmpty()) {
            messageListView.scrollTo(messages.size() - 1);
        }
    };

    @Autowired
    public MessagingController(Data data, LoadMessageHistoryService loadMessageHistoryService, SendMessageService sendMessageService) {
        this.data = data;
        this.loadMessageHistoryService = loadMessageHistoryService;
        this.sendMessageService = sendMessageService;
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

    @FXML
    protected void initialize() {
        assert textArea != null;
        assert sendButton != null;
        assert messageListView != null;

        messageListView.setCellFactory(list -> new MessageListCell());

        contact.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                messageListView.setItems(FXCollections.emptyObservableList());
                return;
            }

            ObservableList<Message> messages = messageListView.getItems();
            if (messages != null) {
                messages.removeListener(scrollDownListener);
            }
            messages = data.getMessages(newValue.getUser().getId());
            messageListView.setItems(messages);
            messages.addListener(scrollDownListener);

            //TODO save handling of different periods and buttons to load messages for selected period
            //currently only one-time load is supported
            if (messages.size() < 2) {
                loadMessageHistoryService.setUserId(newValue.getUser().getId());
                loadMessageHistoryService.restart();
            }
            messageListView.setItems(messages);
        });

        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isControlDown()) {
                event.consume();
                sendMessage();
            }
        });
        sendButton.disableProperty().bind(textArea.textProperty().isEmpty().or(sendMessageService.runningProperty()));

        sendButton.setOnAction(event -> sendMessage());

        sendMessageService.setOnSucceeded(event -> textArea.setText(""));
    }

    private void sendMessage() {
        String text = textArea.getText();
        if (text.isEmpty()) {
            return;
        }
        sendMessageService.setMessage(getContact().getUser().getId(), text);
        sendMessageService.restart();
    }*/
}
