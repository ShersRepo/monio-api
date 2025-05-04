package com.smart_tiger.monio.modules.user;

import com.smart_tiger.monio.modules.user.dto.UserAccountCreateDto;
import com.smart_tiger.monio.modules.user.dto.UserAccountDto;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserAccountDto entityToDto(UserAccount userAccount);

    UserAccount dtoToEntity(UserAccountDto userAccountDto);

    UserAccount createDtoToEntity(UserAccountCreateDto userAccount);

}
