package com.smart_tiger.monio.modules.calendarrecurrencerules;

import jakarta.persistence.*;
import lombok.Data;
import org.joda.time.LocalDateTime;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "recurrence_rules", schema = "ledger")
@Data
public class RecurrenceRule implements Serializable {

    @Serial
    private static final long serialVersionUID = -3285659731333633741L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "interval", nullable = false)
    private int interval;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequency", nullable = false, length = 255)
    private RecurrenceFrequency frequency;

}
