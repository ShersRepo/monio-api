package com.smart_tiger.monio.modules.ledger.fiscalitem;

import com.smart_tiger.monio.modules.ledger.Ledger;
import com.smart_tiger.monio.modules.ledger.constant.Currency;
import com.smart_tiger.monio.modules.ledger.constant.CurrencyConverter;
import com.smart_tiger.monio.modules.ledger.constant.FiscalItemStatus;
import com.smart_tiger.monio.modules.user.entity.UserAccount;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "fiscal_item", schema = "ledger")
@Getter
@Setter
@RequiredArgsConstructor
public class FiscalItem implements Serializable {

    @Serial
    private static final long serialVersionUID = -3059016358839147194L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank
    private String name;

    @Column(name = "description", length = 400)
    private String description;

    @Positive
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    @Convert(converter = CurrencyConverter.class)
    private Currency currency;

    @Column(name = "status", nullable = false, length = 75)
    @Convert(converter = FiscalItemStatus.class, attributeName = "status")
    private FiscalItemStatus status;

    @Column(name = "is_expenditure", nullable = false)
    private boolean expenditure;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_id", nullable = false)
    private Ledger ledger;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof FiscalItem that)) return false;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return getId() != null ? getId().hashCode() : 31;
    }

}
