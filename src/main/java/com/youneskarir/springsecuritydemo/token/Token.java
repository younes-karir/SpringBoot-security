package com.youneskarir.springsecuritydemo.token;


import com.youneskarir.springsecuritydemo.model.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {
    @Id @SequenceGenerator(sequenceName = "token_sequence", name = "token_sequence", allocationSize = 1)
    @GeneratedValue(generator = "token_sequence",strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;
    private boolean expired;
    private boolean revoked;
    
    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id",
            referencedColumnName = "id"
    )
    private User user;
}
