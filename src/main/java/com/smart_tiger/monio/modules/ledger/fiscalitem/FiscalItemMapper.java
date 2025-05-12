package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.modules.ledger.dto.FiscalItemCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FiscalItemMapper {

    @Mapping(target = "createdBy", ignore = true)
    FiscalItem createDtoToEntity(FiscalItemCreateDto createDto);

    FiscalItemDto entityToDto(FiscalItem entity);

    FiscalItem dtoToEntity(FiscalItemDto dto);

}
