package com.vaultops.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "locations")
@com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = Location.LocationDeserializer.class)
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    @NotBlank(message = "Location name is required")
    @Size(max = 255, message = "Location name must not exceed 255 characters")
    private String name;

    @Column(name = "description")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Column(name = "address")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @Column(name = "max_capacity", nullable = false)
    @NotNull(message = "Maximum capacity is required")
    @jakarta.validation.constraints.Min(value = 1, message = "Maximum capacity must be at least 1")
    private Integer maxCapacity;

    public static class LocationDeserializer extends com.fasterxml.jackson.databind.JsonDeserializer<Location> {
        @Override
        public Location deserialize(com.fasterxml.jackson.core.JsonParser p, com.fasterxml.jackson.databind.DeserializationContext ctxt)
                throws java.io.IOException {
            System.out.println("DEBUG: LocationDeserializer invoked! Token: " + p.getCurrentToken() + ", getValueAsString(): " + p.getValueAsString());
            if (p.getCurrentToken() == com.fasterxml.jackson.core.JsonToken.VALUE_STRING) {
                String name = p.getText();
                System.out.println("DEBUG: VALUE_STRING name: " + name);
                if (name == null || name.trim().isEmpty()) {
                    System.out.println("DEBUG: Returning null for empty VALUE_STRING");
                    return null;
                }
                try {
                    Long id = Long.parseLong(name);
                    Location loc = new Location();
                    loc.setId(id);
                    System.out.println("DEBUG: Returning Location by ID: " + id);
                    return loc;
                } catch (NumberFormatException e) {
                    Location loc = new Location();
                    loc.setId(1L);
                    loc.setName(name);
                    loc.setMaxCapacity(1000);
                    System.out.println("DEBUG: Returning Location by Name: " + name);
                    return loc;
                }
            } else if (p.getCurrentToken() == com.fasterxml.jackson.core.JsonToken.START_OBJECT) {
                com.fasterxml.jackson.databind.JsonNode node = p.readValueAsTree();
                System.out.println("DEBUG: START_OBJECT node: " + node);
                Location loc = new Location();
                if (node.has("id") && !node.get("id").isNull()) {
                    loc.setId(node.get("id").asLong());
                }
                if (node.has("name") && !node.get("name").isNull()) {
                    loc.setName(node.get("name").asText());
                }
                if (node.has("description") && !node.get("description").isNull()) {
                    loc.setDescription(node.get("description").asText());
                }
                if (node.has("address") && !node.get("address").isNull()) {
                    loc.setAddress(node.get("address").asText());
                }
                if (node.has("maxCapacity") && !node.get("maxCapacity").isNull()) {
                    loc.setMaxCapacity(node.get("maxCapacity").asInt());
                } else if (node.has("max_capacity") && !node.get("max_capacity").isNull()) {
                    loc.setMaxCapacity(node.get("max_capacity").asInt());
                } else {
                    loc.setMaxCapacity(1000);
                }
                System.out.println("DEBUG: Returning Location object: " + loc);
                return loc;
            }
            System.out.println("DEBUG: Returning null fallback");
            return null;
        }
    }
}
