package com.smart_tiger.monio.modules.ledger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.modules.ledger.dto.*;
import com.smart_tiger.monio.modules.ledger.fiscalitem.FiscalItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("UnitTest")
class LedgerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LedgerService ledgerService;

    @Mock
    private FiscalItemService fiscalItemService;

    @InjectMocks
    private LedgerController ledgerController;

    private ObjectMapper objectMapper;
    private UUID testLedgerId;
    private UUID testUserId;
    private LedgerDto testLedgerDto;
    private LedgerCreateDto testLedgerCreateDto;
    private FiscalItemCreateDto testFiscalItemCreateDto;
    private FiscalItemDto testFiscalItemDto;
    private FiscalItemDraftDto testFiscalItemDraftDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ledgerController)
                .setControllerAdvice(new com.smart_tiger.monio.middleware.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        // Initialize test data
        testLedgerId = UUID.randomUUID();
        testUserId = UUID.randomUUID();

        testLedgerDto = new LedgerDto(testLedgerId);
        testLedgerDto.setName("Test Ledger");

        testLedgerCreateDto = new LedgerCreateDto();
        testLedgerCreateDto.setName("Test Ledger");

        testFiscalItemCreateDto = new FiscalItemCreateDto("Test Fiscal Item");

        testFiscalItemDto = new FiscalItemDto(UUID.randomUUID());
        testFiscalItemDto.setName("Test Fiscal Item");

        testFiscalItemDraftDto = new FiscalItemDraftDto();
        testFiscalItemDraftDto.setId(UUID.randomUUID());
        testFiscalItemDraftDto.setName("Test Fiscal Item Draft");
    }

    @Test
    void LedgerController_handleCreateLedger_shouldReturnCreatedLedger() throws Exception {
        when(ledgerService.createLedger(any(LedgerCreateDto.class))).thenReturn(testLedgerDto);

        mockMvc.perform(post("/ledger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLedgerCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ledger/" + testLedgerDto.getId().toString()))
                .andExpect(jsonPath("$.data.id").value(testLedgerDto.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(testLedgerDto.getName()));
    }

    @Test
    void LedgerController_handleCreateFiscalItem_shouldReturnCreatedFiscalItem() throws Exception {
        when(fiscalItemService.createFiscalItem(any(FiscalItemCreateDto.class), eq(testLedgerId)))
                .thenReturn(testFiscalItemDto);

        mockMvc.perform(post("/ledger/" + testLedgerId + "/fiscal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFiscalItemCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ledger/fiscal/" + testFiscalItemDto.getId().toString()))
                .andExpect(jsonPath("$.data.id").value(testFiscalItemDto.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(testFiscalItemDto.getName()));
    }

    @Test
    void LedgerController_handleCreateDraftItem_shouldReturnCreatedDraftItem() throws Exception {
        when(fiscalItemService.createDraftFiscalItem(any(FiscalItemCreateDto.class), eq(testLedgerId)))
                .thenReturn(testFiscalItemDraftDto);

        mockMvc.perform(post("/ledger/" + testLedgerId + "/fiscal-draft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFiscalItemCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ledger/fiscal/" + testFiscalItemDraftDto.getId().toString()))
                .andExpect(jsonPath("$.data.id").value(testFiscalItemDraftDto.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(testFiscalItemDraftDto.getName()));
    }

    @Test
    void LedgerController_handleGetLedger_shouldReturnLedger() throws Exception {
        when(ledgerService.fetchLedger(testLedgerId)).thenReturn(testLedgerDto);

        mockMvc.perform(get("/ledger/" + testLedgerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testLedgerDto.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(testLedgerDto.getName()));
    }

    @Test
    void handleGetLedgerWithFiscalItems_shouldReturnLedgerWithFiscalItems() throws Exception {
        when(ledgerService.fetchLedgerWithFiscalItems(testLedgerId)).thenReturn(testLedgerDto);

        mockMvc.perform(get("/ledger/" + testLedgerId + "/with-fiscal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testLedgerDto.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(testLedgerDto.getName()));
    }

    @Test
    void LedgerController_handleGetUsersLedgerWithFiscalItems_shouldReturnUsersLedgerWithFiscalItems() throws Exception {
        when(ledgerService.fetchUsersLedgerWithFiscalItems(testUserId)).thenReturn(testLedgerDto);

        mockMvc.perform(get("/ledger/" + testUserId + "/with-fiscal-for-user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(testLedgerDto.getId().toString()))
                .andExpect(jsonPath("$.data.name").value(testLedgerDto.getName()));
    }

    @Test
    void LedgerController_handleCreateLedger_shouldHandleNotAuthorisedException() throws Exception {
        when(ledgerService.createLedger(any(LedgerCreateDto.class)))
                .thenThrow(new NotAuthorisedException());

        mockMvc.perform(post("/ledger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLedgerCreateDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void LedgerController_handleCreateFiscalItem_shouldHandleNotAuthorisedException() throws Exception {
        when(fiscalItemService.createFiscalItem(any(FiscalItemCreateDto.class), eq(testLedgerId)))
                .thenThrow(new NotAuthorisedException());

        mockMvc.perform(post("/ledger/" + testLedgerId + "/fiscal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFiscalItemCreateDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void LedgerController_handleCreateDraftItem_shouldHandleNotAuthorisedException() throws Exception {
        when(fiscalItemService.createDraftFiscalItem(any(FiscalItemCreateDto.class), eq(testLedgerId)))
                .thenThrow(new NotAuthorisedException());

        mockMvc.perform(post("/ledger/" + testLedgerId + "/fiscal-draft")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFiscalItemCreateDto)))
                .andExpect(status().isUnauthorized());
    }

}
