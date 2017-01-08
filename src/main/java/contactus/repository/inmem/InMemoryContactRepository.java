package contactus.repository.inmem;

import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.repository.ContactRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class InMemoryContactRepository extends InMemoryRepository<Contact> implements ContactRepository {
    @Override
    public List<Contact> loadAll() {
        List<Contact> result = new ArrayList<>(getData().values());
        result.sort(Comparator.comparing(Contact::getScreenName));

        return result;
    }

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
