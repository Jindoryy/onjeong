package com.a503.onjeong.domain.usergroup;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class UserGroupId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "group_id")
    private Long groupId;

    @Builder
    public UserGroupId(
            Long userId,
            Long groupId
    ) {
        this.userId=userId;
        this.groupId=groupId;
    }
}
