package com.toomet.chat.room;

import com.toomet.chat.client.UserClient;
import com.toomet.chat.client.dto.User;
import com.toomet.chat.client.dto.UserClientResponseDto;
import com.toomet.chat.exceptions.BadRequestException;
import com.toomet.chat.exceptions.ForbiddenException;
import com.toomet.chat.exceptions.NotFoundException;
import com.toomet.chat.image.Image;
import com.toomet.chat.image.ImageService;
import com.toomet.chat.image.ImageUploader;
import com.toomet.chat.image.ImageUploaderResponse;
import com.toomet.chat.jwt.JwtService;
import com.toomet.chat.member.Member;
import com.toomet.chat.member.MemberService;
import com.toomet.chat.message.MessageImage;
import com.toomet.chat.room.dto.*;
import com.toomet.chat.room.pub.RoomMemberShipPublic;
import com.toomet.chat.room.pub.UpdateRoomPublic;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserClient userClient;
    private final MemberService memberService;
    private final JwtService jwtService;
    private final ImageService imageService;
    private final ImageUploader imageUploader;
    private final RabbitTemplate rabbitTemplate;
    private final int maxMemberInRoom = 500;

    @Value("${spring.rabbitmq.exchange.socket_exchange}")
    private String socketExchange;

    @Value("${spring.rabbitmq.routing.socket_update_chat_room}")
    private String socketUpdateChatRoomRoutingKey;

    @Value("${spring.rabbitmq.routing.socket_chat_room_member_ship}")
    private String socketChatRoomMemberShipRoutingKey;

    public NewRoomResponseDto createNewRoom(NewRoomRequestDto dto) throws IOException {

        if (dto.getMember().size() < 2) {
            throw new BadRequestException("Để trò chuyện cần có ít nhất 2 thành viên");
        }

        if (dto.getType().equals(Room.RoomType.GROUP) && dto.getMember().size() < 3) {
            throw new BadRequestException("Cuộc trò chuyện nhóm cần có ít nhất 3 thành viên");
        }

        if (dto.getType().equals(Room.RoomType.SINGLE) && dto.getMember().size() > 2) {
            throw new BadRequestException("Nhóm đơn có tối đa 2 thành viên");
        }

        if (dto.getMember().size() > maxMemberInRoom)
            throw new BadRequestException("Số thành viên tôi đa của 1 phòng là " + maxMemberInRoom);

        RoomSetting setting = new RoomSetting();
        setting.setColor(dto.getColor());
        setting.setIcon(dto.getIcon());

        Room room = Room.builder()
                .name(dto.getName())
                .type(dto.getType())
                .setting(setting)
                .build();

        String cloudPath = UUID.randomUUID().toString();
        Resource avatarFile = new ClassPathResource("static/default-avatar.png");
        byte[] avatarBytes = Files.readAllBytes(Path.of(avatarFile.getURI()));
        ImageUploaderResponse imageUploaderResponse = imageUploader.uploadImage(cloudPath, avatarBytes);

        Image avatar = Image.builder()
                .cloudPublicId(imageUploaderResponse.getPublicId())
                .format(imageUploaderResponse.getFormat())
                .url(imageUploaderResponse.getUrl())
                .room(room)
                .cloudPath(cloudPath)
                .build();

        List<Member> members = dto
                .getMember()
                .stream()
                .map((Long memberId) -> {
                            try {
                                UserClientResponseDto response = userClient.getUserInfo(memberId);
                                User member = User.convertFromUserClientResponse(response);
                                return Member.builder()
                                        .id(member.getId())
                                        .room(room)
                                        .build();
                            } catch (Exception e) {
                                throw new BadRequestException("Không tìm thấy người dùng " + memberId);
                            }
                        }
                )
                .toList();

        room.setAvatar(avatar);
        room.setMembers(members);

        Room newRoom = roomRepository.save(room);

        return NewRoomResponseDto.convertFromRoom(newRoom);
    }

    public String joinRoom(Long roomId, Long userId, JoinRoomDto dto) {
        if (jwtService.isTokenExpired(dto.getToken())) {
            throw new ForbiddenException("Lời mời đã hết hạn");
        }

        Long tokenRoomId = jwtService.extractJoinRoomToken(dto.getToken());

        if (!tokenRoomId.equals(roomId)) {
            throw new BadRequestException("Mã phòng không hợp lệ");
        }
        return this.joinRoom(roomId, userId);
    }

    private String joinRoom(Long roomId, Long memberId) {

        Room room = getRoomById(roomId);
        List<Member> members = new java.util.ArrayList<>(room.getMembers().stream().toList());

        Member existedMember = room.getMembers().stream()
                .filter(m -> m.getId().equals(memberId)).findFirst().orElse(null);

        if (existedMember != null && existedMember.getState().equals(Member.RoomState.MEMBER)) {
            throw new BadRequestException("Người dùng " + memberId + " đã là thành viên của phòng này!");
        }

        if (
                room.getType().equals(Room.RoomType.GROUP) && members.size() >= maxMemberInRoom ||
                        room.getType().equals(Room.RoomType.SINGLE) && members.size() >= 2
        ) {
            throw new BadRequestException("Số thành viên trong phòng đã đạt tối đa");
        }

        User memberData;
        try {
            UserClientResponseDto responseDto = userClient.getUserInfo(memberId);
            memberData = User.convertFromUserClientResponse(responseDto);
        } catch (Exception e) {
            throw new BadRequestException("Không tìm thấy người dùng " + memberId);
        }

        if (existedMember == null) {
            Member newMember = Member.builder()
                    .id(memberId)
                    .room(room)
                    .build();
            members.add(newMember);
            room.setMembers(members);
            roomRepository.save(room);
        } else {
            existedMember.setState(Member.RoomState.MEMBER);
            memberService.saveMember(existedMember);
        }

        RoomMemberShipPublic roomMemberShipPublic = RoomMemberShipPublic.builder()
                .roomId(roomId)
                .member(memberData)
                .timestamp(new Date())
                .type(RoomMemberShipPublic.MemberShipType.JOIN)
                .build();

        rabbitTemplate.convertAndSend(socketExchange, socketChatRoomMemberShipRoutingKey, roomMemberShipPublic);

        return "Tham gia phòng thành công. Giờ đây bạn đã là thành viên của phòng " + room.getName();
    }


    public String leaveRoom(Long roomId, Long userId) {
        Room room = getRoomById(roomId);

        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new BadRequestException("Bạn chưa là thành viên của phòng này");
        }


        int roomMember = room.getMembers().size() - 1;

        User memberData;
        try {
            UserClientResponseDto responseDto = userClient.getUserInfo(userId);
            memberData = User.convertFromUserClientResponse(responseDto);
        } catch (Exception e) {
            throw new BadRequestException("Không tìm thấy người dùng " + userId);
        }

        memberService.leaveRoom(userId, roomId);

//        if (
//                room.getType().equals(Room.RoomType.GROUP) && roomMember <= 2 ||
//                        room.getType().equals(Room.RoomType.SINGLE) && roomMember <= 1
//        ) {
//            roomRepository.delete(room);
//        }


        RoomMemberShipPublic roomMemberShipPublic = RoomMemberShipPublic.builder()
                .roomId(roomId)
                .member(memberData)
                .timestamp(new Date())
                .type(RoomMemberShipPublic.MemberShipType.LEAVE)
                .build();

        rabbitTemplate.convertAndSend(socketExchange, socketChatRoomMemberShipRoutingKey, roomMemberShipPublic);
        return "Bạn đã rời phòng " + room.getName();
    }

    public Page<RoomResponseDto> getAllRoom(Long memberId, int page, int limit) {
        Page<Room> rooms = roomRepository
                .getAllByMemberId(
                        memberId,
                        PageRequest
                                .of(page, limit,
                                        Sort.by("updatedAt").descending())
                );

        return rooms.map((room) -> roomResponseConverter(room, memberId));
    }

    public RoomResponseDto getRoomByIdAndUserId(Long roomId, Long userId) {
        // Retrieve a reference to the Room entity using the provided roomId
        Room room = roomRepository.getReferenceById(roomId);

        // If the user is not a member of the room, throw a ForbiddenException
        if (memberService.isNotRoomMember(userId, roomId))
            throw new ForbiddenException("Bạn không có quyền truy cập phòng này");

        // Convert the Room entity to a RoomResponseDto and return it
        return roomResponseConverter(room, userId);
    }

    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(
                () -> new NotFoundException("Không tìm thấy phòng " + roomId)
        );
    }

    public RoomSettingResponseDto getRoomSetting(Long roomId, Long userId) {
        Room room = roomRepository.getReferenceById(roomId);
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Bạn không có quyền truy cập phòng này");
        }
        return RoomSettingResponseDto.convertFromRoom(room);
    }

    public List<RoomResponseDto> searchRoom(Long userId, String keyword) {
        List<Room> rooms = roomRepository.searchByMemberIdAndName(userId, keyword);
        return rooms
                .stream()
                .map((room -> roomResponseConverter(room, userId))
                ).toList();
    }

    public String addMember(Long roomId, Long userId, AddMemberDto dto) {
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Phòng không tồn tại hoặc bạn chưa là thành viên của phòng này!");
        }
        dto.getMembers().forEach(memberId -> this.joinRoom(roomId, memberId));
        return "Thêm thành viên thành công";
    }

    public JoinTokenResponseDto generateJoinToken(Long roomId, Long userId) {
        if (memberService.isNotRoomMember(userId, roomId))
            throw new ForbiddenException("Bạn không có quyền truy cập phòng này");

        String token = jwtService.generateJoinToken(roomId);
        Long exp = jwtService.getTokenExpiredTime();

        return JoinTokenResponseDto.builder()
                .token(token)
                .exp(exp)
                .build();
    }

    public String updateAvatar(Long roomId, Long userId, MultipartFile avatar) {
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Phòng không tồn tại hoặc bạn chưa là thành viên của phòng này!");
        }
        Room room = getRoomById(roomId);

        Image roomAvatar = room.getAvatar();

        ImageUploaderResponse response = imageService.upsertImage(roomAvatar.getCloudPath(), avatar);
        System.out.println(response);
        roomAvatar.setCloudPublicId(response.getPublicId());
        roomAvatar.setUrl(response.getUrl());
        roomAvatar.setFormat(response.getFormat());
        room.setAvatar(roomAvatar);
        Room updatedRoom = roomRepository.save(room);

        sendUpdatedRoomToSocket(updatedRoom, userId);

        return roomAvatar.getUrl();
    }

    public String updateName(Long roomId, Long userId, UpdateRoomNameDto dto) {
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Phòng không tồn tại hoặc bạn chưa là thành viên của phòng này!");
        }
        Room room = getRoomById(roomId);
        room.setName(dto.getNewName());
        Room updatedRoom = roomRepository.save(room);

        sendUpdatedRoomToSocket(updatedRoom, userId);

        return room.getName();
    }

    public RoomSettingResponseDto updateSetting(Long roomId, Long userId, UpdateSettingDto dto) {
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Phòng không tồn tại hoặc bạn chưa là thành viên của phòng này!");
        }
        Room room = getRoomById(roomId);
        RoomSetting roomSetting = room.getSetting();
        if (dto.getColor() != null) roomSetting.setColor(dto.getColor());
        if (dto.getIcon() != null) roomSetting.setIcon(dto.getIcon());
        room.setSetting(roomSetting);
        Room updatedRoom = roomRepository.save(room);

        sendUpdatedRoomToSocket(updatedRoom, userId);

        return RoomSettingResponseDto.convertFromRoom(room);
    }

    public Page<RoomImageResponseDto> getAllRoomImage(Long roomId, Long userId, int page, int limit) {
        if (memberService.isNotRoomMember(userId, roomId)) {
            throw new ForbiddenException("Phòng không tồn tại hoặc bạn chưa là thành viên của phòng này!");
        }
        PageRequest request = PageRequest.of(page, limit, Sort.by("updatedAt").descending());
        Page<MessageImage> images = roomRepository.getAllRoomImage(roomId, request);
        return images.map(RoomImageResponseDto::convertFromMessageImage);
    }

    private String[] splitRoomName(String roomName) {
        return roomName.split("@");
    }

    private RoomResponseDto roomResponseConverter(Room room, Long userId) {
        if (room.getType() == Room.RoomType.GROUP) return RoomResponseDto.convertFromRoom(room);
        try {
            String[] users = splitRoomName(room.getName());
            String id = Objects.equals(users[0], userId.toString()) ? users[1] : users[0];
            UserClientResponseDto response = userClient.getUserInfo(Long.parseLong(id));
            User member = User.convertFromUserClientResponse(response);
            return RoomResponseDto.convertToSingleRoom(room, member);
        } catch (Exception e) {
            return RoomResponseDto.convertFromRoom(room);
        }
    }

    private void sendUpdatedRoomToSocket(Room room, Long userId) {
        UpdateRoomPublic updateRoomPublic = UpdateRoomPublic.convertFromRoom(room, userId);
        rabbitTemplate.convertAndSend(socketExchange, socketUpdateChatRoomRoutingKey, updateRoomPublic);
    }
}
