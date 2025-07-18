package com.likelion.culture_test.domain.cluster.entity;

import com.likelion.culture_test.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "generation", cascade = CascadeType.ALL)
    private List<Cluster> clusters = new ArrayList<>();
}
