package com.example.projectservice.projectmember;

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
        @UniqueConstraint(columnNames={"memberId"})
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
    private Integer memberId;
    @Enumerated(EnumType.ORDINAL)
    private MemberRole role;

    @CreationTimestamp
    private Date creationDate;
}

