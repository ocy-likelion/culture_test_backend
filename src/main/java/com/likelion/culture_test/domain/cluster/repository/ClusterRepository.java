package com.likelion.culture_test.domain.cluster.repository;

import com.likelion.culture_test.domain.cluster.entity.Cluster;
import com.likelion.culture_test.domain.cluster.entity.ClusterGeneration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {

    List<Cluster> findByGeneration(ClusterGeneration generation);
}
