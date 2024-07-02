package com.toomet.chat.member;

import com.google.gson.Gson;
import com.toomet.chat.client.UserClient;
import com.toomet.chat.client.dto.User;
import com.toomet.chat.client.dto.UserClientResponseDto;
import com.toomet.chat.exceptions.BadRequestException;
import com.toomet.chat.exceptions.ForbiddenException;
import com.toomet.chat.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserClient userClient;
    private final Gson gson;
    private final Jedis jedis;

    public Member getMemberById(MemberId memberId) {
        return memberRepository.getReferenceById(memberId);
    }

    public Member getMemberById(Long memberId, Long roomId) {
        return memberRepository.findByIdAndRoomId(memberId, roomId).orElseThrow(
                () -> new NotFoundException("Người dùng id: " + memberId + " chưa là thành viên của phòng hoặc phòng không tồn tại.")
        );
    }

    public User getMemberInfo(Long memberId) {

        String memberCacheString = jedis.get("cache-member-" + memberId);

        UserClientResponseDto response = gson.fromJson(memberCacheString, UserClientResponseDto.class);

        if (response == null) {
            response = userClient.getUserInfo(memberId);
            String memberString = gson.toJson(response);
            jedis.set("cache-member-" + response.getId(), memberString);
        }

        return User.convertFromUserClientResponse(response);
    }

    public List<User> getAllMemberByRoomId(Long userId, Long roomId) {
        if (isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Bạn không có quyền truy cập phòng này");
        }
        List<Long> members = memberRepository.getAllByRoomId(roomId);

        List<User> responses = new ArrayList<>();

        members.forEach(memberId -> {
            try {
                UserClientResponseDto userResponse = userClient.getUserInfo(memberId);
                responses.add(User.convertFromUserClientResponse(userResponse));
            } catch (Exception e) {
                log.error(e.toString());
            }
        });
        return responses;
    }

    public void leaveRoom(Long memberId, Long roomId) {
        Member member = getMemberById(memberId, roomId);
        if (member == null) {
            throw new BadRequestException("Thành viên không tồn tại!");
        }
        member.setState(Member.RoomState.LEAVED);
//        member.setDeletedMessageTime(new Date());

        memberRepository.save(member);
    }

    public void saveMember(Member member) {
        memberRepository.save(member);
    }

    public boolean isNotRoomMember(Long memberId, Long roomId) {
        Member member = getMemberById(memberId, roomId);
        return member == null || member.getState().equals(Member.RoomState.LEAVED);
    }

    public Date deleteMessage(Long userId, Long roomId) {
        if (isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Bạn không có quyền truy cập phòng này");
        }

        Date deleteTimestamp = new Date();
        Member member = getMemberById(userId, roomId);
        member.setDeletedMessageTime(deleteTimestamp);
        memberRepository.save(member);

        return deleteTimestamp;
    }
}
