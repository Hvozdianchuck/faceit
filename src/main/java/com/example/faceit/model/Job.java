package com.example.faceit.model;

import com.example.faceit.converters.StringListConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Job {
    @Id
    private String slug;
    @JsonProperty("company_name")
    private String companyName;
    private String title;
    @Lob
    private String description;
    private boolean remote;
    private String url;
    @Convert(converter = StringListConverter.class)
    @Column(name = "tags")
    private List<String> tags;
    @JsonProperty("job_types")
    @Convert(converter = StringListConverter.class)
    @Column(name = "job_types")
    private List<String> jobTypes;
    private String location;
    @JsonProperty("created_at")
    private Instant datePosted;


    // Getters and setters, Constructors
}

