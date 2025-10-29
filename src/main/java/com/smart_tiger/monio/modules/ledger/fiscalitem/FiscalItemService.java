package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.middleware.security.SecurityContextService;
import com.smart_tiger.monio.modules.ledger.Ledger;
import com.smart_tiger.monio.modules.ledger.LedgerRepository;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDraftDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.smart_tiger.monio.utils.StringUtils.isNotNullOrEmpty;

@Service
@RequiredArgsConstructor
public class FiscalItemService {

    private final FiscalItemRepository repo;
    private final LedgerRepository ledgerRepo;
    private final UserAccountRepository userRepo;
    private final FiscalItemMapper mapper;
    private final SecurityContextService contextService;
    private final FiscalDraftItemRepository draftRepo;

    public FiscalItemDto createFiscalItem(FiscalItemCreateDto createDto, UUID ledgerId) throws NotAuthorisedException {
        FiscalItem toCreate = mapper.createDtoToEntity(createDto);

        Ledger ledger = ledgerRepo.findById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent ledger could not be found"));

        UserAccount createdByUser = userRepo.findById(contextService.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Created By user could not be found"));

        toCreate.setLedger(ledger);
        toCreate.setCreatedBy(createdByUser);

        FiscalItem created = repo.save(toCreate);

        /*TODO remove draft variant */
        return mapper.entityToDto(created);
    }

    public FiscalItemDraftDto createDraftFiscalItem(FiscalItemCreateDto draftDto, UUID ledgerId) throws NotAuthorisedException {
        FiscalDraftItem toCreateDraft = mapper.createDtoToDraftEntity(draftDto);

        Ledger ledger = ledgerRepo.findById(ledgerId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent ledger could not be found"));

        UserAccount createdByUser = userRepo.findById(contextService.getCurrentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Created By user could not be found"));

        toCreateDraft.setLedger(ledger);
        toCreateDraft.setCreatedBy(createdByUser);

        FiscalDraftItem created = draftRepo.save(toCreateDraft);
        return mapper.draftEntityToDto(created);
    }

    public FiscalItemDraftDto updateFiscalItem(FiscalItemDraftDto updateDto) throws ResourceNotFoundException {
        FiscalDraftItem entityToUpdate = draftRepo.findById(updateDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));

        if (isNotNullOrEmpty(updateDto.getName())) {
            entityToUpdate.setName(updateDto.getName());
        }

        if (isNotNullOrEmpty(updateDto.getDescription())) {
            entityToUpdate.setDescription(updateDto.getDescription());
        }

        entityToUpdate.setExpenditure(updateDto.isExpenditure());

        return mapper.draftEntityToDto(draftRepo.save(entityToUpdate));
    }

    public FiscalItemDto fetchFiscalItem(UUID fiscalItemId) throws ResourceNotFoundException {
        return repo.findById(fiscalItemId)
                .map(mapper::entityToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
    }

    public void deleteDraftFiscalItem(UUID draftId) {
        draftRepo.deleteById(draftId);
    }

}
