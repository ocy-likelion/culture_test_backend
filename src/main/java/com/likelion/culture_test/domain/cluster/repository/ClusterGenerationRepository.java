package com.likelion.culture_test.domain.cluster.repository;

import com.likelion.culture_test.domain.cluster.entity.ClusterGeneration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClusterGenerationRepository extends JpaRepository<ClusterGeneration, Long> {

    Optional<ClusterGeneration> findTopByOrderByIdDesc();
}
