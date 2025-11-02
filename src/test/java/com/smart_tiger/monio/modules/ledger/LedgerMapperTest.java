package com.smart_tiger.monio.modules.ledger;

import com.smart_tiger.monio.modules.ledger.constant.Currency;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerDto;
import com.smart_tiger.monio.modules.ledger.fiscalitem.FiscalItem;
import com.smart_tiger.monio.modules.ledger.fiscalitem.FiscalItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Tag("UnitTest")
class LedgerMapperTest {

    @Mock
    private FiscalItemMapper fiscalItemMapper;

    @InjectMocks
    private LedgerMapperImpl ledgerMapper;

    private UUID testLedgerId;
    private UUID testUserId;
    private Ledger testLedger;
    private LedgerDto testLedgerDto;
    private LedgerCreateDto testLedgerCreateDto;
    private List<String> testNotes;
    private List<FiscalItem> testFiscalItems;
    private List<FiscalItemDto> testFiscalItemDtos;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        testLedgerId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
        testNotes = Arrays.asList("Note 1", "Note 2");
        
        // Set up fiscal items
        testFiscalItems = new ArrayList<>();
        FiscalItem fiscalItem = new FiscalItem();
        fiscalItem.setId(UUID.randomUUID());
        testFiscalItems.add(fiscalItem);
        
        testFiscalItemDtos = new ArrayList<>();
        FiscalItemDto fiscalItemDto = new FiscalItemDto(fiscalItem.getId());
        testFiscalItemDtos.add(fiscalItemDto);
        
        // Set up mock behavior
        when(fiscalItemMapper.entityToDto(any(FiscalItem.class))).thenReturn(fiscalItemDto);
        when(fiscalItemMapper.dtoToEntity(any(FiscalItemDto.class))).thenReturn(fiscalItem);

        // Set up test ledger
        testLedger = new Ledger();
        testLedger.setId(testLedgerId);
        testLedger.setName("Test Ledger");
        testLedger.setComment("Test Comment");
        testLedger.setNotes(testNotes);
        testLedger.setCreatedBy(testUserId);
        testLedger.setDefaultCurrency(Currency.GBP);
        testLedger.setFiscalItems(testFiscalItems);

        // Set up test ledger DTO
        testLedgerDto = new LedgerDto(testLedgerId);
        testLedgerDto.setName("Test Ledger");
        testLedgerDto.setComment("Test Comment");
        testLedgerDto.setNotes(testNotes);
        testLedgerDto.setDefaultCurrency(Currency.GBP);
        testLedgerDto.setFiscalItems(testFiscalItemDtos);

        // Set up test ledger create DTO
        testLedgerCreateDto = new LedgerCreateDto();
        testLedgerCreateDto.setName("Test Ledger");
        testLedgerCreateDto.setComment("Test Comment");
        testLedgerCreateDto.setNotes(testNotes);
        testLedgerCreateDto.setCreatedBy(testUserId);
        testLedgerCreateDto.setDefaultCurrency(Currency.GBP);
    }

    @Test
    void LedgerMapper_createDtoToEntity_shouldMapCorrectly() {
        // Act
        Ledger result = ledgerMapper.createDtoToEntity(testLedgerCreateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testLedgerCreateDto.getName());
        assertThat(result.getComment()).isEqualTo(testLedgerCreateDto.getComment());
        assertThat(result.getNotes()).isEqualTo(testLedgerCreateDto.getNotes());
        assertThat(result.getCreatedBy()).isEqualTo(testLedgerCreateDto.getCreatedBy());
        assertThat(result.getDefaultCurrency()).isEqualTo(testLedgerCreateDto.getDefaultCurrency());
    }

    @Test
    void LedgerMapper_entityToDto_shouldMapCorrectly() {
        // Act
        LedgerDto result = ledgerMapper.entityToDto(testLedger);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLedger.getId());
        assertThat(result.getName()).isEqualTo(testLedger.getName());
        assertThat(result.getComment()).isEqualTo(testLedger.getComment());
        assertThat(result.getNotes()).isEqualTo(testLedger.getNotes());
        assertThat(result.getDefaultCurrency()).isEqualTo(testLedger.getDefaultCurrency());
        assertThat(result.getFiscalItems()).isNotNull();
        assertThat(result.getFiscalItems().size()).isEqualTo(testLedger.getFiscalItems().size());
    }

    @Test
    void LedgerMapper_dtoToEntity_shouldMapCorrectly() {
        // Act
        Ledger result = ledgerMapper.dtoToEntity(testLedgerDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLedgerDto.getId());
        assertThat(result.getName()).isEqualTo(testLedgerDto.getName());
        assertThat(result.getComment()).isEqualTo(testLedgerDto.getComment());
        assertThat(result.getNotes()).isEqualTo(testLedgerDto.getNotes());
        assertThat(result.getDefaultCurrency()).isEqualTo(testLedgerDto.getDefaultCurrency());
        assertThat(result.getFiscalItems()).isNotNull();
        assertThat(result.getFiscalItems().size()).isEqualTo(testLedgerDto.getFiscalItems().size());
    }

    @Test
    void LedgerMapper_createDtoToEntity_shouldHandleNullValues() {
        // Arrange
        testLedgerCreateDto.setComment(null);
        testLedgerCreateDto.setNotes(null);
        testLedgerCreateDto.setDefaultCurrency(null);

        // Act
        Ledger result = ledgerMapper.createDtoToEntity(testLedgerCreateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(testLedgerCreateDto.getName());
        assertThat(result.getComment()).isNull();
        assertThat(result.getNotes()).isNull();
        assertThat(result.getDefaultCurrency()).isNull();
    }

    @Test
    void LedgerMapper_entityToDto_shouldHandleNullValues() {
        // Arrange
        testLedger.setComment(null);
        testLedger.setNotes(null);
        testLedger.setDefaultCurrency(null);
        testLedger.setFiscalItems(null);

        // Act
        LedgerDto result = ledgerMapper.entityToDto(testLedger);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLedger.getId());
        assertThat(result.getName()).isEqualTo(testLedger.getName());
        assertThat(result.getComment()).isNull();
        assertThat(result.getNotes()).isNull();
        assertThat(result.getDefaultCurrency()).isNull();
        assertThat(result.getFiscalItems()).isNull();
    }

    @Test
    void LedgerMapper_dtoToEntity_shouldHandleNullValues() {
        // Arrange
        testLedgerDto.setName(null);
        testLedgerDto.setComment(null);
        testLedgerDto.setNotes(null);
        testLedgerDto.setDefaultCurrency(null);
        testLedgerDto.setFiscalItems(null);

        // Act
        Ledger result = ledgerMapper.dtoToEntity(testLedgerDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testLedgerDto.getId());
        assertThat(result.getName()).isNull();
        assertThat(result.getComment()).isNull();
        assertThat(result.getNotes()).isNull();
        assertThat(result.getDefaultCurrency()).isNull();
        assertThat(result.getFiscalItems()).isNull();
    }

    @Test
    void LedgerMapper_createDtoToEntity_shouldReturnNullForNullInput() {
        // Act
        Ledger result = ledgerMapper.createDtoToEntity(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void LedgerMapper_entityToDto_shouldReturnNullForNullInput() {
        // Act
        LedgerDto result = ledgerMapper.entityToDto(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void LedgerMapper_dtoToEntity_shouldReturnNullForNullInput() {
        // Act
        Ledger result = ledgerMapper.dtoToEntity(null);

        // Assert
        assertThat(result).isNull();
    }
}