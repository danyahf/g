package com.danya.trainee;

import com.danya.trainer.Trainer;
import com.danya.training.Training;
import com.danya.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "trainees")
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateOfBirth;
    private String address;

    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Setter(AccessLevel.PRIVATE)
    @ManyToMany
    @JoinTable(
            name = "trainees_trainers",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    private Set<Trainer> trainers = new HashSet<>();

    @Setter(AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "trainee", cascade = {CascadeType.REMOVE})
    private Set<Training> trainings = new HashSet<>();

    public Trainee(User user, Date dateOfBirth, String address) {
        this.user = user;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    public String getUsername() {
        return user.getUsername();
    }
}
