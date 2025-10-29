package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.modules.ledger.dto.FiscalItemCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDraftDto;
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

    @Mapping(target = "createdBy", ignore = true)
    FiscalDraftItem draftDtoToEntity(FiscalItemDraftDto dto);

    FiscalDraftItem createDtoToDraftEntity(FiscalItemCreateDto dto);

    @Mapping(target = "createdBy", ignore = true)
    FiscalItemDraftDto draftEntityToDto(FiscalDraftItem entity);

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
