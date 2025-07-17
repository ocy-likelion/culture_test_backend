package com.likelion.culture_test.domain.cluster.entity;

import com.likelion.culture_test.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cluster_generations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClusterGeneration extends BaseEntity {

    private String version;
    private int clusterCount;
    @Column(columnDefinition = "TEXT")
    private String description;
}
