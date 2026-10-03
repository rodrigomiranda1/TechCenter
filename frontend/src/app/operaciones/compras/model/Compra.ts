export class Compra {
  idCompra!: number;
  fechaCompra!: string;
  nombreProveedor!: string;
  total!: number;
}

export interface ProveedorSelect {
  idProveedor: number;
  razonSocial: string;
}

export interface ProductoSelect {
  idProducto: number;
  nombre: string;
}
