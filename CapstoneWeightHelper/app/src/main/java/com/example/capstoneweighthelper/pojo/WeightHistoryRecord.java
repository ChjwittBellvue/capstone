package com.example.capstoneweighthelper.pojo;

public class WeightHistoryRecord {
    private Integer id;
    private Integer userId;
    private Double entryWeight;
    private Long entryDate;
    private String pictureLocation;
    private String permanentRecord;

    // Constructors
    public WeightHistoryRecord() {
        this.id = null;
        this.entryWeight = 0.0;
        this.entryDate = null;
        this.pictureLocation = null;
        this.permanentRecord = null;
    }

    public WeightHistoryRecord(Integer id, Integer userId, Double entryWeight, Long entryDate, String pictureLocation, String permanentRecord) {
        this.id = id;
        this.userId = userId;
        this.entryWeight = entryWeight;
        this.entryDate = entryDate;
        this.pictureLocation = pictureLocation;
        this.permanentRecord = permanentRecord;
    }

    // Mutators
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getEntryWeight() {
        return entryWeight;
    }

    public void setEntryWeight(Double entryWeight) {
        this.entryWeight = entryWeight;
    }

    public Long getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Long entryDate) {
        this.entryDate = entryDate;
    }

    public String getPictureLocation() {
        return pictureLocation;
    }

    public void setPictureLocation(String pictureLocation) {
        this.pictureLocation = pictureLocation;
    }

    public String getPermanentRecord() {
        return permanentRecord;
    }

    public void setPermanentRecord(String permanentRecord) {
        this.permanentRecord = permanentRecord;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // toString
    @Override
    public String toString() {
        return "WeightHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", entryWeight=" + entryWeight +
                ", entryDate='" + entryDate + '\'' +
                ", pictureLocation='" + pictureLocation + '\'' +
                ", permanentRecord='" + permanentRecord + '\'' +
                '}';
    }
}
