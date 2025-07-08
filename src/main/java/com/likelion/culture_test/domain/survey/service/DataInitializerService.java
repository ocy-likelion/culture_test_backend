package com.likelion.culture_test.domain.survey.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.likelion.culture_test.domain.survey.dto.response.InitDataDto;
import com.likelion.culture_test.domain.survey.dto.response.InitDataDto.ChoiceDto;
import com.likelion.culture_test.domain.survey.dto.response.InitDataDto.PropertyDto;
import com.likelion.culture_test.domain.survey.dto.response.InitDataDto.QuestionDto;
import com.likelion.culture_test.domain.survey.dto.response.InitDataDto.SurveyDto;
import com.likelion.culture_test.domain.survey.entity.*;
import com.likelion.culture_test.domain.survey.enums.Category;
import com.likelion.culture_test.domain.survey.repository.PropertyRepository;
import com.likelion.culture_test.domain.survey.repository.QuestionRepository;
import com.likelion.culture_test.domain.survey.repository.SurveyRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DataInitializerService {

  private final SurveyRepository surveyRepository;
  private final QuestionRepository questionRepository;
  private final PropertyRepository propertyRepository;
  private final ObjectMapper objectMapper;

  
  //@PostConstruct
  @EventListener(ApplicationReadyEvent.class)
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


      for (SurveyDto surveyDto : initData.surveys()) {
        Survey survey = Survey.builder()
            .title(surveyDto.title())
            .isMain(surveyDto.isMain())
            .build();


        List<SurveyQuestion> surveyQuestions = new ArrayList<>();
        int questionOrder = 1;
        for (QuestionDto questionDto : surveyDto.questions()) {
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

                if (!questionDto.isSelective()) {
                  choice.setProperty(question.getProperty());
                }

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


  public void backupData() throws IOException {
    List<Property> properties = propertyRepository.findAll();
    List<Survey> surveys = surveyRepository.findAll();

    List<PropertyDto> propertyDtos = properties.stream()
        .map(property -> new PropertyDto(
            "P_" + property.getId(),
            property.getName(),
            property.getCategory().name()))
        .toList();

    List<SurveyDto> surveyDtos = surveys.stream()
        .map(survey -> {
          List<QuestionDto> questionDtos = survey.getSurveyQuestions().stream()
              .sorted(Comparator.comparingInt(SurveyQuestion::getDisplayOrder))
              .map(surveyQuestion  -> {
                Question question = surveyQuestion.getQuestion();
                List<ChoiceDto> choiceDtos = question.getChoices().stream()
                    .map(choice -> new ChoiceDto(
                        choice.getDisplayOrder(),
                        choice.getContent(),
                        choice.getProperty() != null ? "P_" + choice.getProperty().getId() : null // key 역생성
                    ))
                    .toList();
                return new QuestionDto(
                    question.getContent(),
                    question.isSelective(),
                    question.getProperty() != null ? "P_" + question.getProperty().getId() : null, // key 역생성
                    choiceDtos
                );
              })
              .toList();
          return new SurveyDto(survey.getTitle(), survey.isMain(), questionDtos);
        })
        .toList();

    InitDataDto backupData = new InitDataDto(propertyDtos, surveyDtos);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    String backupJson = objectMapper.writeValueAsString(backupData);

    Path path = Paths.get("data/backup.json");
    Files.writeString(path, backupJson);
    log.info("⭐ 설문조사 DB 데이터 백업 완료 : data/backup.json");
  }

}
