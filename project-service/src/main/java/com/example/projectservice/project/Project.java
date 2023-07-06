package com.example.projectservice.project;

import com.example.projectservice.projectmember.ProjectMember;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "project",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"adminId", "title"})
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
    @Column(nullable = false)
    private Integer adminId;
    @Column(nullable = false)
    private String adminEmail;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Boolean isPublic;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> members = new HashSet<>();
    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProjectMember> invitations = new HashSet<>();

    @CreationTimestamp
    private Date creationDate;
}

