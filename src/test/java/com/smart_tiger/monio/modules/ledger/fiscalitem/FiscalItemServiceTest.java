package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.middleware.security.SecurityContextService;
import com.smart_tiger.monio.modules.ledger.Ledger;
import com.smart_tiger.monio.modules.ledger.LedgerRepository;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDraftDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import com.smart_tiger.monio.modules.user.UserAccountRepository;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class FiscalItemServiceTest {

    @Mock
    private FiscalItemRepository repo;

    @Mock
    private LedgerRepository ledgerRepo;

    @Mock
    private UserAccountRepository userRepo;

    @Mock
    private FiscalItemMapper mapper;

    @Mock
    private SecurityContextService contextService;

    @Mock
    private FiscalDraftItemRepository draftRepo;

    @InjectMocks
    private FiscalItemService service;

    @Test
    void createFiscalItem_shouldCreatePersistedItem() throws Exception {
        UUID ledgerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FiscalItemCreateDto createDto = new FiscalItemCreateDto("Rent");
        Ledger ledger = new Ledger();
        UserAccount user = new UserAccount();
        FiscalItem entity = new FiscalItem();
        FiscalItem saved = new FiscalItem();
        UUID fiscalId = UUID.randomUUID();
        saved.setId(fiscalId);
        FiscalItemDto mappedDto = new FiscalItemDto(fiscalId);

        when(mapper.createDtoToEntity(createDto)).thenReturn(entity);
        when(ledgerRepo.findById(ledgerId)).thenReturn(Optional.of(ledger));
        when(contextService.getCurrentUserId()).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(repo.save(entity)).thenReturn(saved);
        when(mapper.entityToDto(saved)).thenReturn(mappedDto);

        FiscalItemDto result = service.createFiscalItem(createDto, ledgerId);

        ArgumentCaptor<FiscalItem> captor = ArgumentCaptor.forClass(FiscalItem.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getLedger()).isEqualTo(ledger);
        assertThat(captor.getValue().getCreatedBy()).isEqualTo(user);
        assertThat(result).isEqualTo(mappedDto);
    }

    @Test
    void createFiscalItem_shouldThrowWhenLedgerMissing() throws Exception {
        UUID ledgerId = UUID.randomUUID();
        FiscalItemCreateDto createDto = new FiscalItemCreateDto("Rent");

        when(mapper.createDtoToEntity(createDto)).thenReturn(new FiscalItem());
        when(ledgerRepo.findById(ledgerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createFiscalItem(createDto, ledgerId));

        verify(userRepo, never()).findById(any());
        verify(repo, never()).save(any());
    }

    @Test
    void createFiscalItem_shouldThrowWhenCreatedByUserMissing() throws Exception {
        UUID ledgerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FiscalItemCreateDto createDto = new FiscalItemCreateDto("Rent");

        when(mapper.createDtoToEntity(createDto)).thenReturn(new FiscalItem());
        when(ledgerRepo.findById(ledgerId)).thenReturn(Optional.of(new Ledger()));
        when(contextService.getCurrentUserId()).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createFiscalItem(createDto, ledgerId));

        verify(repo, never()).save(any());
    }

    @Test
    void createDraftFiscalItem_shouldCreateDraft() throws Exception {
        UUID ledgerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FiscalItemCreateDto createDto = new FiscalItemCreateDto("Rent");
        Ledger ledger = new Ledger();
        UserAccount user = new UserAccount();
        FiscalDraftItem entity = new FiscalDraftItem();
        FiscalDraftItem saved = new FiscalDraftItem();
        UUID draftId = UUID.randomUUID();
        saved.setId(draftId);
        FiscalItemDraftDto mappedDto = new FiscalItemDraftDto();
        mappedDto.setId(draftId);
        mappedDto.setLedgerId(ledgerId);

        when(mapper.createDtoToDraftEntity(createDto)).thenReturn(entity);
        when(ledgerRepo.findById(ledgerId)).thenReturn(Optional.of(ledger));
        when(contextService.getCurrentUserId()).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(draftRepo.save(entity)).thenReturn(saved);
        when(mapper.draftEntityToDto(saved)).thenReturn(mappedDto);

        FiscalItemDraftDto result = service.createDraftFiscalItem(createDto, ledgerId);

        ArgumentCaptor<FiscalDraftItem> captor = ArgumentCaptor.forClass(FiscalDraftItem.class);
        verify(draftRepo).save(captor.capture());
        assertThat(captor.getValue().getLedger()).isEqualTo(ledger);
        assertThat(captor.getValue().getCreatedBy()).isEqualTo(user);
        assertThat(result).isEqualTo(mappedDto);
    }

    @Test
    void createDraftFiscalItem_shouldThrowWhenLedgerMissing() throws Exception {
        UUID ledgerId = UUID.randomUUID();
        FiscalItemCreateDto createDto = new FiscalItemCreateDto("Rent");

        when(mapper.createDtoToDraftEntity(createDto)).thenReturn(new FiscalDraftItem());
        when(ledgerRepo.findById(ledgerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createDraftFiscalItem(createDto, ledgerId));
        verify(draftRepo, never()).save(any());
    }

    @Test
    void createDraftFiscalItem_shouldThrowWhenCreatedByUserMissing() throws Exception {
        UUID ledgerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        FiscalItemCreateDto createDto = new FiscalItemCreateDto("Rent");

        when(mapper.createDtoToDraftEntity(createDto)).thenReturn(new FiscalDraftItem());
        when(ledgerRepo.findById(ledgerId)).thenReturn(Optional.of(new Ledger()));
        when(contextService.getCurrentUserId()).thenReturn(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createDraftFiscalItem(createDto, ledgerId));
        verify(draftRepo, never()).save(any());
    }

    @Test
    void updateFiscalItem_shouldUpdateProvidedFields() {
        UUID draftId = UUID.randomUUID();
        FiscalDraftItem existing = new FiscalDraftItem();
        existing.setId(draftId);
        existing.setName("Old Name");
        existing.setDescription("Old Description");
        existing.setAmount(BigDecimal.ONE);
        existing.setExpenditure(false);

        FiscalItemDraftDto updateDto = new FiscalItemDraftDto();
        updateDto.setId(draftId);
        updateDto.setLedgerId(UUID.randomUUID());
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");
        updateDto.setAmount(new BigDecimal("12.34"));
        updateDto.setExpenditure(true);

        FiscalItemDraftDto mappedDto = new FiscalItemDraftDto();
        mappedDto.setId(draftId);
        mappedDto.setName("New Name");

        when(draftRepo.findById(draftId)).thenReturn(Optional.of(existing));
        when(draftRepo.save(existing)).thenReturn(existing);
        when(mapper.draftEntityToDto(existing)).thenReturn(mappedDto);

        FiscalItemDraftDto result = service.updateFiscalItem(updateDto);

        assertThat(existing.getName()).isEqualTo("New Name");
        assertThat(existing.getDescription()).isEqualTo("New Description");
        assertThat(existing.getAmount()).isEqualByComparingTo("12.34");
        assertThat(existing.isExpenditure()).isTrue();
        assertThat(result).isEqualTo(mappedDto);
    }

    @Test
    void updateFiscalItem_shouldKeepExistingNameAndDescriptionWhenBlank() {
        UUID draftId = UUID.randomUUID();
        FiscalDraftItem existing = new FiscalDraftItem();
        existing.setId(draftId);
        existing.setName("Old Name");
        existing.setDescription("Old Description");

        FiscalItemDraftDto updateDto = new FiscalItemDraftDto();
        updateDto.setId(draftId);
        updateDto.setLedgerId(UUID.randomUUID());
        updateDto.setName(" ");
        updateDto.setDescription(null);
        updateDto.setAmount(BigDecimal.ZERO);
        updateDto.setExpenditure(false);

        when(draftRepo.findById(draftId)).thenReturn(Optional.of(existing));
        when(draftRepo.save(existing)).thenReturn(existing);
        when(mapper.draftEntityToDto(existing)).thenReturn(updateDto);

        service.updateFiscalItem(updateDto);

        assertThat(existing.getName()).isEqualTo("Old Name");
        assertThat(existing.getDescription()).isEqualTo("Old Description");
        assertThat(existing.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void updateFiscalItem_shouldThrowWhenDraftMissing() {
        UUID draftId = UUID.randomUUID();
        FiscalItemDraftDto updateDto = new FiscalItemDraftDto();
        updateDto.setId(draftId);

        when(draftRepo.findById(draftId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateFiscalItem(updateDto));
        verify(draftRepo, never()).save(any());
    }

    @Test
    void fetchFiscalItem_shouldReturnMappedItem() {
        UUID itemId = UUID.randomUUID();
        FiscalItem item = new FiscalItem();
        FiscalItemDto dto = new FiscalItemDto(itemId);

        when(repo.findById(itemId)).thenReturn(Optional.of(item));
        when(mapper.entityToDto(item)).thenReturn(dto);

        FiscalItemDto result = service.fetchFiscalItem(itemId);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void fetchFiscalItem_shouldThrowWhenMissing() {
        UUID itemId = UUID.randomUUID();
        when(repo.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.fetchFiscalItem(itemId));
        verify(mapper, never()).entityToDto(any());
    }

    @Test
    void deleteDraftFiscalItem_shouldDeleteById() {
        UUID draftId = UUID.randomUUID();

        service.deleteDraftFiscalItem(draftId);

        verify(draftRepo).deleteById(draftId);
    }
}
