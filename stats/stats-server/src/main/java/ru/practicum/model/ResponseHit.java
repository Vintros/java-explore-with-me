package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class ResponseHit {

    private String app;
    @Id
    private String uri;
    private Long hits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ResponseHit that = (ResponseHit) o;
        return uri != null && Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
