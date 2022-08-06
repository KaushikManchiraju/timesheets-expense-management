/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.timesheets.entity;

import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.security.entity.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Table(name = "TS_EXPENSES")
@Entity(name = "ts_Expenses")
public class Expenses extends StandardEntity implements ExpensesBase {
    private static final long serialVersionUID = -2719363358925708470L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID")
    @NotNull
    protected User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TASK_ID")
    protected Task task;

    @NotNull
    @Column(name = "AMOUNT", nullable = false)
    protected Double amount;

    @Column(name = "DATE_", nullable = false)
    @NotNull
    @Temporal(TemporalType.DATE)
    protected Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_TYPE_ID")
    @OnDeleteInverse(DeletePolicy.DENY)
    protected ActivityType activityType;

    @ManyToMany
    @JoinTable(name = "TS_EXPENSES_TAG_LINK",
            joinColumns = @JoinColumn(name = "EXPENSES_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    protected Set<Tag> tags;

    @Column(name = "NOTES")
    protected String notes;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Column(name = "REJECTION_REASON")
    protected String rejectionReason;

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public ExpensesStatus getStatus() {
        return status == null ? null : ExpensesStatus.fromId(status);
    }

    public void setStatus(ExpensesStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}