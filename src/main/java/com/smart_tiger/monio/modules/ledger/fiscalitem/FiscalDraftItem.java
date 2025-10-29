package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.modules.ledger.Ledger;
import com.smart_tiger.monio.modules.ledger.fiscaleventrules.FiscalRecurrenceRule;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fiscal_draft_item", schema = "ledger")
@Getter
@Setter
@RequiredArgsConstructor
public class FiscalDraftItem {

    @Serial
    private static final long serialVersionUID = 3559561284045248240L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 400)
    private String name;

    @Column(length = 400)
    private String description;

    @PositiveOrZero
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "is_expenditure", nullable = false)
    private boolean expenditure;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "recurrence_rule_id")
    private FiscalRecurrenceRule recurrenceRule;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_id", nullable = false)
    private Ledger ledger;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

}
