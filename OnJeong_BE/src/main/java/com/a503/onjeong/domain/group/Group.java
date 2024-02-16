package com.a503.onjeong.domain.group;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate
@Table(name="meet")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(name = "group_name")
    private String name;

    @Column(name = "owner_id")
    private Long ownerId;
    @Builder
    public Group(
            String name,
            Long ownerId
    ){
        this.name=name;
        this.ownerId=ownerId;
    }
    public void updateGroupName (String name) {
        this.name = name;
    }
}
