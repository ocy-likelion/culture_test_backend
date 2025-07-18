package com.likelion.culture_test.domain.survey.service;

import com.likelion.culture_test.domain.survey.dto.request.ChoiceRequest;
import com.likelion.culture_test.domain.survey.dto.request.CreateQuestionRequest;
import com.likelion.culture_test.domain.survey.dto.request.UpdateQuestionRequest;
import com.likelion.culture_test.domain.survey.dto.response.QuestionResponse;
import com.likelion.culture_test.domain.survey.entity.Choice;
import com.likelion.culture_test.domain.survey.entity.Property;
import com.likelion.culture_test.domain.survey.entity.Question;
import com.likelion.culture_test.domain.survey.repository.QuestionRepository;
import com.likelion.culture_test.global.exceptions.CustomException;
import com.likelion.culture_test.global.exceptions.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

  private final QuestionRepository questionRepository;
  private final PropertyService propertyService;


  public Page<QuestionResponse> findAllByPage(Pageable pageable) {
    return questionRepository.findAll(pageable).map(QuestionResponse::fromEntity);
  }


  public Question findById(Long questionId) {
    return questionRepository.findById(questionId)
        .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_NOT_FOUND));
  }


  @Transactional
  public QuestionResponse create(CreateQuestionRequest request) {
    validateQuestionRequest(request.isSelective(), request.propertyId(), request.choices());
    Property property = getPropertyIfExists(request.propertyId());

    Question question = Question.builder()
        .content(request.content())
        .isSelective(request.isSelective())
        .property(property)
        .choices(new ArrayList<>())
        .build();

    List<Choice> choices;
    if (!request.isSelective()) {
      choices = createLikertChoices(question);

    } else {
      choices = request.choices().stream().map(choiceRequest -> {
        Property choiceProperty = getPropertyIfExists(choiceRequest.propertyId());

        return Choice.builder()
            .question(question)
            .content(choiceRequest.content())
            .displayOrder(choiceRequest.displayOrder())
            .property(choiceProperty)
            .build();

      }).collect(Collectors.toList());
    }

    question.setChoices(choices);
    Question savedQuestion = questionRepository.save(question);

    return QuestionResponse.fromEntity(savedQuestion);
  }


  private List<Choice> createLikertChoices(Question question) {
    String[] contents = {
      "전혀 아니다", "아니다", "보통이다", "그렇다", "매우 그렇다"
    };

    List<Choice> choices = new ArrayList<>();
    for (int i = 0; i < contents.length; i++) {
      choices.add(
          Choice.builder()
              .question(question)
              .content(contents[i])
              .displayOrder(i + 1)
              .property(null)
              .build()
      );
    }

    return choices;
  }


  private void validateQuestionRequest(boolean isSelective, Long propertyId, List<ChoiceRequest> choices) {
    if (isSelective) {
      if (choices == null || choices.size() < 2) {
        throw new CustomException(ErrorCode.SELECTIVE_QUESTION_NEEDS_CHOICES);
      }

      boolean hasProperty = choices.stream()
          .anyMatch(choice -> choice.propertyId() == null);
      if (hasProperty) {
        throw new CustomException(ErrorCode.SELECTIVE_CHOICE_NEEDS_PROPERTY);
      }

    } else {
      if (propertyId == null) {
        throw new CustomException(ErrorCode.LIKERT_QUESTION_NEEDS_PROPERTY);
      }
    }
  }


  private Property getPropertyIfExists(Long propertyId) {
    return propertyId != null ? propertyService.findById(propertyId) : null;
  }


  @Transactional
  public void deleteById(Long questionId) {
    Question question = findById(questionId);
    questionRepository.delete(question);
  }


  public Page<QuestionResponse> findQuestionsByProperty(Long propertyId, Pageable pageable) {
    Property property = propertyService.findById(propertyId);
    return questionRepository.findAllByProperty(property, pageable)
        .map(QuestionResponse::fromEntity);
  }


  @Transactional
  public QuestionResponse update(Long questionId, UpdateQuestionRequest request) {
    validateQuestionRequest(request.isSelective(), request.propertyId(), request.choices());

    Property property = getPropertyIfExists(request.propertyId());

    Question question = findById(questionId);
    question.setContent(request.content());
    question.setSelective(request.isSelective());
    question.setProperty(property);


    if (request.isSelective()) {
      List<Choice> choices = request.choices().stream().map(choiceRequest -> {
        Property choiceProperty = getPropertyIfExists(choiceRequest.propertyId());

        return Choice.builder()
            .question(question)
            .content(choiceRequest.content())
            .displayOrder(choiceRequest.displayOrder())
            .property(choiceProperty)
            .build();

      }).collect(Collectors.toList());

      question.setChoices(choices);
    }

    return QuestionResponse.fromEntity(question);
  }
}
