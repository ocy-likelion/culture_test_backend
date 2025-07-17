package com.likelion.culture_test.domain.cluster.service;

import com.likelion.culture_test.domain.cluster.dto.ClusterResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClusterService {

    public void saveClustered(ClusterResponseDto dto){

        List<Integer> labels = dto.getResult().getLabels();
        List<List<Double>> centroids = dto.getResult().getCentroids();

    }


}
