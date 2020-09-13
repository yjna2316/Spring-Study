package jpabook.jpashop.api;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.order.query.*;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
/**
 * XtoMany 조회 성능 최적화
 */
public class OrderApiController {
    private final OrderRepository orderRepository; // 순수 엔티티
    private final OrderQueryRepository orderQueryRepository; // 화면 데이터

    // v1. 엔티티를 그대로 반환
    // 엔티티가 변하면 API 스펙이 변함
    // 양방향 연관 관계 문제 - 무한루프
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // 지연로딩 강제 초기화 필요
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    /** 엔티티를 DTO로 변환 - fetch join X **/
    /** N+1 성능 문제 발생 가능 **/
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        // 각 order 엔티티를 loop를 돌면서 DTO로 변환해준다.
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    /** 엔티티를 DTO로 변환 - fetch join 최적화 **/
    /** XtoMany 관계 경우, 중복 데이터 반환 && order 기준 페이징 불가 **/
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithItem(); // Query 1번 BUT 데이터 중복 (4row) -> select 절에 distinct를 추가한다.
        List<OrderDto> result = orders.stream() // 루프 4번 돈다  -> 중복발생
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    /** 엔티티를 DTO로 변환 - 페이징과 한계 돌파 **/
    /**
     *  1. ToOne(OtoOne, MtoOne) 관계는 모두 fetch join한다. (ToOne 관계는 row수를 증가시키지 않으므로 페이징 쿼리에 영향을 주지 않는다.)
     *  2. 컬렉션은 지연 로딩으로 조회한다.
     *  3. 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size, @BatchSize를 적용한다.
     *     이 옵션을 사용하면 컬렉션이나, 프록시 객체를 설정한 size만큼 한꺼번에 조회할 수 있다. (핵심은 IN 쿼리)
     *     결과, 1:N:M 쿼리를 1:1:1 쿼리로 바꿔준다!!
     **/
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit); // xto1 관계이므로 paging 쿼리로 가져와도 괜찮다.

        // 설정한 batch 사이즈 상관없이 전체 데이터를 로딩해야하므로 메모리 사용량은 같다. 
        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return result;
    }

    /**  JPA에서 DTO로 직접 조회 - 컬렉션 조회  **/
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> orderV4() {
        return orderQueryRepository.findOrderQueryDtos();
    } // Query 1+N번

    /**  JPA에서 DTO로 직접 조회 - 컬렉션 조회 최적화  **/
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> orderV5() {
        return orderQueryRepository.findAllByDto_optimization(); // Query 2번
    }

    /** JPA에서 DTO로 직접 조회 - 컬렉션 조회 flat data 최적화 **/
    // order 기준 페이징 불가. orderitem 기준 페이징은 가능
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> orderV6() { // v5와 동일한 api 스펙으로 내려주기 위해 OrderQueryDto로 반환했다.
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat(); // Query 1번

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                        o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()), mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                        o.getItemName(), o.getOrderPrice(), o.getCount()), toList()) )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    /**
     * 엔티티를 DTO로 변환하기 위한 생성자 & 강제 지연 로딩
     *
     * 지연 로딩으로 너무 많은 쿼리 던질 수 있다. N+1 쿼리
     * 필요한 데이터만 정제 가능
     * Q. 왜 static class?
     **/
    @Getter // @Data 안쓰면 접근 제한 문제로 serialize 과정에서 이슈 생김 : InvalidDefinitionException: No serializer found for class
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // private List<OrderItem> orderItems; // ** DTO는 엔티티와의 의존성을 아예 끊어야 한다. ** 주의!!
        private List<OrderItemDto> orderItems;

        // 지연로딩 초기화
        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());
        }
    }

    @Getter
    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
        }
    }
}
