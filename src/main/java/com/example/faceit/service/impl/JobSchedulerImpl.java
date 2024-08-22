package com.example.faceit.service.impl;

import com.example.faceit.dto.JobPage;
import com.example.faceit.exception.JobFetchException;
import com.example.faceit.model.Job;
import com.example.faceit.repository.JobRepository;
import com.example.faceit.service.JobScheduler;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class JobSchedulerImpl implements JobScheduler {
    private final JobRepository jobRepository;
    private final RestTemplate restTemplate;
    private final String API_URL = "https://www.arbeitnow.com/api/job-board-api?page=";


    public JobSchedulerImpl(JobRepository jobRepository, RestTemplate restTemplate) {
        this.jobRepository = jobRepository;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    @Transactional
    public void init() {
        List<Job> newJobs = fetchAllJobs();
        jobRepository.saveAll(newJobs);
    }

    @Scheduled(fixedRate = 600000)
    @Override
    public void fetchAndSaveJobs() {
        List<Job> newJobs = fetchAllJobs();

        List<Job> existingJobs = jobRepository.findAllById(
                newJobs.stream()
                        .map(Job::getSlug)
                        .collect(Collectors.toList())
        );

        Map<String, Job> existingJobsMap = existingJobs.stream()
                .collect(Collectors.toMap(Job::getSlug, job -> job));

        List<Job> jobsToSave = newJobs.stream()
                .map(job -> {
                    Job existingJob = existingJobsMap.get(job.getSlug());
                    if (existingJob != null) {
                        return updateJob(existingJob, job) ? existingJob : null;
                    }
                    return job;
                })
                .filter(Objects::nonNull) // Фільтрація null-значень
                .collect(Collectors.toList());

        if (!jobsToSave.isEmpty()) {
            jobRepository.saveAll(jobsToSave);
        }
    }

    public List<Job> fetchAllJobs() {
        List<CompletableFuture<ResponseEntity<JobPage>>> futureList = IntStream.rangeClosed(1, 5)
                .mapToObj(this::getPageAsync)
                .toList();

        return futureList.stream()
                .map(CompletableFuture::join)
                .map(ResponseEntity::getBody).filter(Objects::nonNull)
                .flatMap(jobPage -> Arrays.stream(jobPage.data()))
                .collect(Collectors.toList());
    }

    public CompletableFuture<ResponseEntity<JobPage>> getPageAsync(int pageNumber) {
        String url = API_URL + pageNumber;
        return CompletableFuture.supplyAsync(() -> {
            try {
                return restTemplate.getForEntity(url, JobPage.class);
            } catch (Exception e) {
                throw new JobFetchException("Error fetching page " + pageNumber, e);
            }
        });
    }


    private boolean updateJob(Job existingJob, Job newJob) {
        boolean isUpdated = false;

        if (!Objects.equals(existingJob.getTitle(), newJob.getTitle())) {
            existingJob.setTitle(newJob.getTitle());
            isUpdated = true;
        }
        if (!Objects.equals(existingJob.getCompanyName(), newJob.getCompanyName())) {
            existingJob.setCompanyName(newJob.getCompanyName());
            isUpdated = true;
        }
        if (!Objects.equals(existingJob.getDescription(), newJob.getDescription())) {
            existingJob.setDescription(newJob.getDescription());
            isUpdated = true;
        }
        if (existingJob.isRemote() != newJob.isRemote()) {
            existingJob.setRemote(newJob.isRemote());
            isUpdated = true;
        }
        if (!Objects.equals(existingJob.getUrl(), newJob.getUrl())) {
            existingJob.setUrl(newJob.getUrl());
            isUpdated = true;
        }
        if (!Objects.equals(existingJob.getTags(), newJob.getTags())) {
            existingJob.setTags(newJob.getTags());
            isUpdated = true;
        }
        if (!Objects.equals(existingJob.getJobTypes(), newJob.getJobTypes())) {
            existingJob.setJobTypes(newJob.getJobTypes());
            isUpdated = true;
        }
        if (!Objects.equals(existingJob.getLocation(), newJob.getLocation())) {
            existingJob.setLocation(newJob.getLocation());
            isUpdated = true;
        }
        if (!Objects.equals(existingJob.getDatePosted(), newJob.getDatePosted())) {
            existingJob.setDatePosted(newJob.getDatePosted());
            isUpdated = true;
        }
        return isUpdated;
    }
}
