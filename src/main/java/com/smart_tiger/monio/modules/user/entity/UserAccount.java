package com.smart_tiger.monio.modules.user.entity;

import com.smart_tiger.monio.modules.user.constant.UserAccountStatus;
import com.smart_tiger.monio.modules.user.constant.UserAccountStatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 *
 * User stored in the DB table
 */
@Entity
@Table(name = "user_account", schema = "user_acc")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public final class UserAccount implements Serializable {

    @Serial
    private static final long serialVersionUID = 3427246926041669266L;

    @Id
    @Column(name = "id", length = 45, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 150)
    private String username;

    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 255, message = "Password does not meet the requirements")
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", length = 80)
    private String lastName;

    @Column(name = "due_to_expire_date")
    private LocalDateTime expiryDate;

    @Column(name = "verification_pending_start_date", nullable = false)
    private LocalDateTime verificationPendingStartDate;

    @Column(name = "verification_pending_completion_date")
    private LocalDateTime verificationPendingCompletionDate;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "is_credentials_expired")
    private boolean credentialsExpired;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "status", nullable = false, length = 75)
    @Convert(converter = UserAccountStatusConverter.class)
    private UserAccountStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            schema = "user_acc",
            name = "user_security_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<SecurityRole> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof UserAccount that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 31;
    }

}