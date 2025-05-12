package com.smart_tiger.monio.modules.ledger;

import com.smart_tiger.monio.modules.ledger.constant.Currency;
import com.smart_tiger.monio.modules.ledger.constant.CurrencyConverter;
import com.smart_tiger.monio.modules.ledger.fiscalitem.FiscalItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ledger", schema = "ledger")
@Getter
@Setter
@RequiredArgsConstructor
public class Ledger implements Serializable {

    @Serial
    private static final long serialVersionUID = 3642529203416692784L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "comment", length = 500)
    private String comment;

    @Column(name = "notes", length = 1000)
    private List<String> notes;

    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;

    @Column(name = "default_currency", length = 3)
    @Convert(converter = CurrencyConverter.class)
    private Currency defaultCurrency;

    @OneToMany(
            mappedBy = "ledger",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<FiscalItem> fiscalItems;

}
