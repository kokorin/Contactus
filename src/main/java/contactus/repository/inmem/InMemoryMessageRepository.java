package contactus.repository.inmem;

import contactus.model.Message;
import contactus.repository.MessageRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class InMemoryMessageRepository extends InMemoryRepository<Message> implements MessageRepository {
    private int pts = 1;

    @Override
    protected int getId(Message item) {
        return item.getId();
    }

    @Override
    public Set<Message> loadAll(Integer contactId) {
        return getData().values().stream()
                .filter(m -> Objects.equals(m.getContactId(), contactId))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Message> loadAll(Integer contactId, Instant since) {
        return getData().values().stream()
                .filter(m -> Objects.equals(m.getContactId(), contactId))
                .filter(m -> m.getDate().isAfter(since))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Message> loadLast() {
        Map<Integer, Integer> lastMsgId = new HashMap<>();
        for (Message message : getData().values()) {
            Integer lastId = lastMsgId.get(message.getContactId());
            if (lastId == null || lastId < message.getId()) {
                lastMsgId.put(message.getContactId(), message.getId());
            }
        }

        return lastMsgId.values().stream().map(this::load).collect(Collectors.toSet());
    }

    @Override
    public Map<Integer, Integer> loadUnreadCount() {
        Map<Integer, Integer> result = new HashMap<>();
        for (Message message : getData().values()) {
            if (!message.isUnread()) {
                continue;
            }
            Integer count = result.getOrDefault(message.getContactId(), 0);
            result.put(message.getContactId(), count + 1);
        }

        return result;
    }

    @Override
    public void saveLastPts(int value) {
        this.pts = value;
    }

    @Override
    public int loadLastPts() {
        return pts;
    }
}
