// ═══════════════════════════════════════════════════════════════
//  admin.js — Core utilities, API layer, Router, DeleteConfirm
//  Shared across ALL admin pages
// ═══════════════════════════════════════════════════════════════

/* ───────────────────────────────────────────────────────────────
   DB CONFIG  ← Set apiBase and token once DB is connected
─────────────────────────────────────────────────────────────── */
const pathPrefixBeforeAdmin = window.location.pathname.split("/admin")[0] || "";
const APP_CONTEXT_PATH =
  document.body?.dataset?.contextPath || pathPrefixBeforeAdmin;

const DB_CONFIG = {
  apiBase: `${APP_CONTEXT_PATH}/admin`,
};

/* ───────────────────────────────────────────────────────────────
   MOCK DATA  ← Remove this entire block after DB is wired
─────────────────────────────────────────────────────────────── */
const MOCK = {
  orders: [],
  orderDetails: {},
  users: [],
  products: [],
  categories: [],
};

/* ───────────────────────────────────────────────────────────────
   API LAYER
   Replace each function body with real fetch() when DB ready
─────────────────────────────────────────────────────────────── */
const API = {
  _h() {
    return {
      "Content-Type": "application/json",
    };
  },

  // ── ORDERS ──────────────────────────────────────────────────
  async getOrders() {
    return MOCK.orders; /* await (await fetch(`${DB_CONFIG.apiBase}/orders`,{headers:this._h()})).json() */
  },
  async getOrderById(id) {
    return (
      MOCK.orderDetails[id] ?? {
        ...MOCK.orders.find((o) => o.id === id),
        shipping_address_id: 10,
        payment_method_id: 2,
        shipping_method_id: 1,
        subtotal: 800000,
        total_amount: 850000,
      }
    );
  },
  async updateOrder(id, d) {
    console.log("UPDATE ORDER", id, d);
    return true;
  },
  async deleteOrder(id) {
    console.log("DELETE ORDER", id);
    return true;
  },

  // ── USERS ────────────────────────────────────────────────────
  async getUsers() {
    const response = await fetch(`${DB_CONFIG.apiBase}/user/all`, {
      headers: this._h(),
    });
    if (!response.ok) {
      throw new Error("Khong the tai danh sach user");
    }
    return response.json();
  },
  async createUser(d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/user/add`, {
      method: "POST",
      headers: this._h(),
      body: JSON.stringify(d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the tao user");
    }
    return response.json();
  },
  async updateUser(id, d) {
    const response = await fetch(`${DB_CONFIG.apiBase}/user/${id}`, {
      method: "PUT",
      headers: this._h(),
      body: JSON.stringify(d),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the cap nhat user");
    }
    return response.json();
  },
  async deleteUser(id) {
    const response = await fetch(`${DB_CONFIG.apiBase}/user/${id}`, {
      method: "DELETE",
      headers: this._h(),
    });
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Khong the xoa user");
    }
    return true;
  },
  async restoreUser(id) {
    const res = await fetch(`${DB_CONFIG.apiBase}/user/${id}/restore`, {
      method: "POST",
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể phục hồi tài khoản");
    return res.json();
  },

  // ── PRODUCTS ─────────────────────────────────────────────────
  async getProducts() {
    const res = await fetch(`${DB_CONFIG.apiBase}/product/all`, {
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể tải sản phẩm");
    return res.json();
  },
  async getProductById(id) {
    const res = await fetch(`${DB_CONFIG.apiBase}/product/${id}`, {
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể tải thông tin sản phẩm");
    return res.json();
  },
  async createProduct(d) {
    const res = await fetch(`${DB_CONFIG.apiBase}/product/add`, {
      method: "POST",
      headers: this._h(),
      body: JSON.stringify(d),
    });
    if (!res.ok) throw new Error("Không thể tạo sản phẩm");
    return res.json();
  },
  async updateProduct(id, d) {
    const res = await fetch(`${DB_CONFIG.apiBase}/product/${id}`, {
      method: "PUT",
      headers: this._h(),
      body: JSON.stringify(d),
    });
    if (!res.ok) throw new Error("Không thể cập nhật sản phẩm");
    return res.json();
  },
  async deleteProduct(id) {
    const res = await fetch(`${DB_CONFIG.apiBase}/product/${id}`, {
      method: "DELETE",
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể xóa sản phẩm");
    return res.json();
  },
  async restoreProduct(id) {
    const res = await fetch(`${DB_CONFIG.apiBase}/product/${id}/restore`, {
      method: "POST",
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể phục hồi sản phẩm");
    return res.json();
  },

  // ── CATEGORIES ──────────────────────────────────────────────
  async getCategories() {
    const res = await fetch(`${DB_CONFIG.apiBase}/category/all`, {
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể tải danh mục");
    return res.json();
  },
  async createCategory(d) {
    const res = await fetch(`${DB_CONFIG.apiBase}/category/add`, {
      method: "POST",
      headers: this._h(),
      body: JSON.stringify(d),
    });
    if (!res.ok) throw new Error("Không thể tạo danh mục");
    return res.json();
  },
  async updateCategory(id, d) {
    const res = await fetch(`${DB_CONFIG.apiBase}/category/${id}`, {
      method: "PUT",
      headers: this._h(),
      body: JSON.stringify(d),
    });
    if (!res.ok) throw new Error("Không thể cập nhật danh mục");
    return res.json();
  },
  async deleteCategory(id) {
    const res = await fetch(`${DB_CONFIG.apiBase}/category/${id}`, {
      method: "DELETE",
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể xóa danh mục");
    return res.json();
  },
  async restoreCategory(id) {
    const res = await fetch(`${DB_CONFIG.apiBase}/category/${id}/restore`, {
      method: "POST",
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể phục hồi danh mục");
    return res.json();
  },

  // ── CARTS ───────────────────────────────────────────────────
  async getCarts() {
    const res = await fetch(`${DB_CONFIG.apiBase}/cart/all`, {
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể tải giỏ hàng");
    return res.json();
  },
  async getCartById(id) {
    const res = await fetch(`${DB_CONFIG.apiBase}/cart/${id}`, {
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể tải thông tin giỏ hàng");
    return res.json();
  },
  async addCartItem(cartId, d) {
    const res = await fetch(`${DB_CONFIG.apiBase}/cart/${cartId}/items`, {
      method: "POST",
      headers: this._h(),
      body: JSON.stringify(d),
    });
    if (!res.ok) throw new Error("Không thể thêm mục vào giỏ");
    return res.json();
  },
  async updateCartItemQuantity(cartId, itemId, quantity) {
    const res = await fetch(`${DB_CONFIG.apiBase}/cart/${cartId}/items/${itemId}`, {
      method: "PUT",
      headers: this._h(),
      body: JSON.stringify({ quantity }),
    });
    if (!res.ok) throw new Error("Không thể cập nhật số lượng");
    return res.json();
  },
  async removeCartItem(cartId, itemId) {
    const res = await fetch(`${DB_CONFIG.apiBase}/cart/${cartId}/items/${itemId}`, {
      method: "DELETE",
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể xóa mục khỏi giỏ");
    return res.json();
  },
  async deleteCart(id) {
    const res = await fetch(`${DB_CONFIG.apiBase}/cart/${id}`, {
      method: "DELETE",
      headers: this._h(),
    });
    if (!res.ok) throw new Error("Không thể xóa giỏ hàng");
    return res.json();
  },

  // ── DASHBOARD STATS ──────────────────────────────────────────
  // Replace with: await (await fetch(`${DB_CONFIG.apiBase}/dashboard/stats`,{headers:this._h()})).json()
  async getDashboardStats() {
    const [orders, users, products] = await Promise.all([
      this.getOrders(),
      this.getUsers(),
      this.getProducts(),
    ]);
    return {
      totalRevenue: orders
        .filter((o) => o.status === "completed")
        .reduce((s, o) => s + o.total, 0),
      totalOrders: orders.length,
      pendingOrders: orders.filter((o) => o.status === "pending").length,
      activeProducts: products.filter((p) => p.stock_quantity > 0).length,
      lowStock: products.filter((p) => p.stock_quantity < 50).length,
      totalUsers: users.filter(
        (u) => String(u.role || "").toUpperCase() === "CUSTOMER",
      ).length,
      recentOrders: orders.slice(0, 5),
    };
  },
};

/* ───────────────────────────────────────────────────────────────
   UTILS
─────────────────────────────────────────────────────────────── */
const Utils = {
  formatCurrency(n) {
    return new Intl.NumberFormat("vi-VN", {
      style: "currency",
      currency: "VND",
    }).format(n);
  },

  statusBadge(s) {
    const map = {
      completed: ["badge-success", "✓ Hoàn thành"],
      pending: ["badge-warning", "⏳ Chờ xử lý"],
      shipping: ["badge-info", "🚚 Đang giao"],
      cancelled: ["badge-danger", "✗ Đã hủy"],
    };
    const [cls, lbl] = map[s] || ["badge-gray", s];
    return `<span class="badge ${cls}">${lbl}</span>`;
  },

  roleBadge(r) {
    const normalizedRole = String(r || "").toUpperCase();
    return normalizedRole === "ADMIN"
      ? `<span class="badge badge-purple">👑 Admin</span>`
      : `<span class="badge badge-info">👤 User</span>`;
  },

  userStatusBadge(s) {
    const normalizedStatus = String(s || "").toUpperCase();
    return normalizedStatus === "ACTIVE"
      ? `<span class="badge badge-success">● Active</span>`
      : `<span class="badge badge-danger">● Inactive</span>`;
  },

  validBadge(v) {
    return v
      ? `<span class="badge badge-success">✓ Valid</span>`
      : `<span class="badge badge-danger">✗ Invalid</span>`;
  },

  toast(msg, type = "success") {
    let c = document.getElementById("toastContainer");
    if (!c) {
      c = document.createElement("div");
      c.id = "toastContainer";
      c.className = "toast-container";
      document.body.appendChild(c);
    }
    const t = document.createElement("div");
    const icons = { success: "✓", error: "✗", warn: "⚠" };
    t.className = `toast ${type}`;
    t.innerHTML = `<span>${icons[type] || "✓"}</span> ${msg}`;
    c.appendChild(t);
    setTimeout(() => {
      t.style.opacity = "0";
      t.style.transition = "opacity .3s";
      setTimeout(() => t.remove(), 300);
    }, 3000);
  },

  initChartBars() {
    document.querySelectorAll(".chart-bar").forEach((b) => {
      b.style.height = "0%";
    });
  },
};

/* ───────────────────────────────────────────────────────────────
   CONFIRM MODAL  (dùng chung cho xóa & phục hồi)
─────────────────────────────────────────────────────────────── */
const DeleteConfirm = {
  _cb: null,

  show(label, onConfirm, opts = {}) {
    this._cb = onConfirm;
    const overlay = document.getElementById("deleteModalOverlay");
    if (!overlay) return;

    const icon = overlay.querySelector(".confirm-icon");
    const title = overlay.querySelector(".confirm-title");
    const text = overlay.querySelector(".confirm-text");
    const btn = document.getElementById("confirmDeleteBtn");
    const target = document.getElementById("deleteTargetLabel");

    const cfg = {
      icon: "🗑️",
      title: "Xác nhận xóa",
      buttonText: "Xác nhận xóa",
      buttonClass: "btn-danger",
      confirmPrefix: "Bạn đang xóa",
      confirmSuffix: "",
      ...opts,
    };

    icon.textContent = cfg.icon;
    title.textContent = cfg.title;
    btn.textContent = cfg.buttonText;
    btn.className = `btn ${cfg.buttonClass}`;
    target.textContent = label;
    text.innerHTML =
      `${cfg.confirmPrefix}<br /><strong id="deleteTargetLabel">${label}</strong>${cfg.confirmSuffix ? `<br />${cfg.confirmSuffix}` : ""}`;
    btn.disabled = false;
    overlay.classList.add("open");
  },

  hide() {
    const overlay = document.getElementById("deleteModalOverlay");
    if (overlay) overlay.classList.remove("open");
  },

  confirm() {
    if (this._cb) this._cb();
    this.hide();
  },
};

/* ───────────────────────────────────────────────────────────────
   MODAL HELPERS
─────────────────────────────────────────────────────────────── */
const Modal = {
  open(id) {
    const el = document.getElementById(id);
    if (el) {
      el.classList.add("open");
    }
  },
  close(id) {
    const el = document.getElementById(id);
    if (el) {
      el.classList.remove("open");
    }
  },
  closeAll() {
    document
      .querySelectorAll(".modal-overlay")
      .forEach((el) => el.classList.remove("open"));
  },
};

/* ───────────────────────────────────────────────────────────────
   LOGOUT / SESSION
─────────────────────────────────────────────────────────────── */
function logout() {
  if (!confirm("Bạn có chắc muốn đăng xuất?")) return;
  closeLogoutDropdown();

  fetch(`${APP_CONTEXT_PATH}/api/auth/admin/logout`, {
    method: "POST",
    credentials: "same-origin",
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Logout failed");
      }
      Utils.toast("Đã đăng xuất thành công", "success");
      setTimeout(() => {
        window.location.href = `${APP_CONTEXT_PATH}/admin/login`;
      }, 300);
    })
    .catch(() => {
      Utils.toast("Không thể đăng xuất. Vui lòng thử lại.", "error");
    });
}

function toggleLogoutDropdown() {
  const menu = document.getElementById("logoutDropdown");
  if (!menu) return;
  menu.style.display = menu.style.display === "block" ? "none" : "block";
}

function closeLogoutDropdown() {
  const menu = document.getElementById("logoutDropdown");
  if (menu) menu.style.display = "none";
}

/* ───────────────────────────────────────────────────────────────
   ACTIVE NAV LINK  — call on each page after DOMContentLoaded
─────────────────────────────────────────────────────────────── */
function setActiveNav(page) {
  document.querySelectorAll(".nav-item[data-page]").forEach((el) => {
    el.classList.toggle("active", el.dataset.page === page);
  });
}

/* ───────────────────────────────────────────────────────────────
   CLOSE MODAL ON OVERLAY CLICK
─────────────────────────────────────────────────────────────── */
document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".modal-overlay").forEach((overlay) => {
    overlay.addEventListener("click", (e) => {
      if (e.target === overlay && overlay.id !== "deleteModalOverlay") {
        overlay.classList.remove("open");
      }
    });
  });

  document.addEventListener("click", (e) => {
    const dropdown = document.getElementById("logoutDropdown");
    const settingsBtn = document.getElementById("settingsBtn");
    if (!dropdown || !settingsBtn) return;
    if (settingsBtn.contains(e.target) || dropdown.contains(e.target)) return;
    dropdown.style.display = "none";
  });

  Utils.initChartBars();
});
