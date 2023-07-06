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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "room_members",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"roomId", "memberId"})
)
public class RoomMember {
    @Id
    @SequenceGenerator(
            name = "room_member_id_sequence",
            sequenceName = "room_member_id_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "room_member_id_sequence"
    )
    private Integer id;
    @Column(nullable = false)
    private Integer memberId;

    @ManyToOne
    @JoinColumn(name = "roomId",nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Room room;

    @CreationTimestamp
    private Date creationDate;
}
