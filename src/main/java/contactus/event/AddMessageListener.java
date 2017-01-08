package contactus.event;

import com.vk.api.sdk.objects.updates.AddMessage;
import contactus.core.Converter;
import contactus.model.Message;
import contactus.repository.MessageRepository;

public class AddMessageListener implements EventListener<AddMessage>{
    private final MessageRepository repository;

    public AddMessageListener(MessageRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onEvent(AddMessage event) {
        Message message = Converter.convertMessage(event.getMessage());
        repository.save(message);
    }
}
