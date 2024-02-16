package com.a503.onjeong.domain.phonebook;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class PhonebookId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "friend_id")
    private Long friendId;

    @Builder
    public PhonebookId(
            Long userId,
            Long friendId
    ) {
        this.userId=userId;
        this.friendId=friendId;
    }
}
