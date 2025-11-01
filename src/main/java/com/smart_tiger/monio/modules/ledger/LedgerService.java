package com.smart_tiger.monio.modules.ledger;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.middleware.security.SecurityContextService;
import com.smart_tiger.monio.modules.ledger.dto.LedgerCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Collections.emptyList;

@Service
@RequiredArgsConstructor
public class LedgerService {

    public static final String MESSAGE_UNABLE_TO_FIND_LEDGER = "Unable to find ledger";
    private final LedgerRepository repo;
    private final LedgerMapper mapper;
    private final SecurityContextService contextService;

    @Transactional
    public LedgerDto createLedger(LedgerCreateDto dto) throws NotAuthorisedException {
        Ledger ledger = mapper.createDtoToEntity(dto);
        ledger.setFiscalItems(emptyList());
        ledger.setCreatedBy(contextService.getCurrentUserId());
        return mapper.entityToDto(repo.save(ledger));
    }

    @Transactional(readOnly = true)
    public LedgerDto fetchLedger(UUID ledgerId) throws ResourceNotFoundException {
        Ledger ledger = repo.findById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_UNABLE_TO_FIND_LEDGER));

        return mapper.entityToDto(ledger);
    }

    @Transactional(readOnly = true)
    public LedgerDto fetchLedgerWithFiscalItems(UUID ledgerId) throws ResourceNotFoundException {
        Ledger ledger = repo.findWithFiscalItemsById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_UNABLE_TO_FIND_LEDGER));

        return mapper.entityToDto(ledger);
    }

    @Transactional(readOnly = true)
    public LedgerDto fetchUsersLedgerWithFiscalItems(UUID userId) throws ResourceNotFoundException {
        Ledger ledger = repo.findWithFiscalItemsByCreatedBy(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE_UNABLE_TO_FIND_LEDGER));

        return mapper.entityToDto(ledger);
    }

}
