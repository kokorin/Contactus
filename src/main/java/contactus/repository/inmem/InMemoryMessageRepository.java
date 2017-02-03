package contactus.repository.inmem;

import contactus.model.Message;
import contactus.repository.MessageRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

class InMemoryMessageRepository extends InMemoryRepository<Message> implements MessageRepository {
    @Override
    public int maxId() {
        return getData().keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    @Override
    protected int getId(Message item) {
        return item.getId();
    }

    @Override
    public List<Message> loadAll(Integer userId) {
        List<Message> result = getData().values().stream()
                .filter(m -> Objects.equals(m.getUserId(), userId))
                .collect(Collectors.toList());
        result.sort(Comparator.comparing(Message::getDate).reversed());

        return result;
    }

    @Override
    public List<Message> loadAll(Integer userId, Instant since) {
        List<Message> result = getData().values().stream()
                .filter(m -> Objects.equals(m.getUserId(), userId))
                .filter(m -> m.getDate().isAfter(since))
                .collect(Collectors.toList());
        result.sort(Comparator.comparing(Message::getDate).reversed());

        return result;
    }

    @Override
    public Message loadLast(Integer userId) {
        return getData().values().stream()
                .filter(m -> Objects.equals(m.getUserId(), userId))
                .reduce((l, r) -> l.getDate().isAfter(r.getDate()) ? l : r)
                .orElse(null);
    }

    @Override
    public List<Message> loadLast() {
        Map<Integer, Integer> lastMsgId = new HashMap<>();
        for (Message message : getData().values()) {
            Integer lastId = lastMsgId.get(message.getFromId());
            if (lastId == null || lastId < message.getId()) {
                lastMsgId.put(message.getFromId(), message.getId());
            }
        }

        return lastMsgId.values().stream().map(this::load).collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Integer> loadUnreadCount() {
        Map<Integer, Integer> result = new HashMap<>();
        for (Message message : getData().values()) {
            if (!message.isUnread()) {
                continue;
            }
            Integer count = result.getOrDefault(message.getFromId(), 0);
            result.put(message.getFromId(), count + 1);
        }

        return result;
    }
}
