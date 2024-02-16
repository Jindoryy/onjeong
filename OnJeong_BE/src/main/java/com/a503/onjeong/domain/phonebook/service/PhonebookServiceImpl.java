package com.a503.onjeong.domain.phonebook.service;

import com.a503.onjeong.domain.phonebook.Phonebook;
import com.a503.onjeong.domain.phonebook.dto.PhonebookAllDTO;
import com.a503.onjeong.domain.phonebook.dto.PhonebookDTO;
import com.a503.onjeong.domain.phonebook.repository.PhonebookRepository;
import com.a503.onjeong.domain.user.User;
import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.domain.usergroup.UserGroup;
import com.a503.onjeong.domain.usergroup.repository.UserGroupRepository;
import com.a503.onjeong.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PhonebookServiceImpl implements PhonebookService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final PhonebookRepository phonebookRepository;

    @Override
    @Transactional
    public void phonebookSave(PhonebookAllDTO phonebookAllDTO) { //연락처에서 유저들만 phonebook db에 저장
        Long userId = phonebookAllDTO.getUserId(); //유저 id
        Map<String, String> phonebook = phonebookAllDTO.getPhonebook(); //<전화번호, 이름>
        List<String> phoneNumList = new ArrayList<>(phonebook.keySet());//전화번호 리스트
        List<User> userList = userRepository.findByPhoneBook(phoneNumList); //연락처에 있는 유저객체가 담긴 리스트
        User owner_user = userRepository.findById(userId).orElseThrow();
        for (User user : userList) {
            //db 저장
            Phonebook phonebook1 = Phonebook.builder()
                    .phonebookNum(user.getPhoneNumber())
                    .phonebookName(phonebook.get(user.getPhoneNumber()))
                    .userId(userId)
                    .user(owner_user)
                    .friend(user)
                    .friendId(user.getId())
                    .build();
            phonebookRepository.save(phonebook1);
        }
    }

    @Override
    public List<PhonebookDTO> phonebookList(Long userId, Long groupId) { //엔티티를 dto로 변환 후 반환
        Optional<List<Phonebook>> phonebookList = phonebookRepository.findAllByUserId(userId);
        List<UserGroup> userList = new ArrayList<>();
        List<PhonebookDTO> phonebookDTOList = new ArrayList<>();

        if (groupId != null) { //모임 생성 시
            userList = userGroupRepository.findAllByGroupId(groupId);
        }
        for (Phonebook phonebook : phonebookList.orElseThrow()) {
            int isChecked = 0;
            for (UserGroup userGroup : userList) { //모임 수정 시 이미 있는 구성원 체크
                if (userGroup.getUser().getId() == phonebook.getFriend().getId()) {
                    isChecked = 1;
                }
            }

            String profileUrl =userRepository.findById(phonebook.getFriend().getId()).orElseThrow().getProfileUrl();
            PhonebookDTO phonebookDTO = PhonebookDTO.builder()
                    .freindId(phonebook.getFriend().getId())
                    .userId(userId)
                    .phonebookNum(phonebook.getPhonebookNum())
                    .phonebookName(phonebook.getPhonebookName())
                    .isChecked(isChecked)
                    .profileUrl(profileUrl)
                    .build();
            phonebookDTOList.add(phonebookDTO);
        }
        return phonebookDTOList;
    }


}
