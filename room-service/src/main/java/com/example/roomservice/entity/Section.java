package com.example.roomservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "section",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"roomId", "name"})
)
public class Section {
    @Id
    @SequenceGenerator(
            name = "section_id_sequence",
            sequenceName = "section_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "section_id_sequence"
    )
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "roomId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Room room;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();

}
