package com.security1.security1.config.auth;

//시큐리티가 /login 주소 요청이 오면  낚아채서 로그인을 진행시킨다.
//로그인을 진행이 완료가 되면 시큐리티 session 을 만들어줍니다.(Security ContextHolder)
//오브젝트 => Authentication 타입 객체
//Authentication 안에 User 정보가 있어야 됨.
//User오브젝트 타입 => UserDetails 타입 객체

import com.security1.security1.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Data
//PrincipalDetails 에 UserDetails, OAuth2User 다중상속해서 2가지 타입 모두 Authentication 안에 들어갈 수 있도록 한다.
//Security Session => Authentication => UserDetails(PrincipalDetails)
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user; //콤포지션

    public PrincipalDetails(User user){
        this.user = user;
    }



    // 해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        //우리 사이트 1년동안 회원이 로그인을 안하면 휴면 계정으로 하기로 함.
        //user.getLoginDate();
        //현재 시간 - 로긴 시간 => 1년을 초과하면 return false;
        return true;
    }


    // OAuth2User @Override 부분
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
