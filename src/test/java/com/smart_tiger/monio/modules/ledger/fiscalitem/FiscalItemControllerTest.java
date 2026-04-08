package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smart_tiger.monio.middleware.exception.GlobalExceptionHandler;
import com.smart_tiger.monio.middleware.exception.ResourceNotFoundException;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDraftDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class FiscalItemControllerTest {

    @Mock
    private FiscalItemService service;

    @InjectMocks
    private FiscalItemController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getFiscalItem_shouldReturnItem() throws Exception {
        UUID fiscalId = UUID.randomUUID();
        FiscalItemDto dto = new FiscalItemDto(fiscalId);
        dto.setName("Rent");

        when(service.fetchFiscalItem(fiscalId)).thenReturn(dto);

        mockMvc.perform(get("/ledger/fiscal/" + fiscalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Fetched data"))
                .andExpect(jsonPath("$.data.id").value(fiscalId.toString()))
                .andExpect(jsonPath("$.data.name").value("Rent"));
    }

    @Test
    void getFiscalItem_shouldReturnNotFoundWhenMissing() throws Exception {
        UUID fiscalId = UUID.randomUUID();
        when(service.fetchFiscalItem(fiscalId)).thenThrow(new ResourceNotFoundException("Resource not found"));

        mockMvc.perform(get("/ledger/fiscal/" + fiscalId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void handlePatchDraftItem_shouldReturnPatchedItem() throws Exception {
        UUID draftId = UUID.randomUUID();
        UUID ledgerId = UUID.randomUUID();
        FiscalItemDraftDto dto = new FiscalItemDraftDto();
        dto.setId(draftId);
        dto.setLedgerId(ledgerId);
        dto.setName("Draft");

        when(service.updateFiscalItem(any(FiscalItemDraftDto.class))).thenReturn(dto);

        mockMvc.perform(patch("/ledger/fiscal/fiscal-draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Patched data"))
                .andExpect(jsonPath("$.data.id").value(draftId.toString()));
    }

    @Test
    void handlePatchDraftItem_shouldRejectInvalidPayload() throws Exception {
        FiscalItemDraftDto dto = new FiscalItemDraftDto();

        mockMvc.perform(patch("/ledger/fiscal/fiscal-draft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }
}
