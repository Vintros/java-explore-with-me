package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.admin.storage.AdmUserRepository;
import ru.practicum.common.dto.UserDto;
import ru.practicum.common.exceptions.EntityNoAccessException;
import ru.practicum.common.exceptions.EntityNotFoundException;
import ru.practicum.common.mapper.UserMapper;
import ru.practicum.common.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdmUserServiceImpl implements AdmUserService {

    private final AdmUserRepository repository;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (repository.existsByEmail(userDto.getEmail())) {
            throw new EntityNoAccessException("Email must be unique");
        }
        User createdUser = repository.save(mapper.convertToUser(userDto));
        return mapper.convertToUserDto(createdUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("This user not founded");
        }
        repository.deleteById(id);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        List<User> users;
        if (ids.isEmpty()) {
            users = repository.findAll(pageable).toList();
        } else {
            users = repository.findAllByIdIn(ids, pageable);
        }
        return mapper.convertAllToUserDto(users);
    }
}
