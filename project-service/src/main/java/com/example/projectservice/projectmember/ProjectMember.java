package com.example.projectservice.projectmember;

import com.example.projectservice.project.Project;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
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


    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne
    @JoinColumn(name = "projectId")
    @JsonIgnore
    private Project project;
    @Column(nullable = false,unique=true)
    private Integer memberId;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private MemberRole role;

    @CreationTimestamp
    private Date creationDate;
}

