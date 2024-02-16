package com.a503.onjeong.domain.phonebook.controller;

import com.a503.onjeong.domain.phonebook.dto.PhonebookAllDTO;
import com.a503.onjeong.domain.phonebook.dto.PhonebookDTO;
import com.a503.onjeong.domain.phonebook.service.PhonebookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/phonebook")
@RestController
public class PhonebookControllerImpl implements PhonebookController {

    private final PhonebookService phonebookService;

    @Override
    @PostMapping("")
    public ResponseEntity<Void> phonebookSave(
            @RequestBody PhonebookAllDTO phonebookAllDTO) { //연락처 동기화
        phonebookService.phonebookSave(phonebookAllDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    @GetMapping("")
    public ResponseEntity<List<PhonebookDTO>> phonebookList(    //phonebook db에 저장되어있는 모든 연락처 가져오기
            Long userId, @RequestParam(required = false) Long groupId) {
        // 모임 생성 , 수정 시 연락처 전부 가져오기 (모임에 있는지 체크 후 반환)
        List<PhonebookDTO> phonebookDTOList= phonebookService.phonebookList(userId,groupId);
        return new ResponseEntity<>(phonebookDTOList,HttpStatus.OK);
    }


}
