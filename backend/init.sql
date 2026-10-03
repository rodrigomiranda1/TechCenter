USE techcenter_db;

-- ==========================
-- 1. ROLES Y USUARIOS
-- ==========================
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN', 'Administrador del sistema'),
('CLIENTE', 'Cliente de la tienda'),
('EMPLEADO', 'Empleado de la ferretería');

-- Contraseñas en texto plano como tenías en tu script
INSERT INTO usuarios (username, email, password_hash) VALUES
('rodrigo', 'rodrigo@admin.com', 'admin'),
('arturo', 'arturo@gmail.com', 'arturo'),
('oliver', 'oliver@gmail.com', 'oliver');

INSERT INTO usuario_roles (id_usuario, id_rol) VALUES
(1, 1),
(2, 2),
(3, 3);

-- ==========================
-- 2. CLIENTES Y PROVEEDORES
-- ==========================
INSERT INTO clientes (id_usuario, nombres, apellidos, dni, telefono, direccion) VALUES
(1, 'Christopher', 'Ramirez', '89252515', '97235252', 'Huacho'),
(2, 'Matias', 'Gomez', '8726135', '978262754', 'Lima'),
(3, 'Jerson', 'Quicaño', '74125896', '999222333', 'Lima');

INSERT INTO proveedores (ruc, razon_social, telefono, correo, direccion) VALUES
('20100011111', 'Deltron Peru S.A.', '016193000', 'ventas@deltron.com.pe', 'Av. Milo Lockett 555, Lima'),
('20200022222', 'Ingram Micro Peru S.A.C.', '016140000', 'contacto@ingrammicro.com', 'Av. Republica de Panama 3055, San Isidro, Lima'),
('20300033333', 'Intcomex Peru S.A.C.', '017123000', 'ventas.pe@intcomex.com', 'Av. Argentina 2415, Lima'),
('20400044444', 'Tech Data Perú S.A.C.', '015132000', 'ventas@techdata.pe', 'Av. Javier Prado Este 4200, Surco, Lima'),
('20500055555', 'Grupo Deltron S.A.', '016193001', 'atencion@deltron.pe', 'Av. Materiales 3045, Lima');

-- ==========================
-- 3. CATEGORÍAS Y MARCAS
-- ==========================
INSERT INTO categorias (nombre, descripcion, activo) VALUES
('Celulares', 'Smartphones y teléfonos móviles', TRUE),
('Laptops', 'Computadoras portátiles para diferentes tipos de usuarios', TRUE),
('Computadoras', 'Computadoras de escritorio y equipos completos', TRUE),
('Procesadores', 'Procesadores para computadoras de escritorio y portátiles', TRUE),
('Tarjetas Gráficas', 'Tarjetas gráficas para gaming, diseño y procesamiento', TRUE),
('Placas Madre', 'Placas madre para computadoras', TRUE),
('Memoria RAM', 'Memorias RAM para computadoras y laptops', TRUE),
('Almacenamiento', 'Discos SSD, HDD y unidades de almacenamiento', TRUE),
('Fuentes de Poder', 'Fuentes de alimentación para computadoras', TRUE),
('Gabinetes', 'Gabinetes y chasis para computadoras', TRUE),
('Refrigeración', 'Disipadores, coolers y sistemas de refrigeración', TRUE),
('Monitores', 'Monitores para gaming, oficina y diseño', TRUE),
('Tablets', 'Tablets y dispositivos móviles de pantalla táctil', TRUE),
('Smartwatches', 'Relojes inteligentes y accesorios', TRUE),
('Audífonos', 'Audífonos, headsets y dispositivos de audio', TRUE),
('Parlantes', 'Parlantes y equipos de sonido', TRUE),
('Consolas', 'Consolas de videojuegos', TRUE),
('Controles', 'Controles y mandos para videojuegos', TRUE),
('Teclados', 'Teclados convencionales y gaming', TRUE),
('Mouse', 'Mouse convencionales, inalámbricos y gaming', TRUE),
('Cámaras', 'Cámaras digitales, webcams y accesorios', TRUE),
('Accesorios', 'Cables, cargadores, adaptadores y otros accesorios', TRUE);

INSERT INTO marcas (nombre, activo) VALUES
('Apple', TRUE), ('Samsung', TRUE), ('Xiaomi', TRUE), ('Motorola', TRUE), ('Google', TRUE),
('Huawei', TRUE), ('ASUS', TRUE), ('Lenovo', TRUE), ('HP', TRUE), ('Dell', TRUE),
('Acer', TRUE), ('MSI', TRUE), ('Gigabyte', TRUE), ('ASRock', TRUE), ('AMD', TRUE),
('Intel', TRUE), ('NVIDIA', TRUE), ('Kingston', TRUE), ('Corsair', TRUE), ('G.Skill', TRUE),
('Crucial', TRUE), ('Western Digital', TRUE), ('Seagate', TRUE), ('Logitech', TRUE),
('Razer', TRUE), ('HyperX', TRUE), ('SteelSeries', TRUE), ('Sony', TRUE), ('Microsoft', TRUE),
('Nintendo', TRUE), ('JBL', TRUE), ('Bose', TRUE), ('LG', TRUE), ('Philips', TRUE);

-- ==========================
-- 4. PRODUCTOS
-- ==========================
INSERT INTO productos (codigo, nombre, descripcion, precio, stock_actual, stock_minimo, id_categoria, id_marca, activo) VALUES
('TEC001','iPhone 16 128GB','Smartphone Apple con pantalla Super Retina XDR',3299.00,15,3,(SELECT id_categoria FROM categorias WHERE nombre='Celulares' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC002','iPhone 16 Pro 256GB','Smartphone profesional Apple con cámara avanzada',4499.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Celulares' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC003','iPhone 16 Pro Max 256GB','Smartphone Apple de alta gama con gran pantalla',5299.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Celulares' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC004','Samsung Galaxy S25 256GB','Smartphone Samsung de gama alta',3499.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Celulares' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Samsung' LIMIT 1),TRUE),
('TEC005','Samsung Galaxy A56 256GB','Smartphone Samsung de gama media alta',1599.00,20,4,(SELECT id_categoria FROM categorias WHERE nombre='Celulares' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Samsung' LIMIT 1),TRUE),
('TEC006','Xiaomi Redmi Note 14 Pro','Smartphone Xiaomi con cámara de alta resolución',1199.00,25,5,(SELECT id_categoria FROM categorias WHERE nombre='Celulares' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Xiaomi' LIMIT 1),TRUE),
('TEC007','Motorola Edge 50 Fusion','Smartphone Motorola con pantalla OLED',1399.00,18,4,(SELECT id_categoria FROM categorias WHERE nombre='Celulares' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Motorola' LIMIT 1),TRUE),
('TEC008','Google Pixel 9','Smartphone Google con cámara avanzada e inteligencia artificial',2999.00,7,2,(SELECT id_categoria FROM categorias WHERE nombre='Celulares' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Google' LIMIT 1),TRUE),
('TEC009','MacBook Air M3 13 pulgadas','Laptop Apple con chip M3 y almacenamiento SSD',4299.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Laptops' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC010','MacBook Pro M4 14 pulgadas','Laptop profesional Apple con chip M4',6999.00,5,2,(SELECT id_categoria FROM categorias WHERE nombre='Laptops' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC011','ASUS TUF Gaming A15','Laptop gaming con procesador AMD y tarjeta gráfica dedicada',3899.00,9,2,(SELECT id_categoria FROM categorias WHERE nombre='Laptops' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='ASUS' LIMIT 1),TRUE),
('TEC012','Lenovo Legion 5','Laptop gaming de alto rendimiento',4599.00,7,2,(SELECT id_categoria FROM categorias WHERE nombre='Laptops' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Lenovo' LIMIT 1),TRUE),
('TEC013','HP Pavilion 15','Laptop para trabajo, estudio y uso general',2399.00,14,3,(SELECT id_categoria FROM categorias WHERE nombre='Laptops' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='HP' LIMIT 1),TRUE),
('TEC014','Dell Inspiron 15','Laptop para productividad y oficina',2299.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Laptops' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Dell' LIMIT 1),TRUE),
('TEC015','PC Gaming Ryzen 5 RTX 4060','Computadora gaming ensamblada para juegos en alta calidad',4299.00,6,2,(SELECT id_categoria FROM categorias WHERE nombre='Computadoras' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='AMD' LIMIT 1),TRUE),
('TEC016','PC Gaming Ryzen 7 RTX 4070','Computadora gaming de alto rendimiento',6499.00,4,1,(SELECT id_categoria FROM categorias WHERE nombre='Computadoras' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='AMD' LIMIT 1),TRUE),
('TEC017','PC Oficina Intel Core i5','Computadora para oficina, estudio y navegación',1899.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Computadoras' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Intel' LIMIT 1),TRUE),
('TEC018','PC Oficina Intel Core i7','Computadora para productividad y multitarea',2699.00,7,2,(SELECT id_categoria FROM categorias WHERE nombre='Computadoras' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Intel' LIMIT 1),TRUE),
('TEC019','AMD Ryzen 5 7600','Procesador de 6 núcleos para computadoras gaming',899.00,20,5,(SELECT id_categoria FROM categorias WHERE nombre='Procesadores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='AMD' LIMIT 1),TRUE),
('TEC020','AMD Ryzen 7 7800X3D','Procesador gaming de alto rendimiento con tecnología 3D V-Cache',1899.00,10,3,(SELECT id_categoria FROM categorias WHERE nombre='Procesadores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='AMD' LIMIT 1),TRUE),
('TEC021','AMD Ryzen 9 9950X','Procesador de alto rendimiento para productividad',2799.00,6,2,(SELECT id_categoria FROM categorias WHERE nombre='Procesadores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='AMD' LIMIT 1),TRUE),
('TEC022','Intel Core i5-14600K','Procesador Intel de alto rendimiento para gaming',1399.00,15,3,(SELECT id_categoria FROM categorias WHERE nombre='Procesadores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Intel' LIMIT 1),TRUE),
('TEC023','Intel Core i7-14700K','Procesador Intel para gaming y productividad avanzada',1999.00,9,2,(SELECT id_categoria FROM categorias WHERE nombre='Procesadores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Intel' LIMIT 1),TRUE),
('TEC024','Intel Core i9-14900K','Procesador Intel de gama alta',2899.00,5,2,(SELECT id_categoria FROM categorias WHERE nombre='Procesadores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Intel' LIMIT 1),TRUE),
('TEC025','NVIDIA GeForce RTX 4060 8GB','Tarjeta gráfica para gaming en resolución Full HD',1699.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Tarjetas Gráficas' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='NVIDIA' LIMIT 1),TRUE),
('TEC026','NVIDIA GeForce RTX 4070 Super 12GB','Tarjeta gráfica para gaming de alto rendimiento',2999.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Tarjetas Gráficas' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='NVIDIA' LIMIT 1),TRUE),
('TEC027','NVIDIA GeForce RTX 4080 Super 16GB','Tarjeta gráfica de gama alta para gaming y creación',4999.00,4,1,(SELECT id_categoria FROM categorias WHERE nombre='Tarjetas Gráficas' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='NVIDIA' LIMIT 1),TRUE),
('TEC028','AMD Radeon RX 7800 XT 16GB','Tarjeta gráfica AMD para gaming en alta resolución',2499.00,7,2,(SELECT id_categoria FROM categorias WHERE nombre='Tarjetas Gráficas' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='AMD' LIMIT 1),TRUE),
('TEC029','ASUS Dual RTX 4060 8GB','Tarjeta gráfica ASUS con refrigeración de doble ventilador',1799.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Tarjetas Gráficas' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='ASUS' LIMIT 1),TRUE),
('TEC030','ASUS TUF Gaming B650-Plus','Placa madre AM5 para procesadores AMD Ryzen',899.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Placas Madre' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='ASUS' LIMIT 1),TRUE),
('TEC031','MSI MAG B760 Tomahawk','Placa madre compatible con procesadores Intel',999.00,9,2,(SELECT id_categoria FROM categorias WHERE nombre='Placas Madre' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='MSI' LIMIT 1),TRUE),
('TEC032','Gigabyte B650M DS3H','Placa madre Micro ATX para AMD Ryzen',599.00,15,3,(SELECT id_categoria FROM categorias WHERE nombre='Placas Madre' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Gigabyte' LIMIT 1),TRUE),
('TEC033','ASRock B760M Pro RS','Placa madre Micro ATX para Intel',649.00,11,3,(SELECT id_categoria FROM categorias WHERE nombre='Placas Madre' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='ASRock' LIMIT 1),TRUE),
('TEC034','Kingston Fury Beast 16GB DDR5','Memoria RAM DDR5 de 16GB para PC',299.00,30,6,(SELECT id_categoria FROM categorias WHERE nombre='Memoria RAM' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Kingston' LIMIT 1),TRUE),
('TEC035','Kingston Fury Beast 32GB DDR5','Kit de memoria RAM DDR5 de 32GB',549.00,20,4,(SELECT id_categoria FROM categorias WHERE nombre='Memoria RAM' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Kingston' LIMIT 1),TRUE),
('TEC036','Corsair Vengeance 32GB DDR5','Memoria RAM DDR5 para gaming y productividad',599.00,18,4,(SELECT id_categoria FROM categorias WHERE nombre='Memoria RAM' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Corsair' LIMIT 1),TRUE),
('TEC037','G.Skill Trident Z5 32GB','Memoria RAM DDR5 de alto rendimiento',699.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Memoria RAM' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='G.Skill' LIMIT 1),TRUE),
('TEC038','Kingston NV2 1TB NVMe','SSD NVMe PCIe para almacenamiento de alta velocidad',329.00,30,6,(SELECT id_categoria FROM categorias WHERE nombre='Almacenamiento' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Kingston' LIMIT 1),TRUE),
('TEC039','Samsung 990 Pro 1TB','SSD NVMe de alto rendimiento para PC',499.00,18,4,(SELECT id_categoria FROM categorias WHERE nombre='Almacenamiento' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Samsung' LIMIT 1),TRUE),
('TEC040','Western Digital Blue 1TB HDD','Disco duro mecánico de 1TB',229.00,25,5,(SELECT id_categoria FROM categorias WHERE nombre='Almacenamiento' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Western Digital' LIMIT 1),TRUE),
('TEC041','Seagate Barracuda 2TB','Disco duro de 2TB para almacenamiento general',299.00,20,4,(SELECT id_categoria FROM categorias WHERE nombre='Almacenamiento' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Seagate' LIMIT 1),TRUE),
('TEC042','Corsair CX650 650W','Fuente de poder certificada para PC',349.00,15,3,(SELECT id_categoria FROM categorias WHERE nombre='Fuentes de Poder' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Corsair' LIMIT 1),TRUE),
('TEC043','Corsair RM750e 750W','Fuente modular de 750W para equipos gaming',499.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Fuentes de Poder' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Corsair' LIMIT 1),TRUE),
('TEC044','ASUS TUF Gaming 850W','Fuente de poder de alta capacidad para gaming',599.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Fuentes de Poder' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='ASUS' LIMIT 1),TRUE),
('TEC045','ASUS TUF Gaming GT501','Gabinete gaming con amplio espacio interno',699.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Gabinetes' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='ASUS' LIMIT 1),TRUE),
('TEC046','MSI MAG Forge 112R','Gabinete gaming con panel lateral de vidrio',329.00,15,3,(SELECT id_categoria FROM categorias WHERE nombre='Gabinetes' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='MSI' LIMIT 1),TRUE),
('TEC047','NZXT H5 Flow','Gabinete compacto con excelente ventilación',449.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Gabinetes' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='NZXT' LIMIT 1),TRUE),
('TEC048','Cooler Master Hyper 212','Disipador de aire para procesadores',179.00,20,4,(SELECT id_categoria FROM categorias WHERE nombre='Refrigeración' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Cooler Master' LIMIT 1),TRUE),
('TEC049','NZXT Kraken 240','Sistema de refrigeración líquida de 240mm',599.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Refrigeración' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='NZXT' LIMIT 1),TRUE),
('TEC050','Noctua NH-D15','Disipador de aire de alto rendimiento',499.00,7,2,(SELECT id_categoria FROM categorias WHERE nombre='Refrigeración' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Noctua' LIMIT 1),TRUE),
('TEC051','ASUS TUF Gaming 24 pulgadas 180Hz','Monitor gaming Full HD de alta frecuencia',799.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Monitores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='ASUS' LIMIT 1),TRUE),
('TEC052','LG UltraGear 27 pulgadas','Monitor gaming QHD de 27 pulgadas',1399.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Monitores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='LG' LIMIT 1),TRUE),
('TEC053','AOC 24G2 24 pulgadas','Monitor gaming Full HD de 24 pulgadas',699.00,15,3,(SELECT id_categoria FROM categorias WHERE nombre='Monitores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='AOC' LIMIT 1),TRUE),
('TEC054','BenQ PD2705Q 27 pulgadas','Monitor QHD orientado a diseño y productividad',1599.00,6,2,(SELECT id_categoria FROM categorias WHERE nombre='Monitores' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='BenQ' LIMIT 1),TRUE),
('TEC055','iPad 10.9 pulgadas 64GB','Tablet Apple para estudio, trabajo y entretenimiento',1799.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Tablets' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC056','iPad Air M2 11 pulgadas','Tablet Apple con chip M2 y pantalla Liquid Retina',2899.00,7,2,(SELECT id_categoria FROM categorias WHERE nombre='Tablets' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC057','Samsung Galaxy Tab S9 FE','Tablet Samsung para productividad y entretenimiento',1799.00,9,2,(SELECT id_categoria FROM categorias WHERE nombre='Tablets' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Samsung' LIMIT 1),TRUE),
('TEC058','Apple Watch Series 10','Reloj inteligente Apple con funciones deportivas y de salud',1999.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Smartwatches' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC059','Samsung Galaxy Watch 7','Reloj inteligente Samsung con seguimiento deportivo',1299.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Smartwatches' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Samsung' LIMIT 1),TRUE),
('TEC060','Apple AirPods Pro 2','Audífonos inalámbricos con cancelación activa de ruido',999.00,20,4,(SELECT id_categoria FROM categorias WHERE nombre='Audífonos' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Apple' LIMIT 1),TRUE),
('TEC061','Sony WH-1000XM5','Audífonos inalámbricos premium con cancelación de ruido',1399.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Audífonos' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Sony' LIMIT 1),TRUE),
('TEC062','HyperX Cloud III','Headset gaming con micrófono integrado',449.00,18,4,(SELECT id_categoria FROM categorias WHERE nombre='Audífonos' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='HyperX' LIMIT 1),TRUE),
('TEC063','JBL Flip 6','Parlante Bluetooth portátil resistente al agua',499.00,15,3,(SELECT id_categoria FROM categorias WHERE nombre='Parlantes' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='JBL' LIMIT 1),TRUE),
('TEC064','JBL Charge 5','Parlante Bluetooth portátil de gran autonomía',699.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Parlantes' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='JBL' LIMIT 1),TRUE),
('TEC065','PlayStation 5 Slim','Consola Sony PlayStation 5 versión Slim',2499.00,8,2,(SELECT id_categoria FROM categorias WHERE nombre='Consolas' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Sony' LIMIT 1),TRUE),
('TEC066','Xbox Series X','Consola Microsoft de alto rendimiento',2399.00,6,2,(SELECT id_categoria FROM categorias WHERE nombre='Consolas' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Microsoft' LIMIT 1),TRUE),
('TEC067','Nintendo Switch OLED','Consola híbrida Nintendo con pantalla OLED',1599.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Consolas' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Nintendo' LIMIT 1),TRUE),
('TEC068','DualSense PS5','Control inalámbrico para PlayStation 5',349.00,20,4,(SELECT id_categoria FROM categorias WHERE nombre='Controles' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Sony' LIMIT 1),TRUE),
('TEC069','Xbox Wireless Controller','Control inalámbrico compatible con Xbox y PC',299.00,18,4,(SELECT id_categoria FROM categorias WHERE nombre='Controles' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Microsoft' LIMIT 1),TRUE),
('TEC070','Logitech G Pro X TKL','Teclado mecánico gaming compacto',699.00,10,2,(SELECT id_categoria FROM categorias WHERE nombre='Teclados' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Logitech' LIMIT 1),TRUE),
('TEC071','Razer BlackWidow V4','Teclado mecánico gaming con iluminación RGB',649.00,12,3,(SELECT id_categoria FROM categorias WHERE nombre='Teclados' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Razer' LIMIT 1),TRUE),
('TEC072','Logitech G502 X','Mouse gaming de alta precisión',449.00,20,4,(SELECT id_categoria FROM categorias WHERE nombre='Mouse' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Logitech' LIMIT 1),TRUE),
('TEC073','Razer DeathAdder V3','Mouse gaming ergonómico de alto rendimiento',399.00,15,3,(SELECT id_categoria FROM categorias WHERE nombre='Mouse' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Razer' LIMIT 1),TRUE),
('TEC074','Cargador USB-C 65W','Cargador rápido compatible con celulares y laptops',149.00,35,8,(SELECT id_categoria FROM categorias WHERE nombre='Accesorios' LIMIT 1),(SELECT id_marca FROM marcas WHERE nombre='Samsung' LIMIT 1),TRUE);

-- ==========================
-- 5. IMÁGENES DE PRODUCTOS
-- ==========================
INSERT INTO producto_imagenes(id_producto, url_imagen)
SELECT p.id_producto, i.url_imagen
FROM productos p
JOIN (
    SELECT 'TEC001' AS codigo, '/img/products/TEC001 – iPhone 16 128GB.jpg' AS url_imagen
    UNION ALL SELECT 'TEC002', '/img/products/TEC002 – iPhone 16 Pro 256GB.jpg'
    UNION ALL SELECT 'TEC003', '/img/products/TEC003 – iPhone 16 Pro Max 256GB.jpg'
    UNION ALL SELECT 'TEC004', '/img/products/TEC004 – Samsung Galaxy S25 256GB.jpg'
    UNION ALL SELECT 'TEC005', '/img/products/TEC005 – Samsung Galaxy A56 256GB.jpg'
    UNION ALL SELECT 'TEC006', '/img/products/TEC006 – Xiaomi Redmi Note 14 Pro.jpg'
    UNION ALL SELECT 'TEC007', '/img/products/TEC007 – Motorola Edge 50 Fusion.jpg'
    UNION ALL SELECT 'TEC008', '/img/products/TEC008 – Google Pixel 9.jpg'
    UNION ALL SELECT 'TEC009', '/img/products/TEC009 – MacBook Air M3 13 pulgadas.jpg'
    UNION ALL SELECT 'TEC010', '/img/products/TEC010 – MacBook Pro M4 14 pulgadas.jpg'
    UNION ALL SELECT 'TEC011', '/img/products/TEC011 – ASUS TUF Gaming A15.jpg'
    UNION ALL SELECT 'TEC012', '/img/products/TEC012 – Lenovo Legion 5.jpg'
    UNION ALL SELECT 'TEC013', '/img/products/TEC013 – HP Pavilion 15.jpg'
    UNION ALL SELECT 'TEC014', '/img/products/TEC014 – Dell Inspiron 15.jpg'
    UNION ALL SELECT 'TEC015', '/img/products/TEC015 – PC Gaming Ryzen 5 RTX 4060.jpg'
    UNION ALL SELECT 'TEC016', '/img/products/TEC016 – PC Gaming Ryzen 7 RTX 4070.jpg'
    UNION ALL SELECT 'TEC017', '/img/products/TEC017 – PC Oficina Intel Core i5.jpg'
    UNION ALL SELECT 'TEC018', '/img/products/TEC018 – PC Oficina Intel Core i7.jpg'
    UNION ALL SELECT 'TEC019', '/img/products/TEC019 – AMD Ryzen 5 7600.jpg'
    UNION ALL SELECT 'TEC020', '/img/products/TEC020 – AMD Ryzen 7 7800X3D.jpg'
    UNION ALL SELECT 'TEC021', '/img/products/TEC021 – AMD Ryzen 9 9950X.jpg'
    UNION ALL SELECT 'TEC022', '/img/products/TEC022 – Intel Core i5-14600K.jpg'
    UNION ALL SELECT 'TEC023', '/img/products/TEC023 – Intel Core i7-14700K.jpg'
    UNION ALL SELECT 'TEC024', '/img/products/TEC024 – Intel Core i9-14900K.jpg'
    UNION ALL SELECT 'TEC025', '/img/products/TEC025 – NVIDIA GeForce RTX 4060 8GB.jpg'
    UNION ALL SELECT 'TEC026', '/img/products/TEC026 – NVIDIA GeForce RTX 4070 Super 12GB.jpg'
    UNION ALL SELECT 'TEC027', '/img/products/TEC027 – NVIDIA GeForce RTX 4080 Super 16GB.jpg'
    UNION ALL SELECT 'TEC028', '/img/products/TEC028 – AMD Radeon RX 7800 XT 16GB.jpg'
    UNION ALL SELECT 'TEC029', '/img/products/TEC029 – ASUS Dual RTX 4060 8GB.jpg'
    UNION ALL SELECT 'TEC030', '/img/products/TEC030 – ASUS TUF Gaming B650-Plus.jpg'
    UNION ALL SELECT 'TEC031', '/img/products/TEC031 – MSI MAG B760 Tomahawk.jpg'
    UNION ALL SELECT 'TEC032', '/img/products/TEC032 – Gigabyte B650M DS3H.jpg'
    UNION ALL SELECT 'TEC033', '/img/products/TEC033 – ASRock B760M Pro RS.jpg'
    UNION ALL SELECT 'TEC034', '/img/products/TEC034 – Kingston Fury Beast 16GB DDR5.jpg'
    UNION ALL SELECT 'TEC035', '/img/products/TEC035 – Kingston Fury Beast 32GB DDR5.jpg'
    UNION ALL SELECT 'TEC036', '/img/products/TEC036 – Corsair Vengeance 32GB DDR5.jpg'
    UNION ALL SELECT 'TEC037', '/img/products/TEC037 – G.Skill Trident Z5 32GB.jpg'
    UNION ALL SELECT 'TEC038', '/img/products/TEC038 – Kingston NV2 1TB NVMe.jpg'
    UNION ALL SELECT 'TEC039', '/img/products/TEC039 – Samsung 990 Pro 1TB.jpg'
    UNION ALL SELECT 'TEC040', '/img/products/TEC040 – Western Digital Blue 1TB HDD.jpg'
    UNION ALL SELECT 'TEC041', '/img/products/TEC041 – Seagate Barracuda 2TB.jpg'
    UNION ALL SELECT 'TEC042', '/img/products/TEC042 – Corsair CX650 650W.jpg'
    UNION ALL SELECT 'TEC043', '/img/products/TEC043 – Corsair RM750e 750W.jpg'
    UNION ALL SELECT 'TEC045', '/img/products/TEC045 – ASUS TUF Gaming GT501.jpg'
    UNION ALL SELECT 'TEC046', '/img/products/TEC046 – MSI MAG Forge 112R.jpg'
    UNION ALL SELECT 'TEC047', '/img/products/TEC047 – NZXT H5 Flow.jpg'
    UNION ALL SELECT 'TEC048', '/img/products/TEC048 – Cooler Master Hyper 212.jpg'
    UNION ALL SELECT 'TEC049', '/img/products/TEC049 – NZXT Kraken 240.jpg'
    UNION ALL SELECT 'TEC050', '/img/products/TEC050 – Noctua NH-D15.jpg'
    UNION ALL SELECT 'TEC051', '/img/products/TEC051 – ASUS TUF Gaming 24 pulgadas 180Hz.jpg'
    UNION ALL SELECT 'TEC052', '/img/products/TEC052 – LG UltraGear 27 pulgadas.jpg'
    UNION ALL SELECT 'TEC053', '/img/products/TEC053 – AOC 24G2 24 pulgadas.jpg'
    UNION ALL SELECT 'TEC054', '/img/products/TEC054 – BenQ PD2705Q 27 pulgadas.jpg'
    UNION ALL SELECT 'TEC055', '/img/products/TEC055 – iPad 10.9 pulgadas 64GB.jpg'
    UNION ALL SELECT 'TEC056', '/img/products/TEC056 – iPad Air M2 11 pulgadas.jpg'
    UNION ALL SELECT 'TEC057', '/img/products/TEC057 – Samsung Galaxy Tab S9 FE.jpg'
    UNION ALL SELECT 'TEC058', '/img/products/TEC058 – Apple Watch Series 10.jpg'
    UNION ALL SELECT 'TEC059', '/img/products/TEC059 – Samsung Galaxy Watch 7.jpg'
    UNION ALL SELECT 'TEC060', '/img/products/TEC060 – Apple AirPods Pro 2.jpg'
    UNION ALL SELECT 'TEC061', '/img/products/TEC061 – Sony WH-1000XM5.jpg'
    UNION ALL SELECT 'TEC062', '/img/products/TEC062 – HyperX Cloud III.jpg'
    UNION ALL SELECT 'TEC063', '/img/products/TEC063 – JBL Flip 6.jpg'
    UNION ALL SELECT 'TEC064', '/img/products/TEC064 – JBL Charge 5.jpg'
    UNION ALL SELECT 'TEC065', '/img/products/TEC065 – PlayStation 5 Slim.jpg'
    UNION ALL SELECT 'TEC066', '/img/products/TEC066 – Xbox Series X.jpg'
    UNION ALL SELECT 'TEC067', '/img/products/TEC067 – Nintendo Switch OLED.jpg'
    UNION ALL SELECT 'TEC068', '/img/products/TEC068 – DualSense PS5.jpg'
    UNION ALL SELECT 'TEC069', '/img/products/TEC069 – Xbox Wireless Controller.jpg'
    UNION ALL SELECT 'TEC070', '/img/products/TEC070 – Logitech G Pro X TKL.jpg'
    UNION ALL SELECT 'TEC071', '/img/products/TEC071 – Razer BlackWidow V4.jpg'
    UNION ALL SELECT 'TEC072', '/img/products/TEC072 – Logitech G502 X.jpg'
    UNION ALL SELECT 'TEC073', '/img/products/TEC073 – Razer DeathAdder V3.jpg'
    UNION ALL SELECT 'TEC074', '/img/products/TEC074 – Cargador USB-C 65W.jpg'
) i ON i.codigo = p.codigo;