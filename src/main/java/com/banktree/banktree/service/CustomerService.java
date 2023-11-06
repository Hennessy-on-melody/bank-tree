package com.banktree.banktree.service;


import com.banktree.banktree.config.MailComponents;
import com.banktree.banktree.domain.entity.Customer;
import com.banktree.banktree.domain.entity.CustomerStatus;
import com.banktree.banktree.domain.form.CustomerSignUpForm;
import com.banktree.banktree.exception.UsersException;
import com.banktree.banktree.repository.CustomerRepository;
import com.banktree.banktree.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.banktree.banktree.domain.form.CustomerSignUpForm.*;
import static com.banktree.banktree.exception.ErrorCode.ALREADY_EXIST_USER;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final NotificationRepository notificationRepository;
    private final MailComponents mailComponents;

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(signUpRequest.getEmail());
        if (optionalCustomer.isPresent()){
            throw new UsersException(ALREADY_EXIST_USER);
        }

        String encPw = BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt());
        String uuid = UUID.randomUUID().toString();

        Customer customer = Customer.builder()
                .email(signUpRequest.getEmail())
                .name(signUpRequest.getName())
                .password(encPw)
                .phoneNumber(signUpRequest.getPhoneNumber())
                .customerStatus(CustomerStatus.UNAUTHORIZED)
                .build();

        customerRepository.save(customer);
        String email = signUpRequest.getEmail();
        String subject = "뱅크트리 가입 인증";
        String text = "<p>가입 진행을 위해 아래 링크를 클릭하세요</p>" +
                "<div><a target='_blank' href='http://localhost:8080/customer/email-auth?id="
                + uuid + "'> 가입완료 </a></div>";
        mailComponents.sendMail(email, subject, text);

        return SignUpResponse.from(signUpRequest);
    }
}
