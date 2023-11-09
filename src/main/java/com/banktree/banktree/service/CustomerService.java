package com.banktree.banktree.service;


import com.banktree.banktree.config.JwtTokenProvider;
import com.banktree.banktree.config.MailComponents;
import com.banktree.banktree.domain.entity.Account;
import com.banktree.banktree.domain.entity.Customer;
import com.banktree.banktree.domain.entity.CustomerStatus;
import com.banktree.banktree.domain.form.CreateAccountForm;
import com.banktree.banktree.domain.form.CustomerLoginForm;
import com.banktree.banktree.domain.form.TokenInfo;
import com.banktree.banktree.exception.UsersException;
import com.banktree.banktree.repository.AccountRepository;
import com.banktree.banktree.repository.CustomerRepository;
import com.banktree.banktree.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.banktree.banktree.domain.form.CreateAccountForm.*;
import static com.banktree.banktree.domain.form.CustomerSignUpForm.SignUpRequest;
import static com.banktree.banktree.domain.form.CustomerSignUpForm.SignUpResponse;
import static com.banktree.banktree.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService implements UserDetailsService {
    private final CustomerRepository customerRepository;
    private final NotificationRepository notificationRepository;
    private final MailComponents mailComponents;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountRepository accountRepository;
    private final static String MESSAGE = "계좌가 생성되었습니다.";

    @Transactional
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        Optional<Customer> optionalCustomer = customerRepository.findByEmail(signUpRequest.getEmail());
        if (optionalCustomer.isPresent()) {
            throw new UsersException(ALREADY_EXIST_USER);
        }

        String encPw = BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt());
        String uuid = UUID.randomUUID().toString().replace("-", "");

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

    public TokenInfo login(CustomerLoginForm customerLoginForm) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        customerLoginForm.getEmail(), customerLoginForm.getEmail());
        validateAccount(customerLoginForm.getEmail(), customerLoginForm.getPassword());

        log.info(authenticationToken.getName());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return jwtTokenProvider.generateToken(authenticationToken);

    }

    private void validateAccount(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsersException(USER_NOT_FOUNDED));

        if (passwordEncoder.matches(password, customer.getPassword())) {
            throw new UsersException(WRONG_PASSWORD);
        }


    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(username)
                .orElseThrow(() -> new UsersException(USER_NOT_FOUNDED));

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(customer.getEmail(), customer.getPassword(), grantedAuthorities);

    }


    @Transactional
    public CreateAccountRes createAccount(CreateAccountReq createAccountReq) {
        Customer customer = customerRepository.findByEmail(createAccountReq.getUserEmail())
                .orElseThrow(() -> new UsersException(USER_NOT_FOUNDED));

        if (customer.getCustomerStatus() == CustomerStatus.UNAUTHORIZED) {
            throw new UsersException(UNAUTHORIZED_USER);
        } else {
            Account newAccount = accountRepository.save(Account.builder()
                    .customer(customer)
                    .amount(0L)
                    .accountNumber(generateNumber())
                    .accountType(createAccountReq.getAccountType())
                    .build());

            return CreateAccountRes.builder()
                    .userEmail(createAccountReq.getUserEmail())
                    .accountNumber(newAccount.getAccountNumber())
                    .accountType(createAccountReq.getAccountType())
                    .accountNickname(createAccountReq.getAccountNickname())
                    .message(MESSAGE)
                    .build();
        }
    }

    private Long generateNumber() {
        Random random = new Random();
        long min = 1000000000L;
        long max = 9999999999L;
        Long accountNumber = min + ((long) (random.nextDouble() * (max - min)));

        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isPresent()) {
            generateNumber();
        } else {
            return accountNumber;
        }
        return null;
    }
}
