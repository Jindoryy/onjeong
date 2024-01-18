package com.a503.onjeong.domain.phonebook.dto;

import lombok.Data;

import java.util.List;

@Data
public class PhonebookDTO {

    private Long userId;
    private List<Phonebook> phonebookList;

    @Data
    public static class Phonebook {
        private String phonebookName;
        private String phonebookNum;
    }
}
