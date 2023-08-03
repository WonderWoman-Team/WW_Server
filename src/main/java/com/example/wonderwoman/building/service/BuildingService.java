package com.example.wonderwoman.building.service;

import com.example.wonderwoman.building.entity.Building;
import com.example.wonderwoman.member.entity.School;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingService {

    // 학교 정보를 기반으로 건물 목록을 조회하는 메서드
    public List<Building> getBuildingsBySchool(School school) {
        List<Building> buildings = school.getBuildingList();
        System.out.println("회원의 학교:" + school + "에 해당하는 건물 목록: " + buildings);
        return buildings;
    }

}
