package com.smart_tiger.monio.modules.ledger.dto;

import com.smart_tiger.monio.modules.ledger.constant.Currency;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
public class FiscalItemCreateDto {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 100)
    private final String name;

    @Size(max = 400)
    private String description;

    @PositiveOrZero
    @Digits(integer = 19, fraction = 2)
    private BigDecimal amount;

    private Currency currency;

    @NotNull
    private boolean expenditure;

}
