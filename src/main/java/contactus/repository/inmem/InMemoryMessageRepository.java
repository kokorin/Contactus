package contactus.repository.inmem;

import com.vk.api.sdk.objects.messages.Message;
import contactus.repository.MessageRepository;

class InMemoryMessageRepository extends InMemoryRepository<Message> implements MessageRepository {
    @Override
    public int maxId() {
        return getData().keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    @Override
    protected int getId(Message item) {
        return item.getId();
    }
}
