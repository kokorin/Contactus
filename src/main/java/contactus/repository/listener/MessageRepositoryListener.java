package contactus.repository.listener;

import contactus.event.EventListener;
import contactus.model.Message;
import contactus.repository.MessageRepository;

public class MessageRepositoryListener implements EventListener<Message> {
    private final MessageRepository repository;

    public MessageRepositoryListener(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onEvent(Message message) {
        repository.save(message);
    }
}
