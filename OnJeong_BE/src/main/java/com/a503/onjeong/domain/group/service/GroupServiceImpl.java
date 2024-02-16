package com.a503.onjeong.domain.group.service;

import com.a503.onjeong.domain.group.Group;
import com.a503.onjeong.domain.group.dto.GroupDTO;
import com.a503.onjeong.domain.group.dto.GroupUserListDTO;
import com.a503.onjeong.domain.group.repository.GroupRepository;
import com.a503.onjeong.domain.usergroup.service.UserGroupService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserGroupService userGroupService;

    @Override
    public List<GroupDTO> groupList(Long userId) {
        Optional<List<Group>> groupList = groupRepository.findAllByOwnerId(userId); //내 그룹 리스트
        return userGroupService.findAllByGroupId(groupList);
    }

    @Override
    @Transactional
    public void groupCreate(GroupUserListDTO groupUserListDTO) {
        Group group = Group.builder()
                .name(groupUserListDTO.getName())
                .ownerId(groupUserListDTO.getOwnerId())
                .build();
        Long groupId = groupRepository.save(group).getId(); //그룹 생성, id 바로 반환
        userGroupService.userGroupCreate(groupUserListDTO.getUserList(), groupId);
    }

    @Override
    @Transactional
    public void groupDelete(Long groupId) {
        userGroupService.userGroupDelete(groupId);
        groupRepository.deleteById(groupId);
    }

    @Override
    @Transactional
    public void groupUpdate(GroupUserListDTO groupUserListDTO) {
        //groupId로 name 찾고 , groupDTO.name이랑 같은지 판별,
        //같다면 이름은 업데이트 안해도 됨
        Group group = groupRepository.findById(groupUserListDTO.getGroupId()).orElseThrow();
        if (!group.getName().equals(groupUserListDTO.getName())) {
            group.updateGroupName(groupUserListDTO.getName());
            groupRepository.save(group);
        }
        //성능 비교 해야함
        //1번 방법
        //1. user-group 다 삭제하고  2. 새로 insert
        //그룹
        userGroupService.userGroupDelete(groupUserListDTO.getGroupId());
        userGroupService.userGroupCreate(groupUserListDTO.getUserList(), groupUserListDTO.getGroupId());
        //2번 방법
        //1. user-group에서 groupID모든 객체 가져와서
        // userList서 userId 있다면 그대로, 없다면 삭제
    }

    @Override
    public GroupDTO groupDetail(GroupDTO groupDTO) {
        Group group = groupRepository.findById(groupDTO.getGroupId()).orElseThrow();
        groupDTO.updateName(group.getName());
        groupDTO.updateOwnerId(group.getOwnerId());
        return groupDTO;
    }


}
