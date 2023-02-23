package ru.practicum.common.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "public", name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;

    private Float lat;

    private Float lon;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id.equals(location.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

