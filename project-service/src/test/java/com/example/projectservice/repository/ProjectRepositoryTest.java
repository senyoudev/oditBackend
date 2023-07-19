package com.example.projectservice.repository;

import com.example.projectservice.project.Project;
import com.example.projectservice.project.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectRepositoryTest {
    @Mock
    private ProjectRepository underTest;

    @Test
    void getUserProjects() {

        // Given
        Integer userId = 11;
        List<Project> projects = new ArrayList<>();
        projects.add(Project.builder().id(1).title("Project1").description("desc 1").isPublic(true).adminId(userId).build());
        projects.add(Project.builder().id(2).title("Project2").description("desc 2").isPublic(false).adminId(userId).build());

        // When
        when(underTest.findByAdminId(userId)).thenReturn(projects);
        List<Project> resultProjects = underTest.findByAdminId(userId);

        // Then
        assertEquals(2, resultProjects.size());
        assertEquals(1, resultProjects.get(0).getId());
    }

}
