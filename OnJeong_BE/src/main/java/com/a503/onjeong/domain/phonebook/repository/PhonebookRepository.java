package com.a503.onjeong.domain.phonebook.repository;

import com.a503.onjeong.domain.phonebook.Phonebook;
import com.a503.onjeong.domain.phonebook.PhonebookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhonebookRepository extends JpaRepository<Phonebook, PhonebookId> {
    @Query(value = "SELECT p FROM Phonebook as p JOIN FETCH p.friend WHERE p.user.id = :userId")
    Optional<List<Phonebook>> findAllByUserId(@Param("userId") Long userId);
}
