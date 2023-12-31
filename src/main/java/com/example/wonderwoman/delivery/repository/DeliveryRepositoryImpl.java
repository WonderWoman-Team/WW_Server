package com.example.wonderwoman.delivery.repository;

import com.example.wonderwoman.delivery.entity.Building;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.ReqType;
import com.example.wonderwoman.delivery.entity.SanitarySize;
import com.example.wonderwoman.delivery.response.DeliveryResponseDto;
import com.example.wonderwoman.member.entity.Member;
import com.example.wonderwoman.member.entity.School;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.wonderwoman.delivery.entity.QDeliveryPost.deliveryPost;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public Slice<DeliveryResponseDto> getSliceOfDelivery(Member member,
                                                         final String reqType,
                                                         final String school,
                                                         final List<Building> building,
                                                         final List<SanitarySize> sanitarySize,
                                                         final Long lastId,
                                                         Pageable pageable) {
        JPAQuery<DeliveryPost> results = queryFactory.selectFrom(deliveryPost)
                .where(
                        reqTypeLike(reqType),
                        schoolLike(school),
                        buildingLike(building),
                        sanitarySizeEq(sanitarySize),
                        ltDeliveryPostId(lastId)
                )
                .offset(pageable.getOffset()) // 기존 offset 설정 유지
                .limit(pageable.getPageSize() + 1);

        // 정렬 정보가 있을 경우 정렬 적용
        if (pageable.getSort().isSorted()) {
            PathBuilder pathBuilder = new PathBuilder(deliveryPost.getType(), deliveryPost.getMetadata());
            for (Sort.Order order : pageable.getSort()) {
                results.orderBy(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
            }
        }

        List<DeliveryResponseDto> contents = results.fetch()
                .stream()
                .map(o -> DeliveryResponseDto.of(o, o.isWrittenPost(member)))
                .collect(Collectors.toList());

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    public BooleanExpression ltDeliveryPostId(@Nullable Long lastId) {
        return lastId == null ? null : deliveryPost.id.lt(lastId);
    }

    public BooleanExpression reqTypeLike(final String reqType) {
        return StringUtils.hasText(reqType) ? deliveryPost.postReqType.eq(ReqType.resolve(reqType)) : null;
    }

    public BooleanExpression schoolLike(final String school) {
        return StringUtils.hasText(school) ? deliveryPost.school.eq(School.resolve(school)) : null;
    }

    private BooleanBuilder buildingLike(final List<Building> buildingList) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (buildingList.isEmpty()) return booleanBuilder;

        List<BooleanExpression> expressions = new ArrayList<>();

        for (Building building : buildingList) {
            booleanBuilder.or(deliveryPost.building.contains(building));
        }

        return booleanBuilder;
    }

    private BooleanBuilder sanitarySizeEq(final List<SanitarySize> sizeList) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (sizeList.isEmpty()) return booleanBuilder;

        for (SanitarySize sanitarySize : sizeList) {
            booleanBuilder.or(deliveryPost.sanitarySize.eq(sanitarySize));
        }

        return booleanBuilder;
    }
}
