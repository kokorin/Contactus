package contactus.repository.listener;

import contactus.event.EventListener;
import contactus.model.ContactGroup;
import contactus.repository.ContactRepository;

public class ContactGroupRepositoryListener implements EventListener<ContactGroup> {
    private final ContactRepository repository;

    public ContactGroupRepositoryListener(ContactRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onEvent(ContactGroup contactGroup) {
        repository.saveContactGroup(contactGroup);
    }

}
