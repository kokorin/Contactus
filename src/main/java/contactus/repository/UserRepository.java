package contactus.repository;

import com.vk.api.sdk.objects.friends.FriendsList;
import com.vk.api.sdk.objects.friends.UserXtrLists;
import com.vk.api.sdk.objects.users.User;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.UserMin;

public interface UserRepository extends Repository<UserFull> {
    void saveMin(UserMin item);
    void saveCommon(User item);
    void saveFriend(UserXtrLists item);
    void saveFriendsList(FriendsList friendsList);

    UserMin loadMin(int id);
    User loadCommon(int id);
    UserXtrLists loadFriend(int id);
    FriendsList loadFriendsList(int id);
}
