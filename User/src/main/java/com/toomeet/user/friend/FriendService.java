package com.toomeet.user.friend;

import com.toomeet.user.exceptions.BadRequestException;
import com.toomeet.user.friend.dto.FriendResponseDto;
import com.toomeet.user.user.Status;
import com.toomeet.user.user.User;
import com.toomeet.user.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    public boolean isExistingFriend(Long user1Id, Long user2Id) {
        return friendRepository
                .existsFriendByUser1IdAndUser2Id(user1Id, user2Id);
    }

    public void saveFriend(User user1, User user2) {
        Friend newFriend = Friend.builder()
                .user1(user1)
                .user2(user2)
                .build();

        friendRepository.save(newFriend);
    }

    public Page<FriendResponseDto> getAllFriend(User user, int page, int limit) {
        Page<Friend> friends = friendRepository.getAllByUserId(user.getId(), PageRequest.of(page, limit));
        return friends
                .map(friend ->
                        friend.getUser1().getId().equals(user.getId()) ?
                                friend.getUser2() :
                                friend.getUser1())
                .map(friend ->
                        mapper.map(friend, FriendResponseDto.class)
                );
    }

    public String removeFriend(User user, Long friendId) {

        if (user.getId().equals(friendId)) {
            throw new BadRequestException("Bạn không thể hủy kết bạn với chính bạn");
        }

        if (!isExistingFriend(user.getId(), friendId)) {
            throw new BadRequestException("Bạn và người này chưa phải là bạn");
        }
        friendRepository.deleteByUser1IdAndUser2Id(user.getId(), friendId);
        return "Hủy kết bạn thành công";
    }


    public Page<FriendResponseDto> getOnlineFriends(User user, int page, int limit) {
        Page<Friend> friends = friendRepository.getAllByStatus(user.getId(), Status.ONLINE, PageRequest.of(page, limit));
        return friends.map(friend ->
                friend.getUser1().getId().equals(user.getId()) ?
                        mapper.map(friend.getUser2(), FriendResponseDto.class) :
                        mapper.map(friend.getUser1(), FriendResponseDto.class)
        );

    }

    public Page<FriendResponseDto> searchFriend(User user, String keyword, int page, int limit) {
        PageRequest request = PageRequest.of(page, limit);
        Page<Friend> friends = friendRepository.searchByName(user.getId(), keyword, request);
        return friends.map(friend ->
                friend.getUser1().getId().equals(user.getId()) ?
                        mapper.map(friend.getUser2(), FriendResponseDto.class) :
                        mapper.map(friend.getUser1(), FriendResponseDto.class)
        );
    }
}
