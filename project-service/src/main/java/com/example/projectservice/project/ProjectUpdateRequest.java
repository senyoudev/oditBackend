package com.example.projectservice.project;

public record ProjectUpdateRequest(String title, String description, Boolean isPublic) {
}
