package com.mgt.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity(name="seller")
@Table(name="seller")
public class Seller {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String shopName;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private Users user;
	@Column(length = 50)
	private String nidNo;
	@Column(length = 30)
	private String phone;
	@Column(length = 150)
	private String email;
	@Column(length = 500)
	private String address;
	@Column(length = 120)
	private String district;
	@Lob
	@Column(columnDefinition = "TEXT")
	private String artisanStory;
	@Lob
	@Column(columnDefinition = "TEXT")
	private String craftProcess;
	@Column(length = 120)
	private String productCategory;
	@Column(length = 80)
	private String businessType;
	@Column(length = 50)
	private String paymentMethod;
	@Column(length = 80)
	private String paymentNumber;
	@Column(length = 255)
	private String profilePhoto;
	@Column(length = 255)
	private String nidFrontImage;
	@Column(length = 255)
	private String nidBackImage;
	private Boolean verified = false;
	private Double rating = 0.0;
	private Integer reviewCount = 0;
	private Double commissionRate = 10.0;
	@Column(length = 30)
	private String status = "PENDING";
	@Lob
	@Column(columnDefinition = "TEXT")
	private String rejectionReason;
	private LocalDate createdAt;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShopName() {
		return shopName;
	}
	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	public String getNidNo() {
		return nidNo;
	}
	public void setNidNo(String nidNo) {
		this.nidNo = nidNo;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getArtisanStory() {
		return artisanStory;
	}
	public void setArtisanStory(String artisanStory) {
		this.artisanStory = artisanStory;
	}
	public String getCraftProcess() {
		return craftProcess;
	}
	public void setCraftProcess(String craftProcess) {
		this.craftProcess = craftProcess;
	}
	public String getProductCategory() { return productCategory; }
	public void setProductCategory(String productCategory) { this.productCategory = productCategory; }
	public String getBusinessType() { return businessType; }
	public void setBusinessType(String businessType) { this.businessType = businessType; }
	public String getPaymentMethod() { return paymentMethod; }
	public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
	public String getPaymentNumber() { return paymentNumber; }
	public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }
	public String getProfilePhoto() { return profilePhoto; }
	public void setProfilePhoto(String profilePhoto) { this.profilePhoto = profilePhoto; }
	public String getNidFrontImage() { return nidFrontImage; }
	public void setNidFrontImage(String nidFrontImage) { this.nidFrontImage = nidFrontImage; }
	public String getNidBackImage() { return nidBackImage; }
	public void setNidBackImage(String nidBackImage) { this.nidBackImage = nidBackImage; }
	public Boolean getVerified() {
		return verified;
	}
	public void setVerified(Boolean verified) {
		this.verified = verified;
	}
	public Double getRating() {
		return rating;
	}
	public void setRating(Double rating) {
		this.rating = rating;
	}
	public Integer getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}
	public Double getCommissionRate() {
		return commissionRate;
	}
	public void setCommissionRate(Double commissionRate) {
		this.commissionRate = commissionRate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRejectionReason() {
		return rejectionReason;
	}
	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
	public LocalDate getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}
	
	
}
