package com.smart_tiger.monio.ledger;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.middleware.security.SecurityContextService;
import com.smart_tiger.monio.modules.ledger.Ledger;
import com.smart_tiger.monio.modules.ledger.LedgerMapper;
import com.smart_tiger.monio.modules.ledger.LedgerRepository;
import com.smart_tiger.monio.modules.ledger.LedgerService;
import com.smart_tiger.monio.modules.ledger.constant.Currency;
import com.smart_tiger.monio.modules.ledger.dto.LedgerCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
@SpringBootTest
class LedgerServiceTest {

    @Autowired
    private LedgerService ledgerService;
    @Autowired
    private LedgerMapper ledgerMapper;
    @MockitoBean
    private LedgerRepository ledgerRepository;
    @MockitoBean
    private SecurityContextService securityContextService;

    @Test
    void LedgerService_createLedger_success() throws NotAuthorisedException {
        LedgerCreateDto newLedgerDto = newCreateLedgerDto();
        Ledger savedLedger = ledgerMapper.createDtoToEntity(newLedgerDto);
        savedLedger.setId(UUID.randomUUID());

        UUID userId = UUID.randomUUID();

        when(securityContextService.getCurrentUserId()).thenReturn(userId);
        when(ledgerRepository.save(any(Ledger.class))).thenReturn(savedLedger);

        LedgerDto result = ledgerService.createLedger(newLedgerDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedLedger.getId());
    }




    private LedgerCreateDto newCreateLedgerDto() {
        LedgerCreateDto dto = new LedgerCreateDto();
        dto.setName("Test Ledger");
        dto.setComment("This is a test ledger");
        dto.setDefaultCurrency(Currency.GBP);
        dto.setNotes(List.of());
        return dto;
    }

    private Ledger newLedger() {
        Ledger dto = new Ledger();
        dto.setName("Test Ledger");
        dto.setId(UUID.randomUUID());
        dto.setDefaultCurrency(Currency.GBP);
        dto.setNotes(List.of());
        return dto;
    }

}
