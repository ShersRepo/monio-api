package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.middleware.response.ApiResponse;
import com.smart_tiger.monio.modules.ledger.dto.FiscalItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.smart_tiger.monio.middleware.response.SuccessResponseType.okFetch;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/ledger/fiscal")
@RequiredArgsConstructor
public class FiscalItemController {

    private final FiscalItemService service;

    @GetMapping("/{fiscalId}")
    public ResponseEntity<ApiResponse<FiscalItemDto>> getFiscalItem(@PathVariable UUID fiscalId) {
        return ok(okFetch(service.fetchFiscalItem(fiscalId)));
    }

}
