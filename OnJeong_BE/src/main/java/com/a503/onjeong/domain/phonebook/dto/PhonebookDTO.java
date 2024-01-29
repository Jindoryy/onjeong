package com.a503.onjeong.domain.phonebook.dto;

import com.a503.onjeong.domain.phonebook.PhonebookId;
import com.a503.onjeong.domain.user.User;
import lombok.Data;

@Data
public class PhonebookDTO {
    private PhonebookId phonebookId;
    private User user;
    private String phonebookNum;
    private String phonebookName;
}
