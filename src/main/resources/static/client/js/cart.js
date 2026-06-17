/**
 * WineStore - Cart Page JS
 * Renders cart items and handles quantity/remove updates
 */

const CartPage = (() => {
  let apiItems = [];

  const renderCart = () => {
    const container = document.querySelector("#cart-items");
    const emptyState = document.querySelector("#cart-empty");
    const cartActions = document.querySelector("#cart-actions");
    if (!container) return;

    let items;
    if (Cart.isLoggedIn()) {
      items = apiItems;
    } else {
      items = Cart.getItems();
    }

    if (items.length === 0) {
      container.innerHTML = "";
      if (emptyState) emptyState.style.display = "block";
      if (cartActions) cartActions.style.display = "none";
      updateSummary(items);
      return;
    }

    if (emptyState) emptyState.style.display = "none";
    if (cartActions) cartActions.style.display = "";

    container.innerHTML = items
      .map(
        (item) => `
      <div class="cart-item" data-id="${item.id}">
        <div class="cart-product-info">
          <img class="cart-product-img" src="${item.img || "/images/placeholder.png"}" alt="${item.name}">
          <div>
            <div class="cart-product-name">${item.name}</div>
            <div class="cart-product-brand">${item.brand || ""}</div>
          </div>
        </div>
        <div class="cart-price">${formatPrice(item.price)}</div>
        <div class="cart-qty">
          <button class="qty-btn" data-action="minus" data-id="${item.id}">\u2212</button>
          <input class="qty-input" type="number" value="${item.qty}" min="1" data-id="${item.id}">
          <button class="qty-btn" data-action="plus" data-id="${item.id}">+</button>
        </div>
        <div class="cart-total">${formatPrice(item.price * item.qty)}</div>
        <button class="cart-remove" data-remove="${item.id}" title="X\u00f3a">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14H6L5 6"/>
            <path d="M10 11v6M14 11v6"/><path d="M9 6V4h6v2"/>
          </svg>
        </button>
      </div>
    `,
      )
      .join("");

    updateSummary(items);
  };

  const updateSummary = (items) => {
    if (!items) {
      if (Cart.isLoggedIn()) {
        items = apiItems;
      } else {
        items = Cart.getItems();
      }
    }
    const subtotal = items.reduce((s, i) => s + i.price * i.qty, 0);
    const shipping = subtotal >= 500000 ? 0 : 30000;
    const total = subtotal + shipping;
    const el = (id) => document.querySelector(id);
    if (el("#summary-subtotal"))
      el("#summary-subtotal").textContent = formatPrice(subtotal);
    if (el("#summary-shipping"))
      el("#summary-shipping").textContent =
        shipping === 0 ? "Mi\u1ec5n ph\u00ed" : formatPrice(shipping);
    if (el("#summary-total"))
      el("#summary-total").textContent = formatPrice(total);
  };

  const formatPrice = (p) =>
    new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(p);

  const bindEvents = () => {
    const container = document.querySelector("#cart-items");
    if (!container) return;

    container.addEventListener("click", (e) => {
      const removeBtn = e.target.closest("[data-remove]");
      if (removeBtn) {
        e.stopPropagation();
        const id = removeBtn.dataset.remove;
        if (Cart.isLoggedIn()) {
          Cart.remove(id).then(fetchCartFromApi).catch(() => {});
        } else {
          Cart.remove(id);
          renderCart();
        }
        return;
      }
      const qtyBtn = e.target.closest(".qty-btn");
      if (qtyBtn) {
        e.stopPropagation();
        const id = qtyBtn.dataset.id;
        const input = container.querySelector(`.qty-input[data-id="${id}"]`);
        if (!input) return;
        let val = parseInt(input.value) || 1;
        if (qtyBtn.dataset.action === "minus" && val <= 1) {
          // Remove item when decreasing from 1
          if (Cart.isLoggedIn()) {
            Cart.remove(id).then(fetchCartFromApi).catch(() => {});
          } else {
            Cart.remove(id);
            renderCart();
          }
          return;
        }
        val = qtyBtn.dataset.action === "minus" ? val - 1 : val + 1;
        input.value = val;
        if (Cart.isLoggedIn()) {
          Cart.updateQty(id, val).then(fetchCartFromApi).catch(() => {});
        } else {
          Cart.updateQty(id, val);
          renderCart();
        }
      }
    });

    container.addEventListener("change", (e) => {
      if (e.target.classList.contains("qty-input")) {
        e.stopPropagation();
        const id = e.target.dataset.id;
        const raw = parseInt(e.target.value);
        if (isNaN(raw) || raw <= 0) {
          // Remove item when quantity is 0 or invalid
          if (Cart.isLoggedIn()) {
            Cart.remove(id).then(fetchCartFromApi).catch(() => {});
          } else {
            Cart.remove(id);
            renderCart();
          }
          return;
        }
        if (Cart.isLoggedIn()) {
          Cart.updateQty(id, raw).then(fetchCartFromApi).catch(() => {});
        } else {
          Cart.updateQty(id, raw);
          renderCart();
        }
      }
    });
  };

  const fetchCartFromApi = () => {
    fetch("/VitaStore/api/cart")
      .then((r) => r.json())
      .then((data) => {
        apiItems = (data.items || []).map((i) => ({
          id: i.id,
          name: i.productName,
          price: i.price,
          qty: i.quantity,
          img: i.imageUrl || "/images/placeholder.png",
          brand: "",
        }));
        renderCart();
      })
      .catch(() => {});
  };

  const init = () => {
    if (!document.querySelector("#cart-items")) return;
    if (Cart.isLoggedIn()) {
      fetchCartFromApi();
    } else {
      renderCart();
    }
    bindEvents();
  };

  return { init };
})();

document.addEventListener("DOMContentLoaded", CartPage.init);
