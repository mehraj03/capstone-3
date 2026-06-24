package org.yearup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yearup.models.CartItem;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<CartItem, Integer>
{
    List<CartItem> findByUserId(int userId);

    CartItem findByUserIdAndProductId(int userId, int productId);

    void deleteByUserId(int userId);
}
