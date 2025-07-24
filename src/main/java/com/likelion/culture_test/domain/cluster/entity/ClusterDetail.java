package com.likelion.culture_test.domain.cluster.entity;

import com.likelion.culture_test.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "clusterDetails")
public class ClusterDetail extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id", nullable = false, unique = true)
    private Cluster cluster;

    @Column(columnDefinition = "TEXT")
    private String detailDescription;

}
