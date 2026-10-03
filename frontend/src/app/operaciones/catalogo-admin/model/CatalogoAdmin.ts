const BACKEND_URL = 'http://localhost:8080';

export class Categoria {
  idCategoria?: number;
  nombre!: string;
  descripcion!: string;
  activo!: boolean;
}

export class Marca {
  idMarca?: number;
  nombre!: string;
  activo!: boolean;
}

export class ProductoAdmin {
  idProducto?: number;
  codigo!: string;
  nombre!: string;
  descripcion!: string;
  precio!: number;
  stockActual!: number;
  stockMinimo!: number;
  idCategoria!: number;
  idMarca!: number;
  urlImagen!: string;
  activo!: boolean;
}

export function urlImagenAdmin(p: ProductoAdmin): string {
  if (!p.urlImagen) return `${BACKEND_URL}/img/products/producto-default.svg`;
  return p.urlImagen.startsWith('http') ? p.urlImagen : `${BACKEND_URL}${p.urlImagen}`;
}
