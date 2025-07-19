package com.likelion.culture_test.domain.cluster.entity;

import com.likelion.culture_test.domain.result.entity.Result;
import com.likelion.culture_test.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "clusters")
public class Cluster extends BaseEntity {

    @Column(length = 20, nullable = false)
    private String name;

    @Column(nullable = false)
    private int label;

    @ElementCollection
    @CollectionTable(name = "cluster_centroids", joinColumns = @JoinColumn(name = "cluster_id"))
    @Column(name = "centroid_value")
    private List<Double> centroid;


    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Result> results = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generation_id", nullable = false)
    private ClusterGeneration generation;

}
