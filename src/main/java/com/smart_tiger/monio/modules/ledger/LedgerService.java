package com.smart_tiger.monio.modules.ledger;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.middleware.security.SecurityContextService;
import com.smart_tiger.monio.modules.ledger.dto.LedgerCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerDto;
import com.smart_tiger.monio.modules.ledger.fiscalitem.FiscalItemMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository repo;
    private final LedgerMapper mapper;
    private final FiscalItemMapper fiscalMapper;
    private final SecurityContextService contextService;

    public LedgerDto createLedger(LedgerCreateDto dto) throws NotAuthorisedException {
        Ledger ledger = mapper.createDtoToEntity(dto);
        ledger.setNotes(emptyList());
        ledger.setFiscalItems(emptyList());
        ledger.setCreatedBy(contextService.getCurrentUserId());
        return mapper.entityToDto(repo.save(ledger));
    }

    public LedgerDto fetchLedger(UUID ledgerId) throws ResourceNotFoundException {
        Ledger ledger = repo.findById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to find ledger"));

        return mapper.entityToDto(ledger);
    }

    public LedgerDto fetchLedgerWithFiscalItems(UUID ledgerId) throws ResourceNotFoundException {
        Ledger ledger = repo.findWithFiscalItemsById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException("Unable to find ledger"));

        return mapper.entityToDto(ledger);
    }

}
