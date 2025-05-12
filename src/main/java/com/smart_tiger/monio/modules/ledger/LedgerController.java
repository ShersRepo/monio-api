package com.smart_tiger.monio.modules.ledger;

import com.smart_tiger.monio.middleware.exception.NotAuthorisedException;
import com.smart_tiger.monio.middleware.response.ApiResponse;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerCreateDto;
import com.smart_tiger.monio.modules.ledger.dto.LedgerDto;
import com.smart_tiger.monio.modules.ledger.fiscalitem.FiscalItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

import static com.smart_tiger.monio.middleware.response.SuccessResponseType.created;
import static com.smart_tiger.monio.middleware.response.SuccessResponseType.okFetch;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;
    private final FiscalItemService fiscalItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<LedgerDto>> handleCreateLedger(@Valid @RequestBody LedgerCreateDto dto) throws NotAuthorisedException {
        LedgerDto createdResult = ledgerService.createLedger(dto);
        return created(URI.create("/api/ledger/" + createdResult.getId().toString()))
                .body(created(createdResult));
    }

    @PostMapping("/{ledgerId}/fiscal")
    public ResponseEntity<ApiResponse<FiscalItemDto>> handleCreateFiscalItem(
            @Valid @RequestBody FiscalItemCreateDto dto,
            @PathVariable UUID ledgerId
    ) throws NotAuthorisedException {
        FiscalItemDto createdResult = fiscalItemService.createFiscalItem(dto, ledgerId);
        return created(URI.create("/api/ledger/fiscal/" + createdResult.getId().toString()))
                .body(created(createdResult));
    }

    @GetMapping("/{ledgerId}")
    public ResponseEntity<ApiResponse<LedgerDto>> handleGetLedger(
            @Valid @PathVariable UUID ledgerId
    ) {
        return ok(okFetch(ledgerService.fetchLedger(ledgerId)));
    }

    @GetMapping("/{ledgerId}/with-fiscal")
    public ResponseEntity<ApiResponse<LedgerDto>> handleGetLedgerWithFiscalItems(
            @Valid @PathVariable UUID ledgerId
    ) {
        return ok(okFetch(ledgerService.fetchLedgerWithFiscalItems(ledgerId)));
    }

}
