package contactus.event;

import contactus.model.Message;
import contactus.repository.MessageRepository;

public class MessageListener implements EventListener<Message>{
    private final MessageRepository repository;

    public MessageListener(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onEvent(Message message) {
        repository.save(message);
    }
}
