package jpabook.jpashop.repository.order.query;

import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

// <단축키> f2 누르면 다음 에러로 간다.
@Repository
@RequiredArgsConstructor

// entity가 아닌 화면에 fit한 애들은 따로 분리시킨다.

/***
 * 컬렉션은 별도로 조회
 * Query: 루트 1번, 컬렉션 N번 실행 (루트: 최초 던지는 쿼리)
 * ToOne(N:1:1) 관계를 먼저 조회하고, ToMany(1:N) 관계는 각각 별도로 처리한다.
 * ** row 수가 증가하지 않는 ToOne 관계는 조인으로 최적화 하기 쉬우므로 한번에 조회하고, ToMany 관계는 최적화가 어려우므로 findOrderItems() 같은 별도 메소드로 조회
 * ** N+1 문제 발
 */
public class OrderQueryRepository {

    private final EntityManager em;

    // N+1 쿼리
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); // Query 1번 -> N개(2개)

        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId()); // Query N번(2번)
            o.setOrderItems(orderItems);
        });

        return result;
    }

    // [최적화] 쿼리 횟수를 1+N -> 2번으로 줄이기
    // where In절을 이용한다.
    public List<OrderQueryDto> findAllByDto_optimization() {
        // order 정보 가져옴  // Query 1번 -> 주문 2
        List<OrderQueryDto> result = findOrders();

        // order ids만 뽑아 온다.
        // [단축키] option+command+M (메소드로 따로 분리)
        List<Long> orderIds = toOrderIds(result);

        // orders에 포함된 상품 정보 가져온다.  // Query 1번 -> 1개
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(orderIds);

        // 주문을 하나씩 돌면서 상품 정보도 같이 넣어준다.
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        "from OrderItem  oi " +
                        "join oi.item i " +
                        "where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // 최적화: order id로 grouping 해준후, Map에 저장한다. // collect 이용하면 Map으로 바꿔준다.
        // 단축키 command+option+v (왼쪽에 자동으로 변수 생성)
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream().collect(groupingBy(OrderItemQueryDto::getOrderId));
        return orderItemMap;
    }

    private List<Long> toOrderIds(List<OrderQueryDto> result) {
        List<Long> orderIds = result.stream()
                .map(o -> o.getOrderId())
                .collect(Collectors.toList());

        return orderIds;
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return  em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findOrders() {
        // ?? 생성자에 list를 넣지 못한다. 1대다 컬렉션 관계이기 때문 ?? -> 잘 와닿지 않음 -> 아.. 앞에 처럼 2개가 아닌 중복된 4개를 가지고 오게 되나보다..
        // 엔티티나, address같은 value object만 사용할 수 있기 때문에, dto를 반환하려면 new를 이용해야 한다.
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
