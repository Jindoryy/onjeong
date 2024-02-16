package com.a503.onjeong.domain.group.dto;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupUserListDTO {
    private Long groupId;
    private Long ownerId;
    private String name;
    private List<Long> userList;

    @Builder
    public GroupUserListDTO(
            Long groupId,
            Long ownerId,
            String name,
            List<Long> userList
    ) {
        this.groupId = groupId;
        this.name = name;
        this.ownerId = ownerId;
        this.userList = userList;
    }
}
