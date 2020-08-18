package hello.hellospringmock.domain;

// 웹 등록 화면에서 데이터를 전달 받을 폼 객체
public class MemberForm {
    // option + enter
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } // spring이 setName을 호출해서 입력한 name을 저장한다.
}
