package com.smart_tiger.monio.modules.ledger;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.middleware.security.SecurityContextService;
import com.smart_tiger.monio.modules.ledger.dto.LedgerCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
class LedgerServiceTest {

    @Mock
    private LedgerRepository ledgerRepository;

    @Mock
    private LedgerMapper ledgerMapper;

    @Mock
    private SecurityContextService securityContextService;

    @InjectMocks
    private LedgerService ledgerService;

    private UUID testLedgerId;
    private UUID testUserId;
    private Ledger testLedger;
    private LedgerDto testLedgerDto;
    private LedgerCreateDto testLedgerCreateDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        testLedgerId = UUID.randomUUID();
        testUserId = UUID.randomUUID();

        testLedger = new Ledger();
        testLedger.setId(testLedgerId);
        testLedger.setName("Test Ledger");
        testLedger.setCreatedBy(testUserId);
        testLedger.setFiscalItems(Collections.emptyList());

        testLedgerDto = new LedgerDto(testLedgerId);
        testLedgerDto.setName("Test Ledger");

        testLedgerCreateDto = new LedgerCreateDto();
        testLedgerCreateDto.setName("Test Ledger");
    }

    @Test
    void LedgerService_createLedger_shouldReturnCreatedLedger() throws NotAuthorisedException {
        // Arrange
        when(ledgerMapper.createDtoToEntity(any(LedgerCreateDto.class))).thenReturn(testLedger);
        when(securityContextService.getCurrentUserId()).thenReturn(testUserId);
        when(ledgerRepository.save(any(Ledger.class))).thenReturn(testLedger);
        when(ledgerMapper.entityToDto(any(Ledger.class))).thenReturn(testLedgerDto);

        // Act
        LedgerDto result = ledgerService.createLedger(testLedgerCreateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLedgerId);
        assertThat(result.getName()).isEqualTo("Test Ledger");

        verify(ledgerMapper).createDtoToEntity(testLedgerCreateDto);
        verify(securityContextService).getCurrentUserId();
        verify(ledgerRepository).save(testLedger);
        verify(ledgerMapper).entityToDto(testLedger);
    }

    @Test
    void LedgerService_createLedger_shouldThrowNotAuthorisedException_whenSecurityContextServiceThrows() throws NotAuthorisedException {
        // Arrange
        when(ledgerMapper.createDtoToEntity(any(LedgerCreateDto.class))).thenReturn(testLedger);
        when(securityContextService.getCurrentUserId()).thenThrow(new NotAuthorisedException());

        // Act & Assert
        assertThrows(NotAuthorisedException.class, () -> ledgerService.createLedger(testLedgerCreateDto));

        verify(ledgerMapper).createDtoToEntity(testLedgerCreateDto);
        verify(securityContextService).getCurrentUserId();
        verify(ledgerRepository, never()).save(any(Ledger.class));
        verify(ledgerMapper, never()).entityToDto(any(Ledger.class));
    }

    @Test
    void LedgerService_fetchLedger_shouldReturnLedger() {
        // Arrange
        when(ledgerRepository.findById(testLedgerId)).thenReturn(Optional.of(testLedger));
        when(ledgerMapper.entityToDto(testLedger)).thenReturn(testLedgerDto);

        // Act
        LedgerDto result = ledgerService.fetchLedger(testLedgerId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLedgerId);
        assertThat(result.getName()).isEqualTo("Test Ledger");

        verify(ledgerRepository).findById(testLedgerId);
        verify(ledgerMapper).entityToDto(testLedger);
    }

    @Test
    void LedgerService_fetchLedger_shouldThrowResourceNotFoundException_whenLedgerNotFound() {
        // Arrange
        when(ledgerRepository.findById(testLedgerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ledgerService.fetchLedger(testLedgerId));

        verify(ledgerRepository).findById(testLedgerId);
        verify(ledgerMapper, never()).entityToDto(any(Ledger.class));
    }

    @Test
    void LedgerService_fetchLedgerWithFiscalItems_shouldReturnLedgerWithFiscalItems() {
        // Arrange
        when(ledgerRepository.findWithFiscalItemsById(testLedgerId)).thenReturn(Optional.of(testLedger));
        when(ledgerMapper.entityToDto(testLedger)).thenReturn(testLedgerDto);

        // Act
        LedgerDto result = ledgerService.fetchLedgerWithFiscalItems(testLedgerId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLedgerId);
        assertThat(result.getName()).isEqualTo("Test Ledger");

        verify(ledgerRepository).findWithFiscalItemsById(testLedgerId);
        verify(ledgerMapper).entityToDto(testLedger);
    }

    @Test
    void LedgerService_fetchLedgerWithFiscalItems_shouldThrowResourceNotFoundException_whenLedgerNotFound() {
        // Arrange
        when(ledgerRepository.findWithFiscalItemsById(testLedgerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ledgerService.fetchLedgerWithFiscalItems(testLedgerId));

        verify(ledgerRepository).findWithFiscalItemsById(testLedgerId);
        verify(ledgerMapper, never()).entityToDto(any(Ledger.class));
    }

    @Test
    void LedgerService_fetchUsersLedgerWithFiscalItems_shouldReturnUsersLedgerWithFiscalItems() {
        // Arrange
        when(ledgerRepository.findWithFiscalItemsByCreatedBy(testUserId)).thenReturn(Optional.of(testLedger));
        when(ledgerMapper.entityToDto(testLedger)).thenReturn(testLedgerDto);

        // Act
        LedgerDto result = ledgerService.fetchUsersLedgerWithFiscalItems(testUserId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLedgerId);
        assertThat(result.getName()).isEqualTo("Test Ledger");

        verify(ledgerRepository).findWithFiscalItemsByCreatedBy(testUserId);
        verify(ledgerMapper).entityToDto(testLedger);
    }

    @Test
    void LedgerService_fetchUsersLedgerWithFiscalItems_shouldThrowResourceNotFoundException_whenLedgerNotFound() {
        // Arrange
        when(ledgerRepository.findWithFiscalItemsByCreatedBy(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ledgerService.fetchUsersLedgerWithFiscalItems(testUserId));

        verify(ledgerRepository).findWithFiscalItemsByCreatedBy(testUserId);
        verify(ledgerMapper, never()).entityToDto(any(Ledger.class));
    }
}