package com.example.projectservice.project;

public record ProjectCreationRequest(Integer owner, String title, String description, Boolean isPublic) {
}
