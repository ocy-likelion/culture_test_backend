package com.likelion.culture_test.domain.survey.repository;

import com.likelion.culture_test.domain.survey.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

  Optional<Survey> findByIsMain(boolean isMain);

  @Modifying
  @Query("UPDATE Survey s SET s.isMain = false WHERE s.isMain = true")
  void updateAllMainToFalse();
}
