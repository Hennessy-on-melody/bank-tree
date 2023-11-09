package com.banktree.banktree.controller;


import com.banktree.banktree.repository.AccountRepository;
import com.banktree.banktree.repository.CustomerRepository;
import com.banktree.banktree.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.banktree.banktree.domain.form.CreateAccountForm.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    @PostMapping("/createAccount")
    public ResponseEntity<CreateAccountRes> createAccount(@RequestBody CreateAccountReq createAccountReq){
        return ResponseEntity.ok(customerService.createAccount(createAccountReq));
    }

}
