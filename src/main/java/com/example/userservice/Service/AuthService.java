package com.example.userservice.Service;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.example.userservice.DTO.UserDto;
import com.example.userservice.Exceptions.InvalidCredentials;
import com.example.userservice.Exceptions.UserNotFoundException;
import com.example.userservice.Models.Role;
import com.example.userservice.Models.Session;
import com.example.userservice.Models.SessionStatus;
import com.example.userservice.Models.User;
import com.example.userservice.Repository.SessionRepository;
import com.example.userservice.Repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class AuthService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SessionRepository sessionRepository;

    @Autowired
    public AuthService(UserRepository userRepository,
                       SessionRepository sessionRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository=userRepository;
        this.sessionRepository = sessionRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
    }
    public UserDto signUp(String email,String password){
        User user=new User();
        user.setEmail(email);
        //user.setPassword(password);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        User savedUser=userRepository.save(user);
        System.out.println("savedUser"+savedUser.getEmail());
        return UserDto.setUser(savedUser);
    }
    public ResponseEntity<UserDto> login(String email,String password) throws UserNotFoundException,InvalidCredentials{
        Optional<User> userOptional=userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new UserNotFoundException("user not found");
        }
        User user=userOptional.get();
       /* if(!user.getPassword().equals(password)){
            throw new InvalidCredentials("invalid credentials");
        }*/
        if(!bCryptPasswordEncoder.matches(password,user.getPassword())){
            throw new InvalidCredentials("invalid credentials");
        }
        //String token= RandomStringUtils.randomAlphanumeric(25);
        // Create a test key suitable for the desired HMAC-SHA algorithm:
        MacAlgorithm algorithm= Jwts.SIG.HS256;
        SecretKey key=algorithm.key().build();
       /* String message = "{\n" +
//                "  \"email\": \"harsh@scaler.com\",\n" +
//                "  \"roles\": [\n" +
//                "    \"student\",\n" +
//                "    \"ta\"\n" +
//                "  ],\n" +
//                "  \"expiry\": \"31stJan2024\"\n" +
//                "}";
                //JSON -> Key : Value*/
        //hardcoding is not good so use map for json type
        Map<String,Object> jsonMap=new HashMap<>();
        jsonMap.put("email",user.getEmail());
        jsonMap.put("roles", List.of(user.getRoles()));
        jsonMap.put("createdAt", new Date());
        jsonMap.put("expiryAt", DateUtils.addDays(new Date(), 30));
        String token=Jwts.builder().claims(jsonMap).signWith(key,algorithm).compact();
        //above line converted map to json object to use as a payload in jwt
        Session session=new Session();
        session.setUser(user);
        session.setToken(token);
        session.setSessionStatus(SessionStatus.ACTIVE);
        sessionRepository.save(session);
        //session.setExpiryDate();
        UserDto userDto=new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        MultiValueMapAdapter<String,String> header=new MultiValueMapAdapter<>(new HashMap<>());
        header.add(HttpHeaders.SET_COOKIE,"Auth token-"+token);
        ResponseEntity<UserDto> response=new ResponseEntity<>(userDto,header, HttpStatus.OK);
        return response;
    }
    public ResponseEntity<Void> logout(String token,Long userId){
        Optional<Session> sessionOptional=sessionRepository.findByTokenAndUser_Id(token,userId);
        if(sessionOptional.isEmpty()){
            return null;
        }
        Session session=sessionOptional.get();
        session.setSessionStatus(SessionStatus.ENDED);
        sessionRepository.save(session);
        return ResponseEntity.ok().build();
    }
    public SessionStatus validate(String token, Long userId) {
        Optional<Session> sessionOptional = sessionRepository.findByTokenAndUser_Id(token, userId);

        if (sessionOptional.isEmpty()) {
            return null;
        }
        Session session=sessionOptional.get();
        if(!session.getSessionStatus().equals(SessionStatus.ACTIVE)){
            return session.getSessionStatus();
        }
        //this is how we decode token and take information from payload
        Jws<Claims> claimsJws=Jwts.parser().build().parseSignedClaims(token);
        String email=(String) claimsJws.getPayload().get("email");
        List<Role> roles=(List<Role>) claimsJws.getPayload().get("roles");

        return SessionStatus.ACTIVE;
    }
}
