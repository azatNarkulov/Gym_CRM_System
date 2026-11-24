package com.epam.workload.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trainer_workloads", uniqueConstraints = {
        @UniqueConstraint(name = "uk_trainer_workload_username", columnNames = "trainer_username")
})
public class TrainerWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trainer_username", nullable = false)
    private String username;

    @Column(name = "trainer_first_name", nullable = false)
    private String firstName;

    @Column(name = "trainer_last_name", nullable = false)
    private String lastName;

    @Column(name = "trainer_active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MonthlyWorkload> monthlyWorkloads = new ArrayList<>();

    protected TrainerWorkload() {
    }

    public TrainerWorkload(String username, String firstName, String lastName, boolean active) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }

    public void addMonthlyWorkload(MonthlyWorkload workload) {
        monthlyWorkloads.add(workload);
        workload.setTrainer(this);
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isActive() {
        return active;
    }

    public List<MonthlyWorkload> getMonthlyWorkloads() {
        return monthlyWorkloads;
    }

    public void updateProfile(String firstName, String lastName, boolean active) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }
}
