package contactus.event;

import contactus.model.ContactGroup;
import contactus.repository.ContactRepository;

public class ContactGroupListener implements EventListener<ContactGroup>{
    private final ContactRepository repository;

    public ContactGroupListener(ContactRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onEvent(ContactGroup contactGroup) {
        repository.saveContactGroup(contactGroup);
    }

}
