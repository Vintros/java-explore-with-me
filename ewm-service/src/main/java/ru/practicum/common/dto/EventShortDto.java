package ru.practicum.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.common.model.Category;
import ru.practicum.common.model.Location;
import ru.practicum.common.model.User;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class EventShortDto {

    private Long id;

    private String annotation;

    private CategoryDtoForEvent category;

    private Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private UserDtoForEvent initiator;

    private Boolean paid;

    private String title;

    private Long views;

    public static class EventShortDtoBuilder {

        private final CategoryDtoForEvent category = new CategoryDtoForEvent();
        private final UserDtoForEvent initiator = new UserDtoForEvent();
        private final LocationDtoForEvent location = new LocationDtoForEvent();

        public EventShortDtoBuilder category(Category category) {
            this.category.id = category.getId();
            this.category.name = category.getName();
            return this;
        }

        public EventShortDtoBuilder initiator(User initiator) {
            this.initiator.id = initiator.getId();
            this.initiator.name = initiator.getName();
            return this;
        }

        public EventShortDtoBuilder location(Location location) {
            this.location.lat = location.getLat();
            this.location.lon = location.getLon();
            return this;
        }
    }

    @Data
    private static class CategoryDtoForEvent {
        private Long id;
        private String name;

    }

    @Data
    private static class UserDtoForEvent {

        private Long id;
        private String name;
    }

    @Data
    private static class LocationDtoForEvent {

        private Float lat;
        private Float lon;
    }
}
