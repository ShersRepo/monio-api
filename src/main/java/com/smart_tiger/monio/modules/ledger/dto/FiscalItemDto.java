package com.smart_tiger.monio.modules.ledger.dto;

import com.smart_tiger.monio.modules.ledger.constant.FiscalItemStatus;
import com.smart_tiger.monio.modules.ledger.constant.Currency;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode
public class FiscalItemDto {

    @NotNull
    @NotBlank
    private final UUID id;

    @NotNull
    @NotBlank
    private String name;

    @Size(max = 400)
    private String description;

    @PositiveOrZero
    @Digits(integer = 19, fraction = 2)
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private FiscalItemStatus status;

    @NotNull
    private boolean expenditure;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull
    private UUID ledgerId;

    private boolean requiresShiftToAfterWeekend;

    private boolean requiresShiftToBeforeWeekend;

}
