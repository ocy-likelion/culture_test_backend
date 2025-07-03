package com.likelion.culture_test.domain.survey.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.culture_test.domain.survey.entity.*;
import com.likelion.culture_test.domain.survey.enums.Category;
import com.likelion.culture_test.domain.survey.repository.PropertyRepository;
import com.likelion.culture_test.domain.survey.repository.QuestionRepository;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DataInitializerService {

  private final SurveyRepository surveyRepository;
  private final QuestionRepository questionRepository;
  private final PropertyRepository propertyRepository;
  private final ObjectMapper objectMapper;

  public record ChoiceInitDto(int displayOrder, String content, String property) {}
  public record QuestionInitDto(String content, boolean isSelective, String property, List<ChoiceInitDto> choices) {}
  public record SurveyInitDto(String title, boolean isMain, List<QuestionInitDto> questions) {}
  public record PropertyInitDto(String key, String name, String category) {}
  public record InitDataDto(List<PropertyInitDto> properties, List<SurveyInitDto> surveys) {}


  @PostConstruct
  @Transactional
  public void initializeData() {
    if (surveyRepository.count() > 0) {
      return;
    }

    try {
      ClassPathResource resource = new ClassPathResource("data/initData.json");
      InputStream inputStream = resource.getInputStream();
      InitDataDto initData = objectMapper.readValue(inputStream, InitDataDto.class);

      Map<String, Property> propertyMap = new HashMap<>();
      initData.properties().forEach(dto -> {
        Property property = Property.builder()
            .name(dto.name())
            .category(Category.valueOf(dto.category()))
            .build();

        propertyRepository.save(property);
        propertyMap.put(dto.key(), property);
      });


      for (SurveyInitDto surveyDto : initData.surveys()) {
        Survey survey = Survey.builder()
            .title(surveyDto.title())
            .isMain(surveyDto.isMain())
            .build();


        List<SurveyQuestion> surveyQuestions = new ArrayList<>();
        int questionOrder = 1;
        for (QuestionInitDto questionDto : surveyDto.questions()) {
          Question question = Question.builder()
              .content(questionDto.content())
              .isSelective(questionDto.isSelective())
              .choices(new ArrayList<>())
              .build();

          if (questionDto.property() != null) {
            question.setProperty(propertyMap.get(questionDto.property()));
          }

          List<Choice> choices = questionDto.choices().stream()
              .map(choiceDto -> {
                Choice choice = Choice.builder()
                    .content(choiceDto.content())
                    .displayOrder(choiceDto.displayOrder())
                    .question(question)
                    .build();

                if (questionDto.isSelective() && choiceDto.property() != null) {
                  choice.setProperty(propertyMap.get(choiceDto.property()));
                }
                return choice;

              }).collect(Collectors.toList());
          question.setChoices(choices);

          questionRepository.save(question);

          SurveyQuestion surveyQuestion = SurveyQuestion.builder()
              .survey(survey)
              .question(question)
              .displayOrder(questionOrder++)
              .build();
          surveyQuestions.add(surveyQuestion);
        }
        survey.setSurveyQuestions(surveyQuestions);

        surveyRepository.save(survey);
        log.info("⭐ 설문조사 초기 데이터 적재 성공");
        log.info("⭐ {}개 질문 등록 성공", surveyQuestions.size());
      }
    } catch (Exception e) {
      throw new RuntimeException("설문조사 초기 데이터 적재 실패: " + e.getMessage(), e);
    }
  }
}
