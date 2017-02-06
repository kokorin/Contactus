package contactus.repository;

import java.util.Collection;
import java.util.Set;

public interface Repository<T>{
   void save(T item);
   void saveAll(Collection<T> items);
   void delete(int id);
   T load(int id);
   Set<T> loadAll();
}
