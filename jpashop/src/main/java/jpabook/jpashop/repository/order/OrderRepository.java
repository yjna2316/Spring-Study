package jpabook.jpashop.repository.order;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" +
                    orderSearch.getMemberName() + "%");
            criteria.add(name);

        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    public List<Order>   findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status"; }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName()); }
        return query.getResultList();
        }

    /** fetch join: 쿼리 한번만 날림 **/
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
               ).setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /**  join: 쿼리 1번만 날리지만 지연로딩 때문에 밖에서 N번 추가로 날리게 됨 **/
    /*    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join o.member m" +
                        " join o.delivery d", Order.class)
                .getResultList();
    }*/

    /**
     * 주의사항
     *
     * 1. 컬렉션(1대다) fetch join은 페이징 불가능
     *    모든 데이터를 메모리에 올린 후 메모리단에서 페이징 처리 한다. => 1대다 관계에서는 outofmemory 에러 가능
     *    ToOne 관계로만 이루어져 있을 때는 fetch join 사용 괜
     *    주문기준(order)가 아니라 다(order_item)기준으로 페이징 처리가 된다.
     *
     * 2. 컬렉션(1대다) fetch join은 1번만 할것
     *    2개 이상이 되면 1대다대다 가 되서 너무 많은 row를 가지고 올 수 있음.
     */

    /**
     * 컬렉션(1대다) fetch join
     * 문제점 : 1대다 관계의 경우, Join시 뻥튀기가 되서 나온다. (주문은 2개인데 DB는 4개 row 뱉음) -> api response도 2개 아닌 중복된 4개 반환
     * 해결책 : select distinct 이용
     * 효과 :   1. DB 쿼리 select distinct -> but 이전과 동일하게 4row 반환 (row내용 완전 동일하지 않기 때문)
     *         2. 애플리케이션 단에서 루트 엔티티가 (id) 동일하면 중복 제거
     */

    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d " +
                        "join fetch o.orderItems oi " +
                        "join fetch oi.item i", Order.class)
//                .setFirstResult(1)
//                .setMaxResults(100)
                .getResultList();
    }
}
