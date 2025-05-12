package com.smart_tiger.monio.modules.ledger.dto;

import com.smart_tiger.monio.modules.ledger.constant.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class LedgerDto {

    @NotNull
    @NotBlank
    private final UUID id;
    private String name;
    private String comment;
    private List<String> notes;
    private Currency defaultCurrency;
    private List<FiscalItemDto> fiscalItems;

}
