package com.example.projectservice.invite;


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
@Table
public class Invite {

    @Id
    @SequenceGenerator(
            name = "invite_id_sequence",
            sequenceName = "invite_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "invite_id_sequence"
    )
    private Integer id;
    @Column(nullable = false)
    private Integer projectId;
    @Column(nullable = false)
    private Integer invitedId;

    @CreationTimestamp
    private Date creationDate;
}

