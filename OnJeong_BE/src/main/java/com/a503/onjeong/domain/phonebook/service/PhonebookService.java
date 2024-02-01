package com.a503.onjeong.domain.phonebook.service;

import com.a503.onjeong.domain.phonebook.dto.PhonebookAllDTO;
import com.a503.onjeong.domain.phonebook.dto.PhonebookDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PhonebookService {
     void phonebookSave(PhonebookAllDTO phonebookAllDTO);


    List<PhonebookDTO> phonebookList(Long userId, Long groupId);
}
