/**
 * WineStore - Main App JS
 * Handles: Cart, Contact Bubble, Filter, Toast Notifications
 */

// ===== CART STATE =====
const Cart = (() => {
  const lsKey = "vitastore_cart";
  let items = [];
  let isSyncing = false;

  const isLoggedIn = () => document.querySelector('.user-dropdown') !== null;

  const loadLocal = () => {
    items = JSON.parse(localStorage.getItem(lsKey) || "[]");
  };

  const saveLocal = () => {
    localStorage.setItem(lsKey, JSON.stringify(items));
  };

  const getCount = () => {
    if (isLoggedIn()) {
      const badge = document.querySelector(".cart-badge");
      return badge ? parseInt(badge.textContent) || 0 : 0;
    }
    return items.reduce((s, i) => s + i.qty, 0);
  };

  const updateBadge = () => {
    if (isLoggedIn()) {
      fetchCartFromApi();
      return;
    }
    document.querySelectorAll(".cart-badge").forEach((el) => {
      const c = items.reduce((s, i) => s + i.qty, 0);
      el.textContent = c;
      el.style.display = c > 0 ? "flex" : "none";
    });
  };

  const fetchCartFromApi = () => {
    fetch("/VitaStore/api/cart")
      .then((r) => r.json())
      .then((data) => {
        const totalItems = data.items
          ? data.items.reduce((s, i) => s + i.quantity, 0)
          : 0;
        document.querySelectorAll(".cart-badge").forEach((el) => {
          el.textContent = totalItems;
          el.style.display = totalItems > 0 ? "flex" : "none";
        });
      })
      .catch(() => {});
  };

  const add = (id, name, price, img, brand) => {
    if (isLoggedIn()) {
      return fetch("/VitaStore/api/cart/items", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ productId: parseInt(id), quantity: 1 }),
      })
        .then((r) => {
          if (!r.ok) return r.json().then(d => { if (handleApiError(d)) return; Notify.show(d.error || "Lỗi"); throw d; });
          fetchCartFromApi();
          Toast.show(`Đã thêm "${name}" vào giỏ hàng`, "success");
          return r;
        })
        .catch((err) => {
          if (!err?.error) Notify.show("Lỗi khi thêm vào giỏ hàng");
          throw err;
        });
    } else {
      const existing = items.find((i) => i.id === id);
      if (existing) {
        existing.qty++;
      } else {
        items.push({ id, name, price, img, brand, qty: 1 });
      }
      saveLocal();
      updateBadge();
      Toast.show(`Đã thêm "${name}" vào giỏ hàng`, "success");
      return Promise.resolve();
    }
  };

  const remove = (id) => {
    if (isLoggedIn()) {
      return fetch(`/VitaStore/api/cart/items/${id}`, { method: "DELETE" })
        .then((r) => {
          if (!r.ok) return r.json().then(d => { if (handleApiError(d)) return; Notify.show(d.error || "Lỗi"); throw d; });
          fetchCartFromApi();
          return r;
        })
        .catch((err) => {
          if (!err?.error) Notify.show("Lỗi khi xóa sản phẩm");
          throw err;
        });
    } else {
      items = items.filter((i) => i.id !== id);
      saveLocal();
      updateBadge();
      return Promise.resolve();
    }
  };

  const updateQty = (id, qty) => {
    if (isLoggedIn()) {
      return fetch(`/VitaStore/api/cart/items/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ quantity: Math.max(1, qty) }),
      })
        .then((r) => {
          if (!r.ok) return r.json().then(d => { if (handleApiError(d)) return; Notify.show(d.error || "Lỗi"); throw d; });
          fetchCartFromApi();
          return r;
        })
        .catch((err) => {
          if (!err?.error) Notify.show("Lỗi khi cập nhật số lượng");
          throw err;
        });
    } else {
      const item = items.find((i) => i.id === id);
      if (item) {
        item.qty = Math.max(1, qty);
        saveLocal();
      }
      return Promise.resolve();
    }
  };

  const getTotal = () => {
    if (isLoggedIn()) return 0;
    return items.reduce((s, i) => s + i.price * i.qty, 0);
  };

  const getItems = () => {
    if (isLoggedIn()) return [];
    return [...items];
  };

  const clear = () => {
    if (isLoggedIn()) {
      fetch("/VitaStore/api/cart", { method: "DELETE" })
        .then((r) => {
          if (!r.ok) return r.json().then(d => { if (handleApiError(d)) return; Notify.show(d.error || "Lỗi"); throw d; });
          fetchCartFromApi();
        })
        .catch((err) => { if (!err?.error) Notify.show("Lỗi khi xóa giỏ hàng"); });
    } else {
      items = [];
      saveLocal();
      updateBadge();
    }
  };

  const syncLocalToApi = () => {
    if (isSyncing) return;
    const localItems = JSON.parse(localStorage.getItem(lsKey) || "[]");
    if (localItems.length === 0) {
      loadLocal();
      fetchCartFromApi();
      return;
    }

    isSyncing = true;
    const payload = localItems.map((i) => ({
      productId: parseInt(i.id),
      quantity: i.qty,
    }));

    fetch("/VitaStore/api/cart/sync", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    })
      .then((r) => {
        if (!r.ok) return r.json().then(d => { if (handleApiError(d)) return; Notify.show(d.error || "Lỗi"); throw d; });
        localStorage.removeItem(lsKey);
        loadLocal();
        fetchCartFromApi();
      })
      .catch((err) => { if (!err?.error) fetchCartFromApi(); })
      .finally(() => { isSyncing = false; });
  };

  const init = () => {
    loadLocal();
    if (isLoggedIn()) {
      syncLocalToApi();
    } else {
      updateBadge();
    }
  };

  return {
    add,
    remove,
    updateQty,
    getTotal,
    getItems,
    getCount,
    clear,
    updateBadge,
    init,
    fetchCartFromApi,
    isLoggedIn,
  };
})();

// ===== CART DROPDOWN =====
const CartDropdown = (() => {
  const update = () => {
    const dropdown = document.getElementById("cartDropdownItems");
    const totalEl = document.getElementById("cartDropdownTotal");
    if (!dropdown) return;

    if (Cart.isLoggedIn()) {
      fetch("/VitaStore/api/cart")
        .then((r) => r.json())
        .then((data) => {
          const mappedItems = (data.items || []).map((i) => ({
            name: i.productName,
            price: i.price,
            qty: i.quantity,
            img: i.imageUrl || "/images/placeholder.png",
          }));
          renderItems(mappedItems, data.totalAmount || 0);
        })
        .catch(() => {});
    } else {
      const items = Cart.getItems();
      const total = Cart.getTotal();
      renderItems(items, total);
    }
  };

  const renderItems = (items, total) => {
    const dropdown = document.getElementById("cartDropdownItems");
    const totalEl = document.getElementById("cartDropdownTotal");
    if (!dropdown) return;

    if (items.length === 0) {
      dropdown.innerHTML =
        '<div class="cart-dropdown-empty">Chưa có sản phẩm</div>';
      if (totalEl) totalEl.textContent = "Tổng: 0₫";
      return;
    }

    dropdown.innerHTML = items
      .map(
        (item) => `
      <div class="cart-dropdown-item">
        <img class="cart-dropdown-item-img" src="${item.img || "/images/placeholder.png"}" alt="${item.name}">
        <div class="cart-dropdown-item-info">
          <div class="cart-dropdown-item-name">${item.name}</div>
          <div class="cart-dropdown-item-qty">SL: ${item.qty}</div>
        </div>
        <div class="cart-dropdown-item-price">${formatCurrency(item.price * item.qty)}</div>
      </div>
    `,
      )
      .join("");

    if (totalEl) totalEl.textContent = `Tổng: ${formatCurrency(total)}`;
  };

  const formatCurrency = (value) => {
    if (typeof value === "number") {
      return new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
      }).format(value);
    }
    return value + "₫";
  };

  document.addEventListener("DOMContentLoaded", () => {
    const wrap = document.querySelector(".cart-wrap");
    if (wrap) {
      wrap.addEventListener("mouseenter", update);
      wrap.addEventListener("click", (e) => {
        if (window.matchMedia("(hover: none)").matches) {
          e.preventDefault();
          const dd = wrap.querySelector(".cart-dropdown");
          if (dd) {
            dd.classList.toggle("open");
            update();
          }
        }
      });
    }
    document.addEventListener("click", (e) => {
      if (window.matchMedia("(hover: none)").matches && wrap && !wrap.contains(e.target)) {
        const dd = wrap.querySelector(".cart-dropdown");
        if (dd) dd.classList.remove("open");
      }
    });
  });

  return { update };
})();

const handleApiError = (data) => {
  if (data && data.redirect) {
    sessionStorage.setItem("adminError", data.error);
    localStorage.removeItem("vitastore_cart");
    window.location.href = '/VitaStore/';
    return true;
  }
  return false;
};

// ===== TOAST =====
const Toast = (() => {
  const getContainer = () => {
    let c = document.querySelector(".toast-container");
    if (!c) {
      c = document.createElement("div");
      c.className = "toast-container";
      document.body.appendChild(c);
    }
    return c;
  };

  const show = (msg, type = "") => {
    const t = document.createElement("div");
    t.className = `toast ${type}`;
    const icon = type === "success" ? "✓" : type === "error" ? "✕" : "ℹ";
    t.innerHTML = `<span>${icon}</span><span>${msg}</span>`;
    getContainer().appendChild(t);
    setTimeout(() => t.remove(), 3200);
  };

  return { show };
})();

const Notify = {
  show(msg) {
    const existing = document.querySelector(".notify-fixed");
    if (existing) existing.remove();
    const el = document.createElement("div");
    el.className = "notify-fixed";
    el.style.cssText = "position:fixed;top:80px;left:50%;transform:translateX(-50%);z-index:99999;max-width:1200px;width:90%;padding:14px 24px;background:#fef2f2;color:#dc2626;border:2px solid #fca5a5;border-radius:10px;font-weight:700;font-size:15px;text-align:center;box-shadow:0 8px 30px rgba(0,0,0,0.12)";
    el.textContent = msg;
    document.body.appendChild(el);
    setTimeout(() => { el.style.transition = "opacity 0.4s"; el.style.opacity = "0"; setTimeout(() => el.remove(), 400); }, 4000);
  }
};

// ===== CONTACT BUBBLE =====
const ContactBubble = (() => {
  const init = () => {
    const toggle = document.querySelector(".contact-toggle");
    const popup = document.querySelector(".contact-popup");
    if (!toggle || !popup) return;

    toggle.addEventListener("click", () => {
      popup.classList.toggle("open");
    });

    // Close on outside click
    document.addEventListener("click", (e) => {
      if (!e.target.closest(".contact-bubble-wrap")) {
        popup.classList.remove("open");
      }
    });

    const closeBtn = popup.querySelector(".contact-popup-close");
    if (closeBtn)
      closeBtn.addEventListener("click", () => popup.classList.remove("open"));
  };
  return { init };
})();

// ===== FILTER (AJAX) =====
const ShopFilter = (() => {
  let debounceTimer;
  const FILTER_KEYS = [
    "price",
    "category",
    "sort",
    "q",
  ];

    const getParams = () => {
      const params = new URLSearchParams();
      // Price: single-choice
      const selectedPrice = document.querySelector('input[name="price"]:checked');
      if (selectedPrice) params.set("price", selectedPrice.value);
      // Category: multi-select
      document.querySelectorAll('input[name="category"]:checked').forEach((cb) => {
        params.append("category", cb.value);
      });
      // Sort
    const sort = document.querySelector(".sort-select");
    if (sort && sort.value && sort.value !== "default") {
      params.set("sort", sort.value);
    }
    // Search keyword (header search or shop page search)
    const kw = document.querySelector(".shop-search-input") || document.querySelector(".search-input");
    if (kw && kw.value.trim()) params.set("q", kw.value.trim());
    return params;
  };

  const buildShopUrl = (params) => {
    const url = new URL(window.location.origin + window.location.pathname);
    params.forEach((value, key) => url.searchParams.append(key, value));
    return url;
  };

  const updateResults = () => {
    const grid = document.querySelector("#product-grid");
    if (!grid) return;

    // Show loading state
    grid.style.opacity = "0.5";

    const params = getParams();
    const url = buildShopUrl(params);
    window.location.href = url.toString();
  };

  const init = () => {
    const filterForm = document.querySelector("#filter-form");
    if (!filterForm) return;

    // Normalize legacy URLs that may contain multiple checked price values.
    const priceChecked = filterForm.querySelectorAll(
      'input[name="price"]:checked',
    );
    if (priceChecked.length > 1) {
      priceChecked.forEach((el, idx) => {
        if (idx > 0) el.checked = false;
      });
    }

    filterForm.querySelectorAll('input[type="checkbox"]').forEach((cb) => {
      cb.addEventListener("change", () => {
        if (cb.name === "price" && cb.checked) {
          filterForm.querySelectorAll('input[name="price"]').forEach((el) => {
            if (el !== cb) el.checked = false;
          });
        }
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(updateResults, 400);
      });
    });

    const sortSelect = document.querySelector(".sort-select");
    if (sortSelect) sortSelect.addEventListener("change", updateResults);

    // Shop search input with debounce
    const shopSearch = document.querySelector(".shop-search-input");
    if (shopSearch) {
      shopSearch.addEventListener("input", () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(updateResults, 500);
      });
      shopSearch.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
          clearTimeout(debounceTimer);
          updateResults();
        }
      });
    }

    // Clear all filters
    const clearBtn = document.querySelector(".filter-clear-all");
    if (clearBtn) {
      clearBtn.addEventListener("click", () => {
        filterForm
          .querySelectorAll('input[type="checkbox"]')
          .forEach((cb) => (cb.checked = false));
        if (sortSelect) sortSelect.value = "default";
        updateResults();
      });
    }

    // Clear category filters only
    const clearCategoryBtn = document.querySelector(".filter-clear-category");
    if (clearCategoryBtn) {
      clearCategoryBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        filterForm
          .querySelectorAll('input[name="category"]')
          .forEach((cb) => (cb.checked = false));
        updateResults();
      });
    }

    // Collapsible filter sections
    document.querySelectorAll(".filter-section-title").forEach((title) => {
      title.addEventListener("click", () => {
        title.classList.toggle("collapsed");
        const options = title.nextElementSibling;
        if (options)
          options.style.display = title.classList.contains("collapsed")
            ? "none"
            : "";
      });
    });
  };

  return { init, updateResults };
})();

// ===== QTY CONTROLS =====
const QtyControl = (() => {
  const init = () => {
    document.addEventListener("click", (e) => {
      if (e.target.closest(".qty-btn")) {
        const btn = e.target.closest(".qty-btn");
        const input = btn.parentElement.querySelector(".qty-input");
        if (!input) return;
        let val = parseInt(input.value) || 1;
        val = btn.dataset.action === "minus" ? Math.max(1, val - 1) : val + 1;
        input.value = val;
        input.dispatchEvent(new Event("change"));
      }
    });
  };
  return { init };
})();

// ===== ADD TO CART BUTTONS =====
const initAddToCartButtons = () => {
  document.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-add-cart]");
    if (!btn) return;
    if (btn.disabled) return;
    const id = btn.dataset.id;
    const name = btn.dataset.name;
    const price = parseFloat(btn.dataset.price);
    const img = btn.dataset.img || "";
    const brand = btn.dataset.brand || "";
    Cart.add(id, name, price, img, brand);

    // Animate button
    btn.classList.add("adding");
    setTimeout(() => btn.classList.remove("adding"), 800);
  });
};

// ===== CHECKOUT - LOGIN GUARD =====
const initCheckoutGuard = () => {
  const checkoutBtn = document.querySelector("[data-checkout]");
  if (!checkoutBtn) return;
  checkoutBtn.addEventListener("click", (e) => {
    const isLoggedIn = document.querySelector('.user-dropdown') !== null;
    if (!isLoggedIn) {
      e.preventDefault();
      const overlay = document.querySelector(".login-required-overlay");
      if (overlay) overlay.style.display = "flex";
    }
  });

  const overlay = document.querySelector(".login-required-overlay");
  if (overlay) {
    const closeBtn = overlay.querySelector("[data-modal-close]");
    if (closeBtn)
      closeBtn.addEventListener(
        "click",
        () => (overlay.style.display = "none"),
      );
    overlay.addEventListener("click", (e) => {
      if (e.target === overlay) overlay.style.display = "none";
    });
  }
};

// ===== PAYMENT OPTIONS =====
const initPaymentOptions = () => {
  document.querySelectorAll(".payment-option").forEach((opt) => {
    opt.addEventListener("click", () => {
      document
        .querySelectorAll(".payment-option")
        .forEach((o) => o.classList.remove("selected"));
      opt.classList.add("selected");
      const radio = opt.querySelector('input[type="radio"]');
      if (radio) radio.checked = true;
    });
  });
};

// ===== INIT ALL =====
document.addEventListener("DOMContentLoaded", () => {
  Cart.init();
  ContactBubble.init();
  ShopFilter.init();
  QtyControl.init();
  initAddToCartButtons();
  initCheckoutGuard();
  initPaymentOptions();
});
