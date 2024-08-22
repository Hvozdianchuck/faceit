package com.example.faceit.controller;

import com.example.faceit.model.Job;
import com.example.faceit.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping
    public ResponseEntity<Page<Job>> getJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(jobService.getJobs(pageRequest));
    }

    @GetMapping("/top")
    public ResponseEntity<List<Job>> getTop10Jobs() {
        return ResponseEntity.ok(jobService.getTop10Jobs());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getLocationStats() {
        return ResponseEntity.ok(jobService.getLocationStats());
    }
}