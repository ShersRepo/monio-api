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
public class LedgerCreateDto {

    @NotNull
    @NotBlank
    private String name;
    private String comment;
    private UUID createdBy;
    private Currency defaultCurrency;
    private List<String> notes;

}
