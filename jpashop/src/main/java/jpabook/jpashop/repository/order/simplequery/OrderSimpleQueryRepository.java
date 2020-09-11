package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
/**
 * OrderRepository에 있던 함수를 클래스로 따로 빼왔다.
 * OrderRepository에서는 순수 엔티티만 조회하도록 해야하는데 얘는 화면 의존성이 있는 쿼리를 조회하고 있기 때문이다.
 */
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDto> findOrderDtos() {
        // 엔티티나, address같은 value object만 사용할 수 있기 때문에, dto를 반환하려면 new를 이용해야 한다.
        return em.createQuery(
                "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
