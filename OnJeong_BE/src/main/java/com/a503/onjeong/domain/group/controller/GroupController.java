package com.a503.onjeong.domain.group.controller;

import com.a503.onjeong.domain.group.dto.GroupDTO;
import com.a503.onjeong.domain.group.dto.GroupUserListDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/group")
@RestController
public interface GroupController {
    @GetMapping("/list")
    ResponseEntity<List<GroupDTO>> groupList(@RequestParam(value = "userId") Long userId);

    @GetMapping("/detail")
    ResponseEntity<GroupDTO> groupDetail(@RequestParam(value = "groupId") Long groupId);

    @PostMapping("")
    ResponseEntity<Void> groupCreate(@RequestBody GroupUserListDTO groupUserListDTO);

    @DeleteMapping("")
    ResponseEntity<Void> groupDelete(@RequestParam(value = "groupId") Long groupId);

    @PutMapping("")
    ResponseEntity<Void> groupUpdate(@RequestBody GroupUserListDTO groupUserListDTO);


}
