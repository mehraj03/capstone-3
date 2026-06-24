---
title: "Bug: Product edits don't fully save"
labels: bug, required
---

**Phase 2 — Bug fix (required).**

Admins report that when they edit a product, some changes don't stick. For example, updating
a product's **stock** returns `OK` but the value in the database never actually changes
(other fields such as price and description update correctly).

### Tasks
- [ ] Reproduce by editing a product's stock via `PUT /products/{id}` and re-reading it
- [ ] Find and fix the cause so **every** field updates
- [ ] Add a unit test that proves the fix

### Done when
- Editing any field (including stock) persists correctly
