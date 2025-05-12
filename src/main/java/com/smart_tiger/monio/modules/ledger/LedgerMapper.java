package com.smart_tiger.monio.modules.ledger;

import com.smart_tiger.monio.modules.ledger.dto.LedgerCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerDto;
import com.smart_tiger.monio.modules.ledger.fiscalitem.FiscalItemMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {FiscalItemMapper.class})
public interface LedgerMapper {

    Ledger createDtoToEntity(LedgerCreateDto dto);

    LedgerDto entityToDto(Ledger entity);

    Ledger dtoToEntity(LedgerDto dto);

}
