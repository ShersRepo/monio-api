package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.modules.ledger.dto.FiscalItemCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FiscalItemMapper {

    @Mapping(target = "createdBy", ignore = true)
    FiscalItem createDtoToEntity(FiscalItemCreateDto createDto);

    FiscalItemDto entityToDto(FiscalItem entity);

    FiscalItem dtoToEntity(FiscalItemDto dto);

    @AfterMapping
    default FiscalItemDto afterMapping(FiscalItem source, @MappingTarget FiscalItemDto target) {
        if (null != source.getRecurrenceRule()) {
            target.setRequiresShiftToAfterWeekend(target.isRequiresShiftToAfterWeekend());
            target.setRequiresShiftToBeforeWeekend(target.isRequiresShiftToBeforeWeekend());
            return target;
        } else {
            return target;
        }
    }

}
