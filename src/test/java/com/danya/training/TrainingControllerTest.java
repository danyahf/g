package com.danya.training;

import com.danya.exception.EntityNotFoundException;
import com.danya.exception.GlobalExceptionHandler;
import com.danya.exception.TrainerDoesNotMatchTrainingTypeException;
import com.danya.training.api.TrainingController;
import com.danya.training.dto.CreateTrainingDto;
import com.danya.trainingType.TrainingTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TrainingControllerTest {

    private final TrainingService trainingService = mock(TrainingService.class);
    private final TrainingController trainingController = new TrainingController(trainingService);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(trainingController)
            .setValidator(new LocalValidatorFactoryBean())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void createReturns201ForValidPayload() throws Exception {
        Long expectedId = 123L;
        when(trainingService.createProfile(any(CreateTrainingDto.class))).thenReturn(expectedId);

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreatePayloadJson()))
                .andExpect(status().isCreated())
                .andExpect(content().string(String.valueOf(expectedId)));

        verify(trainingService, times(1)).createProfile(any(CreateTrainingDto.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    void createReturns404WhenEntityNotFound() throws Exception {
        when(trainingService.createProfile(any(CreateTrainingDto.class)))
                .thenThrow(new EntityNotFoundException("Trainee profile not found"));

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreatePayloadJson()))
                .andExpect(status().isNotFound());

        verify(trainingService, times(1)).createProfile(any(CreateTrainingDto.class));
        verifyNoMoreInteractions(trainingService);
    }

    @Test
    void createReturns409WhenTrainerDoesNotMatchTrainingType() throws Exception {
        when(trainingService.createProfile(any(CreateTrainingDto.class)))
                .thenThrow(new TrainerDoesNotMatchTrainingTypeException(""));

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validCreatePayloadJson()))
                .andExpect(status().isConflict());

        verify(trainingService, times(1)).createProfile(any(CreateTrainingDto.class));
        verifyNoMoreInteractions(trainingService);
    }

    private String validCreatePayloadJson() throws Exception {
        CreateTrainingDto payload = CreateTrainingDto.builder()
                .traineeUsername("trainee.user")
                .trainerUsername("trainer.user")
                .trainingTypeName(TrainingTypeName.YOGA)
                .name("Morning Flow")
                .trainingDate(LocalDate.now())
                .duration(60)
                .build();
        return objectMapper.writeValueAsString(payload);
    }
}
