package com.liminghan.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liminghan.market.entity.PaymentRecord;

public interface PaymentService extends IService<PaymentRecord> {

    PaymentRecord mockPay(Long orderId);

    PaymentRecord getByOrderId(Long orderId);
}
