package com.mgt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;

@Entity(name = "users")
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String user_code;
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String address;
    private String role; // admin | customer | vendor
    private String profileImage;

    // ✅ FIX: password field ছিলই না Users.java তে
    // Login করতে হলে password অবশ্যই লাগবে
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // ===== Getters & Setters =====

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUser_code() { return user_code; }
    public void setUser_code(String user_code) { this.user_code = user_code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
