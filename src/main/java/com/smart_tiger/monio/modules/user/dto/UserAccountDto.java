package com.smart_tiger.monio.modules.user.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserAccountDto {

    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;

}
