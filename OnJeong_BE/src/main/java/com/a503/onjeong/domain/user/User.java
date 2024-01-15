package com.a503.onjeong.domain.user;

import com.a503.onjeong.global.BaseEntity;
import jakarta.persistence.*;

@Entity
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long Id;
}
