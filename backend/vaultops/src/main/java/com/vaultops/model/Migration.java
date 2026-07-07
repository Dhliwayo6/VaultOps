package com.vaultops.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "migration")
public class Migration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    @jakarta.validation.constraints.NotNull(message = "Asset is required")
    private Asset asset;

    @Column(name = "from_location")
    @jakarta.validation.constraints.NotBlank(message = "From location is required")
    @jakarta.validation.constraints.Size(max = 255, message = "From location must not exceed 255 characters")
    private String fromLocation;

    @Column(name = "to_location")
    @jakarta.validation.constraints.NotBlank(message = "To location is required")
    @jakarta.validation.constraints.Size(max = 255, message = "To location must not exceed 255 characters")
    private String toLocation;

    @Column(name = "moved_by")
    @jakarta.validation.constraints.NotBlank(message = "Moved by is required")
    @jakarta.validation.constraints.Size(max = 255, message = "Moved by must not exceed 255 characters")
    private String movedBy;

    @Column(name = "description")
    @jakarta.validation.constraints.Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
}
