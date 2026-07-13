package com.ecoatm.salesplatform.model.pws;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Read model over the {@code pws.pws_constants} singleton config row (Mendix
 * {@code ecoatm_pws$pwsconstants}). Mapped so the SLA-tag service can honor the
 * admin-configurable {@code sla_days} knob instead of a hardcoded interval.
 *
 * <p>The {@code PWSAdminController} still reads/writes this table via
 * {@code JdbcTemplate}; this entity is a JPA read view and the two coexist on
 * the same table.
 */
@Entity
@Table(name = "pws_constants", schema = "pws")
public class PwsConstants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sla_days")
    private Integer slaDays;

    @Column(name = "sales_email")
    private String salesEmail;

    @Column(name = "send_first_reminder")
    private Boolean sendFirstReminder;

    @Column(name = "send_second_reminder")
    private Boolean sendSecondReminder;

    @Column(name = "hours_first_counter_reminder")
    private Integer hoursFirstCounterReminder;

    @Column(name = "hours_second_counter_reminder")
    private Integer hoursSecondCounterReminder;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getSlaDays() { return slaDays; }
    public void setSlaDays(Integer slaDays) { this.slaDays = slaDays; }

    public String getSalesEmail() { return salesEmail; }
    public void setSalesEmail(String salesEmail) { this.salesEmail = salesEmail; }

    public Boolean getSendFirstReminder() { return sendFirstReminder; }
    public void setSendFirstReminder(Boolean sendFirstReminder) { this.sendFirstReminder = sendFirstReminder; }

    public Boolean getSendSecondReminder() { return sendSecondReminder; }
    public void setSendSecondReminder(Boolean sendSecondReminder) { this.sendSecondReminder = sendSecondReminder; }

    public Integer getHoursFirstCounterReminder() { return hoursFirstCounterReminder; }
    public void setHoursFirstCounterReminder(Integer hoursFirstCounterReminder) { this.hoursFirstCounterReminder = hoursFirstCounterReminder; }

    public Integer getHoursSecondCounterReminder() { return hoursSecondCounterReminder; }
    public void setHoursSecondCounterReminder(Integer hoursSecondCounterReminder) { this.hoursSecondCounterReminder = hoursSecondCounterReminder; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }
}
