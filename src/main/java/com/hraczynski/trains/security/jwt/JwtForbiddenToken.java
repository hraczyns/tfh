package com.hraczynski.trains.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_blacklist")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtForbiddenToken {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private LocalDateTime expiresAt;
    @Column(length = 511)
    private String value;
}
