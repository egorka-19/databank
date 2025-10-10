package com.example.databank.Model;

import java.io.Serializable;

public class Task implements Serializable {
    private String id;
    private Long taskNumber;
    private String childPhone;
    private String title;
    private String category; // Учеба, Дом, Спорт
    private Integer reward; // сумма вознаграждения
    private String status; // in_progress, pending_review, completed, rejected
    private String imageUrl; // доказательство выполнения
    private Long createdAt;
    private Long updatedAt;
    private String rejectionReason;

    public Task() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getTaskNumber() { return taskNumber; }
    public void setTaskNumber(Long taskNumber) { this.taskNumber = taskNumber; }

    public String getChildPhone() { return childPhone; }
    public void setChildPhone(String childPhone) { this.childPhone = childPhone; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getReward() { return reward; }
    public void setReward(Integer reward) { this.reward = reward; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Long updatedAt) { this.updatedAt = updatedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}


