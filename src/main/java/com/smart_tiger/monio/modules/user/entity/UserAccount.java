package com.smart_tiger.monio.modules.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User stored in the DB table
 */
@Entity
@Table(name = "user_account", schema = "user")
@Data
public final class UserAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = 3427246926041669266L;

    @Id
    @Column(name = "id", length = 45)
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull(message = "Must not be null")
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 150)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "expired")
    private boolean expired;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "is_locked")
    private boolean locked;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "is_credentials_expired")
    private boolean credentialsExpired;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_security_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<SecurityRole> roles = new HashSet<>();

}

