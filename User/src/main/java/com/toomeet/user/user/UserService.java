package com.toomeet.user.user;

import com.google.gson.Gson;
import com.toomeet.user.exceptions.NotFoundException;
import com.toomeet.user.user.dto.UserOverviewDto;
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
    private final Jedis jedis;
    private final Gson gson;

    public User getUserById(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy userId " + userId));
    }

    public Page<UserOverviewDto> getSuggestionsUser(User user, int page, int limit) {
        Page<User> users = userRepository.getSuggestions(
                user.getId(),
                PageRequest.of(page, limit, Sort.by("name").ascending())
        );
        return users.map(item -> UserOverviewDto.builder()
                .id(item.getId())
                .name(item.getName())
                .profile(mapper.map(item.getProfile(), UserOverviewDto.Profile.class))
                .build());
    }
}
