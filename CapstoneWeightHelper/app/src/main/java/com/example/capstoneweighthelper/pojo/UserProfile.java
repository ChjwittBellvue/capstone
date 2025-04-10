package com.example.capstoneweighthelper.pojo;

import java.util.Date;

public class UserProfile {
    private Integer id;
    private String name;
    private Integer currentWeight;
    private Integer goalWeight;
    private Long goalDate;
    private String gender;
    private Integer heightFeet;
    private Integer heightInches;

    public UserProfile() {
    }

    public UserProfile(Integer id, String name, Integer currentWeight, Integer goalWeight, Long goalDate,
                       String gender, Integer heightFeet, Integer heightInches) {
        this.id = id;
        this.name = name;
        this.currentWeight = currentWeight;
        this.goalWeight = goalWeight;
        this.goalDate = goalDate;
        this.gender = gender;
        this.heightFeet = heightFeet;
        this.heightInches = heightInches;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Integer currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Integer getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(Integer goalWeight) {
        this.goalWeight = goalWeight;
    }

    public Long getGoalDate() {
        return goalDate;
    }

    public void setGoalDate(Long goalDate) {
        this.goalDate = goalDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getHeightFeet() {
        return heightFeet;
    }

    public void setHeightFeet(Integer heightFeet) {
        this.heightFeet = heightFeet;
    }

    public Integer getHeightInches() {
        return heightInches;
    }

    public void setHeightInches(Integer heightInches) {
        this.heightInches = heightInches;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id=" + id +
                ", name=" + name +
                ", currentWeight='" + currentWeight + '\'' +
                ", goalWeight='" + goalWeight + '\'' +
                ", goalDate=" + goalDate +
                ", gender='" + gender + '\'' +
                ", heightFeet=" + heightFeet +
                ", heightInches=" + heightInches +
                '}';
    }
}

