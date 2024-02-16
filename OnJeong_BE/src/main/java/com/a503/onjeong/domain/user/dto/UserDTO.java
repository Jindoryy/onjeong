package com.a503.onjeong.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String name;
    private String phoneNumber;
    private String profileUrl;
    @Builder
    public UserDTO(
            Long userId,
            String name,
            String phoneNumber
    ) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
