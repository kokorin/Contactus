package contactus.repository;

import java.io.Closeable;
import java.util.Collection;

public interface Repository<T> extends Closeable {
   void save(T item);
   void addAll(Collection<T> items);
   void delete(int id);
   T load(int id);

   @Override
   void close();
}
