package ru.practicum.common.dto;

import lombok.Data;
import ru.practicum.common.util.StateRequest;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    @NotEmpty
    private List<Long> requestIds;

    @NotNull
    private StateRequest status;

}
