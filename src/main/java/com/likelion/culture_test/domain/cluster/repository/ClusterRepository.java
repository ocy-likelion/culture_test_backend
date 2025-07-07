package com.likelion.culture_test.domain.cluster.repository;

import com.likelion.culture_test.domain.cluster.entity.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {
}
