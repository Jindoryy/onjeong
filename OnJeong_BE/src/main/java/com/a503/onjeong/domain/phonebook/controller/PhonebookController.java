package com.a503.onjeong.domain.phonebook.controller;

import com.a503.onjeong.domain.phonebook.dto.PhonebookAllDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public interface PhonebookController {
    public ResponseEntity<Void> phonebookSave(@RequestBody PhonebookAllDTO phonebookAllDTO);

}
