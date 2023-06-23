package com.example.projectservice.project;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"owner", "title"})
)
public class Project {

    @Id
    @SequenceGenerator(
            name = "project_id_sequence",
            sequenceName = "project_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_id_sequence"
    )
    private Integer id;
    private Integer owner;
    private String title;
    private String description;
    private Boolean isPublic;

    @CreationTimestamp
    private Date creationDate;
}
