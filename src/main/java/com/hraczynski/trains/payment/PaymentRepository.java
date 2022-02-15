package com.hraczynski.trains.payment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Payment findByPaymentId(String paymentId);
}
