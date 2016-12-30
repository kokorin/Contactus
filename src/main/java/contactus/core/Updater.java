package contactus.core;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.friends.responses.GetFieldsResponse;
import com.vk.api.sdk.objects.friends.responses.GetListsResponse;
import com.vk.api.sdk.objects.messages.LongpollParams;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.responses.GetLongPollHistoryResponse;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.queries.users.UserField;
import contactus.event.EventDispatcher;

public class Updater implements Runnable {
    private final UserActor actor;
    private final VkApiClient client;
    private final int maxSeenMessageId;
    private final EventDispatcher eventDispatcher;

    private volatile boolean stopped = false;

    private Updater(UserActor actor, VkApiClient client, int maxSeenMessageId, EventDispatcher eventDispatcher) {
        this.actor = actor;
        this.client = client;
        this.maxSeenMessageId = maxSeenMessageId;
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    public void run() {
        try {
            GetListsResponse listsResponse = client.friends().getLists(actor).execute();
            for (FriendsList friendsList : listsResponse.getItems()) {
                eventDispatcher.dispatchEvent(friendsList);
            }

            GetFieldsResponse friendsResponse = client.friends().get(actor, UserField.NICKNAME, UserField.PHOTO_50).execute();
            for (UserXtrLists user : friendsResponse.getItems()) {
                eventDispatcher.dispatchEvent(user);
            }

            LongpollParams params = client.messages().getLongPollServer(actor).needPts(true).execute();

            GetLongPollHistoryResponse historyResponse = client.messages().getLongPollHistory(actor)
                    .maxMsgId(maxSeenMessageId)
                    .ts(params.getTs())
                    //.pts(params.getPts())
                    .execute();
            for (Message message : historyResponse.getMessages().getMessages()) {
                eventDispatcher.dispatchEvent(message);
            }

            for (User user : historyResponse.getProfiles()) {
                eventDispatcher.dispatchEvent(user);
            }
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stopped = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserActor actor;
        private VkApiClient client;
        private int maxSeenMessageId;
        private EventDispatcher eventDispatcher;

        private Builder() {}

        public Builder actor(UserActor actor) {
            this.actor = actor;
            return this;
        }

        public Builder client(VkApiClient client) {
            this.client = client;
            return this;
        }

        public Builder maxSeenMessageId(int maxSeenMessageId) {
            this.maxSeenMessageId = maxSeenMessageId;
            return this;
        }

        public Builder eventDispatcher(EventDispatcher eventDispatcher) {
            this.eventDispatcher = eventDispatcher;
            return this;
        }

        public Updater build() {
            return new Updater(actor, client, maxSeenMessageId, eventDispatcher);
        }
    }
}
