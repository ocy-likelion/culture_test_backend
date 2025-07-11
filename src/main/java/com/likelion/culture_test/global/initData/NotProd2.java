package com.likelion.culture_test.global.initData;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.culture_test.domain.result.dto.ResultRequestDto;
import com.likelion.culture_test.domain.result.service.ResultService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Configuration
@Profile("!prod")
public class NotProd2 {

    @Bean(name = "applicationRunner2")
    public ApplicationRunner applicationRunner(ResultService resultService) {
        return new ApplicationRunner() {
            @Transactional
            @Override
            public void run(ApplicationArguments args) throws Exception {

                ObjectMapper objectMapper = new ObjectMapper();
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/result_data.json");

                if (inputStream == null) {
                    throw new IllegalStateException("result_data.json 파일을 찾을 수 없습니다.");
                }

                List<ResultRequestDto> responses = objectMapper
                        .readerForListOf(ResultRequestDto.class)
                        .readValue(inputStream);


                for (ResultRequestDto dto: responses){
                    resultService.processSurveyResult(dto);
                }



            }
        };
    }
}
