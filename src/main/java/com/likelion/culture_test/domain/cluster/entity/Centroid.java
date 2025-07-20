package com.likelion.culture_test.domain.cluster.entity;

import com.likelion.culture_test.domain.survey.enums.Category;
import com.likelion.culture_test.global.jpa.BaseEntity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "centroids")
public class Centroid extends BaseEntity {
    @Column(name = "value_column") // 그냥 value로 하면 h2에서안받아들여짐
    private Double value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false)
    private Cluster cluster;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

}
