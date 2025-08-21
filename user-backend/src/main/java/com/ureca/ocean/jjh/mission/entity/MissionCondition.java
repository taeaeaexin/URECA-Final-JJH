package com.ureca.ocean.jjh.mission.entity;

import com.ureca.ocean.jjh.user.entity.Attendance;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Data
@Table(name = "mission_conditions")
public class MissionCondition {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "store_usage_id")
    private UUID storeUsageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = true)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = true)
    private Attendance attendance;

    @Column(name = "require_type")
    private String requireType;

    @Column(name = "require_value")
    private int requireValue;
}
