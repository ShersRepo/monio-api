package com.smart_tiger.monio.modules.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserAccountDto {

    private String username;
    private String firstName;
    private String lastName;
    private String email;

}
