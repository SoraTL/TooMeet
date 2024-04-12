package com.toomeet.user.user;

import com.google.gson.Gson;
import com.toomeet.user.exceptions.NotFoundException;
import com.toomeet.user.user.dto.UserInfo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final Jedis jedis;

    public User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy userId " + userId));
    }

    public UserInfo getUserInfo(Long userId) {
        final String cacheInfoKey = "use-cache-" + userId;
//        String userInfoString = jedis.get(cacheInfoKey);

//        if (userInfoString != null) {
//            return gson.fromJson(userInfoString, UserInfo.class);
//        }

        User user = getUserById(userId);
        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .status(user.getStatus())
                .profile(mapper.map(user.getProfile(), UserInfo.Profile.class))
                .build();
//        String cacheString = gson.toJson(userInfo);
//        jedis.set(cacheInfoKey, cacheString);
        return userInfo;
    }

    public Page<UserInfo> getSuggestionsUser(User user, int page, int limit) {
        Page<User> users = userRepository.getSuggestions(
                user.getId(),
                PageRequest.of(page, limit, Sort.by("createdAt").ascending())
        );
        return users.map(item -> UserInfo.builder()
                .id(item.getId())
                .name(item.getName())
                .profile(mapper.map(item.getProfile(), UserInfo.Profile.class))
                .build());
    }


}
