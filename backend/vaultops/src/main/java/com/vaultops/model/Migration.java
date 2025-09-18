package com.vaultops.model;

import jakarta.persistence.*;

@Entity
@Table(name = "migration")
public class Migration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @Column(name = "from_location")
    private String fromLocation;

    @Column(name = "to_location")
    private String toLocation;

    @Column(name = "moved_by")
    private String movedBy;

    @Column(name = "description")
    private String description;

    public Migration(Long id, Asset asset, String fromLocation, String toLocation, String movedBy, String description) {
        this.id = id;
        this.asset = asset;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.movedBy = movedBy;
        this.description = description;
    }

    public Migration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getMovedBy() {
        return movedBy;
    }

    public void setMovedBy(String movedBy) {
        this.movedBy = movedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
