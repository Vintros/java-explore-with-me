package ru.practicum.common.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.common.dto.UserDto;
import ru.practicum.common.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMapper {

    public User convertToUser(UserDto userDto) {
        return new User(null, userDto.getEmail(), userDto.getName());
    }

    public UserDto convertToUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public List<UserDto> convertAllToUserDto(List<User> users) {
        return users.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

}
