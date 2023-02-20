package ru.practicum.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.AdmUserService;
import ru.practicum.common.dto.UserDto;
import ru.practicum.common.model.FromSizeRequest;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdmUserController {

    private final AdmUserService service;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Create user {}", userDto);
        return service.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = FromSizeRequest.of(from, size, sort);
        log.info("Get user by ids: {}", ids);
        return service.getUsers(ids, pageable);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        log.info("Delete user by id: {}", id);
        service.deleteUser(id);
    }


}
