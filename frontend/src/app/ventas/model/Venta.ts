export class ClienteBusqueda {
  encontrado!: boolean;
  idCliente!: number | null;
  dni!: string;
  nombres!: string;
  apellidos!: string;
  telefono!: string;
  direccion!: string;
}

export class ItemVentaCarrito {
  id!: number;
  nombre!: string;
  precio!: number;
  stock!: number;
  cantidad!: number;
  subtotal!: number;
}

export class VentaListadoItem {
  idVenta!: number;
  serie!: string;
  correlativo!: number;
  nombreCliente!: string;
  dniCliente!: string;
  fecha!: string;
  hora!: string;
  metodoPago!: string;
  total!: number;
  estado!: string;
}

export class VentaPagina {
  contenido!: VentaListadoItem[];
  paginaActual!: number;
  totalPaginas!: number;
  totalElementos!: number;
  primera!: boolean;
  ultima!: boolean;
}

export class VentaComprobanteItem {
  nombreProducto!: string;
  cantidad!: number;
  precioUnitario!: number;
  importe!: number;
}

export class VentaComprobante {
  idVenta!: number;
  tipoComprobante!: string;
  serie!: string;
  correlativo!: number;
  fecha!: string;
  nombreCliente!: string;
  dniCliente!: string;
  direccionCliente!: string;
  nombreUsuario!: string;
  metodoPago!: string;
  estado!: string;
  tipoEntrega!: string;
  observacion!: string;
  subtotal!: number;
  igv!: number;
  total!: number;
  items!: VentaComprobanteItem[];
}
