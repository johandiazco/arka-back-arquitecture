package com.arkaback.ports.input.Cart;

public interface SendAbandonedCartReminder {
    boolean execute(Long cartId);
    Integer sendToAllAbandoned();
}
