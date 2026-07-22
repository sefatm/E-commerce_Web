package com.mgt.model;

import javax.persistence.*;
import java.time.LocalDate;

@Table(name = "add_product")
@Entity(name = "product")
public class AddProduct {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 255)
    private String name;

    @Column(length = 255)
    private String nameBn;
    @Column(length = 50)
    private String unit = "piece";
    private Double weight;
    private Integer minimumOrderQuantity = 1;
    private LocalDate productionDate;
    private LocalDate expiryDate;
    private Boolean organic = false;
    private Boolean returnAvailable = true;
    @Column(length = 500)
    private String deliveryAreas;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private Double price;

    private Double salePrice;
    private Integer stock = 0;
    @Column(length = 255)
    private String sku;
    private Boolean isFeatured = false;
    private Boolean isOnSale = false;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private ProductCategory category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 50)
    private String status;
    @Column(length = 50)
    private String approvalStatus = "PENDING";
    @Lob
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;
    @Column(length = 500)
    private String originArea;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String artisanStory;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String craftProcess;
    private Boolean preOrderAvailable = false;
    private Integer estimatedProductionDays;
    @Column(length = 500)
    private String image;
    private LocalDate createdAt;

    // ===== Getters & Setters =====

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public String getNameBn() { return nameBn; }
    public void setNameBn(String nameBn) { this.nameBn = nameBn; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Integer getMinimumOrderQuantity() { return minimumOrderQuantity; }
    public void setMinimumOrderQuantity(Integer minimumOrderQuantity) { this.minimumOrderQuantity = minimumOrderQuantity; }
    public LocalDate getProductionDate() { return productionDate; }
    public void setProductionDate(LocalDate productionDate) { this.productionDate = productionDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Boolean getOrganic() { return organic; }
    public void setOrganic(Boolean organic) { this.organic = organic; }
    public Boolean getReturnAvailable() { return returnAvailable; }
    public void setReturnAvailable(Boolean returnAvailable) { this.returnAvailable = returnAvailable; }
    public String getDeliveryAreas() { return deliveryAreas; }
    public void setDeliveryAreas(String deliveryAreas) { this.deliveryAreas = deliveryAreas; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getSalePrice() { return salePrice; }
    public void setSalePrice(Double salePrice) { this.salePrice = salePrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }

    public Boolean getIsOnSale() { return isOnSale; }
    public void setIsOnSale(Boolean isOnSale) { this.isOnSale = isOnSale; }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getOriginArea() { return originArea; }
    public void setOriginArea(String originArea) { this.originArea = originArea; }

    public String getArtisanStory() { return artisanStory; }
    public void setArtisanStory(String artisanStory) { this.artisanStory = artisanStory; }

    public String getCraftProcess() { return craftProcess; }
    public void setCraftProcess(String craftProcess) { this.craftProcess = craftProcess; }

    public Boolean getPreOrderAvailable() { return preOrderAvailable; }
    public void setPreOrderAvailable(Boolean preOrderAvailable) { this.preOrderAvailable = preOrderAvailable; }

    public Integer getEstimatedProductionDays() { return estimatedProductionDays; }
    public void setEstimatedProductionDays(Integer estimatedProductionDays) { this.estimatedProductionDays = estimatedProductionDays; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
}
