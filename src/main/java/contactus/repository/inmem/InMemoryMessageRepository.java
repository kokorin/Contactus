package contactus.repository.inmem;

import contactus.model.Message;
import contactus.repository.MessageRepository;

import java.time.Instant;
import java.util.*;
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
    public Set<Message> loadAllUnread(Integer contactId, Message.Direction direction) {
        return getData().values().stream()
                .filter(Message::isUnread)
                .filter(m -> Objects.equals(m.getContactId(), contactId))
                .filter(m -> Objects.equals(m.getDirection(), direction))
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
    public Map<Integer, Set<Integer>> loadUnreadIds() {
        Map<Integer, Set<Integer>> result = new HashMap<>();
        getData().values().stream()
                .filter(Message::isUnread)
                .forEach(m -> result.computeIfAbsent(m.getContactId(), id -> new HashSet<>()).add(m.getId()));
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
