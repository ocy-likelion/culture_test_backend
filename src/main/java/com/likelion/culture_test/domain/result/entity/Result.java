package com.likelion.culture_test.domain.result.entity;

import com.likelion.culture_test.domain.cluster.entity.Cluster;
import com.likelion.culture_test.domain.survey.entity.Survey;
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
@Table(name = "results")
public class Result extends BaseEntity {

    @Column(name = "field")
    private String field; //

    @Column(name = "user_id", nullable = false)
    private Long userId; // 나중에 유저객체 만들어지면 그때

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = true)
    private Cluster cluster;
}
