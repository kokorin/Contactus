package contactus.repository.inmem;

import contactus.repository.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

abstract class InMemoryRepository<T> implements Repository<T> {
    private final Map<Integer, T> data = new HashMap<>();

    @Override
    public void save(T item) {
        data.put(getId(item), item);
    }

    @Override
    public void addAll(Collection<T> items) {
        for (T item : items) {
            save(item);
        }
    }

    @Override
    public void delete(int id) {
        data.remove(id);
    }

    @Override
    public T load(int id) {
        return data.get(id);
    }

    @Override
    public void close() {
        data.clear();
    }

    protected Map<Integer, T> getData() {
        return data;
    }

    protected abstract int getId(T item);
}
