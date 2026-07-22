package com.mgt.model;

import javax.persistence.*;

@Entity(name = "settings")
@Table(name = "settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String section;

    @Column(nullable = false, length = 100)
    private String settingKey;

    @Column(columnDefinition = "TEXT")
    private String settingValue;

    public Settings() {}

    public Settings(String section, String settingKey, String settingValue) {
        this.section      = section;
        this.settingKey   = settingKey;
        this.settingValue = settingValue;
    }

    // Getters & Setters
    public Long   getId()           { return id; }
    public void   setId(Long id)    { this.id = id; }

    public String getSection()                  { return section; }
    public void   setSection(String section)    { this.section = section; }

    public String getSettingKey()               { return settingKey; }
    public void   setSettingKey(String key)     { this.settingKey = key; }

    public String getSettingValue()             { return settingValue; }
    public void   setSettingValue(String value) { this.settingValue = value; }
}
