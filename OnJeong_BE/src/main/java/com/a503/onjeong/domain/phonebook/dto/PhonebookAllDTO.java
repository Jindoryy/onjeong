package com.a503.onjeong.domain.phonebook.dto;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class PhonebookAllDTO {
    private Long userId;
    private Map<String,String> phonebook = new HashMap<>();
}
