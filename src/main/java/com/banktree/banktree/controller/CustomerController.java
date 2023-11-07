package com.banktree.banktree.controller;


import com.banktree.banktree.domain.entity.Customer;
import com.banktree.banktree.domain.form.CustomerSignUpForm;
import com.banktree.banktree.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.banktree.banktree.domain.form.CustomerSignUpForm.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
@Slf4j
public class CustomerController {
    private final CustomerService customerService;


    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest){
        return ResponseEntity.ok(customerService.signUp(signUpRequest));
    }

    @GetMapping("/email-auth")
    public ResponseEntity<String> emailAuth(HttpServletRequest request){
        String uuid = request.getParameter("id").replace("-","");
        log.info("uuid = " + uuid);
        String emailAuthKey = customerService.confirmMail(uuid);
        return ResponseEntity.ok(emailAuthKey);
    }
//    @GetMapping("/login")

}
