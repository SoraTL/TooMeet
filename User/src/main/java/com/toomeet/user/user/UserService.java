package com.toomeet.user.user;

import com.toomeet.user.exceptions.NotFoundException;
import com.toomeet.user.user.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public User getUserById(Long userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy userId " + userId));
    }



}
