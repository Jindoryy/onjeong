package com.a503.onjeong.domain.group.dto;

import com.a503.onjeong.domain.user.dto.UserDTO;
import lombok.Builder;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class GroupDTO {
    private Long groupId;
    private Long ownerId;
    private String name;
    private List<UserDTO> userList;

    @Builder
    public GroupDTO(
            Long groupId,
            Long ownerId,
            String name,
            List<UserDTO> userList
    ) {
        this.groupId = groupId;
        this.name = name;
        this.ownerId = ownerId;
        this.userList = userList;
    }
    public void updateOwnerId (Long ownerId) {
        this.ownerId = ownerId;
    }
    public void updateName (String name) {
        this.name = name;
    }

}
