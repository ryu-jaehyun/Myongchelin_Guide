package com.example.demo.controller;

import com.example.demo.nickNameRequest;
import com.example.demo.user.*;
import com.example.demo.userIdRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @CrossOrigin(origins = "*")
    @PostMapping("/name")
    public ResponseEntity<Object> namevalidation(@RequestBody nickNameRequest request){ //이부분 중요 요청받을때 객체로 변환시켜야됨

        boolean isNickNameAvailable = userService.checkNicknameAvailability(request.getNickName());
        if (!isNickNameAvailable) {
            return ResponseEntity.badRequest().body("닉네임 중복!");
        }
        return ResponseEntity.ok("닉네임 검증 통과!");
    }
    @CrossOrigin(origins = "*")
    @PostMapping("/id")
    public ResponseEntity<Object> idvalidation(@RequestBody userIdRequest request){
        boolean isUserIdAvailable = userService.checkUserIdAvailability(request.getUserId());
        if (!isUserIdAvailable ) {
            return ResponseEntity.badRequest().body("아이디 중복!");
        }
        return ResponseEntity.ok("아이디 검증 통과!");
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User user)  {

        // 중복 확인을 모두 통과한 경우 회원가입 로직 수행
        userService. registerNewUserAccount(user);

        return ResponseEntity.ok("Signup successful");
    }
   @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody User user1,  HttpServletRequest request,
                                        HttpServletResponse response) {
        String userId = user1.getId();
        String userPw = user1.getUserPw();
       System.out.println(userId);
        User user = userRepository.findByUserIdAndPassword(userId, userPw);
        if(user !=null){
            // 기존 세션 무효화 -->로그아웃 기능 간단하게 구현
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            // 새로운 세션 생성
            HttpSession newSession = request.getSession(true);

            newSession.setAttribute("userId", user.getId());
            Object a =newSession.getId();
            System.out.println(a);

            // 쿠키 생성
            Cookie sessionCookie = new Cookie("sessionId", newSession.getId());
            sessionCookie.setMaxAge(60*60*24 ); // 쿠키 유효 기간 설정 (예: 1일)
            sessionCookie.setPath("/"); // 쿠키의 유효 경로 설정
            response.addCookie(sessionCookie);
            return ResponseEntity.ok("Login successful!");
        }
        else{
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sorry, login failed!");

        }
    }



    }

