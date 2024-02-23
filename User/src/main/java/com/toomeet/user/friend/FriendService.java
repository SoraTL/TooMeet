package com.toomeet.user.friend;

import com.toomeet.user.exceptions.BadRequestException;
import com.toomeet.user.friend.dto.FriendResponseDto;
import com.toomeet.user.user.User;
import com.toomeet.user.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<FriendResponseDto> getAllFriend(User user) {
        List<Friend> friends = friendRepository.getAllByUser1IdOrUser2Id(user.getId(), user.getId());
        return friends
                .stream()
                .map(friend ->
                        friend.getUser1().getId().equals(user.getId()) ?
                                friend.getUser2() :
                                friend.getUser1())
                .map(friend ->
                        mapper.map(friend, FriendResponseDto.class)
                ).toList();
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

    
}
