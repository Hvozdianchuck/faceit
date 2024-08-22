package com.example.faceit.service.impl;

import com.example.faceit.model.Job;
import com.example.faceit.repository.JobRepository;
import com.example.faceit.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    @Autowired
    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }


    @Override
    public Page<Job> getJobs(PageRequest pageRequest) {
        return jobRepository.findAll(pageRequest);
    }

    @Override
    public List<Job> getTop10Jobs() {
        return jobRepository.findTop10ByOrderByDatePostedDesc();
    }

    @Override
    public Map<String, Long> getLocationStats() {
        return jobRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Job::getLocation, Collectors.counting()));
    }


}
