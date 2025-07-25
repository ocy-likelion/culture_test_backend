package com.likelion.culture_test.domain.cluster.repository;

import com.likelion.culture_test.domain.cluster.entity.Centroid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CentroidRepository extends JpaRepository<Centroid, Long> {
}
