package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.middleware.security.SecurityContextService;
import com.smart_tiger.monio.modules.ledger.Ledger;
import com.smart_tiger.monio.modules.ledger.LedgerRepository;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FiscalItemService {

    private final FiscalItemRepository repo;
    private final LedgerRepository ledgerRepo;
    private final UserAccountRepository userRepo;
    private final FiscalItemMapper mapper;
    private final SecurityContextService contextService;

    public FiscalItemDto createFiscalItem(FiscalItemCreateDto createDto, UUID ledgerId) throws NotAuthorisedException {
        FiscalItem toCreate = mapper.createDtoToEntity(createDto);

        Ledger ledger = ledgerRepo.findById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent ledger could not be found"));

        UserAccount createdByUser = userRepo.findById(contextService.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Created By user could not be found"));

        toCreate.setLedger(ledger);
        toCreate.setCreatedBy(createdByUser);

        FiscalItem created = repo.save(toCreate);
        return mapper.entityToDto(created);
    }

    public FiscalItemDto fetchFiscalItem(UUID fiscalItemId) throws ResourceNotFoundException {
        return repo.findById(fiscalItemId)
                .map(mapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

}
