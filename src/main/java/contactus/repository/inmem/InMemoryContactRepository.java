package contactus.repository.inmem;

import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.repository.ContactRepository;

import java.util.*;

class InMemoryContactRepository extends InMemoryRepository<Contact> implements ContactRepository {
    private final Map<Integer, ContactGroup> groups = new HashMap<>();

    @Override
    public List<Contact> loadAll() {
        List<Contact> result = new ArrayList<>(getData().values());
        result.sort(Comparator.comparing(Contact::getScreenName));

        return result;
    }

    @Override
    public void saveContactGroup(ContactGroup contactGroup) {
        groups.put(contactGroup.getId(), contactGroup);
    }

    @Override
    public ContactGroup loadGroup(int id) {
        return groups.get(id);
    }

    @Override
    public List<ContactGroup> loadGroups() {
        return new ArrayList<>(groups.values());
    }

    @Override
    protected int getId(Contact item) {
        return item.getId();
    }
}
