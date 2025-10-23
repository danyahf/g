package com.danya.user;

import com.danya.user.role.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_users_roles_user")),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false,
                    foreignKey = @ForeignKey(name = "fk_users_roles_role"))
    )
    private Set<Role> roles = new HashSet<>();

    public User(String firstName, String lastName, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
    }

    public void addRole(Role role) {
        roles.add(role);
    }
}
