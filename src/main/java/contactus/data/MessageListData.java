package contactus.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import contactus.event.MessageEvent;
import contactus.event.MessageEvent.Type;
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
                .sorted(Comparator.comparing(Message::getDate));
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

        if (!Objects.equals(message.getContactId(), contactId)) {
            return;
        }

        if (messageEvent.getType() == Type.UPDATE) {int index = -1;
            //Looking for message with the same id
            //TODO reverse loop?
            for (int i = 0; i < messages.size(); ++i) {
                Message m = messages.get(i);
                if (m.getId() != null && m.getId().equals(message.getId())) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                messages.add(message);
            } else {
                messages.set(index, message);
            }
        }

        if (messageEvent.getType() == Type.SENT) {
            Platform.runLater(() -> {
                int index = -1;
                //Looking for message with the same random ID and id (if present)
                //TODO reverse loop?
                for (int i = 0; i < messages.size(); ++i) {
                    Message m = messages.get(i);
                    if ((m.getId() == null || m.getId().equals(message.getId()))
                            && m.getRandomId() != null
                            && m.getRandomId().equals(message.getRandomId())
                            ) {
                        index = i;
                        break;
                    }
                }

                if (index == -1) {
                    messages.add(message);
                } else {
                    messages.set(index, message);
                }
            });
            return;
        }

        if (messageEvent.getType() == Type.REMOVE) {
            Platform.runLater(() -> messages.removeIf(m -> Objects.equals(message.getId(), m.getId())));
            return;
        }

        //RECEIVE OR SENDING
        Platform.runLater(() -> messages.add(message));
    }
}
