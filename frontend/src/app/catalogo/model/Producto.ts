export class Producto {
  idProducto!: number;
  codigo!: string;
  nombre!: string;
  descripcion!: string;
  precio!: number;
  stockActual!: number;
  stockMinimo!: number;
  idCategoria!: number;
  nombreCategoria!: string;
  idMarca!: number;
  nombreMarca!: string;
  activo!: boolean;
  imagenUrl!: string;
}

const BACKEND_URL = '';

export function urlImagenProducto(producto: Producto): string {
  if (!producto.imagenUrl) return `${BACKEND_URL}/img/products/producto-default.svg`;
  return producto.imagenUrl.startsWith('http') ? producto.imagenUrl : `${BACKEND_URL}${producto.imagenUrl}`;
}
