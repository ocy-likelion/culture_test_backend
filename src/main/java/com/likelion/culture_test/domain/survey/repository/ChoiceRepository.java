package com.likelion.culture_test.domain.survey.repository;

import com.likelion.culture_test.domain.survey.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    @Query("SELECT c.question.id, MAX(c.displayOrder) FROM Choice c WHERE c.question.id IN :questionIds GROUP BY c.question.id")
    List<Object[]> findMaxDisplayOrderByQuestionIds(@Param("questionIds") List<Long> questionIds);

}
