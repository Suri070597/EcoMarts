--Cần bổ sung: Copy đoạn trong commit rồi chạy thêm

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'EcoMart')
BEGIN
    ALTER DATABASE EcoMart SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE EcoMart;
END
GO

CREATE DATABASE EcoMart;
GO

USE EcoMart;
GO


CREATE TABLE Account (
    AccountID INT PRIMARY KEY IDENTITY(1,1),
    Username NVARCHAR(100) NOT NULL UNIQUE,
    [Password] NVARCHAR(255) NOT NULL,
    Email NVARCHAR(255) NOT NULL UNIQUE,
    FullName NVARCHAR(100) NOT NULL,
    Phone VARCHAR(15),
    [Address] NVARCHAR(255),
    Gender NVARCHAR(10),
    [Role] INT NOT NULL, -- 0: Customer, 1: Admin, 2: Staff
    [Status] NVARCHAR(50) DEFAULT 'Active', -- Active, Inactive, Suspended
);

INSERT INTO Account (Username, [Password], Email, FullName, Phone, [Address], Gender, [Role], [Status])
VALUES
-- Admin = admin123@
(N'admin123', N'ecd00aa1acd325ba7575cb0f638b04a5', N'admin@ecomart.vn', N'Admin EcoMart', '0938123456', N'Nguyễn Văn Cừ, TP.Cần Thơ', N'Nữ', 1, N'Active')

CREATE TABLE Customer (
    CustomerID INT PRIMARY KEY IDENTITY(1,1),
    AccountID INT NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    Phone VARCHAR(20),
    Gender NVARCHAR(10),
    [Address] NVARCHAR(200),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE
);
CREATE TABLE Staff (
    StaffID INT PRIMARY KEY IDENTITY(1,1),
    AccountID INT NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    Phone VARCHAR(20),
    Gender NVARCHAR(10),
    [Address] NVARCHAR(200),
    [Status] VARCHAR(50) DEFAULT 'Active',
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE
);


CREATE TABLE Token_Table (
    TokenID INT PRIMARY KEY IDENTITY(1,1),
    AccountID INT NOT NULL,
    Token NVARCHAR(255) NOT NULL,
    [Status] NVARCHAR(50) NOT NULL,
    Time_Add DATETIME NOT NULL,
    Time_Exp DATETIME NOT NULL,
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE
);

CREATE TABLE Manufacturer (
    ManufacturerID INT PRIMARY KEY IDENTITY(1,1),
    BrandName NVARCHAR(100) NOT NULL,
    CompanyName NVARCHAR(100) NOT NULL,
    [Address] NVARCHAR(255),
    Email NVARCHAR(255),
    Phone VARCHAR(15) NOT NULL,
    [Status] BIT DEFAULT 1
);

INSERT INTO Manufacturer (BrandName, CompanyName, [Address], Email, Phone, [Status])
VALUES
(N'Thịnh An', N'Công ty Thịnh An', N'123 Lê Văn Việt, TP. Thủ Đức, TP.HCM', N'thinhan@fruit.vn', '0909123456', 1);

CREATE TABLE Category (
    CategoryID INT PRIMARY KEY IDENTITY(1,1),
    CategoryName NVARCHAR(100) NOT NULL,
    ParentID INT NULL,
	ImageURL NVARCHAR(255),
    [Description] NVARCHAR(255),
    FOREIGN KEY (ParentID) REFERENCES Category(CategoryID)
);

-- Main categories first
INSERT INTO Category (CategoryName, [Description]) VALUES
(N'Nước giải khát', N'Các loại đồ uống giải khát'),
(N'Sữa các loại', N'Các loại sản phẩm từ sữa'),
(N'Trái cây', N'Trái cây tươi các loại'),
(N'Bánh kẹo', N'Các loại bánh kẹo'),
(N'Mẹ và bé', N'Sản phẩm dành cho mẹ và bé'),
(N'Mỹ phẩm', N'Các sản phẩm làm đẹp'),
(N'Sản phẩm nổi bật', N'Sản phẩm đặc biệt được đề xuất');

DECLARE @NuocGiaiKhat INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Nước giải khát');
DECLARE @Sua INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Sữa các loại');
DECLARE @TraiCay INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Trái cây');
DECLARE @BanhKeo INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Bánh kẹo');
DECLARE @MeVaBe INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Mẹ và bé');
DECLARE @MyPham INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Mỹ phẩm');

-- Subcategories with parent IDs from variables
INSERT INTO Category (CategoryName, ParentID, [Description]) VALUES
-- Nước giải khát
(N'Nước ngọt', @NuocGiaiKhat, N'Các loại nước ngọt có ga'),
(N'Nước trà', @NuocGiaiKhat, N'Nước trà các loại'),
(N'Nước suối', @NuocGiaiKhat, N'Nước suối tinh khiết'),
(N'Nước yến', @NuocGiaiKhat, N'Các loại nước yến'),
(N'Nước ép trái cây', @NuocGiaiKhat, N'Nước ép từ trái cây'),
(N'Trà dạng gói', @NuocGiaiKhat, N'Trà túi lọc, trà khô'),
(N'Cà phê gói', @NuocGiaiKhat, N'Các loại cà phê đóng gói');

-- Sữa các loại
INSERT INTO Category (CategoryName, ParentID, [Description]) VALUES
(N'Sữa chua', @Sua, N'Các loại sữa chua'),
(N'Sữa đặc', @Sua, N'Sữa đặc các loại'),
(N'Sữa tươi', @Sua, N'Sữa tươi thanh trùng và tiệt trùng'),
(N'Sữa chua uống men', @Sua, N'Sữa chua dạng uống có lợi khuẩn'),
(N'Phô mai', @Sua, N'Các loại phô mai'),
(N'Ngũ cốc', @Sua, N'Sữa ngũ cốc các loại');

-- Trái cây
INSERT INTO Category (CategoryName, ParentID, [Description]) VALUES
(N'Sầu Riêng', @TraiCay, N'Sầu riêng các loại'),
(N'Táo', @TraiCay, N'Táo nhập khẩu và trong nước'),
(N'Vải', @TraiCay, N'Vải thiều các loại'),
(N'Thanh Long', @TraiCay, N'Thanh long ruột đỏ và trắng'),
(N'Xoài', @TraiCay, N'Xoài các loại'),
(N'Chôm Chôm', @TraiCay, N'Chôm chôm nhãn và thường');

-- Bánh kẹo
INSERT INTO Category (CategoryName, ParentID, [Description]) VALUES
(N'Snack', @BanhKeo, N'Các loại snack'),
(N'Bánh bông lan', @BanhKeo, N'Bánh bông lan các loại'),
(N'Bánh tươi-Sandwich', @BanhKeo, N'Bánh mì sandwich và bánh tươi'),
(N'Bánh quế', @BanhKeo, N'Bánh quế các loại'),
(N'Bánh que', @BanhKeo, N'Bánh que các hương vị'),
(N'Kẹo singum', @BanhKeo, N'Kẹo singum các loại'),
(N'Kẹo cứng', @BanhKeo, N'Kẹo cứng các hương vị'),
(N'Kẹo dẻo', @BanhKeo, N'Kẹo dẻo và marshmallow'),
(N'Rau câu', @BanhKeo, N'Các loại thạch rau câu'),
(N'Trái cây sấy', @BanhKeo, N'Các loại trái cây sấy khô');

-- Mẹ và bé
INSERT INTO Category (CategoryName, ParentID, [Description]) VALUES
(N'Sữa tắm - Dầu gội cho bé', @MeVaBe, N'Sản phẩm tắm gội cho bé'),
(N'Nước giặt - xả cho bé', @MeVaBe, N'Sản phẩm giặt đồ cho bé'),
(N'Bình sữa - núm vú', @MeVaBe, N'Bình sữa và phụ kiện');

-- Mỹ phẩm
INSERT INTO Category (CategoryName, ParentID, [Description]) VALUES
(N'Nước tẩy trang', @MyPham, N'Nước tẩy trang và bông tẩy trang'),
(N'Sữa rửa mặt', @MyPham, N'Các loại sữa rửa mặt'),
(N'Mặt nạ', @MyPham, N'Mặt nạ dưỡng da các loại'),
(N'Kem chống nắng', @MyPham, N'Kem chống nắng các loại'),
(N'Sữa tắm', @MyPham, N'Sữa tắm dưỡng da'),
(N'Son', @MyPham, N'Son môi các loại');

CREATE TABLE Product (
    ProductID INT PRIMARY KEY IDENTITY(1,1),

    ProductName NVARCHAR(255) NOT NULL, -- tên
    CategoryID INT NOT NULL, -- thể loại
    PriceBox DECIMAL(10,2) NULL, -- giá bán của 1 thùng
    PriceUnit DECIMAL(10,2) NULL, -- giá bán của 1 lon
    PricePack DECIMAL(10,2) NULL, -- giá bán của 1 lốc
    UnitPerBox INT NOT NULL DEFAULT 1,  -- số lượng unit trong thùng
    BoxUnitName NVARCHAR(50) NOT NULL,
    ItemUnitName NVARCHAR(50) NOT NULL,
    [Description] NVARCHAR(MAX),
    ImageURL NVARCHAR(255),

    CreatedAt DATETIME DEFAULT GETDATE(),
    [Status] NVARCHAR(50) DEFAULT N'Còn hàng',
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID)
);

CREATE TABLE ProductUnitConversion (
    ConversionID INT PRIMARY KEY IDENTITY(1,1),
    ProductID INT NOT NULL,
    UnitPerBoxChange INT NOT NULL DEFAULT 0, -- Số lượng lon trong thùng sau khi chuyển đổi
    UnitsPerPackChange INT NULL, -- Số lượng lốc trong 1 thùng sau khi chuyển đổi
    BoxQuantity INT NULL, -- Số lượng thùng sử dụng trong lần chuyển đổi
    PackSize INT NULL, -- Số đơn vị trong 1 lốc tại thời điểm chuyển đổi (nếu có)
    ConversionDate DATETIME NOT NULL DEFAULT GETDATE(), -- Thời gian chuyển đổi
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE
);



-- Get the correct category IDs dynamically
DECLARE @NuocNgot INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Nước ngọt' AND ParentID = @NuocGiaiKhat);
DECLARE @NuocTra INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Nước trà' AND ParentID = @NuocGiaiKhat);
DECLARE @NuocSuoi INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Nước suối' AND ParentID = @NuocGiaiKhat);
DECLARE @SuaChua INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Sữa chua' AND ParentID = @Sua);
DECLARE @SuaDac INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Sữa đặc' AND ParentID = @Sua);
DECLARE @SuaTuoi INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Sữa tươi' AND ParentID = @Sua);
DECLARE @SauRieng INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Sầu Riêng' AND ParentID = @TraiCay);
DECLARE @Tao INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Táo' AND ParentID = @TraiCay);
DECLARE @Xoai INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Xoài' AND ParentID = @TraiCay);
DECLARE @Snack INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Snack' AND ParentID = @BanhKeo);
DECLARE @BanhBongLan INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Bánh bông lan' AND ParentID = @BanhKeo);
DECLARE @SuaTamBe INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Sữa tắm - Dầu gội cho bé' AND ParentID = @MeVaBe);
DECLARE @NuocGiat INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Nước giặt - xả cho bé' AND ParentID = @MeVaBe);
DECLARE @BinhSua INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Bình sữa - núm vú' AND ParentID = @MeVaBe);
DECLARE @Son INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Son' AND ParentID = @MyPham);
DECLARE @SuaRuaMat INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Sữa rửa mặt' AND ParentID = @MyPham);
DECLARE @KemChongNang INT = (SELECT CategoryID FROM Category WHERE CategoryName = N'Kem chống nắng' AND ParentID = @MyPham);

CREATE TABLE CartItem (
    CartItemID INT PRIMARY KEY IDENTITY(1,1),
    AccountID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity DECIMAL(10,2) NOT NULL DEFAULT 1, -- Gộp trực tiếp kiểu và default
    AddedAt DATETIME DEFAULT GETDATE(),
    [Status] NVARCHAR(50) DEFAULT 'Active', -- Active, SavedForLater, Removed
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

-- Get product IDs dynamically to ensure correct references
DECLARE @CocaCola INT = (SELECT ProductID FROM Product WHERE ProductName = N'Coca-Cola Lon 330ml');
DECLARE @TaoMy INT = (SELECT ProductID FROM Product WHERE ProductName = N'Táo Mỹ');
DECLARE @JohnsonBaby INT = (SELECT ProductID FROM Product WHERE ProductName = N'Sữa tắm Johnson Baby 500ml');
DECLARE @SuaVinamilk INT = (SELECT ProductID FROM Product WHERE ProductName = N'Sữa tươi Vinamilk 180ml');

CREATE TABLE [Order] (
    OrderID INT PRIMARY KEY IDENTITY(1,1),
    AccountID INT NOT NULL,
    OrderDate DATETIME DEFAULT GETDATE(),
    TotalAmount DECIMAL(10,2) NOT NULL,
    ShippingAddress NVARCHAR(255) NOT NULL,
    ShippingPhone VARCHAR(15) NOT NULL,
    PaymentMethod NVARCHAR(50) NOT NULL, -- "Cash", "VNPay"
    PaymentStatus NVARCHAR(50) DEFAULT N'Chưa thanh toán', -- "Chưa thanh toán", "Đã thanh toán"
    OrderStatus NVARCHAR(50) DEFAULT N'Đang xử lý', -- "Đang xử lý", "Đang giao hàng", "Đã giao", "Đã hủy"
    Notes NVARCHAR(255),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);

CREATE TABLE OrderDetail (
    OrderDetailID INT PRIMARY KEY IDENTITY(1,1),
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity DECIMAL(10,2) NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    SubTotal AS (Quantity * UnitPrice) PERSISTED,
    FOREIGN KEY (OrderID) REFERENCES [Order](OrderID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

CREATE TABLE Review (
    ReviewID INT PRIMARY KEY IDENTITY(1,1),
    ProductID INT NOT NULL,
    AccountID INT NOT NULL,
    OrderID INT NULL,
    ParentReviewID INT NULL,
    Rating INT NULL CHECK (Rating BETWEEN 1 AND 5),
    Comment NVARCHAR(MAX),
    ImageURL NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE(),
    UpdatedAt DATETIME NULL DEFAULT GETDATE(),
    IsRead BIT DEFAULT 0,
    Status NVARCHAR(20) DEFAULT 'VISIBLE',
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE,
    FOREIGN KEY (OrderID) REFERENCES [Order](OrderID),
    FOREIGN KEY (ParentReviewID) REFERENCES Review(ReviewID)
);


-- Get additional product IDs
DECLARE @Keo INT = (SELECT ProductID FROM Product WHERE ProductName = N'Kẹo Socola M&M gói 100g');
DECLARE @SonMoi INT = (SELECT ProductID FROM Product WHERE ProductName = N'Son môi Maybelline');
-- Note: @Son above is a CategoryID, but here we need the ProductID

CREATE TABLE Promotion (
    PromotionID INT PRIMARY KEY IDENTITY(1,1),
    PromotionName NVARCHAR(100) NOT NULL,
    [Description] NVARCHAR(255),
    DiscountPercent DECIMAL(5,2) CHECK (DiscountPercent >= 0 AND DiscountPercent <= 100),
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    IsActive BIT DEFAULT 1, -- 1 = active, 0 = inactive
    CHECK (StartDate < EndDate)
);

CREATE TABLE Product_Promotion (
    ProductPromotionID INT PRIMARY KEY IDENTITY(1,1),
    ProductID INT NOT NULL,
    PromotionID INT NOT NULL,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (PromotionID) REFERENCES Promotion(PromotionID),
    UNIQUE (ProductID, PromotionID)
);

-- Get Pepsi and Xoai
DECLARE @Pepsi INT = (SELECT ProductID FROM Product WHERE ProductName = N'Pepsi Lon 330ml');
DECLARE @XoaiCat INT = (SELECT ProductID FROM Product WHERE ProductName = N'Xoài Cát Hòa Lộc');

CREATE TABLE Voucher (
    VoucherID INT PRIMARY KEY IDENTITY(1,1),
    VoucherCode VARCHAR(20) NOT NULL UNIQUE,
    [Description] NVARCHAR(255),
    DiscountAmount DECIMAL(10,2),
    MinOrderValue DECIMAL(10,2) DEFAULT 0,
    MaxUsage INT DEFAULT 1,
    UsageCount INT DEFAULT 0,
    StartDate DATETIME NOT NULL,
    EndDate DATETIME NOT NULL,
    IsActive BIT DEFAULT 1,
    CategoryID INT NULL, -- NULL if applicable to all categories
    ClaimLimit INT NULL, -- Số lượt có thể lấy tối đa (NULL => không giới hạn)
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID),
    CHECK (StartDate < EndDate)
);


CREATE TABLE VoucherUsage (
    VoucherUsageID INT PRIMARY KEY IDENTITY(1,1),
    VoucherID INT NOT NULL,
    AccountID INT NOT NULL,
    OrderID INT NOT NULL,
    UsedDate DATETIME DEFAULT GETDATE(),
    DiscountAmount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (VoucherID) REFERENCES Voucher(VoucherID),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    FOREIGN KEY (OrderID) REFERENCES [Order](OrderID)
);

CREATE TABLE Inventory (
    InventoryID INT PRIMARY KEY IDENTITY(1,1),
    ProductID INT NOT NULL,
    PackageType NVARCHAR(10) NOT NULL, -- 'BOX' | 'UNIT' | 'PACK' | 'KG'
    Quantity DECIMAL(18,2) NOT NULL DEFAULT 0,
    PackSize INT NOT NULL DEFAULT 0,   -- Số đơn vị trong 1 lốc (0 cho BOX/UNIT)
    LastUpdated DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_Inventory_Product FOREIGN KEY (ProductID) REFERENCES Product(ProductID) ON DELETE CASCADE,
    CONSTRAINT UQ_Inventory UNIQUE (ProductID, PackageType, PackSize),
    CONSTRAINT CHK_PackageType CHECK (PackageType IN ('BOX', 'UNIT', 'PACK', 'KG'))
);


CREATE TABLE AccountVoucher (
    AccountVoucherID INT PRIMARY KEY IDENTITY(1,1),
    AccountID INT NOT NULL,
    VoucherID INT NOT NULL,
    DateAssigned DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID),
    FOREIGN KEY (VoucherID) REFERENCES Voucher(VoucherID)
);

CREATE TABLE StockIn (
    StockInID INT PRIMARY KEY IDENTITY(1,1),
    ManufacturerID INT NOT NULL,
    ReceiverID INT NOT NULL,
    DateIn DATETIME NOT NULL DEFAULT GETDATE(),
    Note NVARCHAR(255),
    Status NVARCHAR(20) NOT NULL DEFAULT 'Pending', -- Pending / Completed / Canceled
	ExpiryDate DATE,
    CONSTRAINT FK_StockIn_Manufacturer FOREIGN KEY (ManufacturerID) REFERENCES Manufacturer(ManufacturerID),
    CONSTRAINT FK_StockIn_Receiver FOREIGN KEY (ReceiverID) REFERENCES Account(AccountID)
);

CREATE TABLE StockInDetail (
    StockInDetailID INT PRIMARY KEY IDENTITY(1,1),
    StockInID INT NOT NULL,
    InventoryID INT NOT NULL,
    Quantity DECIMAL(18,2) NOT NULL,
    UnitPrice DECIMAL(18,2),
    
    CONSTRAINT FK_StockInDetail_StockIn FOREIGN KEY (StockInID) REFERENCES StockIn(StockInID) ON DELETE CASCADE,
    CONSTRAINT FK_StockInDetail_Inventory FOREIGN KEY (InventoryID) REFERENCES Inventory(InventoryID)
);

-- Xóa constraint CHK_PackageType trong bảng Inventory
ALTER TABLE Inventory
DROP CONSTRAINT CHK_PackageType;


INSERT INTO Manufacturer (BrandName, CompanyName, [Address], Email, Phone, [Status])
VALUES
(N'Vinamilk', N'Vietnam Dairy Products JSC', N'36-38 Ngô Đức Kế, Quận 1, TP.HCM', 'contact@vinamilk.com.vn', '0281234567', 1),
(N'Trung Nguyên', N'Trung Nguyên Group', N'82-84 Bùi Thị Xuân, Quận 1, TP.HCM', 'info@trungnguyen.com', '0282345678', 1),
(N'Tân Hiệp Phát', N'Tân Hiệp Phát Beverage Group', N'219 Đại lộ Bình Dương, Bình Dương', 'support@thp.com.vn', '0274388888', 1),
(N'Pepsi', N'PepsiCo Vietnam', N'Lô 13 VSIP, Bình Dương', 'contact@pepsico.com', '0274356789', 1),
(N'CocaCola', N'Coca-Cola Beverages Vietnam', N'485 Hà Nội Highway, Thủ Đức, TP.HCM', 'service@coca-cola.com', '0288765432', 1),
(N'Kinh Đô', N'Kinh Đô Corporation', N'141 Nguyễn Du, Quận 1, TP.HCM', 'info@kinhdo.com.vn', '0283456123', 1),
(N'Hải Hà', N'Hải Hà Confectionery JSC', N'25 Trương Định, Hai Bà Trưng, Hà Nội', 'contact@haiha.com.vn', '0249876543', 1),
(N'Nutifood', N'Nutifood Nutrition Food JSC', N'281-283 Hoàng Diệu, Quận 4, TP.HCM', 'hello@nutifood.com.vn', '0287654321', 1),
(N'Nestlé', N'Nestlé Vietnam Ltd.', N'5th Floor, Empress Tower, 138-142 Hai Bà Trưng, Quận 1, TP.HCM', 'consumer.services@vn.nestle.com', '0283838888', 1),
(N'Orion', N'Orion Food Vina', N'Lô CN2-2, KCN Mỹ Phước 3, Bình Dương', 'orion@orion.com.vn', '0274222333', 1);



