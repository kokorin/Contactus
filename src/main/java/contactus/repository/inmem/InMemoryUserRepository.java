package contactus.repository.inmem;

import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.UserMin;
import contactus.repository.UserRepository;

class InMemoryUserRepository extends InMemoryRepository<UserFull> implements UserRepository {
    @Override
    public void saveMin(UserMin item) {

    }

    @Override
    public void saveCommon(User item) {

    }

    @Override
    public void saveFriend(UserXtrLists item) {

    }

    @Override
    public void saveFriendsList(FriendsList friendsList) {

    }

    @Override
    public UserMin loadMin(int id) {
        return null;
    }

    @Override
    public User loadCommon(int id) {
        return null;
    }

    @Override
    public UserXtrLists loadFriend(int id) {
        return null;
    }

    @Override
    public FriendsList loadFriendsList(int id) {
        return null;
    }

    @Override
    protected int getId(UserFull item) {
        return item.getId();
    }
}
