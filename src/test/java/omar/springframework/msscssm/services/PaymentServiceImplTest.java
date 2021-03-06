package omar.springframework.msscssm.services;

import omar.springframework.msscssm.domain.Payment;
import omar.springframework.msscssm.domain.PaymentEvent;
import omar.springframework.msscssm.domain.PaymentState;
import omar.springframework.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;
    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.99")).build();

    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPayment = paymentService.newPayment(payment);
        System.out.println(savedPayment.getState());
        StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());

        Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println(sm.getState().getId());
        System.out.println(preAuthedPayment);



    }

    @Transactional
    @RepeatedTest(10)
    void testAuth(){
        Payment savedPayment = paymentService.newPayment(payment);
        StateMachine<PaymentState, PaymentEvent> preAuth = paymentService.preAuth(savedPayment.getId());

        if (preAuth.getState().getId() == PaymentState.PRE_AUTH) {
            System.out.println("Payment is Pre Authorized");
            StateMachine<PaymentState, PaymentEvent> sm = paymentService.authorizePayment(savedPayment.getId());
            System.out.println("Result: " + sm.getState().getId());
        }else{
            System.out.println("Payment failed Pre Auth...");

        }
    }
}