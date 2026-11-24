package com.epam.workload.domain;

import com.epam.workload.persistence.DurationAttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.Duration;

@Entity
@Table(name = "monthly_workloads", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_trainer_workload_year_month",
                columnNames = {"trainer_id", "workload_year", "workload_month"})
})
public class MonthlyWorkload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "trainer_id", nullable = false)
    private TrainerWorkload trainer;

    @Column(name = "workload_year", nullable = false)
    private int year;

    @Column(name = "workload_month", nullable = false)
    private int month;

    @Convert(converter = DurationAttributeConverter.class)
    @Column(name = "training_summary_duration", nullable = false)
    private Duration trainingSummaryDuration = Duration.ZERO;

    protected MonthlyWorkload() {
    }

    public MonthlyWorkload(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public TrainerWorkload getTrainer() {
        return trainer;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public Duration getTrainingSummaryDuration() {
        return trainingSummaryDuration;
    }

    public void setTrainer(TrainerWorkload trainer) {
        this.trainer = trainer;
    }

    public void addDuration(Duration duration) {
        trainingSummaryDuration = trainingSummaryDuration.plus(duration);
    }

    public void subtractDuration(Duration duration) {
        trainingSummaryDuration = trainingSummaryDuration.minus(duration);
    }
}
