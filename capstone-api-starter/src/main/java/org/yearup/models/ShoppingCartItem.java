package org.yearup.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ShoppingCartItem
{
    private Product product = null;
    private int quantity = 1;
    private double discountPercent = 0;

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public double getDiscountPercent()
    {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent)
    {
        this.discountPercent = discountPercent;
    }

    @JsonIgnore
    public int getProductId()
    {
        return this.product.getProductId();
    }

    public double getLineTotal()
    {
        double basePrice = product.getPrice();
        double subTotal = basePrice * this.quantity;
        double discountAmount = subTotal * discountPercent;

        return subTotal - discountAmount;
    }
}
