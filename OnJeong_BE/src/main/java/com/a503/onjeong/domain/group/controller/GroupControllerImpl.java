package com.a503.onjeong.domain.group.controller;

import com.a503.onjeong.domain.group.dto.GroupDTO;
import com.a503.onjeong.domain.group.dto.GroupUserListDTO;
import com.a503.onjeong.domain.group.service.GroupService;
import com.a503.onjeong.domain.usergroup.service.UserGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/group")
@RestController
public class GroupControllerImpl implements GroupController {
    private final GroupService groupService;
    private final UserGroupService userGroupService;

    @Override
    @GetMapping("/list")                 //내 그룹 전체조회
    public ResponseEntity<List<GroupDTO>> groupList(@RequestParam(value = "userId") Long userId) {
        List<GroupDTO> groupList = groupService.groupList(userId);
        return new ResponseEntity<>(groupList, HttpStatus.OK);
    }

    @Override
    @GetMapping("/detail")               //한 그룹 구성원 등 상세조회
    public ResponseEntity<GroupDTO> groupDetail(@RequestParam(value = "groupId") Long groupId) {
        GroupDTO groupDTO = userGroupService.groupDetail(groupId);
        groupDTO = groupService.groupDetail(groupDTO);
        return new ResponseEntity<>(groupDTO, HttpStatus.OK);
    }

    @Override
    @PostMapping("")               //그룹 생성
    public ResponseEntity<Void> groupCreate(@RequestBody GroupUserListDTO groupUserListDTO) {
        groupService.groupCreate(groupUserListDTO);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Override
    @DeleteMapping("")              //그룹 삭제
    public ResponseEntity<Void> groupDelete(@RequestParam(value = "groupId") Long groupId) {
        groupService.groupDelete(groupId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Override
    @PutMapping("")                 //그룹 정보,구성원 수정
    public ResponseEntity<Void> groupUpdate(@RequestBody GroupUserListDTO groupUserListDTO) {
        groupService.groupUpdate(groupUserListDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
