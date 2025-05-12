package com.smart_tiger.monio.modules.user.entity;

import com.smart_tiger.monio.middleware.security.verification.AppSecurityRole;
import com.smart_tiger.monio.modules.user.constant.AppSecurityRoleConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "security_role", schema = "user_acc")
@Data
public final class SecurityRole implements GrantedAuthority, Serializable {

    @Serial
    private static final long serialVersionUID = -2537079719032694969L;

    @Id
    @Column(name = "id", length = 45)
    @GeneratedValue(strategy = GenerationType.UUID)
    @NotNull(message = "Must not be null")
    private UUID id;

    @Column(unique = true, nullable = false, length = 70)
    @Convert(converter = AppSecurityRoleConverter.class, attributeName = "name")
    private AppSecurityRole name;

    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Override
    public String getAuthority() {
        return name.toString();
    }

}
