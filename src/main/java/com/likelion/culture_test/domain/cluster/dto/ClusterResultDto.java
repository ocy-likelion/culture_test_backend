package com.likelion.culture_test.domain.cluster.dto;

import com.likelion.culture_test.domain.cluster.entity.Centroid;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ClusterResultDto {

    private List<Integer> labels;
    private List<List<Double>> centroids;
}
