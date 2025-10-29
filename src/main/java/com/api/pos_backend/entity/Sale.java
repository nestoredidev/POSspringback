package com.api.pos_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Sales")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_id")
    private Long id;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @CreatedDate
    @Column(name = "sale_date")
    private String date;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleDetails> saleDetails;

    @PrePersist
    public void prePersist() {
        this.date = LocalDate.now().toString();

    }
}