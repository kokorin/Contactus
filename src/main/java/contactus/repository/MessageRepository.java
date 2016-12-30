package contactus.repository;

import com.vk.api.sdk.objects.messages.Message;

public interface MessageRepository extends Repository<Message>{
    int maxId();
}
