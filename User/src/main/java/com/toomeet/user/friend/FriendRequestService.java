package com.toomeet.user.friend;

import com.toomeet.user.exceptions.BadRequestException;
import com.toomeet.user.exceptions.ConflictException;
import com.toomeet.user.exceptions.ForbiddenException;
import com.toomeet.user.exceptions.NotFoundException;
import com.toomeet.user.friend.dto.AddFriendRequestDto;
import com.toomeet.user.friend.dto.FriendRequestResponseDto;
import com.toomeet.user.friend.dto.ReplyAddFriendDto;
import com.toomeet.user.user.User;
import com.toomeet.user.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendRequestService {

    private final FriendService friendService;
    private final FriendRequestRepository requestRepository;
    private final UserService userService;
    private final ModelMapper mapper;

    public String addFriend(User sender, AddFriendRequestDto dto) {

        Long receiverId = dto.getReceiverId();

        if (sender.getId().equals(dto.getReceiverId())) {
            throw new BadRequestException("Bạn không thế kết bạn với chính mình");
        }

        User receiver = userService.getUserById(receiverId);

        if (friendService.isExistingFriend(sender.getId(), receiverId)) {
            throw new ConflictException("Người dùng đã là bạn bè");
        }

        if (isExistingRequest(sender.getId(), receiverId)) {
            throw new ConflictException("Yêu cầu kết bạn đã được gửi");
        }


        FriendRequest newFriendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .message(dto.getMessage())
                .build();

        requestRepository.save(newFriendRequest);
        return "Yêu cầu kết bạn đã được gửi";

    }

    public String acceptFriend(User sender, ReplyAddFriendDto dto) {
        FriendRequest request = isValidReplyRequest(sender, dto);
        friendService.saveFriend(request.getSender(), request.getReceiver());
        requestRepository.deleteById(dto.getRequestId());
        return "Bạn và " + request.getSender().getName() + " đã là bạn bè";
    }

    public String rejectFriend(User sender, ReplyAddFriendDto dto) {
        isValidReplyRequest(sender, dto);
        requestRepository.deleteById(dto.getRequestId());
        return "Từ chối thành công";
    }

    public List<FriendRequestResponseDto> getSentFriendRequests(User user) {
        List<FriendRequest> requests = requestRepository.getAllBySenderId(user.getId());
        return convertToResponseDto(requests);
    }

    public List<FriendRequestResponseDto> getReceivedFriendRequests(User user) {
        List<FriendRequest> requests = requestRepository.getAllByReceiverId(user.getId());
        return convertToResponseDto(requests);
    }

    private boolean isExistingRequest(Long senderId, Long receiverId) {
        return requestRepository.existsBySenderIdAndReceiverId(senderId, receiverId);
    }

    private FriendRequest isValidReplyRequest(User sender, ReplyAddFriendDto dto) {
        Long requestId = dto.getRequestId();

        FriendRequest friendRequest = requestRepository
                .findById(requestId).orElseThrow(
                        () -> new NotFoundException("Không tìm thấy yêu cầu kết bạn này")
                );

        if (!friendRequest.getReceiver().getId().equals(sender.getId())) {
            throw new ForbiddenException("Bạn không có quyền phản hồi yêu cầu này");
        }
        return friendRequest;
    }

    private List<FriendRequestResponseDto> convertToResponseDto(List<FriendRequest> requests) {
        return requests
                .stream()
                .map(request -> mapper.map(request, FriendRequestResponseDto.class))
                .toList();
    }
}
