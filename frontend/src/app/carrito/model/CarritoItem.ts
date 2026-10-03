const BACKEND_URL = 'http://localhost:8080';

export class CarritoItem {
  idProducto!: number;
  codigo!: string;
  nombre!: string;
  precio!: number;
  cantidad!: number;
  subtotal!: number;
  stockActual!: number;
  imagenUrl!: string;
}

export function urlImagenCarrito(item: CarritoItem): string {
  if (!item.imagenUrl) return `${BACKEND_URL}/img/products/producto-default.svg`;
  return item.imagenUrl.startsWith('http') ? item.imagenUrl : `${BACKEND_URL}${item.imagenUrl}`;
}

export class CarritoResumen {
  items!: CarritoItem[];
  total!: number;
}
