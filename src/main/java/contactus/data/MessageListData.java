package contactus.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import contactus.event.MessageEvent;
import contactus.model.Message;
import contactus.repository.MessageRepository;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MessageListData {
    private final EventBus eventBus;
    private final MessageRepository messageRepository;
    private Integer contactId;
    private final ObservableList<Message> messages = FXCollections.observableArrayList();

    public MessageListData(EventBus eventBus, MessageRepository messageRepository) {
        this.eventBus = eventBus;
        this.messageRepository = messageRepository;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
        List<Message> contactMessages = messageRepository.loadAll(contactId);
        Platform.runLater(() -> messages.setAll(contactMessages));
    }

    public ObservableList<Message> getMessages() {
        return FXCollections
                .unmodifiableObservableList(messages)
                .sorted(Comparator.comparing(Message::getId));
    }

    @PostConstruct
    public void postConstruct() {
        eventBus.register(this);
    }

    @PreDestroy
    public void preDestroy() {
        eventBus.unregister(this);
        Platform.runLater(messages::clear);
    }

    @Subscribe
    public void onMessageEvent(MessageEvent messageEvent) {
        Message message = messageEvent.getMessage();

        if (contactId == null || !Objects.equals(message.getFromId(), contactId)) {
            return;
        }

        Platform.runLater(() -> messages.add(message));
    }
}
