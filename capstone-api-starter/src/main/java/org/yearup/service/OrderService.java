package org.yearup.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yearup.models.*;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;
import org.yearup.repository.ShoppingCartRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService
{
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProfileService profileService;

    public OrderService(OrderRepository orderRepository,
                        OrderLineItemRepository orderLineItemRepository,
                        ShoppingCartRepository shoppingCartRepository,
                        ShoppingCartService shoppingCartService,
                        ProfileService profileService)
    {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartService = shoppingCartService;
        this.profileService = profileService;
    }

    @Transactional
    public Order createOrder(int userId)
    {
        // get the user's profile for shipping address info
        Profile profile = profileService.getByUserId(userId);

        // get the user's current shopping cart
        ShoppingCart cart = shoppingCartService.getByUserId(userId);

        // create and save the order
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setShippingAmount(java.math.BigDecimal.ZERO);

        order = orderRepository.save(order);

        // create an order line item for each item in the cart
        List<ShoppingCartItem> cartItems = cart.getItems().values().stream().toList();

        for (ShoppingCartItem cartItem : cartItems)
        {
            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrderId(order.getOrderId());
            lineItem.setProductId(cartItem.getProduct().getProductId());
            lineItem.setSalesPrice(java.math.BigDecimal.valueOf(cartItem.getProduct().getPrice()));
            lineItem.setQuantity(cartItem.getQuantity());

            orderLineItemRepository.save(lineItem);
        }

        // clear the cart now that the order has been created
        shoppingCartRepository.deleteByUserId(userId);

        return order;
    }
}