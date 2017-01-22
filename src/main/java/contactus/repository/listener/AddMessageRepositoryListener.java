package contactus.repository.listener;

import com.vk.api.sdk.objects.updates.AddMessage;
import contactus.core.Converter;
import contactus.event.EventListener;
import contactus.model.Message;
import contactus.repository.MessageRepository;

public class AddMessageRepositoryListener implements EventListener<AddMessage> {
    private final MessageRepository repository;

    public AddMessageRepositoryListener(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onEvent(AddMessage event) {
        Message message = Converter.convertMessage(event.getMessage());
        repository.save(message);
    }
}
