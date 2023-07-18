package com.example.wonderwoman.building.entity;

import com.example.wonderwoman.exception.ErrorCode;
import com.example.wonderwoman.exception.WonderException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Building {
    ECC("이화캠퍼스복합단지(ECC)"),
    HakGwan("학관"),
    POSCO("포스코관"),
    StudentUnion_ehwu("학생문화관"),
    InternationalEdu("국제교육관"),
    EwhaSkTelecom("이화SK텔레콤관"),
    EwhaShinsegae("이화신세관"),
    HumanEcology("생활환경관"),
    WelchRyangAuditorium("대강당"),
    MusicBuilding("음악관"),
    ArtDesignA("조형예술관 A동"),
    ArtDesignB("조형예술관 B동"),
    PhysicalEduA("체육관 A동"),
    PhysicalEduB("체육관 B동"),
    PhysicalEduC("체육관 C동"),
    Helen("헬렌관"),
    PharmaceuticalA("약학관 A동"),
    PharmaceuticalB("약학관 B동"),
    ScienceA("종합과학관 A동"),
    ScienceB("종합과학관 B동"),
    ScienceC("종합과학관 C동"),
    ScienceD("종합과학관 D동"),
    EducationA("교육관 A동"),
    EducationB("교육관 B동"),
    AsanEngineering("아산공학관"),
    NewEngineering("신공학관"),

    Sunheon("순헌관"),
    Myungshin("명신관"),
    Prime("프라임관"),
    Veritas("진리관"),
    Saehim("새힘관"),
    Administration("행정관"),
    SookmyungResidence("명재관"),
    ArenaTheater("원형극장"),
    Renaissance("르네상스플라자/숙명여자대학교박물관"),
    CollegeOfMusic("음악대학"),
    CollegeOfPharmacy("약학대학"),
    CollegeOfFineArts("미술대학"),
    CentennialHall("백주년기념관"),
    CollegeOfScience("과학관"),
    MulticomplexHall("다목적관"),
    SnowflakeSquare("눈꽃광장"),
    SaebitHall("새빛관"),
    StudentUnion_smwu("학생회관"),

    Library("중앙도서관");

    private static final Map<String, Building> buildingMap = Stream.of(values())
            .collect(Collectors.toMap(Building::getValue, Function.identity()));

    @JsonValue
    private final String value;

    @JsonCreator
    public static Building resolve(String value) {
        return Optional.ofNullable(buildingMap.get(value))
                .orElseThrow(() -> new WonderException(ErrorCode.VALUE_NOT_IN_OPTION));
    }
}