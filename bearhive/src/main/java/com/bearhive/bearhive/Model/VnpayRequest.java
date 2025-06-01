package com.bearhive.bearhive.Model;

import lombok.Data;

@Data
public class VnpayRequest {
    private String amount;
    private String orderId;
}
