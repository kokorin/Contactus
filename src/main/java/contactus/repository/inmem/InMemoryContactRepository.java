package contactus.repository.inmem;

import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.repository.ContactRepository;

class InMemoryContactRepository extends InMemoryRepository<Contact> implements ContactRepository {
    @Override
    public void saveContactGroup(ContactGroup contactGroup) {

    }

    @Override
    public ContactGroup loadContactGroup(int id) {
        return null;
    }

    @Override
    protected int getId(Contact item) {
        return item.getId();
    }
}
