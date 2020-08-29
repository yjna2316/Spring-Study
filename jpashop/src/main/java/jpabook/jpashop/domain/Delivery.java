package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    /**
     * 일대일 관계의 경우, 보통 접근이 많은 DB에 FK를 넣는다.
     */
    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded    // value type
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; // ENUM [READY, COMP]
}
