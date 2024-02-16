package com.a503.onjeong.domain.usergroup.service;

import com.a503.onjeong.domain.group.Group;
import com.a503.onjeong.domain.group.dto.GroupDTO;
import com.a503.onjeong.domain.group.repository.GroupRepository;
import com.a503.onjeong.domain.user.User;
import com.a503.onjeong.domain.user.dto.UserDTO;
import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.domain.usergroup.UserGroup;
import com.a503.onjeong.domain.usergroup.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserGroupServiceImpl implements UserGroupService {
    private final UserGroupRepository userGroupRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void userGroupCreate(@NotNull List<Long> userList, Long groupId) {
        Optional<Group> group = groupRepository.findById(groupId);
        Group group1 = group.orElseThrow();

        for (Long userId : userList) {
            UserGroup userGroup = UserGroup.builder()
                    .userId(userId)
                    .groupId(groupId)
                    .group(group1)
                    .user(userRepository.findById(userId).orElseThrow())
                    .build();

            userGroupRepository.save(userGroup);
        }
    }

    @Override
    public void userGroupDelete(Long groupId) {
        userGroupRepository.deleteAllByGroupId(groupId);
    }


    @Override
    public List<GroupDTO> findAllByGroupId(@NotNull Optional<List<Group>> groupList) {
        List<GroupDTO> groupDTOList = new ArrayList<>();
        for (Group group : groupList.orElseThrow()) {
            List<UserGroup> userList = userGroupRepository.findAllByGroupId(group.getId());
            List<UserDTO> userDTOList = new ArrayList<>();
            for (UserGroup userGroup : userList) {
                User user = userGroup.getUser();
                UserDTO userDTO = UserDTO.builder()
                        .userId(user.getId())
                        .name(user.getName())
                        .phoneNumber(user.getPhoneNumber())
                        .build();
                userDTOList.add(userDTO);
            }
            GroupDTO groupDTO = GroupDTO.builder()
                    .userList(userDTOList)
                    .groupId(group.getId())
                    .name(group.getName())
                    .build();
            groupDTOList.add(groupDTO);

        }
        return groupDTOList;
    }

    @Override
    public GroupDTO groupDetail(Long groupId) {
        List<UserGroup> userGroupList = userGroupRepository.findAllByGroupId(groupId);
        List<UserDTO> userDTOList = new ArrayList<>();
        for (UserGroup userGroup : userGroupList) {
            User user = userGroup.getUser();
            UserDTO userDTO = UserDTO.builder()
                    .phoneNumber(user.getPhoneNumber())
                    .userId(user.getId())
                    .name(user.getName())
                    .build();
            userDTO.setProfileUrl(user.getProfileUrl());
            userDTOList.add(userDTO);
        }
        GroupDTO groupDTO = GroupDTO.builder()
                .groupId(groupId)
                .userList(userDTOList)
                .build();
        return groupDTO;
    }
}
