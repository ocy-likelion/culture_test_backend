package com.likelion.culture_test.domain.result.service;

import com.likelion.culture_test.domain.result.dto.ResultDetailResponseDto;
import com.likelion.culture_test.domain.result.dto.ResultQueryDto;
import com.likelion.culture_test.domain.result.dto.ResultRequestDto;
import com.likelion.culture_test.domain.result.entity.ResultDetail;
import com.likelion.culture_test.domain.result.repository.ResultDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ResultDetailService {

    private final ResultDetailRepository resultDetailRepository;

    public List<ResultDetailResponseDto> getUserAnswersDto(ResultQueryDto dto) {
        return resultDetailRepository.findWithDetailsByUserIdAndSurveyId(dto.getUserId(), dto.getSurveyId()).stream()
                .map(rd -> new ResultDetailResponseDto(
                        rd.getQuestion().getId(),
                        rd.getQuestion().getContent(),
                        rd.getChoice().getId(),
                        rd.getChoice().getContent(),
                        rd.getProperty().getName(),
                        rd.getScore()
                ))
                .toList();
    }

    public Map<String, Double> getScoreByProperty(ResultQueryDto dto) {
        List<Object[]> rows = resultDetailRepository.aggregateScoreByProperty(dto.getUserId(), dto.getSurveyId());
        Map<String, Double> map = new HashMap<>();
        for (Object[] row : rows) {
            String property = (String) row[0];
            Double score = (Double) row[1];
            map.put(property, score);
        }
        return map;
    }

    public List<ResultDetail> getUserAnswers(ResultQueryDto dto) {
        return resultDetailRepository.findByUserIdAndSurveyId(dto.getUserId(), dto.getSurveyId());
    }
}
