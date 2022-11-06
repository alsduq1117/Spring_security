package com.security1.security1.config.oauth;

import com.security1.security1.config.auth.PrincipalDetails;
import com.security1.security1.config.oauth.provider.FacebookUserInfo;
import com.security1.security1.config.oauth.provider.GoogleUserInfo;
import com.security1.security1.config.oauth.provider.OAuth2UserInfo;
import com.security1.security1.model.User;
import com.security1.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;



@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    //함수종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration() = " + userRequest.getClientRegistration());  //registrationId로 어떤 Oauth로 로그인 했는지 확인 가능.(google)
        System.out.println("getAccessToken() = " + userRequest.getAccessToken());

        OAuth2User oauth2User = super.loadUser(userRequest);
        // 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인을 완료 -> code를 리턴(OAuth-Client 라이브러리) -> AccessToken 요청
        // 여기까지가 userRequest 정보 -> loadUser 함수 호출 -> 구글로부터 회원프로필 받아준다.
        System.out.println("getAttributes() = " + oauth2User.getAttributes());


        //회원가입을 강제로 진행해볼 예정
        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
        }else {
            System.out.println("우리는 구글과 페이스북만 지원해요");
        }
        String provider = oAuth2UserInfo.getProvider(); // google
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider+"_"+providerId; // google_1097....  username이 충돌할 일이 없다
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        //중복 회원 관리
        User userEntity = userRepository.findByUsername(username);

        if(userEntity==null){
            userEntity=User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else {

        }

      return new PrincipalDetails(userEntity,oauth2User.getAttributes()); //OAuth2User 리턴
    }
}
