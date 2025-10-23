package com.danya.trainer;

import com.danya.trainee.Trainee;
import com.danya.training.Training;
import com.danya.trainingType.TrainingType;
import com.danya.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "trainers")
public class Trainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private TrainingType specialization;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany(mappedBy = "trainers")
    private Set<Trainee> trainees = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "trainer", cascade = {CascadeType.REMOVE})
    private Set<Training> trainings = new HashSet<>();

    public Trainer(User user, TrainingType specialization) {
        this.user = user;
        this.specialization = specialization;
    }

    public String getUsername() {
        return user.getUsername();
    }
}
