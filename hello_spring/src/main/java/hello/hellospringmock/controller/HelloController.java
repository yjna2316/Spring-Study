package hello.hellospringmock.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "this is hello view via Hello Controller !!");
        return "hello";
    }

    // 템플릿 엔진 -> view(렌더링이된(코딩) html)를 클라에 전달해준다
    @GetMapping("hello-mvc")  //외부에서 파라메터를 받아온다.
    public String helloMvc(@RequestParam("name") String name, Model model) { // 쿼리 스트링으로 파라메터를 넘겨줘야한다.
        model.addAttribute("name", name);
        return "hello-template"; // hello-template라는 이름과 맵핑되는 view를 찾아(뷰 리졸버) html로 변환하여 던져준다.(템플릿엔진)
    }

    @GetMapping("hello-string")
    @ResponseBody
    /**
     *  @ResponseBody 사용시 일어나는
     *  1.viewResolver를 사용하지 않고 -> view 생성 필요 x
     *  2. HTTP response BODY에 문자 내용을 그대 반환하게 된다.
     */
    public String helloString(@RequestParam("name") String name) {
        return  "hello! i'm using @ResponseBody 그리고 just returning string in the http body  " + name;
    }

    // 객체 반환시 객체가 JSON으로 변환된다.
    @GetMapping("hello-api")
    @ResponseBody
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }
    // command + N : auto complete method
    static class Hello {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
