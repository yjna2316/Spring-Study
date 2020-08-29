package jpabook.jpashop.domain;


import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.*;
import java.util.*;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    private Long id;
    private String username;



}


//@Entity
//@Getter @Setter
//public class Member {
//    @Id @GeneratedValue
//    @Column(name = "member_id")
//    private Long id;
//
//    private String name;
//
//    @Embedded // 내장 타입, Value Type
//    private Address address;
//
//    @OneToMany(mappedBy = "member") // 회원은 주문(Orders)의 'member' 변수를 통 맵핑된다.
//    private List<Order> orders = new ArrayList<>(); // 주문이 여러개니까 리스트로 받아오는구나
//}
