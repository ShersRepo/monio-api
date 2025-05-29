package com.smart_tiger.monio.modules.ledger.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FiscalRRuleDto {

    @NotNull
    @NotEmpty
    private String frequency;
    private int interval;
    @NotNull
    private String endDate;
    private String byWeekday;

}
