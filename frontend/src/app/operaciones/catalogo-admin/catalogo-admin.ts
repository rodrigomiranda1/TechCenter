import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Catalogoadminservice, CatalogoAdminData } from './catalogoadminservice';
import { Categoria, Marca, ProductoAdmin, urlImagenAdmin } from './model/CatalogoAdmin';
import { Toastservice } from '../../shared/toastservice';

@Component({
  selector: 'app-catalogo-admin',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './catalogo-admin.html',
  styleUrl: './catalogo-admin.css',
})
export class CatalogoAdmin {

  protected readonly urlImagenAdmin = urlImagenAdmin;

  datos: CatalogoAdminData = { productos: [], categorias: [], marcas: [], paginaActual: 1, totalPaginas: 1, totalProductos: 0 };
  cargando = true;
  filtro = '';

  nuevaCategoria = new Categoria();
  nuevaMarca = new Marca();
  nuevoProducto = new ProductoAdmin();

  productoEditando: ProductoAdmin = new ProductoAdmin();

  constructor(
    private catalogoadminservice: Catalogoadminservice,
    private cdr: ChangeDetectorRef,
    private toastservice: Toastservice
  ) {
    this.cargar(1);
  }

  cargar(pagina: number) {
    this.catalogoadminservice.catalogo(pagina).subscribe(datos => {
      this.datos = datos;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }

  get productosFiltrados() {
    const t = this.filtro.trim().toLowerCase();
    if (!t) return this.datos.productos;
    return this.datos.productos.filter(p => p.nombre.toLowerCase().includes(t) || p.codigo.toLowerCase().includes(t));
  }

  get categoriasActivas() {
    return this.datos.categorias.filter(c => c.activo);
  }

  get marcasActivas() {
    return this.datos.marcas.filter(m => m.activo);
  }

  getCategoriaNombre(id: number): string {
    return this.datos.categorias.find(c => c.idCategoria === id)?.nombre || '-';
  }
  getMarcaNombre(id: number): string {
    return this.datos.marcas.find(m => m.idMarca === id)?.nombre || '-';
  }

  paginasArray(): number[] {
    return Array.from({ length: this.datos.totalPaginas }, (_, i) => i + 1);
  }


  guardarCategoria() {
    this.catalogoadminservice.crearCategoria(this.nuevaCategoria).subscribe({
      next: () => { this.toastservice.exito('Categoría guardada correctamente'); this.nuevaCategoria = new Categoria(); this.cargar(this.datos.paginaActual); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al guardar categoría')
    });
  }

  toggleCategoria(c: Categoria) {
    const accion = c.activo
      ? this.catalogoadminservice.desactivarCategoria(c.idCategoria!)
      : this.catalogoadminservice.activarCategoria(c.idCategoria!);
    accion.subscribe({
      next: (r) => { this.toastservice.exito(r.mensaje); this.cargar(this.datos.paginaActual); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al actualizar categoría')
    });
  }


  guardarMarca() {
    this.catalogoadminservice.crearMarca(this.nuevaMarca).subscribe({
      next: () => { this.toastservice.exito('Marca guardada correctamente'); this.nuevaMarca = new Marca(); this.cargar(this.datos.paginaActual); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al guardar marca')
    });
  }

  toggleMarca(m: Marca) {
    const accion = m.activo
      ? this.catalogoadminservice.desactivarMarca(m.idMarca!)
      : this.catalogoadminservice.activarMarca(m.idMarca!);
    accion.subscribe({
      next: (r) => { this.toastservice.exito(r.mensaje); this.cargar(this.datos.paginaActual); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al actualizar marca')
    });
  }

  eliminarMarca(id: number) {
    if (!confirm('¿Quieres eliminar esta marca?')) return;
    this.catalogoadminservice.eliminarMarca(id).subscribe({
      next: (r) => { this.toastservice.exito(r.mensaje); this.cargar(this.datos.paginaActual); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'No se pudo eliminar la marca')
    });
  }


  guardarNuevoProducto() {
    this.catalogoadminservice.crearProducto(this.nuevoProducto).subscribe({
      next: () => { this.toastservice.exito('Producto guardado correctamente'); this.nuevoProducto = new ProductoAdmin(); this.cargar(1); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al guardar producto')
    });
  }

  abrirEdicionProducto(p: ProductoAdmin) {
    this.productoEditando = { ...p };
  }

  guardarEdicionProducto() {
    this.catalogoadminservice.actualizarProducto(this.productoEditando.idProducto!, this.productoEditando).subscribe({
      next: () => { this.toastservice.exito('Producto actualizado correctamente'); this.cargar(this.datos.paginaActual); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al actualizar producto')
    });
  }

  toggleProducto(p: ProductoAdmin) {
    const accion = p.activo
      ? this.catalogoadminservice.desactivarProducto(p.idProducto!)
      : this.catalogoadminservice.activarProducto(p.idProducto!);
    accion.subscribe({
      next: (r) => { this.toastservice.exito(r.mensaje); this.cargar(this.datos.paginaActual); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al actualizar producto')
    });
  }

  eliminarProducto(id: number) {
    if (!confirm('¿Quieres eliminar definitivamente este producto?')) return;
    this.catalogoadminservice.eliminarProducto(id).subscribe({
      next: (r) => { this.toastservice.exito(r.mensaje); this.cargar(this.datos.paginaActual); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'No se pudo eliminar el producto')
    });
  }
}
