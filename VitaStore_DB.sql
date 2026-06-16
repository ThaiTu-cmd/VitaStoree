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
(1, 'Whey Protein', 'Các sản phẩm bổ sung Whey Protein tăng cơ, phục hồi cơ bắp'),
(2, 'Pre-Workout', 'Thức uống tăng năng lượng, tỉnh táo trước khi tập luyện'),
(3, 'Protein Bars & Cookies', 'Bánh đạm, snack dinh dưỡng tiện lợi ăn kiêng'),
(4, 'Creatine', 'Thực phẩm bổ sung tăng sức mạnh và kích thước cơ bắp')
AS new_cat 
ON DUPLICATE KEY UPDATE CategoryName = new_cat.CategoryName;

INSERT INTO Products 
  (ProductID, CategoryID, ProductName, Description, Price, ImageURL, Quantity, Status)
VALUES
-- ─── WHEY PROTEIN (category_id = 1, ID 1–21) ────────────────────────────────
(1, 1, 'Optimum Nutrition Gold Standard 100% Whey 5lbs', 'Gold Standard 100% Whey™ là tiêu chuẩn vàng về chất lượng protein với 24g đạm mỗi lần dùng. Thành phần chính là Whey Protein Isolate giúp hấp thụ nhanh sau tập.', 1650000.00, '/uploads/products/image-1.jpg', 50, 'ACTIVE'),
(2, 1, 'Rule 1 Protein Isolate 5lbs', 'Rule 1 Protein sử dụng 100% whey isolate và hydrolyzed, không có tạp chất. Mỗi serving cung cấp 25g protein tinh sạch với hàm lượng đường gần như bằng 0.', 1550000.00, '/uploads/products/image-2.jpg', 50, 'ACTIVE'),
(3, 1, 'Dymatize ISO 100 Hydrolyzed 5lbs', 'ISO 100 của Dymatize là sản phẩm whey hydrolyzed hàng đầu thế giới. Quá trình thủy phân cắt giảm phân tử đạm xuống mức vi mô giúp hấp thu gần như tức thì sau tập.', 2250000.00, '/uploads/products/image-3.jpg', 50, 'ACTIVE'),
(4, 1, 'Muscletech NitroTech Whey Gold 5lbs', 'NitroTech Whey Gold bổ sung 2.5g Creatine và 5.5g BCAA trong mỗi serving, giúp tối ưu hóa tổng hợp protein và hồi phục cơ bắp sau tập luyện cường độ cao.', 1450000.00, '/uploads/products/image-4.jpg', 50, 'ACTIVE'),
(5, 1, 'Nutrabolics Hydropure 100 Hydrolyzed Whey 4.5lbs', 'Hydropure sử dụng công nghệ lọc lạnh vi mô (CFM) độc quyền kết hợp thủy phân bậc cao, tạo ra nguồn đạm 28g/serving tinh khiết tuyệt đối, gần như 0% lactose.', 2550000.00, '/uploads/products/image-5.jpg', 50, 'ACTIVE'),
(6, 1, 'Mutant Iso Surge 5lbs', 'Mutant Iso Surge cung cấp 25g whey isolate tinh khiết mỗi serving với hương vị phong phú, không gây đầy bụng hay khó tiêu. Phù hợp cả người mới lẫn gymer lâu năm.', 1750000.00, '/uploads/products/image-6.jpg', 50, 'ACTIVE'),
(7, 1, 'Allmax Isoflex Whey Isolate 5lbs', 'Isoflex dùng công nghệ lọc trạng thái rắn độc quyền (SSF) loại bỏ hoàn toàn lactose, fat, và tạp chất. Kết quả: 27g protein tinh khiết 99% mỗi serving.', 1950000.00, '/uploads/products/image-7.jpg', 50, 'ACTIVE'),
(8, 1, 'MyProtein Impact Whey Isolate 1kg', 'Impact Whey Isolate của MyProtein đạt tỷ lệ protein 90%, cực kỳ ít carbs và fat. Sản xuất tại Anh theo chuẩn GMP, có rất nhiều hương vị đa dạng.', 850000.00, '/uploads/products/image-8.jpg', 50, 'ACTIVE'),
(9, 1, 'VPX VP2 Whey Isolate 2lbs', 'VP2 Whey Isolate của VPX được phát triển dựa trên nghiên cứu khoa học về cô lập protein vi mô, cung cấp 22g đạm tinh khiết và các chuỗi peptide sinh học tối ưu.', 920000.00, '/uploads/products/image-9.jpg', 50, 'ACTIVE'),
(10, 1, 'BPI Sports ISO HD 5lbs', 'ISO HD cung cấp 25g whey isolate mỗi serving cùng profile EAA đầy đủ. Công thức ít carb phù hợp cho người đang trong giai đoạn siết nạc.', 1390000.00, '/uploads/products/image-10.jpg', 50, 'ACTIVE'),
(11, 1, 'MusclePharm Combat 100% Whey 5lbs', 'Combat 100% Whey kết hợp whey concentrate, isolate và hydrolyzed giải phóng đạm liên tục 4-6 giờ. Bổ sung enzyme tiêu hóa hỗ trợ hấp thu tối đa.', 1420000.00, '/uploads/products/image-11.jpg', 50, 'ACTIVE'),
(12, 1, 'JYM Supplement Science Pro-JYM 4lbs', 'Pro-JYM do Dr. Jim Stoppani thiết kế with tỷ lệ vàng 4:1:1 giữa Whey Isolate, Micellar Casein và Egg White Protein. Cung cấp đạm đa tầng tối ưu suốt cả ngày.', 1590000.00, '/uploads/products/image-12.jpg', 50, 'ACTIVE'),
(13, 1, 'BSN Syntha-6 Protein 5lbs', 'Syntha-6 là matrix protein đa tầng với 6 loại đạm bổ sung nhau: Whey Concentrate, Isolate, Casein, Calcium Caseinate, Egg Albumin, và Glutamine Peptides.', 1350000.00, '/uploads/products/image-13.jpg', 50, 'ACTIVE'),
(14, 1, 'Xendurance Myotein Premium 4lbs', 'Myotein kết hợp whey isolate và concentrate với phức hợp enzyme DigeZyme® giúp phân giải và hấp thụ protein nhanh hơn 30% so với whey thông thường.', 1850000.00, '/uploads/products/image-14.jpg', 50, 'ACTIVE'),
(15, 1, 'Dymatize Elite 100% Whey 5lbs', 'Elite 100% Whey mang đến 25g protein/serving với hương vị thơm ngon và giá thành tiết kiệm. Chứng nhận Informed-Choice đảm bảo không chất cấm.', 1490000.00, '/uploads/products/image-15.jpg', 50, 'ACTIVE'),
(16, 1, 'Whey Labs 100% Isolate 5lbs', 'Whey Labs 100% Isolate sử dụng nguồn sữa bò từ trang trại Úc không hormon tăng trưởng. Quy trình lọc lạnh CFM cho ra 26g protein/serving cực tinh khiết.', 1250000.00, '/uploads/products/image-16.jpg', 50, 'ACTIVE'),
(17, 1, 'AllMax Nutrition AllWhey Gold 5lbs', 'AllWhey Gold pha trộn whey isolate và concentrate theo tỷ lệ tối ưu, bổ sung enzyme Aminogen® giúp tiêu hóa triệt để. Không gây đầy bụng, phù hợp mọi cơ địa.', 1350000.00, '/uploads/products/image-17.jpg', 50, 'ACTIVE'),
(18, 1, 'PVL ISO Gold Premium Isolate 5lbs', 'ISO Gold của PVL kết hợp 25g whey isolate với 1 tỷ CFU probiotic L. acidophilus giúp cải thiện vi hệ đường ruột, tăng hấp thu protein và tăng cường hệ miễn dịch.', 1690000.00, '/uploads/products/image-18.jpg', 50, 'ACTIVE'),
(19, 1, 'Scitec Nutrition 100% Whey Professional 5lbs', 'Sản phẩm whey protein nổi tiếng từ Châu Âu với formula chứa cả Whey Concentrate và Isolate, bổ sung AmbroZyme enzyme digest. Hơn 60 hương vị đa dạng.', 1490000.00, '/uploads/products/image-19.jpg', 50, 'ACTIVE'),
(20, 1, 'BiotechUSA Iso Whey Zero 5lbs', 'Iso Whey Zero của BiotechUSA là lựa chọn hoàn hảo cho người không dung nạp Lactose. 100% Whey Isolate không lactose, không gluten, 23g protein tinh khiết mỗi serving.', 1950000.00, '/uploads/products/image-20.jpg', 50, 'ACTIVE'),
(21, 1, 'Amix Gold Isolate Whey Protein 5lbs', 'Amix Gold Isolate sản xuất tại CH Séc trên dây chuyền CFM lọc lạnh thế hệ mới nhất, đạt 27g protein/serving và được kiểm định Informed Sport toàn cầu.', 2200000.00, '/uploads/products/image-21.jpg', 50, 'ACTIVE'),

-- ─── PRE-WORKOUT (category_id = 2, ID 22–42) ─────────────
(22, 2, 'Labrada Lean Body Meal Replacement 4.6lbs', 'Lean Body cung cấp 40g protein, 27 vitamin & khoáng chất, chất xơ và tinh bột phức hợp hấp thụ chậm. Thay thế hoàn hảo cho bữa ăn khi bận rộn.', 1650000.00, '/uploads/products/image-22.jpg', 50, 'ACTIVE'),
(23, 2, 'Cellucor C4 Original Pre-Workout 30 servings', 'C4 Original là pre-workout bán chạy nhất thế giới với công thức Beta-Alanine + Creatine Nitrate + Caffeine 150mg cho năng lượng tức thì và pump cơ mạnh mẽ.', 650000.00, '/uploads/products/image-23.jpg', 50, 'ACTIVE'),
(24, 2, 'Nutrex Outlift Clinical Pre-Workout 20 servings', 'Outlift là pre-workout liều lâm sàng với Citrulline 8g, Beta-Alanine 3.2g, Creatine 3g và Caffeine 350mg. Không ma trận ẩn – toàn bộ thành phần công bố minh bạch.', 850000.00, '/uploads/products/image-24.jpg', 50, 'ACTIVE'),
(25, 2, 'JNX Sports The Curse Pre-Workout 50 servings', 'The Curse cung cấp Caffeine 200mg, Citrulline Malate 3g and Beta-Alanine 1.5g với 50 servings tiết kiệm. Hương vị trái cây sảng khoái không bị đắng.', 690000.00, '/uploads/products/image-25.jpg', 50, 'ACTIVE'),
(26, 2, 'Psychotic High Stimulant Pre-Workout 35 serv', 'Psychotic chứa DMAE Bitartrate và Caffeine Anhydrous liều cao tạo focus cực mạnh. Chỉ nên dùng cho người có kinh nghiệm tập luyện và khả năng chịu kích thích tốt.', 680000.00, '/uploads/products/image-26.jpg', 50, 'ACTIVE'),
(27, 2, 'Redcon1 Total War Pre-Workout 30 servings', 'Total War là pre-workout 2-trong-1: năng lượng mạnh từ Caffeine 250mg + DMHA và pump cực độ từ Citrulline Malate 6g + AgmaMax 1g. Ưa thích của giới bodybuilding.', 790000.00, '/uploads/products/image-27.jpg', 50, 'ACTIVE'),
(28, 2, 'Applied Nutrition ABE Pre-Workout 30 serv', 'ABE (All Black Everything) chứa Caffeine 200mg, 3.2g Beta-Alanine và 150mg KSM-66 Ashwagandha. Đây là pre-workout số 1 tại UK với hơn 20 hương vị độc đáo.', 720000.00, '/uploads/products/image-28.jpg', 50, 'ACTIVE'),
(29, 2, 'Ghost Legend Pre-Workout 30 servings', 'Ghost Legend tự hào với công thức Transparent Label hoàn toàn, không prop blend. Citrulline 4g, Beta-Alanine 3.2g, Alpha-GPC 150mg và Caffeine 200mg.', 950000.00, '/uploads/products/image-29.jpg', 50, 'ACTIVE'),
(30, 2, 'Vapor X5 Next Gen Muscletech 30 servings', 'Vapor X5 là hệ thống pre-workout 5-trong-1: Sensory Complex, Muscle Amplifier, Performance Booster, Pump Activator và Energy Accelerator trong một scoop.', 650000.00, '/uploads/products/image-30.jpg', 50, 'ACTIVE'),
(31, 2, 'Mr. Hyde NitroX Pre-Workout 30 servings', 'Mr. Hyde NitroX cung cấp Caffeine Matrix 400mg 3 lớp (Anhydrous + Di-Caffeine Malate + Caffeine Citrate) cho năng lượng bền bỉ 3-4 giờ không bị crash.', 690000.00, '/uploads/products/image-31.jpg', 50, 'ACTIVE'),
(32, 2, 'Pre-JYM High Performance 30 servings', 'Pre-JYM là pre-workout đầu tiên đưa ra nguyên tắc full-disclosure label with 13 thành phần ở liều lâm sàng. Không prop blend, không chất độn. Khoa học và hiệu quả.', 1100000.00, '/uploads/products/image-32.jpg', 50, 'ACTIVE'),
(33, 2, 'Kill It Pre-Workout Rich Piana 30 servings', 'Kill It là triết lý "chỉ cho 5% những ai thực sự chiến" – pre-workout với Citrulline Malate, Beta-Alanine, Agmatine và Caffeine thiết kế cho buổi tập khắc nghiệt nhất.', 820000.00, '/uploads/products/image-33.jpg', 50, 'ACTIVE'),
(34, 2, 'SuperPump Max Gaspari 30 servings', 'SuperPump Max từ Gaspari Nutrition chứa L-Citrulline Silicate và hệ điện giải Sustamine® giúp duy trì hiệu suất và chống mệt mỏi suốt buổi tập.', 780000.00, '/uploads/products/image-34.jpg', 50, 'ACTIVE'),
(35, 2, 'Dust X Extreme Stimulant 25 servings', 'Dust X là sản phẩm kích thích cực mạnh từ Blackstone Labs, chứa DMHA và Synephrine HCl tạo ra tăng kích động tuyệt đối. Chỉ dành cho người có kinh nghiệm.', 890000.00, '/uploads/products/image-35.jpg', 50, 'ACTIVE'),
(36, 2, 'Wrecked Pre-Workout Huge Supps 20 serv', 'Wrecked sử dụng 17 thành phần ở liều lượng khổng lồ: Citrulline 8g, HydroPrime Glycerol 3g, Beta-Alanine 3.5g và Caffeine 250mg. Pre-workout không thỏa hiệp.', 1150000.00, '/uploads/products/image-36.jpg', 50, 'ACTIVE'),
(37, 2, 'Lit Pre-Workout Beyond Raw 30 servings', 'LIT của GNC Beyond Raw sử dụng NeuroFactor™ (chiết xuất vỏ quả cà phê) và ElevATP® (chiết xuất cổ đại) tăng sản xuất ATP nội tế bào, cho năng lượng sạch không crash.', 920000.00, '/uploads/products/image-37.jpg', 50, 'ACTIVE'),
(38, 2, 'ENGN Shred Pre-Workout EVL 30 servings', 'ENGN Shred là pre-workout kết hợp chất đốt mỡ sinh nhiệt CLA 500mg, L-Carnitine 500mg với năng lượng từ Caffeine 210mg. Giải pháp 2-in-1 cho giai đoạn siết.', 750000.00, '/uploads/products/image-38.jpg', 50, 'ACTIVE'),
(39, 2, 'Nitraflex Hyperemia GAT Sport 30 serv', 'Nitraflex là pre-workout độc đáo với Clinical Strength Testosterone-Enhancing Compound (DOPA Mucuna 300mg) kết hợp Citrulline/Arginine 7g tăng NO mạnh.', 760000.00, '/uploads/products/image-39.jpg', 50, 'ACTIVE'),
(40, 2, 'Alani Nu Pre-Workout 30 servings', 'Alani Nu Pre-Workout với 200mg Caffeine, L-Citrulline Malate 6g và Beta-Alanine 1.6g phù hợp cả nam lẫn nữ. Không chứa nhân tạo, hương vị trái cây tự nhiên thơm ngon.', 880000.00, '/uploads/products/image-40.jpg', 50, 'ACTIVE'),
(41, 2, 'Ryse Godzilla Pre-Workout 40 servings', 'Godzilla Pre-Workout của Ryse là sản phẩm khổng lồ với full scoop chứa Citrulline 9g, Beta-Alanine 3.5g và Caffeine 400mg. 40 servings ở liều toàn phần cực kỳ kinh tế.', 1350000.00, '/uploads/products/image-41.jpg', 50, 'ACTIVE'),
(42, 2, 'APS Mesomorph Ultimate Pre-Workout 25serv', 'Mesomorph được mệnh danh là "pre-workout kinh điển nhất mọi thời đại" với DMAA (lúc còn hợp pháp) thay thế bằng DMHA thế hệ mới. Tập trung cực kỳ cao và pump đỉnh.', 950000.00, '/uploads/products/image-42.jpg', 50, 'ACTIVE'),

-- ─── PROTEIN BARS & SNACKS (category_id = 3, ID 43–50) ─────────────────────
(43, 3, 'Quest Nutrition Protein Bar 60g', 'Quest Bar cung cấp 21g protein, 14g chất xơ và chỉ 4-5g net carb. Sử dụng protein blend Milk/Whey Isolate. Hương vị phong phú như chocolate chip cookie hay cookies & cream.', 65000.00, '/uploads/products/image-43.jpg', 200, 'ACTIVE'),
(44, 3, 'Grenade Carb Killa Protein Bar 60g', 'Carb Killa là thanh protein số 1 Châu Âu với vỏ sô-cô-la thật, nhân kem xốp nhiều lớp. 23g protein, chỉ 1-2g đường. Hương vị như bánh ngọt thật sự.', 70000.00, '/uploads/products/image-44.jpg', 200, 'ACTIVE'),
(45, 3, 'Barebells Protein Bar Thụy Điển 55g', 'Barebells từ Thụy Điển nổi tiếng với hương vị đỉnh cao không thua gì kẹo chocolate thật. 20g protein, 0g thêm đường. Các hương vị Salty Peanut và Cookies Cream được yêu thích.', 68000.00, '/uploads/products/image-45.jpg', 200, 'ACTIVE'),
(46, 3, 'MyProtein Impact Protein Bar 64g', 'Impact Bar của MyProtein có lớp nền đạm chắc, lớp giữa caramel mềm mịn và lớp sô-cô-la phủ bên ngoài. 21g protein, giá cực kỳ cạnh tranh trong phân khúc UK.', 62000.00, '/uploads/products/image-46.jpg', 200, 'ACTIVE'),
(47, 3, 'Combat Crunch Protein Bar MusclePharm 63g', 'Combat Crunch có kết cấu giòn xốp độc đáo nhờ công nghệ nướng đặc biệt. 20g protein/bar, chứng nhận Informed-Sport, không dùng rượu đường hay HFCS.', 65000.00, '/uploads/products/image-47.jpg', 200, 'ACTIVE'),
(48, 3, 'BSN Protein Crisp Syntha-6 Bar 57g', 'Protein Crisp của BSN có kết cấu giòn xốp nhờ Puffed Quinoa và Rice Crisps. 20g protein từ Syntha-6 matrix, ít carb, ít mỡ. Nhẹ nhàng và dễ ăn sau tập.', 65000.00, '/uploads/products/image-48.jpg', 200, 'ACTIVE'),
(49, 3, 'Robert Irvine Fit Crunch Bar 46g', 'Fit Crunch do Chef Robert Irvine tạo ra với kết cấu wafer 6 lớp độc đáo tẩm sô-cô-la. 16g protein, 8 hương vị như Peanut Butter, Cookies & Cream siêu ngon.', 60000.00, '/uploads/products/image-49.jpg', 200, 'ACTIVE'),
(50, 3, 'ONE Brands ONE Protein Bar 60g', 'ONE Bar nổi bật với 20g protein và chỉ 1g đường – một trong những tỷ lệ protein-đường tốt nhất thị trường. Hơn 15 hương vị đa dạng, phù hợp chế độ keto và low-carb.', 65000.00, '/uploads/products/image-50.jpg', 200, 'ACTIVE'),

-- ─── CREATINE (category_id = 4, ID 51-65) ──────────────────────────────────
(51, 4, 'Optimum Nutrition Micronized Creatine 600g', 'Cung cấp 5g Creatine Monohydrate nguyên chất mỗi khẩu phần, không mùi, dễ dàng pha trộn with whey protein hoặc thức uống yêu thích.', 650000.00, '/uploads/products/image-51.jpg', 100, 'ACTIVE'),
(52, 4, 'MuscleTech Platinum 100% Creatine 400g', 'Sử dụng công nghệ siêu vi lọc (micronized) giúp Creatine hòa tan cực tốt và hấp thu tối đa vào cơ bắp, không gây đầy bụng.', 450000.00, '/uploads/products/image-52.jpg', 150, 'ACTIVE'),
(53, 4, 'Rule 1 R1 Creatine 375g', 'Sản phẩm từ hãng Rule 1 cung cấp nguồn Creatine sạch, không tạp chất, giúp tái tạo ATP nhanh chóng trong các bài tập tạ nặng.', 420000.00, '/uploads/products/image-53.jpg', 80, 'ACTIVE'),
(54, 4, 'Mutant Creakong CX8 249g', 'Kết hợp Creatine Monohydrate, Creatine Magnalite và Creatine Chelated giúp đẩy lùi sự mệt mỏi, tổng hợp protein nhanh hơn gấp 3 lần.', 550000.00, '/uploads/products/image-54.jpg', 60, 'ACTIVE'),
(55, 4, 'MyProtein Creatine Monohydrate 250g', 'Sản phẩm quốc dân từ MyProtein, thuần chay, không hương liệu, độ tinh khiết cao đáp ứng nhu cầu tập luyện hàng ngày.', 250000.00, '/uploads/products/image-55.jpg', 200, 'ACTIVE'),
(56, 4, 'Cellucor COR-Performance Creatine 360g', 'Mỗi serving chứa 5g Creatine siêu mịn, giúp bạn đẩy tạ nặng hơn và kéo dài thời gian tập luyện cường độ cao.', 480000.00, '/uploads/products/image-56.jpg', 50, 'ACTIVE'),
(57, 4, 'Dymatize Creatine Micronized 500g', 'Sử dụng nguồn nguyên liệu Creapure® nguyên chất từ Đức, đảm bảo không lẫn tạp chất, tối ưu cho việc tích nước trong tế bào cơ.', 680000.00, '/uploads/products/image-57.jpg', 40, 'ACTIVE'),
(58, 4, 'BPI Sports Best Creatine 300g', 'Công thức Best Creatine™ độc quyền kết hợp 6 dạng creatine giúp cơ thể hấp thu toàn diện mà không cần giai đoạn "loading".', 520000.00, '/uploads/products/image-58.jpg', 70, 'ACTIVE'),
(59, 4, 'Nutrex Creatine Drive 300g', 'Sản phẩm an toàn và hiệu quả, phù hợp cho mọi đối tượng chơi thể thao cần tăng cường năng lượng bùng nổ tức thì.', 400000.00, '/uploads/products/image-59.jpg', 90, 'ACTIVE'),
(60, 4, 'AllMax Nutrition Creatine 400g', 'Sản xuất qua công nghệ vi hạt hóa cấp độ dược phẩm, loại bỏ hoàn toàn tình trạng sạn khi uống, tan cực nhanh trong nước.', 490000.00, '/uploads/products/image-60.jpg', 60, 'ACTIVE'),
(61, 4, 'BiotechUSA 100% Creatine Monohydrate 300g', 'Sản phẩm tinh khiết từ Châu Âu giúp cải thiện hiệu suất các bài tập ngắn hạn, cường độ cao như cử tạ, chạy nước rút.', 350000.00, '/uploads/products/image-61.jpg', 120, 'ACTIVE'),
(62, 4, 'Scitec Nutrition 100% Creatine 300g', 'Creatine từ Scitec luôn nổi tiếng về chất lượng, giúp cơ bắp trông to và săn chắc hơn nhờ cơ chế hydrat hóa tế bào.', 380000.00, '/uploads/products/image-62.jpg', 80, 'ACTIVE'),
(63, 4, 'Kaged Muscle Creatine HCl 75 Servings', 'Dạng Creatine Hydrochloride (HCl) đã được cấp bằng sáng chế, chỉ cần liều lượng nhỏ (1-2g) nhưng hấp thu nhanh hơn Monohydrate gấp nhiều lần.', 650000.00, '/uploads/products/image-63.jpg', 50, 'ACTIVE'),
(64, 4, 'RSP Nutrition Creatine Monohydrate 500g', 'Giảm thiểu đau nhức cơ bắp và hỗ trợ phát triển mô cơ mới hiệu quả. Không mùi, không vị, dễ uống.', 460000.00, '/uploads/products/image-64.jpg', 60, 'ACTIVE'),
(65, 4, 'PVL 100% Pure Creatine 300g', 'Đảm bảo 100% không chứa chất cấm trong thể thao. Tăng cường khối lượng cơ nạc và sức mạnh tổng thể đáng kể.', 430000.00, '/uploads/products/image-65.jpg', 70, 'ACTIVE')
AS new_prod
ON DUPLICATE KEY UPDATE 
    CategoryID = new_prod.CategoryID,
    ProductName = new_prod.ProductName,
    Price = new_prod.Price,
    Description = new_prod.Description,
    ImageURL = new_prod.ImageURL,
    Quantity = new_prod.Quantity;