package contactus.repository;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import contactus.event.ContactEvent;
import contactus.event.ContactGroupEvent;
import contactus.event.MessageEvent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class RepositorySaver {
    private final EventBus eventBus;
    private final ContactRepository contactRepository;
    private final MessageRepository messageRepository;

    public RepositorySaver(EventBus eventBus, ContactRepository contactRepository, MessageRepository messageRepository) {
        this.eventBus = eventBus;
        this.contactRepository = contactRepository;
        this.messageRepository = messageRepository;
    }

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
        contactRepository.saveContactGroup(event.getContactGroup());
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        if (event.getType() == MessageEvent.Type.SEND) {
            return;
        }

        messageRepository.save(event.getMessage());
    }
}
