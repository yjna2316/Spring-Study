package com.jayy.book.springboot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@EnableJpaAuditing // JPA Auditing 활성화
@SpringBootApplication /**
 * 스프링 Bean 읽기와 생성 모두 자동으로 설정된다.
 * @SpringBootApplication이 있는 위치부터 설정을 읽어가기 때문에 이 클래스는 항상 최상단에 위치
 **/
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
