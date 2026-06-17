DROP DATABASE IF EXISTS EcommerceDB;
CREATE DATABASE EcommerceDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE EcommerceDB;

CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    FullName VARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Phone VARCHAR(20),
    Role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    Status ENUM('ACTIVE','BLOCKED') DEFAULT 'ACTIVE',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    DeletedAt DATETIME DEFAULT NULL 
);

CREATE TABLE UserAddresses (
    AddressID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    ReceiverName VARCHAR(100) NOT NULL,
    ReceiverPhone VARCHAR(20) NOT NULL,
    Province VARCHAR(100) NOT NULL,
    District VARCHAR(100) NOT NULL,
    Ward VARCHAR(100) NOT NULL,
    StreetAddress VARCHAR(255) NOT NULL,
    IsDefault BOOLEAN DEFAULT FALSE,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    DeletedAt DATETIME DEFAULT NULL, 
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

CREATE TABLE Categories (
    CategoryID INT AUTO_INCREMENT PRIMARY KEY,
    CategoryName VARCHAR(100) NOT NULL,
    Description TEXT,
    DeletedAt DATETIME DEFAULT NULL 
);

CREATE TABLE Products (
    ProductID INT AUTO_INCREMENT PRIMARY KEY,
    ProductName VARCHAR(200) NOT NULL,
    Description TEXT,
    Price DECIMAL(12,2) NOT NULL,
    ImageURL VARCHAR(255),
    CategoryID INT,
    Quantity INT DEFAULT 0, 
    Status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    DeletedAt DATETIME DEFAULT NULL,
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE SET NULL
);

CREATE TABLE Carts (
    CartID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL UNIQUE, 
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
);

CREATE TABLE CartItems (
    CartItemID INT AUTO_INCREMENT PRIMARY KEY,
    CartID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    FOREIGN KEY (CartID) REFERENCES Carts(CartID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE
);

CREATE TABLE Orders (
    OrderID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    CustomerName VARCHAR(100) NOT NULL,
    CustomerPhone VARCHAR(20) NOT NULL,
    OrderDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    TotalAmount DECIMAL(12,2) NOT NULL,
    Status ENUM('PENDING','CONFIRMED','SHIPPING','COMPLETED','CANCELLED') DEFAULT 'PENDING',
    
    -- Thông tin địa chỉ đóng băng theo đơn hàng
    ReceiverName VARCHAR(100) NOT NULL,
    ReceiverPhone VARCHAR(20) NOT NULL,
    Province VARCHAR(100) NOT NULL,
    District VARCHAR(100) NOT NULL,
    Ward VARCHAR(100) NOT NULL,
    StreetAddress VARCHAR(255) NOT NULL,
    
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE RESTRICT 
);

CREATE TABLE OrderItems (
    OrderItemID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    ProductID INT, 
    ProductName VARCHAR(200) NOT NULL, 
    Price DECIMAL(12,2) NOT NULL,       
    Quantity INT NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE,
    CONSTRAINT FK_OrderItems_Products FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE SET NULL
);

CREATE TABLE OrderHistory (
    HistoryID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL,
    Status ENUM('PENDING','CONFIRMED','SHIPPING','COMPLETED','CANCELLED') NOT NULL, 
    Note VARCHAR(255) NOT NULL, 
    ChangeTime DATETIME DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE
);

CREATE TABLE Payments (
    PaymentID INT AUTO_INCREMENT PRIMARY KEY,
    OrderID INT NOT NULL UNIQUE,
    PaymentMethod ENUM('COD', 'VNPAY', 'BANK_TRANSFER') DEFAULT 'COD',
    TransactionNo VARCHAR(100),
    Amount DECIMAL(12,2) NOT NULL,
    PaymentStatus ENUM('PENDING', 'SUCCESS', 'FAILED') DEFAULT 'PENDING',
    PaidAt DATETIME,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE
);

INSERT INTO Categories (CategoryID, CategoryName, Description) VALUES
(1, 'Sữa Tăng Cơ & Whey Protein', 'Các sản phẩm bổ sung Protein, Whey Isolate, Hydrolyzed hỗ trợ phát triển cơ bắp.'),
(2, 'Bánh Protein & Bữa Phụ Dinh Dưỡng', 'Bánh quy, thanh năng lượng giàu protein tiện lợi cho người tập thể hình.');

INSERT INTO Products (ProductID, ProductName, Description, Price, ImageURL, CategoryID, Quantity, Status) VALUES
-- SẢN PHẨM 1-10
(1, 'Labrada Lean Body Protein Shake gói lẻ', '1 gói Lean Body 79g cung cấp: 325 calories, 21g carbohydrate, 40g protein Bữa phụ hoàn hảo, hỗ trợ tăng cơ.', 85000.00, 'https://www.wheystore.vn/images/products/2024/01/23/small/lean-body-79g_1705983462.jpg', 1, 100, 'ACTIVE'),
(2, 'Labrada Lean Body Protein Shake 80 gói', 'Hộp Lean Body 80 gói: Bữa ăn thay thế tiện lợi, thông minh. Bổ sung 40G Protein chất lượng cao.', 6490000.00, 'https://www.wheystore.vn/images/products/2024/01/23/small/combo-80-goi-lean-body_1705983855.jpg', 1, 100, 'ACTIVE'),
(3, 'Applied Nutrition Critical Cookie', 'Mỗi chiếc bánh 85g cung cấp: 20g Protein, 390-391kcal. Cung cấp đầy đủ dưỡng chất cho 1 bữa phụ.', 70000.00, 'https://www.wheystore.vn/images/products/2024/02/05/small/1-banh-critical-cookie_1707123160.jpg', 2, 100, 'ACTIVE'),
(4, 'Applied Nutrition Critical Cookie 12 bánh', 'Hộp 12 bánh Critical Cookie, mỗi bánh 85g cung cấp: 20g Protein, 390-391kcal.', 740000.00, 'https://www.wheystore.vn/images/products/2024/02/05/small/12-banh-critical-cookie_1707123280.jpg', 2, 100, 'ACTIVE'),
(5, 'ON Platinum Hydro Whey 3.5lbs', '30g Protein Hydrolyzed siêu tinh khiết, 15,5g EAA, 8,8g BCAA nâng cao sức bền tập luyện.', 1400000.00, 'https://www.wheystore.vn/images/products/2023/11/23/small/platinum-hydro-whey-3-5lbs_1700708519.jpg', 1, 100, 'ACTIVE'),
(6, 'Hydrolyzed Whey Protein 4.5lbs', '100% Hydrolyzed Whey Protein, 28g Protein siêu tinh khiết, hấp thu nhanh.', 1350000.00, 'https://www.wheystore.vn/images/products/2023/11/28/small/hydropure-4-5lbs_1701141512.jpg', 1, 100, 'ACTIVE'),
(7, 'VX Iso Pro Hydrolyzed Whey Isolate 8lbs', '100% Hydrolyzed Whey Isolate hấp thụ cực nhanh, 27g Protein hỗ trợ phát triển cơ bắp.', 2450000.00, 'https://www.wheystore.vn/images/products/2023/12/15/small/iso-pro-8lbs_1702634300.jpg', 1, 100, 'ACTIVE'),
(8, 'Applied Nutrition Clear Whey Protein 425g', 'Hydrolysed Whey Protein 21.27g Protein, 4874mg BCAA. Hương vị trái cây tự nhiên.', 480000.00, 'https://www.wheystore.vn/images/products/2024/04/05/small/applied-nutrition-clear-whey-protein-425g_1712303860.jpg', 1, 100, 'ACTIVE'),
(9, 'Applied Nutrition Swirl Duo Bar', 'Thanh bánh protein giàu dinh dưỡng, 1 thanh cung cấp 16g protein, ít đường.', 65000.00, 'https://www.wheystore.vn/images/products/2024/01/31/small/thiet-ke-chua-co-ten_1706689421.jpg', 2, 100, 'ACTIVE'),
(10, 'Warrior Crunch Protein Bar 12 thanh', 'Bánh protein chất lượng, giàu đạm. 1 thanh 75g cung cấp 20g protein, 259 Calo.', 690000.00, 'https://www.wheystore.vn/images/products/2024/01/31/small/hop-12-banh-warrior-crunch-bar_1706689228.jpg', 2, 100, 'ACTIVE'),

-- SẢN PHẨM 11-20
(11, 'Warrior RAW Protein Flap Jack 12 thanh', 'Hộp 12 thanh tiết kiệm, thanh bánh protein giàu dinh dưỡng, nhiều hương vị thơm ngon.', 750000.00, 'https://www.wheystore.vn/images/products/2024/02/01/small/hop-12-banh-warrior-raw-protein-flap-jack_1706753568.jpg', 2, 100, 'ACTIVE'),
(12, 'Applied Nutrition Swirl Duo Bar 12 thanh', 'Hộp 12 thanh bánh protein giàu dinh dưỡng, cung cấp năng lượng tức thì.', 690000.00, 'https://www.wheystore.vn/images/products/2024/02/05/small/12-banh-applied-nutrition-swirl_1707120082.jpg', 2, 100, 'ACTIVE'),
(13, 'Warrior Crunch Protein Bar', 'Bánh dẻo mịn bên trong, giòn xốp bên ngoài. Cung cấp 20g protein.', 65000.00, 'https://www.wheystore.vn/images/products/2024/02/05/small/1-banh-warrior-crunch-bar_1707120605.jpg', 2, 100, 'ACTIVE'),
(14, 'Applied Nutrition Protein Crunch Bar 12 thanh', '1 thanh cung cấp 20 gram protein, 214 kcal. Hộp 12 thanh tiết kiệm.', 690000.00, 'https://www.wheystore.vn/images/products/2024/02/05/resized/12-banh-applied-nutrition-protein-crunch-60g_1707123723.jpg', 2, 100, 'ACTIVE'),
(15, 'Sample Rule1 Protein 30.4g', 'Gói dùng thử Rule1 Protein 30.4g cung cấp Whey Protein Isolate và Hydrolysate.', 30000.00, 'https://www.wheystore.vn/images/products/2024/03/05/resized/sample-rule-1-1-serving_1709604941.jpg', 1, 100, 'ACTIVE'),
(16, 'PVL ISO Gold Premium Whey Protein With Probiotic 2lbs', '27G Protein Whey Isolate & Hydrolysate, 6g BCAAs, 1 Tỷ Probiotic lợi khuẩn.', 950000.00, 'https://www.wheystore.vn/images/products/2024/03/28/small/pvl-iso-gold-2lbs_1711619980.jpg', 1, 100, 'ACTIVE'),
(17, 'Hydrolyzed Whey Protein Isolate 4lbs', 'Labrada Pro Series Hydro - 100% Hydrolyzed Whey Protein Isolate hấp thụ nhanh.', 1500000.00, 'https://www.wheystore.vn/images/products/2026/03/10/small/labrada-pro-series-hydro-100-hydrolyzed-whey-protein-isolate-4lbs_1773128179.jpg', 1, 100, 'ACTIVE'),
(18, 'Elite Labs USA 100', '100% IsoBlend 4lbs cung cấp 25g Protein, 5.3g BCAAs chống dị hóa.', 1200000.00, 'https://www.wheystore.vn/images/products/2024/06/28/resized/elitelabs-100-isoblend-4lbs_1719567451.jpg', 1, 100, 'ACTIVE'),
(19, 'Applied Nutrition Diet Whey Protein 1kg', '20g protein, 4.3g BCAA. Hỗ trợ giảm mỡ với CLA, L-Carnitine, Green Tea Extract.', 890000.00, 'https://www.wheystore.vn/images/products/2024/09/21/small/applied-nutrition-diet-whey-protein-1kg_1726890272.jpg', 1, 100, 'ACTIVE'),
(20, 'Mutant Iso Surge 1.6lbs', 'Whey Protein Isolate và Hydrolysate hấp thụ cực nhanh. 25g Protein, 5.5b BCAAs.', 1000000.00, 'https://www.wheystore.vn/images/products/2025/07/12/small/mutant-iso-surge-1-6-lbs_1752294187.jpg', 1, 100, 'ACTIVE'),

-- SẢN PHẨM 21-30
(21, 'Pure Whey 2270g', 'BiotechUSA 100% Pure Whey cung cấp 21g Protein, hàm lượng amino axit cao.', 1500000.00, 'https://www.wheystore.vn/images/products/2025/07/24/small/biotech-usa-100-pure-whey-81-servings_1753346721.png', 1, 100, 'ACTIVE'),
(22, 'Beverly Hydrolyzed Whey Delicatesse 2.2lbs', 'Cung cấp 28,5-39,5g protein, 4.8g BCAAs và 10.5g EAAs giảm đau nhức cơ bắp.', 1350000.00, 'https://www.wheystore.vn/images/products/2025/10/31/small/beverly-hydrolyzed-whey-delicatesse-2-2lbs_1761880514.jpg', 1, 100, 'ACTIVE'),
(23, 'Beverly Isolate CFM Professional 2.2lbs', '100% Premium Lacprodan Whey Isolate cung cấp 30g protein, 8.9g BCAAs.', 1290000.00, 'https://www.wheystore.vn/images/products/2025/10/31/small/beverly-isolate-cfm-professional-2-2lbs_1761875692.jpg', 1, 100, 'ACTIVE'),
(24, 'Allmax Isoflex 2lbs', 'Cung cấp 27g protein, 120 calories. Hỗ trợ phát triển cơ bắp tối đa.', 1250000.00, 'https://www.wheystore.vn/images/products/2025/12/19/small/allmax-isoflex-2lbs_1766111390.png', 1, 100, 'ACTIVE'),
(25, 'Rule1 Protein 1.98lbs', 'Kết hợp 2 loại Whey Isolate và Hydrolyzed Whey. 25g protein, 11g EAAs.', 1020000.00, 'https://www.wheystore.vn/images/products/2026/01/08/small/rule-1-protein-1-98lbs_1767854498.jpg', 1, 100, 'ACTIVE'),
(26, 'Ronnie Coleman Iso Tropic Max 5lbs', 'Whey isolate cao cấp cung cấp 25g protein, 110 kcal giúp tăng cơ nạc.', 2200000.00, 'https://www.wheystore.vn/images/products/2026/01/20/small/ronnie-coleman-iso-tropic-max-5lbs_1768887828.jpg', 1, 100, 'ACTIVE'),
(27, 'ON Gold Standard 100', 'Gold Standard 100% Whey 5lbs cung cấp 24g Protein, 5.5g BCAAs.', 1500000.00, 'https://www.wheystore.vn/images/products/2023/12/13/small/gold-standard-100-whey-5lbs-anh-dai-dien_1702452595.jpg', 1, 100, 'ACTIVE'),
(28, 'PVL ISO Gold Premium Whey Protein With Probiotic 5lbs', '27G Protein Isolate & Hydrolysate, 1 Tỷ Probiotic lợi khuẩn hỗ trợ tiêu hóa.', 1800000.00, 'https://www.wheystore.vn/images/products/2023/12/05/small/iso-gold-5lbs_1701769926.jpg', 1, 100, 'ACTIVE'),
(29, 'ON Gold Standard 100', 'Size 10lbs to hơn, tiết kiệm hơn. Cung cấp 24g Protein (chiếm 79%).', 2800000.00, 'https://www.wheystore.vn/images/products/2023/11/28/small/gold-standard-100-whey-10lbs_1701135152.jpg', 1, 100, 'ACTIVE'),
(30, 'ON Gold Standard 100', 'Size 2lbs nhỏ gọn. Cung cấp 24g Protein, nhiều hơn 5 grams BCAAs.', 900000.00, 'https://www.wheystore.vn/images/products/2023/11/28/small/gold-standard-100-whey-2lbs_1701143321.jpg', 1, 100, 'ACTIVE'),

-- SẢN PHẨM 31-40
(31, 'Rule1 Protein 5lbs', '25g Protein chất lượng từ Whey Isolate & Hydrolyzed, 6g BCAA.', 1600000.00, 'https://www.wheystore.vn/images/products/2023/11/22/small/rule1-protein-5lbs_1700622059.jpg', 1, 100, 'ACTIVE'),
(32, 'Mutant Iso Surge 5lbs', 'Sự kết hợp giữa Whey cô lập và Whey thủy phân. 25g Protein chất lượng.', 1800000.00, 'https://www.wheystore.vn/images/products/2024/10/28/small/mutant-iso-surge-5lbs_1730079173.jpg', 1, 100, 'ACTIVE'),
(33, 'Amix Gold Isolate Whey Protein 5lbs', '100% Whey Protein Isolate, tỷ lệ Protein lên tới 86%.', 1700000.00, 'https://www.wheystore.vn/images/products/2023/11/17/small/1_1700184620.jpg', 1, 100, 'ACTIVE'),
(34, 'Pure Isolate Protein 5lbs', 'BPI ISO HD cung cấp 25g Whey Protein Isolate siêu tinh khiết.', 1600000.00, 'https://www.wheystore.vn/images/products/2025/06/25/small/bpi-iso-hd-5lbs_1750826723.jpg', 1, 100, 'ACTIVE'),
(35, 'Dymatize ISO 100 Hydrolyzed 5lbs', 'Whey Protein Isolate và Hydrolysate tinh khiết. 25g Protein/Serving.', 2250000.00, 'https://www.wheystore.vn/images/products/2023/11/29/small/iso-100-hydrolyzed-5lbs_1701245491.jpg', 1, 100, 'ACTIVE'),
(36, 'Rule1 Protein 10lbs', 'Whey Isolate tinh khiết và Hydrolysate hấp thụ cực nhanh. Size 10lbs.', 3000000.00, 'https://www.wheystore.vn/images/products/2023/11/30/small/rule-1-10lbs_1701328945.jpg', 1, 100, 'ACTIVE'),
(37, 'VX Iso Pro Hydrolyzed Whey Isolate 5lbs', '100% Hydrolyzed Whey Isolate hấp thụ cực nhanh. 27g Protein.', 1800000.00, 'https://www.wheystore.vn/images/products/2023/12/04/small/iso-pro-5lbs_1701679476.jpg', 1, 100, 'ACTIVE'),
(38, 'BiotechUSA Hydro Whey Zero 4lbs', '18g Protein Whey Hydrolyzed & Isolate, chỉ 85 calo/serving.', 1600000.00, 'https://www.wheystore.vn/images/products/2023/12/01/small/hydro-whey-zero-4lbs_1701422997.jpg', 1, 100, 'ACTIVE'),
(39, 'Warrior RAW Protein Flap Jack', 'Bánh Protein yến mạch cung cấp 259 calories, 20g protein, 34g carbs.', 65000.00, 'https://www.wheystore.vn/images/products/2024/01/31/small/warrior-raw-protein-flap-jack_1706687172.jpg', 2, 100, 'ACTIVE'),
(40, 'BiotechUSA Iso Whey Zero 5lbs', 'Whey Protein Isolate tinh khiết, 21g Protein hỗ trợ phát triển cơ bắp.', 1500000.00, 'https://www.wheystore.vn/images/products/2023/12/05/small/iso-whey-zero-5lbs_1701767513.jpg', 1, 100, 'ACTIVE'),

-- SẢN PHẨM 41-49
(41, 'Applied Nutrition Clear Whey Protein 875g', 'Hydrolysed Whey Protein 21.27g Protein, hương vị trái cây tự nhiên.', 850000.00, 'https://www.wheystore.vn/images/products/2023/12/08/small/clear-whey-protein-875g_1702001868.jpg', 1, 100, 'ACTIVE'),
(42, 'Applied Nutrition ISO-XP Whey Protein Isolate 1.8kg', 'ISO-XP Whey Protein Isolate cung cấp 21.8g Protein tinh khiết.', 1600000.00, 'https://www.wheystore.vn/images/products/2023/12/08/small/iso-xp-1-8-kg_1702023598.jpg', 1, 100, 'ACTIVE'),
(43, 'Redcon1 Ration Whey Protein Blend 5lbs', 'Cung cấp 25g Protein từ Whey Hydrolysate và Concentrate.', 1190000.00, 'https://www.wheystore.vn/images/products/2023/12/16/small/ration-whey-protein-blend-5lbs_1702697045.jpg', 1, 100, 'ACTIVE'),
(44, 'Z Protein Hydrolyzed Whey Protein Isolate 5lbs', '100% Whey Hydrolyzed cao cấp, tốc độ hấp thụ nhanh vượt trội.', 1850000.00, 'https://www.wheystore.vn/images/products/2023/12/16/small/iso-pro-8lbs_1702696522.jpg', 1, 100, 'ACTIVE'),
(45, 'Applied Nutrition Diet Whey Protein 1.8kg', '20g protein, 4.3g BCAA. Hỗ trợ giảm mỡ hiệu quả (CLA, L-Carnitine).', 1400000.00, 'https://www.wheystore.vn/images/products/2023/12/16/small/diet-whey-protein-1-8kg_1702701655.jpg', 1, 100, 'ACTIVE'),
(46, 'VX Iso Pro Hydrolyzed Whey Isolate 2lbs', 'Cung cấp 27G Protein/lần dùng. Không Amino Spiking và chất độn.', 950000.00, 'https://www.wheystore.vn/images/products/2023/12/21/small/iso-pro-2lbs_1703152910.jpg', 1, 100, 'ACTIVE'),
(47, 'Labrada Lean Body Protein Shake 4.63lbs', 'Bữa ăn thay thế hoàn hảo cung cấp 35G Protein, 19 G Carb, 8G Fat.', 1750000.00, 'https://www.wheystore.vn/images/products/2024/01/23/small/lean-body-protein-shake-4-63lbs_1705985676.jpg', 1, 100, 'ACTIVE'),
(48, 'Applied Nutrition Protein Crunch Bar 62g', 'Thanh ăn vặt 62g cung cấp 214 kcal, 20 gram protein tiện lợi.', 60000.00, 'https://www.wheystore.vn/images/products/2024/01/31/resized/applied-nutrition-protein-crunch-60g_1706672725.jpg', 2, 100, 'ACTIVE'),
(49, 'Trọng lượng', 'Sample Iso Whey Zero 1 serving cung cấp 100% Whey Isolate.', 30000.00, 'https://www.wheystore.vn/images/products/2024/01/31/resized/iso-whey-zero-25g_1706673443.jpg', 1, 100, 'ACTIVE');