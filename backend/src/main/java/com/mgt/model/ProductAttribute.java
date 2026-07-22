package com.mgt.model;

import javax.persistence.*;

@Entity(name = "product_attribute")
@Table(name = "product_attribute")
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String name;   // e.g. "Color", "Size", "Material"

    @Column(length = 500)
    private String values; // comma-separated e.g. "Red,Blue,Green"

    // ── Getters & Setters ──────────────────────────────────
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValues() { return values; }
    public void setValues(String values) { this.values = values; }
}
