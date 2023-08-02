package com.example.wonderwoman.delivery.repository;

import com.example.wonderwoman.building.entity.Building;
import com.example.wonderwoman.delivery.entity.DeliveryPost;
import com.example.wonderwoman.delivery.entity.ReqType;
import com.example.wonderwoman.delivery.entity.SanitarySize;
import com.example.wonderwoman.delivery.response.DeliveryResponseDto;
import com.example.wonderwoman.member.entity.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.wonderwoman.delivery.entity.QDeliveryPost.deliveryPost;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public Slice<DeliveryResponseDto> getSliceOfDelivery(Member member,
                                                         final String reqType,
                                                         final String building,
                                                         final List<SanitarySize> sanitarySize,
                                                         Pageable pageable) {
        JPAQuery<DeliveryPost> results = queryFactory.selectFrom(deliveryPost)
                .where(
                        reqTypeLike(reqType),
                        buildingLike(building),
                        sanitarySizeEq(sanitarySize)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1);

        for (Sort.Order o : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(deliveryPost.getType(), deliveryPost.getMetadata());
            results.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC :
                    Order.DESC, pathBuilder.get(o.getProperty())));
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

    public BooleanExpression reqTypeLike(final String reqType) {
        return StringUtils.hasText(reqType) ? deliveryPost.postReqType.eq(ReqType.resolve(reqType)) : null;
    }

    private BooleanExpression buildingLike(final String building) {
        return StringUtils.hasText(building) ? deliveryPost.building.eq(Building.resolve(building)) : null;
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
