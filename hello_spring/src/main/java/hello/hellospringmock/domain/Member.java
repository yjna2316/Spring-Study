package hello.hellospringmock.domain;

public class Member {
    private Long id; // Q. 왜 int 가 이닌 Long으로 했을까???
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
