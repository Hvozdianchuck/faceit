package com.example.faceit.repository;

import com.example.faceit.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, String> {
    List<Job> findTop10ByOrderByDatePostedDesc();
}

