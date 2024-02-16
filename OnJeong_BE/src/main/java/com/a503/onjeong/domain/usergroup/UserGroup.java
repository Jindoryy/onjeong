package com.a503.onjeong.domain.usergroup;


import com.a503.onjeong.domain.group.Group;
import com.a503.onjeong.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate
public class UserGroup {

    @EmbeddedId
    private UserGroupId userGroupId;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("groupId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Builder
    public UserGroup(
            Long userId,
            Long groupId,
            User user,
            Group group
    ) {
        this.userGroupId = UserGroupId.builder()
                .groupId(groupId)
                .userId(userId)
                .build();
        this.user=user;
        this.group=group;
    }
}
