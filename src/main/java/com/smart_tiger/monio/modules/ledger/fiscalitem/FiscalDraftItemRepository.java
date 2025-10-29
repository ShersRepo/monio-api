package com.smart_tiger.monio.modules.ledger.fiscalitem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FiscalDraftItemRepository extends JpaRepository<FiscalDraftItem, UUID> {

}
