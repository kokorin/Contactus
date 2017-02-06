package contactus.repository.inmem;

import contactus.model.Contact;
import contactus.model.ContactGroup;
import contactus.repository.ContactRepository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class InMemoryContactRepository extends InMemoryRepository<Contact> implements ContactRepository {
    private final Map<Integer, ContactGroup> groups = new HashMap<>();

    @Override
    public Set<Contact> loadAll() {
        return new HashSet<>(getData().values());
    }

    @Override
    protected int getId(Contact item) {
        return item.getId();
    }
}
