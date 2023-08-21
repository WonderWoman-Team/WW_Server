package com.example.wonderwoman.delivery.entity;

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
    ECC("ECC(이화캠퍼스복합단지)"),
    HakGwan("학관"),
    POSCO("포스코관"),
    StudentUnion_ehwu("학문관(학생문화관)"),
    InternationalEdu("국교관(국제교육관)"),
    EwhaSkTelecom("SK관(이화SK텔레콤관)"),
    EwhaShinsegae("경영관(이화신세계관)"),
    HumanEcology("생활관(생활환경관)"),
    WelchRyangAuditorium("대강당"),
    MusicBuilding("음악관"),
    ArtDesignA("조형A(조형예술관A동)"),
    ArtDesignB("조형B(조형예술관B동)"),
    PhysicalEduA("체육A(체육관A동)"),
    PhysicalEduB("체육B(체육관B동)"),
    PhysicalEduC("체육C(체육관C동)"),
    Helen("헬렌관"),
    PharmaceuticalA("약학관A동"),
    PharmaceuticalB("약학관B동"),
    ScienceA("종과A(종합과학관A동)"),
    ScienceB("종과B(종합과학관B동)"),
    ScienceC("종과C(종합과학관C동)"),
    ScienceD("종과D(종합과학관D동)"),
    EducationA("교육A(교육관A동)"),
    EducationB("교육B(교육관B동)"),
    AsanEngineering("공학A(아산공학관)"),
    NewEngineering("공학B(신공학관)"),

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
    Library_EWHA("중도(중앙도서관)"),
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