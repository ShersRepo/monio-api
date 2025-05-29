package com.smart_tiger.monio.modules.ledger.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class FiscalItemDraftDto {

    @NotNull @NotBlank
    private UUID ledgerId;
    private UUID id;
    private String name;
    @Size(max = 400)
    private String description;
    @PositiveOrZero @Digits(integer = 19, fraction = 2)
    private BigDecimal amount;
    @NotNull
    private boolean expenditure;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID createdBy;

}
