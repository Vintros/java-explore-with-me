package ru.practicum.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(schema = "public", name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Long id;

    @Column(nullable = false)
    private Boolean pinned;

    @Column(length = 200, nullable = false)
    private String title;

    @OneToMany
    @JoinColumn(name = "compilation_id")
    private List<Event> events;
}
