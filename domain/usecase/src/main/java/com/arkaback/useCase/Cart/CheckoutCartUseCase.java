package com.arkaback.useCase.Cart;

import com.arkaback.entity.cart.CartItem;
import com.arkaback.entity.cart.CartStatus;
import com.arkaback.entity.cart.ShoppingCart;
import com.arkaback.entity.inventory.Inventory;
import com.arkaback.entity.order.Order;
import com.arkaback.entity.order.OrderDetail;
import com.arkaback.entity.order.OrderStatu;
import com.arkaback.entity.warehouse.Warehouse;
import com.arkaback.exceptions.domain.EmptyCartException;
import com.arkaback.exceptions.domain.InsufficientStockException;
import com.arkaback.exceptions.infrastructure.CartNotFoundException;
import com.arkaback.ports.input.Cart.CheckoutCart;
import com.arkaback.ports.output.InventoryPersistencePort;
import com.arkaback.ports.output.OrderPersistencePort;
import com.arkaback.ports.output.ShoppingCartPersistencePort;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class CheckoutCartUseCase implements CheckoutCart {

    private final ShoppingCartPersistencePort cartPersistencePort;
    private final OrderPersistencePort orderPersistencePort;
    private final InventoryPersistencePort inventoryPersistencePort;

    @Override
    public Order execute(Long personId, Long warehouseId) {

        validateInput(personId, warehouseId);

        //Buscamos carrito activo del cliente
        ShoppingCart cart = cartPersistencePort.findActiveCartByPersonId(personId)
                .orElseThrow(() -> new CartNotFoundException(
                        "No existe carrito activo para el cliente con ID: " + personId));

        //Valida que el carrito tenga productos
        if (cart.isEmpty()) {
            throw new EmptyCartException("No se puede crear una orden con el carrito vacío");
        }

        //Valida stock disponible para todos los productos
        List<Inventory> inventoriesToUpdate = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Inventory inventory = inventoryPersistencePort
                    .findByProductIdAndWarehouseId(
                            cartItem.getProduct().getId(),
                            warehouseId
                    )
                    .orElseThrow(() -> new InsufficientStockException(
                            "No hay inventario para el producto: " + cartItem.getProduct().getName()
                                    + " en la bodega seleccionada"
                    ));

            int stockDisponible = inventory.getStockActual() - inventory.getStockReserved();

            if (stockDisponible < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                        "Stock insuficiente para el producto: " + cartItem.getProduct().getName() +
                                ". Disponible: " + stockDisponible +
                                ", Solicitado: " + cartItem.getQuantity()
                );
            }

            inventoriesToUpdate.add(inventory);
        }

        //Crea los OrderDetails a partir de los CartItems
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getUnitPrice())
                    .build();

            orderDetails.add(orderDetail);
        }

        //Creamos la orden
        String orderCode = orderPersistencePort.generateOrderCode();

        Order order = Order.builder()
                .person(cart.getPerson())
                .warehouse(Warehouse.builder().id(warehouseId).build())
                .details(orderDetails)
                .orderCode(orderCode)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatu.PENDIENTE)
                .build();

        //Valida la orden
        order.validate();

        //Guarda la orden
        Order savedOrder = orderPersistencePort.save(order);

        //Reserva stock en el inventario
        for (int i = 0; i < savedOrder.getDetails().size(); i++) {
            OrderDetail detail = savedOrder.getDetails().get(i);
            Inventory inventory = inventoriesToUpdate.get(i);

            Inventory updatedInventory = Inventory.builder()
                    .id(inventory.getId())
                    .product(inventory.getProduct())
                    .stockActual(inventory.getStockActual())
                    .stockReserved(inventory.getStockReserved() + detail.getQuantity())
                    .build();

            inventoryPersistencePort.update(updatedInventory);
        }

        //Marca el carrito como comprado
        cartPersistencePort.updateStatus(cart.getId(), CartStatus.PURCHASED);

        //devuleve la orden creada
        return savedOrder;
    }

    private void validateInput(Long personId, Long warehouseId) {
        if (personId == null || personId <= 0) {
            throw new IllegalArgumentException("El ID del cliente es inválido");
        }
        if (warehouseId == null || warehouseId <= 0) {
            throw new IllegalArgumentException("El ID de la bodega es inválido");
        }
    }
}

