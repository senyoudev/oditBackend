package com.example.projectservice.projectmember;

import com.example.projectservice.project.Project;
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

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"projectId", "userId"})
)
public class ProjectMember {

    @Id
    @SequenceGenerator(
            name = "projectmember_id_sequence",
            sequenceName = "projectmember_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "projectmember_id_sequence"
    )
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "projectId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Project project;

    @Column(nullable = false,unique = true)
    private Integer userId;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private MemberRole role;

    @CreationTimestamp
    private Date creationDate;
}

