package com.smart_tiger.monio.modules.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class UserAccountCreateDto {

    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
