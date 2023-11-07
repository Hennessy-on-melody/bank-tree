package com.banktree.banktree.service;


import com.banktree.banktree.config.MailComponents;
import com.banktree.banktree.domain.entity.Customer;
import com.banktree.banktree.domain.entity.CustomerStatus;
import com.banktree.banktree.exception.UsersException;
import com.banktree.banktree.repository.CustomerRepository;
import com.banktree.banktree.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.banktree.banktree.domain.form.CustomerSignUpForm.*;
import static com.banktree.banktree.exception.ErrorCode.ALREADY_EXIST_USER;
import static com.banktree.banktree.exception.ErrorCode.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final NotificationRepository notificationRepository;
    private final MailComponents mailComponents;

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(signUpRequest.getEmail());
        if (optionalCustomer.isPresent()) {
            throw new UsersException(ALREADY_EXIST_USER);
        }

        String encPw = BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt());
        String uuid = UUID.randomUUID().toString().replace("-","");

        Customer customer = Customer.builder()
                .email(signUpRequest.getEmail())
                .name(signUpRequest.getName())
                .password(encPw)
                .phoneNumber(signUpRequest.getPhoneNumber())
                .customerStatus(CustomerStatus.UNAUTHORIZED)
                .emailAuthKey(uuid)
                .build();

        log.info("uuid = " + customer.getEmailAuthKey());
        customerRepository.save(customer);
        log.info("=======회원가입!=======");
        String email = signUpRequest.getEmail();
        String subject = "뱅크트리 가입 인증";
        String url = "http://localhost:8080/customer/email-auth?id=" + uuid;
        System.out.println(url);
        String text = "<p>가입 진행을 위해 아래 링크를 클릭하세요</p>" + "<div> <a target='_blank' href =" + url + "> 가입완료 </a> </div>";

        System.out.println(text);
        mailComponents.sendMail(email, subject, text);

        return SignUpResponse.from(signUpRequest);
    }

    @Transactional
    public String confirmMail(String uuid) {
        log.info("===============confirmMail 시작 ===================");
        System.out.println(uuid);
        Optional<Customer> optionalCustomer = customerRepository.findByEmailAuthKey(uuid);

        if (optionalCustomer.isEmpty()) {
            throw new UsersException(BAD_REQUEST);
        } else {
            Customer customer = optionalCustomer.get();

            customer.setCustomerStatus(CustomerStatus.AUTHORIZED);
            customer.setEmailAuthKey(null);
            customerRepository.save(customer);
        return customer.getEmail();
        }
    }
}
