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

INSERT INTO Account (Username, [Password], Email, FullName, Phone, [Address], Gender, [Role], [Status])
VALUES
-- Admin
(N'admin123', N'admin123@', N'admin@ecomart.vn', N'Admin EcoMart', '0938123456', N'235 Nguyễn Văn Cừ, Q.5, TP.HCM', N'Nữ', 1, N'Active'),

-- Staff
(N'Thacnha', N'Thacnha02@', N'thacnha2@ecomart.vn', N'Trương Thác Nhã', '0909123456', N'12 Lý Thường Kiệt, Q.10, TP.HCM', N'Nữ', 2, N'Active'),
(N'Mantue', N'Mantue03@', N'mantue@ecomart.vn', N'Trần Mẫn Tuệ', '0912345678', N'45 Phan Đình Phùng, Q.Phú Nhuận, TP.HCM', N'Nữ', 2, N'Active'),
(N'Truongsinh', N'Truongsinh04@', N'truongsinh@ecomart.vn', N'Lê Trường Sinh', '0923456789', N'87 Nguyễn Trãi, Q.5, TP.HCM', N'Nam', 2, N'Active'),
(N'Tuenhi', N'Tuenhi05@', N'tuenhi@ecomart.vn', N'Nguyễn Tuệ Nhi', '0977527752', 'Bạc Liêu', N'Nữ', 2, N'Active'),

-- Customers
(N'nguyenvana', N'pass123@', N'nguyenvana@gmail.com', N'Nguyễn Văn A', '0909123456', N'123 Lê Lợi, Q.1, TP.HCM', N'Nam', 0, N'Active'),
(N'tranthib', N'pass456@', N'tranthib@gmail.com', N'Trần Thị B', '0918234567', N'45 Nguyễn Huệ, Q.3, TP.HCM', N'Nữ', 0, N'Active'),
(N'levanc', N'pass789@', N'levanc@gmail.com', N'Lê Văn C', '0987345678', N'78 Trần Phú, Q.5, TP.HCM', N'Nữ', 0, N'Active');

-- Insert Staff data with correct AccountID references
INSERT INTO Staff (AccountID, FullName, Email, Phone, Gender, [Address], [Status])
VALUES
(2, N'Trương Thác Nhã', N'thacnha2@ecomart.vn', '0909123456', N'Nữ', N'12 Lý Thường Kiệt, Q.10, TP.HCM', N'Active'),
(3, N'Trần Mẫn Tuệ', N'mantue@ecomart.vn', '0912345678', N'Nữ', N'45 Phan Đình Phùng, Q.Phú Nhuận, TP.HCM', N'Active'),
(4, N'Lê Trường Sinh', N'truongsinh@ecomart.vn', '0923456789', N'Nam', N'87 Nguyễn Trãi, Q.5, TP.HCM', N'Active'),
(5, N'Nguyễn Tuệ Nhi', N'tuenhi@ecomart.vn', '0977527752', N'Nữ', N'Bạc Liêu', N'Active');

CREATE TABLE Supplier (
    SupplierID INT PRIMARY KEY IDENTITY(1,1),
    BrandName NVARCHAR(100) NOT NULL,
    CompanyName NVARCHAR(100) NOT NULL,
    [Address] NVARCHAR(255),
    Email NVARCHAR(255),
    Phone VARCHAR(15),
    [Status] BIT DEFAULT 1
);

INSERT INTO Supplier (BrandName, CompanyName, [Address], Email, Phone, [Status])
VALUES
(N'Thịnh An', N'Công ty Thịnh An', N'123 Lê Văn Việt, TP. Thủ Đức, TP.HCM', N'thinhan@fruit.vn', '0909123456', 1),
(N'SUNTORY PEPSICO', N'Công ty TNHH Nước giải khát SUNTORY PEPSICO Việt Nam', N'Sun Avenue, Quận 2, TP.HCM', N'contact@suntorypepsico.vn', '02838912345', 1),
(N'Mondelez Kinh Đô', N'Công ty cổ phần bánh kẹo Mondelez Kinh Đô', N'138-142 Hai Bà Trưng, Quận 1, TP.HCM', N'info@mondelezkinhdo.vn', '02838212345', 1),
(N'Vinamilk', N'Công ty Cổ phần Sữa Việt Nam', N'10 Tân Trào, Quận 7, TP.HCM', N'vinamilk@vinamilk.com.vn', '02854155555', 1),
(N'IFREE BEAUTY', N'Công ty TNHH IFREE BEAUTY', N'18A Cộng Hòa, Quận Tân Bình, TP.HCM', N'cs@ifreebeauty.vn', '02839451234', 1);

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
    ProductName NVARCHAR(255) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    [Description] NVARCHAR(MAX),
    StockQuantity INT DEFAULT 0,
    ImageURL NVARCHAR(255),
    Unit NVARCHAR(50), -- đơn vị tính: kg, chai, gói,...
    CreatedAt DATETIME DEFAULT GETDATE(),
	ManufactureDate DATE, 
	ExpirationDate DATE,
    CategoryID INT NOT NULL,
    SupplierID INT NOT NULL,
    [Status] NVARCHAR(50) DEFAULT N'Còn hàng',
    FOREIGN KEY (CategoryID) REFERENCES Category(CategoryID),
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID)
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
    Quantity INT NOT NULL DEFAULT 1,
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
    PaymentMethod NVARCHAR(50) NOT NULL, -- "Cash", "Momo", "VNPay", "Bank Transfer"
    PaymentStatus NVARCHAR(50) DEFAULT N'Chưa thanh toán', -- "Chưa thanh toán", "Đã thanh toán"
    OrderStatus NVARCHAR(50) DEFAULT N'Đang xử lý', -- "Đang xử lý", "Đang giao hàng", "Đã giao", "Đã hủy"
    Notes NVARCHAR(255),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID)
);

CREATE TABLE OrderDetail (
    OrderDetailID INT PRIMARY KEY IDENTITY(1,1),
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    SubTotal AS (Quantity * UnitPrice) PERSISTED,
    FOREIGN KEY (OrderID) REFERENCES [Order](OrderID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

CREATE TABLE Review (
    ReviewID INT PRIMARY KEY IDENTITY(1,1),
    ProductID INT NOT NULL,
    AccountID INT NOT NULL,
    Rating INT CHECK (Rating BETWEEN 1 AND 5) NOT NULL,
    Comment NVARCHAR(MAX),
    ImageURL NVARCHAR(255),
    CreatedAt DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE
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
    Quantity INT CHECK (Quantity >= 0),
    LastUpdated DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);
