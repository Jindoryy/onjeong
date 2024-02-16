package com.a503.onjeong.domain.group.service;

import com.a503.onjeong.domain.group.dto.GroupDTO;
import com.a503.onjeong.domain.group.dto.GroupUserListDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GroupService {
    List<GroupDTO> groupList(Long userId);

    void groupCreate(GroupUserListDTO groupUserListDTO);

    void groupDelete(Long groupId);

    void groupUpdate(GroupUserListDTO groupUserListDTO);


    GroupDTO groupDetail(GroupDTO groupDTO);
}
