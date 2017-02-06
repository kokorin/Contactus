package contactus.repository.inmem;

import contactus.model.ContactGroup;
import contactus.repository.ContactGroupRepository;

public class InMemoryContactGroupRepository extends InMemoryRepository<ContactGroup> implements ContactGroupRepository {
    @Override
    protected int getId(ContactGroup item) {
        return item.getId();
    }
}
