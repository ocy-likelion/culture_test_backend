package com.likelion.culture_test.domain.result.repository;

import com.likelion.culture_test.domain.result.entity.ResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultDetailRepository extends JpaRepository<ResultDetail, Long> {

    @Query("""
        SELECT rd
        FROM ResultDetail rd
        WHERE rd.result.userId = :userId AND rd.result.survey.id = :surveyId
    """)
    List<ResultDetail> findByUserIdAndSurveyId(@Param("userId") Long userId, @Param("surveyId") Long surveyId);

    @Query("""
        SELECT p.category, SUM(rd.score)
        FROM ResultDetail rd
        JOIN rd.property p
        JOIN rd.result r
        WHERE r.userId = :userId AND r.survey.id = :surveyId
        GROUP BY p.category
    """)
    List<Object[]> aggregateScoreByProperty(@Param("userId") Long userId, @Param("surveyId") Long surveyId);




    @Query("""
    SELECT rd FROM ResultDetail rd
    JOIN FETCH rd.question
    JOIN FETCH rd.choice
    JOIN FETCH rd.property
    WHERE rd.result.userId = :userId AND rd.result.survey.id = :surveyId
""")
    List<ResultDetail> findWithDetailsByUserIdAndSurveyId(@Param("userId") Long userId, @Param("surveyId") Long surveyId);


}
