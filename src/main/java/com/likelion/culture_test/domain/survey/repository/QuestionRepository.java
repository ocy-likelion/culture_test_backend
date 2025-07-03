package com.likelion.culture_test.domain.survey.repository;

import com.likelion.culture_test.domain.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
