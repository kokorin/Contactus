package contactus.repository;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import contactus.event.ContactEvent;
import contactus.event.ContactGroupEvent;
import contactus.event.MessageEvent;
import lombok.AllArgsConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@AllArgsConstructor
public class RepositorySaver {
    private final EventBus eventBus;
    private final ContactRepository contactRepository;
    private final ContactGroupRepository contactGroupRepository;
    private final MessageRepository messageRepository;

    @PostConstruct
    public void postConstruct() {
        eventBus.register(this);
    }

    @PreDestroy
    public void preDestroy() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onContactEvent(ContactEvent event) {
        contactRepository.save(event.getContact());
    }

    @Subscribe
    public void onContactGroupEvent(ContactGroupEvent event) {
        contactGroupRepository.save(event.getContactGroup());
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.Type.SENDING) {
            return;
        }

        messageRepository.save(event.getMessage());
    }
}
