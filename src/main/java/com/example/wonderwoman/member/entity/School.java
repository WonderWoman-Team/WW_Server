package com.example.wonderwoman.member.entity;

import com.example.wonderwoman.delivery.entity.Building;
import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum School {
    EWHA("이화여자대학교",
            Arrays.asList(
                    Building.ECC,
                    Building.HakGwan,
                    Building.POSCO,
                    Building.StudentUnion_ehwu,
                    Building.InternationalEdu,
                    Building.EwhaSkTelecom,
                    Building.EwhaShinsegae,
                    Building.HumanEcology,
                    Building.WelchRyangAuditorium,
                    Building.MusicBuilding,
                    Building.ArtDesignA,
                    Building.ArtDesignB,
                    Building.PhysicalEduA,
                    Building.PhysicalEduB,
                    Building.PhysicalEduC,
                    Building.Helen,
                    Building.PharmaceuticalA,
                    Building.PharmaceuticalB,
                    Building.ScienceA,
                    Building.ScienceB,
                    Building.ScienceC,
                    Building.ScienceD,
                    Building.EducationA,
                    Building.EducationB,
                    Building.AsanEngineering,
                    Building.NewEngineering,
                    Building.Library
            )),
    SOOKMYUNG("숙명여자대학교",
            Arrays.asList(
                    Building.Sunheon,
                    Building.Myungshin,
                    Building.Prime,
                    Building.Veritas,
                    Building.Saehim,
                    Building.Administration,
                    Building.SookmyungResidence,
                    Building.ArenaTheater,
                    Building.Renaissance,
                    Building.CollegeOfMusic,
                    Building.CollegeOfPharmacy,
                    Building.CollegeOfFineArts,
                    Building.CentennialHall,
                    Building.CollegeOfScience,
                    Building.MulticomplexHall,
                    Building.SnowflakeSquare,
                    Building.SaebitHall,
                    Building.StudentUnion_smwu,
                    Building.Library
            ));

    private static final Map<String, School> schoolMap = Stream.of(values())
            .collect(Collectors.toMap(School::getSchoolName, Function.identity()));

    @JsonValue
    private final String schoolName;

    private List<Building> buildingList;

    School(String schoolName, List<Building> list) {
        this.schoolName = schoolName;
        this.buildingList = list;
    }


    @JsonCreator
    public static School resolve(String schoolName) {
        return Optional.ofNullable(schoolMap.get(schoolName))
                .orElseThrow(() -> new WonderException(ErrorCode.VALUE_NOT_IN_OPTION));
    }

    public boolean hasBuilding(Building building) {
        return buildingList.stream()
                .anyMatch(b -> b == building);
    }

}
