package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.order.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 주문 api
 * 1. XToOne(ManyToOne, OneToOne) 관계 성능 최적화 방법
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController // rest api
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    /** 주문조회 API **/

    /** V1. 엔티티를 직접 노출시키는 방법: 비추. **/
    @GetMapping("/api/v1/simple-orders") // ? 무한루프 왜 걸리더라?
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName(); // LAZY 강제 초기화 => 필요한 정보만 내려줄 수 있다. (지연 로딩으로 인해 가짜 객체를 들고 있었으므로, 진짜 객체를 가지고 오기 위해 강제로 초기화시켜준다)
            order.getDelivery().getAddress(); // LAZY 강제 초기
        }
        return all; // 이렇게 객체(엔티티)를 그대로 반환하면 안된다
    }

    /** V2. 엔티티를 DTO로 변환 **/
    /**
     * 지연 로딩 조회로 인해 N + 1 발생 가능 (성능 저하 원인)
     * 회원 1명이 주문을 2번 했다면 쿼리 횟수는 => 주문정보(1) + 회원(N) + 배송(N) 이 경우, 총 5번의 쿼리가 날라간다.
     **/
    @GetMapping("/api/v2/simple-orders")
    public List<OrderSimpleQueryDto> ordersV2() {

        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        // 주문 횟수만큼 루프를 돈다. N+1
        // 지연 로딩은 DB를 바로 찌르지 않고 영속성 컨텍스트를 먼저 찌른다.
        // 영속성 컨텍스트에 해당 정보(회원 정보, 배송 정보)가 없을 때 DB를 찌르게 된다.
        List<OrderSimpleQueryDto> result = orders.stream().map(o -> new OrderSimpleQueryDto(o)).collect(Collectors.toList());

        return result;
        // return orderRepository.findAllByString(new OrderSearch()).stream().map(OrderSimpleQueryDto::new).collect(toList());
    }

    /** V3. 엔티티를 DTO로 변환 - fetch join 최적화 **/
    /**
     * 많은 정보를 한번에 가져오기 때문에 v4 보다 재용성은 높지만, 필요한 정보만 추출하는 v4보다는 성능이 떨어진다.
     * 하지만, 트래픽이 많지 않다면 두 버전의 성능 차이는 그리 크지 않다.
     * select 절 보다는 join 여러개 하거나 인덱스 타는 여부에 성능은 크게 영향을 받기 때문이다.
     */
    @GetMapping("/api/v3/simple-orders")
    public List<OrderSimpleQueryDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(0,0); // fetch join으로 쿼리 1번만 날린다. return type: Order
        List<OrderSimpleQueryDto> result = orders.stream().map(OrderSimpleQueryDto::new).collect(Collectors.toList()); // response render
        return result;
    }

    /** V4. DTO로 바로 조회 **/
    /**
     * 원하는 데이터만 select해서 가져는 장점이 있으나 화면 의존성이 있어 재사용성이 낮다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> orderV4() {
        return orderSimpleQueryRepository.findOrderDtos(); // return type: DTO
    }
}
