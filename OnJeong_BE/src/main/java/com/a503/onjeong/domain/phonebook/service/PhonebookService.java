package com.a503.onjeong.domain.phonebook.service;

import com.a503.onjeong.domain.phonebook.dto.PhonebookAllDTO;
import org.springframework.stereotype.Service;

@Service
public interface PhonebookService {
     public void phonebookSave(PhonebookAllDTO phonebookAllDTO);
}
