import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Ventaservice } from '../ventaservice';
import { Productoservice } from '../../catalogo/productoservice';
import { Producto } from '../../catalogo/model/Producto';
import { ItemVentaCarrito } from '../model/Venta';
import { Authservice } from '../../auth/authservice';
import { Toastservice } from '../../shared/toastservice';

@Component({
  selector: 'app-registrar-venta',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './registrar.html',
  styleUrl: './registrar.css',
})
export class Registrar {

  productos: Producto[] = [];

  dni = '';
  idCliente: number | null = null;
  nombres = '';
  apellidos = '';
  telefono = '';
  direccion = '';
  mensajeCliente = '';
  mensajeClienteOk = false;
  today = new Date();

  nuevoDni = '';
  nuevoNombre = '';
  nuevoApellido = '';
  nuevoTelefono = '';
  nuevoDireccion = '';

  tipoComprobante = '';
  seriesDisponibles: string[] = [];
  serie = '';
  correlativo = '';
  observacion = '';

  idProductoSeleccionado: number | null = null;
  precioSeleccionado = 0;
  stockSeleccionado = 0;
  cantidadSeleccionada = 1;

  detalle: ItemVentaCarrito[] = [];

  metodoPago = '';
  bancoTarjeta = 'BCP';
  tipoTarjeta = 'Débito';
  codigoOperacionTarjeta = '';
  codigoOperacionYape = '';

  subtotal = 0;
  igv = 0;
  total = 0;

  procesando = false;
  estadoOverlay: 'oculto' | 'procesando' | 'aprobado' = 'oculto';

  usuarioActual: any = null;

  constructor(
    private ventaservice: Ventaservice,
    private productoservice: Productoservice,
    private authservice: Authservice,
    private toastservice: Toastservice,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.authservice.currentUser$.subscribe(u => { this.usuarioActual = u; this.cdr.detectChanges(); });
    this.productoservice.listar().subscribe(p => { this.productos = p; this.cdr.detectChanges(); });
  }

  buscarCliente() {
    if (this.dni.trim().length !== 8) {
      this.toastservice.error('Ingrese un DNI válido');
      return;
    }

    this.idCliente = null;

    this.ventaservice.buscarClientePorDni(this.dni.trim()).subscribe(c => {
      if (c.encontrado) {
        this.idCliente = c.idCliente;
        this.nombres = c.nombres;
        this.apellidos = c.apellidos;
        this.telefono = c.telefono || '';
        this.direccion = c.direccion || '';
        this.mensajeCliente = '✅ Cliente encontrado';
        this.mensajeClienteOk = true;
      } else {
        this.idCliente = null;
        this.nombres = '';
        this.apellidos = '';
        this.telefono = '';
        this.direccion = '';
        this.mensajeCliente = '❌ Cliente no encontrado. Puede registrar uno nuevo.';
        this.mensajeClienteOk = false;
      }
      this.cdr.detectChanges();
    });
  }

  abrirModalNuevoCliente() {
    this.nuevoDni = this.dni;
  }

  guardarClienteRapido() {
    this.ventaservice.crearClienteRapido({
      dni: this.nuevoDni,
      nombres: this.nuevoNombre,
      apellidos: this.nuevoApellido,
      telefono: this.nuevoTelefono,
      direccion: this.nuevoDireccion
    }).subscribe(c => {
      this.idCliente = c.idCliente;
      this.dni = c.dni;
      this.nombres = c.nombres;
      this.apellidos = c.apellidos;
      this.telefono = c.telefono;
      this.direccion = c.direccion;
      this.mensajeCliente = '✅ Cliente registrado correctamente';
      this.mensajeClienteOk = true;
      this.cdr.detectChanges();
    });
  }

  onProductoChange() {
    const p = this.productos.find(x => x.idProducto === this.idProductoSeleccionado);
    this.precioSeleccionado = p ? p.precio : 0;
    this.stockSeleccionado = p ? p.stockActual : 0;
  }

  agregarProducto() {
    if (!this.idProductoSeleccionado) {
      this.toastservice.error('Seleccione un producto');
      return;
    }
    if (!this.cantidadSeleccionada || this.cantidadSeleccionada <= 0) {
      this.toastservice.error('Ingrese una cantidad válida');
      return;
    }

    const p = this.productos.find(x => x.idProducto === this.idProductoSeleccionado)!;
    const existente = this.detalle.find(d => d.id === p.idProducto);

    if (existente) {
      if (existente.cantidad + this.cantidadSeleccionada > this.stockSeleccionado) {
        this.toastservice.error('No hay suficiente stock.');
        return;
      }
      existente.cantidad += this.cantidadSeleccionada;
      existente.subtotal = existente.cantidad * existente.precio;
    } else {
      if (this.cantidadSeleccionada > this.stockSeleccionado) {
        this.toastservice.error('No hay suficiente stock.');
        return;
      }
      this.detalle.push({
        id: p.idProducto,
        nombre: p.nombre,
        precio: p.precio,
        stock: p.stockActual,
        cantidad: this.cantidadSeleccionada,
        subtotal: p.precio * this.cantidadSeleccionada
      });
    }

    this.idProductoSeleccionado = null;
    this.precioSeleccionado = 0;
    this.stockSeleccionado = 0;
    this.cantidadSeleccionada = 1;
    this.actualizarTotales();
  }

  eliminarProducto(index: number) {
    this.detalle.splice(index, 1);
    this.actualizarTotales();
  }

  vaciarLista() {
    if (this.detalle.length === 0) return;
    if (confirm('¿Desea eliminar todos los productos?')) {
      this.detalle = [];
      this.actualizarTotales();
    }
  }

  actualizarTotales() {
    const totalConIgv = this.detalle.reduce((acc, d) => acc + d.subtotal, 0);
    this.subtotal = totalConIgv / 1.18;
    this.igv = totalConIgv - this.subtotal;
    this.total = totalConIgv;
  }

  onTipoComprobanteChange() {
    this.serie = '';
    this.correlativo = '';
    if (this.tipoComprobante === 'BOLETA') {
      this.seriesDisponibles = ['B001', 'B002', 'B003', 'B004'];
    } else if (this.tipoComprobante === 'FACTURA') {
      this.seriesDisponibles = ['F001', 'F002', 'F003', 'F004'];
    } else {
      this.seriesDisponibles = [];
    }
  }

  onSerieChange() {
    if (!this.serie) return;
    this.ventaservice.correlativo(this.serie).subscribe(numero => {
      this.correlativo = numero;
      this.cdr.detectChanges();
    });
  }

  registrarVenta() {
    if (!this.idCliente) {
      this.toastservice.error('Debe seleccionar o registrar un cliente');
      return;
    }
    if (this.detalle.length === 0) {
      this.toastservice.error('Agregue al menos un producto');
      return;
    }
    if (!this.tipoComprobante || !this.serie) {
      this.toastservice.error('Complete los datos del comprobante');
      return;
    }
    if (!this.metodoPago) {
      this.toastservice.error('Seleccione un método de pago');
      return;
    }

    this.procesando = true;
    this.estadoOverlay = 'procesando';

    const body = {
      idCliente: this.idCliente,
      idUsuario: this.usuarioActual?.idUsuario,
      tipoComprobante: this.tipoComprobante,
      serie: this.serie,
      correlativo: Number(this.correlativo),
      tipoEntrega: 'Retiro en tienda',
      observacion: this.observacion,
      subtotal: Number(this.subtotal.toFixed(2)),
      igv: Number(this.igv.toFixed(2)),
      total: Number(this.total.toFixed(2)),
      metodoPago: this.metodoPago,
      detalle: this.detalle.map(d => ({ id: d.id, cantidad: d.cantidad, precio: d.precio }))
    };

    this.ventaservice.registrar(body).subscribe({
      next: () => {
        this.estadoOverlay = 'aprobado';
        setTimeout(() => {
          this.router.navigate(['/ventas/listado']);
        }, 1400);
      },
      error: (err) => {
        this.procesando = false;
        this.estadoOverlay = 'oculto';
        this.toastservice.error(err.error?.mensaje || 'Error al registrar la venta');
      }
    });
  }
}
