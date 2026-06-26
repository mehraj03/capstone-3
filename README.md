# 🛍️ Capstone 3 — Clothing Store E-Commerce API

A full-stack e-commerce application for a clothing store, built as the final capstone project for the Year Up United Java Focus bootcamp. It's a Spring Boot REST API on the back, a scrappy vanilla-JS storefront on the front, and a MySQL database holding it all together.

> Think of it as a tiny Amazon: you can browse clothes, filter by price/category/color, log in, throw things in a cart, edit your shipping info, and check out. No frameworks doing the heavy lifting on the frontend — just HTML, Axios, and Mustache templates, the old-fashioned way.

## 📖 What This Project Does
A user can:
- **Categories** — full CRUD, with admin-only write access enforced via `@PreAuthorize`
- **Products** — search/filter by category, price range, and color; fixed two pre-existing bugs (a stray filter that silently dropped non-featured products from search results, and an update method that forgot to persist the new stock count)
- **Shopping Cart** — add a product (or bump its quantity if it's already in the cart), update a line item's quantity directly, and clear the whole cart

### Optional — all implemented

- **User Profile** — view/update name, contact info, and shipping address. The profile row is created automatically at registration, so by the time a user can log in, they already have one waiting for them
- **Checkout** — turns a shopping cart into a real, persisted order. This one had *zero* starter code — no entity, no repository, no service, no controller. Built entirely from scratch, layer by layer, matching the existing project's patterns. See the code highlight below for the part I'm proudest of.

## 🚀 Running the Project

### Backend

1. Open `capstone-api-starter` in IntelliJ as a Maven project
2. Spin up a local MySQL instance and run the schema script in `database/` to create the `clothingstore` database
3. Run `ECommerceApplication.java` — the API comes up on `http://localhost:8080`

### Frontend

1. Open `capstone-client-clothingstore/index.html` in IntelliJ and use **Run 'index.html'** (not just double-clicking it) — this serves it over `http://localhost` rather than `file://`, which the page's Axios calls need to work properly
2. Make sure the backend is already running — the frontend talks straight to `http://localhost:8080` (see `js/config.js`)

### API Testing

An Insomnia collection covering every phase (categories → products → cart → profile → checkout) lives alongside the project, with automated test assertions baked into each request so you can fire through the whole suite and watch the pass/fail counts.

### 💛 My Favorite Part

If I had to pick the one line of code in this whole project that I'm most fond of, it's the upsert logic in `ShoppingCartService.addProduct()`:

```java
public ShoppingCart addProduct(int userId, int productId)
{
    CartItem existing = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

    if (existing == null) {
        CartItem newItem = new CartItem();
        newItem.setUserId(userId);
        newItem.setProductId(productId);
        newItem.setQuantity(1);
        shoppingCartRepository.save(newItem);
    }
    else
    {
        existing.setQuantity(existing.getQuantity() + 1);
        shoppingCartRepository.save(existing);
    }
    return getByUserId(userId);
}
```

It's a small method, but it's the first place in the project where I had to actually think about *state*, not just CRUD. "Adding to cart" sounds like one action, but it's secretly two different actions depending on what's already there — insert a new row, or update an existing one — and the method has to figure out which one applies before doing anything. It's the kind of branching logic that feels obvious once it's written, but took a minute to actually reason through the first time. That click — realizing "add to cart" isn't a single SQL statement, it's a decision — is my favorite kind of programming moment, and this is where it happened for me on this project.
## 🔍 Interesting Code: The Checkout Transaction

This is the piece of code I'd most want to walk someone through, because it's where a real database lesson actually clicked for me.

```java
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
    order.setShippingAmount(BigDecimal.ZERO);

    order = orderRepository.save(order);

    // create an order line item for each item in the cart
    List<ShoppingCartItem> cartItems = cart.getItems().values().stream().toList();

    for (ShoppingCartItem cartItem : cartItems)
    {
        OrderLineItem lineItem = new OrderLineItem();
        lineItem.setOrderId(order.getOrderId());
        lineItem.setProductId(cartItem.getProduct().getProductId());
        lineItem.setSalesPrice(BigDecimal.valueOf(cartItem.getProduct().getPrice()));
        lineItem.setQuantity(cartItem.getQuantity());

        orderLineItemRepository.save(lineItem);
    }

    // clear the cart now that the order has been created
    shoppingCartRepository.deleteByUserId(userId);

    return order;
}
```

**Why this matters:** checkout isn't *one* database write — it's potentially several: one `INSERT` for the order, one more `INSERT` for every item in the cart, and then a `DELETE` to clear the cart out. If a checkout with three items in the cart died right after writing line item #2, you'd be left with a half-finished order: a row in `orders`, one orphaned line item, and a cart that *still* has all three items in it — meaning the user could check out the same items twice.

The `@Transactional` annotation is what stops that from happening. It wraps every database operation in this method into a single all-or-nothing unit: either every insert and the final delete all succeed together, or — if anything blows up halfway through — the whole thing rolls back as if none of it ever happened. The cart stays intact, no phantom order, no orphaned line items. The user just sees an error and can try again.

I also made a deliberate design call here worth pointing out: the order's shipping address doesn't come from a request body (the spec says `POST /orders` has none), so it's pulled straight from the user's `Profile` — the only place that data already exists in the system. And each line item's price is captured from the product's *current* price at the moment of checkout rather than referencing the product later, so if a product's price changes next week, this order still reflects what the customer actually paid.

## ⚠️ Known Limitations

Honest about what's *not* perfect, because pretending otherwise is worse than just saying it:

- **Checking out an empty cart returns `201 Created`, not `400 Bad Request`.** The spec for checkout never said to reject an empty cart, so right now it'll happily create an order with zero line items. I noticed this while testing, considered adding a guard clause to throw on an empty cart, and deliberately chose not to under time pressure — it's a known gap, not something that slipped past me.
- **No frontend checkout button.** `POST /orders` is fully built and tested on the backend, but the provided frontend client never had a checkout page or button wired up to call it. I demo checkout through the Insomnia collection instead of the live UI.
- **Shipping is always `$0.00` and discounts are always `$0.00`.** Neither was specified in the requirements, so both default to zero on every order rather than guessing at logic that wasn't asked for.

