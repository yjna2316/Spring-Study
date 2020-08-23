package hello.hellospringmock.domain;

import javax.persistence.*;

@Entity //JPA는 ORM기술이다. 객체와 관계형 데이터베이스를 매핑해줘야 한다. 어떻게? 어노테이션으로!
public class Member {

    /**
     * @Id: Pk 매핑
     * IDENTITY 전략: 값을 넣으면 DB가 Id 생성을 자동으로 해주는
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Q. 왜 int 가 이닌 Long으로 했을까???

    /**
     * @Column(name = "username") : 칼럼명이 다를 경우에도 어노테이션을 통해 맵핑이 가능하
     */
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
