package com.example.faceit.service;

import com.example.faceit.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface JobService {
    Map<String, Long> getLocationStats();
    List<Job> getTop10Jobs();
    Page<Job> getJobs(PageRequest pageRequest);
}
