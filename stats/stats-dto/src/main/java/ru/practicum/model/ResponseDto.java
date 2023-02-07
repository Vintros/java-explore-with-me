package ru.practicum.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Setter
@Getter
@Entity
public class ResponseDto {

    private String app;
    @Id
    private String uri;
    private Long hits;
}
