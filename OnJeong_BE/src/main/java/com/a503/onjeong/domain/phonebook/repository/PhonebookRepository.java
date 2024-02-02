package com.a503.onjeong.domain.phonebook.repository;

import com.a503.onjeong.domain.phonebook.Phonebook;
import com.a503.onjeong.domain.phonebook.PhonebookId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhonebookRepository extends JpaRepository<Phonebook, PhonebookId> {

    Optional<List<Phonebook>> findAllByUserId(Long userId);
}
