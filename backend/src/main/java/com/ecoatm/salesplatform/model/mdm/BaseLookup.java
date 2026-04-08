package com.ecoatm.salesplatform.model.mdm;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseLookup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "legacy_id", unique = true)
    private Long legacyId;

    private String name;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "sort_rank")
    private Integer sortRank;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLegacyId() { return legacyId; }
    public void setLegacyId(Long legacyId) { this.legacyId = legacyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }

    public Integer getSortRank() { return sortRank; }
    public void setSortRank(Integer sortRank) { this.sortRank = sortRank; }
}
