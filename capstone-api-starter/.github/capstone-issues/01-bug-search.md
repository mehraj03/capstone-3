---
title: "Bug: Search leaves products out"
labels: bug, required
---

**Phase 2 — Bug fix (required).**

Users report that the product search / filter returns an **incomplete** list — some products
that should match never show up.

**Reproduce:** call `GET /products` with no query parameters and compare the results against
the products in the database. Some products that exist are missing from the response. Also
try the `cat`, `minPrice`, `maxPrice`, and `subCategory` filters and combinations.

### Tasks
- [ ] Reproduce the missing-products behavior (manual debugging + Insomnia)
- [ ] Find and fix the cause so search returns **every** matching product
- [ ] Add a unit test that proves the fix

### Done when
- Searching with any combination of filters returns all matching products
- A unit test covers the fixed behavior
