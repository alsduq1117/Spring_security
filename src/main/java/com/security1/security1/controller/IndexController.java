package com.security1.security1.controller;

import com.security1.security1.config.auth.PrincipalDetails;
import com.security1.security1.model.User;
import com.security1.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // View를 리턴 하겠다
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetails userDetails) { //DI(의존성 주입)

        System.out.println("/test/login ================");
        // 유저정보 방법1  Authentication DI, 다운캐스팅
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("authentication :" + principalDetails.getUser());

        // @AuthenticationPrincipal 을 통해 getUser
        System.out.println("userDetails:" + userDetails.getUser());
        return "세션 정보 확인하기";

    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication,
        @AuthenticationPrincipal OAuth2User oauth) { //DI(의존성 주입)

        System.out.println("/test/login ================");

        // 구글 로그인 하면 캐스팅하다가 에러가 난다
        // 따라서 Oauth2 로 다운캐스팅 한다.
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication :" + oauth2User.getAttributes());

        System.out.println("oauth2User:" + oauth.getAttributes());
        return "OAuth 세션 정보 확인하기";

    }

    //localhost:8080
    @GetMapping({"","/"})
    public String index(){
        // 머스테치 기본폴더 src/main/resources/
        // 뷰리졸버 설정: templates(prefix), .mustache(suffix)
        return "index";  //sec/main/resources/templates/index.mustache
    }


    //OAuth 로그인을 해도 PrincipalDetails
    //일반 로그인을 해도 PrincipalDetails
    @GetMapping("/user")
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails){
        System.out.println("principalDetails.getUser() = " + principalDetails.getUser());

        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin(){

        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager(){
        return "manager";
    }

    
    // 스프링시큐리티가 해당주소를 낚아 챈다 - SecurityConfig 파일 생성 후 작동 안함.
    @GetMapping("/loginForm")
    public String loginForm(){
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(){
        return "joinForm";
    }
    
    @PostMapping("/join")
    public String join(User user){
        System.out.println(user);
        user.setRole("ROLE.USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user); // 회원가입 잘됨, 비밀번호:1234 => 시큐리티로 로그인을 할 수 없음, 이유는 패스우드가 암호화가 안되있기 때문이다.

        return "redirect:/loginForm";  //redirect 붙이면 함수 호출
    }

    @Secured("ROLE_ADMIN")    //@EnableGlobalMethodSecurity(securedEnabled = true) 로 활성화
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //하나 Secured  여러개 PreAuthorize 사용 //PreAuthorize = 함수가 시작하기 전 , PostAuthorize 함수가 끝나고 난 후
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터정보";
    }

//
}
