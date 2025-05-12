package com.smart_tiger.monio.modules.ledger;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, UUID> {
    @EntityGraph(attributePaths = {"fiscalItems"})
    Optional<Ledger> findWithFiscalItemsById(UUID id);
}