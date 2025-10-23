package com.danya.trainee;

import com.danya.exception.GlobalExceptionHandler;
import com.danya.security.authentication.AuthUserArgumentResolver;
import com.danya.security.authorization.RoleAuthorizationInterceptor;
import com.danya.trainee.api.TraineeController;
import com.danya.trainee.dto.CreateTraineeDto;
import com.danya.trainee.dto.TraineeProfileTrainerDto;
import com.danya.trainee.dto.TraineeWithTrainersDto;
import com.danya.trainee.dto.UpdateTraineeDto;
import com.danya.trainer.TrainerService;
import com.danya.training.TrainingService;
import com.danya.trainingType.TrainingType;
import com.danya.trainingType.TrainingTypeName;
import com.danya.user.dto.CredentialsDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static com.danya.AuthUserRequestPostProcessors.authUser;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TraineeController traineeController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        var validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(traineeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setCustomArgumentResolvers(new AuthUserArgumentResolver())
                .addMappedInterceptors(new String[]{"/**"}, new RoleAuthorizationInterceptor())
                .build();
    }

    private static Stream<Arguments> invalidCreatePayloads() {
        Date dob = new Date(1);
        return Stream.of(
                Arguments.of(new CreateTraineeDto("q", "Jo", dob, "Addr")),
                Arguments.of(new CreateTraineeDto("   ", "Jo", dob, "Addr")),
                Arguments.of(new CreateTraineeDto("q".repeat(56), "Jo", dob, "Addr")),
                Arguments.of(new CreateTraineeDto(null, "Jo", dob, "Addr")),
                Arguments.of(new CreateTraineeDto("Jo", "q", dob, "Addr")),
                Arguments.of(new CreateTraineeDto("Jo", "   ", dob, "Addr")),
                Arguments.of(new CreateTraineeDto("Jo", "X".repeat(56), dob, "Addr")),
                Arguments.of(new CreateTraineeDto("Jo", null, dob, "Addr"))
        );
    }

    private static Stream<Arguments> invalidUpdatePayloads() {
        Date dob = new Date(1);
        return Stream.of(
                Arguments.of(new UpdateTraineeDto("q", "Jo", dob, "Addr", true)),
                Arguments.of(new UpdateTraineeDto("   ", "Jo", dob, "Addr", true)),
                Arguments.of(new UpdateTraineeDto("q".repeat(56), "Jo", dob, "Addr", true)),
                Arguments.of(new UpdateTraineeDto(null, "Jo", dob, "Addr", true)),
                Arguments.of(new UpdateTraineeDto("Jo", "q", dob, "Addr", true)),
                Arguments.of(new UpdateTraineeDto("Jo", "   ", dob, "Addr", true)),
                Arguments.of(new UpdateTraineeDto("Jo", "X".repeat(56), dob, "Addr", true)),
                Arguments.of(new UpdateTraineeDto("Jo", null, dob, "Addr", true)),
                Arguments.of(new UpdateTraineeDto("Jo", "Jo", dob, "Addr", null))
        );
    }

    @Test
    void shouldReturnTraineeProfileWhenGetProfileByUsername() throws Exception {
        TrainingType type = new TrainingType(1, TrainingTypeName.YOGA);
        TraineeProfileTrainerDto trainerDto =
                new TraineeProfileTrainerDto("john.trainer", "John", "Trainer", type);

        TraineeWithTrainersDto traineeProfile = new TraineeWithTrainersDto(
                "Jane",
                "Doe",
                new Date(1),
                "Kyiv, Ukraine",
                true,
                List.of(trainerDto)
        );

        when(traineeService.getProfileByUsername("jane.doe")).thenReturn(traineeProfile);

        mockMvc.perform(get("/trainees/jane.doe")
                        .with(authUser("admin.admin", List.of("ADMIN")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("Kyiv, Ukraine"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers[0].username").value("john.trainer"))
                .andExpect(jsonPath("$.trainers[0].specialization.trainingTypeName").value("YOGA"));

        verify(traineeService).getProfileByUsername("jane.doe");
    }

    @Test
    void shouldReturnTraineeProfileWhenGetProfileMe() throws Exception {
        TrainingType type = new TrainingType(1, TrainingTypeName.YOGA);
        TraineeProfileTrainerDto trainerDto =
                new TraineeProfileTrainerDto("john.trainer", "John", "Trainer", type);

        TraineeWithTrainersDto traineeProfile = new TraineeWithTrainersDto(
                "Jane",
                "Doe",
                new Date(1),
                "Kyiv, Ukraine",
                true,
                List.of(trainerDto)
        );

        when(traineeService.getProfileByUsername("jane.doe")).thenReturn(traineeProfile);

        mockMvc.perform(get("/trainees/me")
                        .with(authUser("jane.doe", List.of("TRAINEE")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("Kyiv, Ukraine"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers[0].username").value("john.trainer"))
                .andExpect(jsonPath("$.trainers[0].specialization.trainingTypeName").value("YOGA"));

        verify(traineeService).getProfileByUsername("jane.doe");
    }

    @Test
    void shouldReturn201AndCredentialsWhenCreateProfile() throws Exception {
        CreateTraineeDto payload = new CreateTraineeDto(
                "firstName",
                "lastName",
                null,
                null
        );
        String username = "firstName.lastName";
        String password = "1234567890";
        CredentialsDto credentials = new CredentialsDto(username, password);

        when(traineeService.createProfile(any(CreateTraineeDto.class)))
                .thenReturn(credentials);

        mockMvc.perform(post("/trainees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.password").value(password));
    }

    @ParameterizedTest
    @MethodSource("invalidCreatePayloads")
    void shouldReturn400WhenCreateProfileForInvalidPayloads(CreateTraineeDto payload) throws Exception {
        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUpdatedProfileWhenUpdateTrainee() throws Exception {
        String username = "jane.doe";
        UpdateTraineeDto payload = new UpdateTraineeDto(
                "Jane",
                "Doe",
                new Date(1),
                "Kyiv, Ukraine",
                true
        );

        TraineeWithTrainersDto updatedProfile = new TraineeWithTrainersDto(
                "Jane_Updated",
                "Doe_Updated",
                new Date(1),
                "Kharkiv, Ukraine",
                true,
                List.of()
        );

        when(traineeService.updateProfile(eq(username), any(UpdateTraineeDto.class)))
                .thenReturn(updatedProfile);


        mockMvc.perform(put("/trainees/{username}", username)
                        .with(authUser("admin.admin", List.of("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane_Updated"))
                .andExpect(jsonPath("$.lastName").value("Doe_Updated"))
                .andExpect(jsonPath("$.address").value("Kharkiv, Ukraine"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers").isEmpty());
    }

    @Test
    void shouldReturnUpdatedProfileWhenUpdateTraineeMe() throws Exception {
        String username = "jane.doe";
        UpdateTraineeDto payload = new UpdateTraineeDto(
                "Jane",
                "Doe",
                new Date(1),
                "Kyiv, Ukraine",
                true
        );


        TraineeWithTrainersDto updatedProfile = new TraineeWithTrainersDto(
                "Jane_Updated",
                "Doe_Updated",
                new Date(1),
                "Kharkiv, Ukraine",
                true,
                List.of()
        );

        when(traineeService.updateProfile(eq(username), any(UpdateTraineeDto.class)))
                .thenReturn(updatedProfile);


        mockMvc.perform(put("/trainees/me")
                        .with(authUser(username, List.of("TRAINEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane_Updated"))
                .andExpect(jsonPath("$.lastName").value("Doe_Updated"))
                .andExpect(jsonPath("$.address").value("Kharkiv, Ukraine"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainers").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidUpdatePayloads")
    void shouldReturn400WhenUpdateForInvalidPayload(UpdateTraineeDto payload) throws Exception {
        String username = "jane.doe";

        mockMvc.perform(put("/trainees/{username}", username)
                        .with(authUser("admin.admin", List.of("ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidUpdatePayloads")
    void shouldReturn400WhenUpdateForInvalidPayloadMe(UpdateTraineeDto payload) throws Exception {

        mockMvc.perform(put("/trainees/me")
                        .with(authUser("john.doe", List.of("TRAINEE")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn204WhenDeleteTrainee() throws Exception {
        String username = "jane.doe";

        doNothing().when(traineeService).deleteByUsername(username);

        mockMvc.perform(delete("/trainees/{username}", username)
                        .with(authUser("jane.doe", List.of("ADMIN")))
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(traineeService).deleteByUsername(username);
    }

    @Test
    void shouldReturn204WhenDeleteTraineeMe() throws Exception {
        String username = "jane.doe";
        doNothing().when(traineeService).deleteByUsername(username);

        mockMvc.perform(delete("/trainees/me")
                        .with(authUser(username, List.of("TRAINEE")))
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(traineeService).deleteByUsername(username);
    }
}
