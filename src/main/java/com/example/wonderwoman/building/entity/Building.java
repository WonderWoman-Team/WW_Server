package com.example.wonderwoman.building.entity;

import com.example.wonderwoman.member.entity.School;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Getter
@Table(name = "building")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "building_id")
    private Long buildingId;

    @NotNull
    @Column(name = "school", length = 20)
    @Enumerated(EnumType.STRING)
    private School school;

    @NotNull
    @Column(name = "building_name", length = 50)
    private BuildingName buildingName;

    @Builder
    public Building(School school, BuildingName buildingName) {
        this.school = school;
        this.buildingName = buildingName;
    }
}
