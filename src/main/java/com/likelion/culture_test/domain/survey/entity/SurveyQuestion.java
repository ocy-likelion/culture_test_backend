package com.likelion.culture_test.domain.survey.entity;

import com.likelion.culture_test.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "survey_questions", uniqueConstraints = {
    @UniqueConstraint(
        name = "SURVEY_QUESTION_UK_1",
        columnNames = {"survey_id", "displayOrder"}
    )
})
public class SurveyQuestion extends BaseEntity {

  @Column(nullable = false)
  private int displayOrder;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "survey_id")
  private Survey survey;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  private Question question;



}
