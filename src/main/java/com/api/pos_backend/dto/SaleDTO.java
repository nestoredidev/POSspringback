package com.api.pos_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaleDTO {
    private Long id;

    @NotNull(message = "El monto es obligatorio")
    private Double totalAmount;


    private CouponDTO coupon;

    @NotNull(message = "El usuario es obligatorio")
    private UserDTO user;

    private List<SaleDetailsDTO> saleDetails;

    private String date;

    private Long userId;

    private String userName;

    private String couponCode;

}