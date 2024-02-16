package com.a503.onjeong.domain.phonebook;

import com.a503.onjeong.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor
@DynamicUpdate  //바뀐 컬럼만 수정
public class Phonebook {

    @EmbeddedId //복합키 매핑(userId+freindId)
    private PhonebookId phonebookId;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("friendId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id")
    private User friend;

    @Column(name = "phonebook_num")
    private String phonebookNum;

    @Column(name = "phonebook_name")
    private String phonebookName;

    @Builder
    public Phonebook(
            Long userId,
            Long friendId,
            User user,
            User friend,
            String phonebookNum,
            String phonebookName
    ) {
        this.phonebookId=new PhonebookId(userId,friendId);
        this.phonebookNum = phonebookNum;
        this.phonebookName = phonebookName;
        this.user=user;
        this.friend=friend;
    }
}
