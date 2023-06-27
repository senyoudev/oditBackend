package com.example.projectservice.project;

public record ProjectCreationRequest(String title, String description, Boolean isPublic) {
}
