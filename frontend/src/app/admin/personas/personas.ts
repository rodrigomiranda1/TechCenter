import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Personasservice } from './personasservice';
import { Cliente, Proveedor, Rol, UsuarioAdmin } from './model/Persona';
import { Toastservice } from '../../shared/toastservice';

@Component({
  selector: 'app-personas',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './personas.html',
  styleUrl: './personas.css',
})
export class Personas {

  clientes: Cliente[] = [];
  proveedores: Proveedor[] = [];
  usuarios: UsuarioAdmin[] = [];
  roles: Rol[] = [];

  buscarCliente = '';
  buscarProveedor = '';
  buscarUsuario = '';

  nuevoCliente = new Cliente();
  nuevoProveedor = new Proveedor();
  nuevoUsuario = new UsuarioAdmin();

  clienteEditando: Cliente = new Cliente();
  proveedorEditando: Proveedor = new Proveedor();

  cargando = true;

  constructor(private personasservice: Personasservice, private cdr: ChangeDetectorRef, private toastservice: Toastservice) {
    this.cargarTodo();
  }

  cargarTodo() {
    this.personasservice.listarClientes().subscribe(c => { this.clientes = c; this.cdr.detectChanges(); });
    this.personasservice.listarProveedores().subscribe(p => { this.proveedores = p; this.cdr.detectChanges(); });
    this.personasservice.listarUsuarios().subscribe(u => { this.usuarios = u; this.cdr.detectChanges(); });
    this.personasservice.listarRoles().subscribe(r => { this.roles = r; this.cargando = false; this.cdr.detectChanges(); });
  }

  get clientesFiltrados() {
    const t = this.buscarCliente.trim().toLowerCase();
    if (!t) return this.clientes;
    return this.clientes.filter(c => (c.nombres + ' ' + (c.apellidos || '')).toLowerCase().includes(t));
  }

  get proveedoresFiltrados() {
    const t = this.buscarProveedor.trim().toLowerCase();
    if (!t) return this.proveedores;
    return this.proveedores.filter(p => p.razonSocial.toLowerCase().includes(t));
  }

  get usuariosFiltrados() {
    const t = this.buscarUsuario.trim().toLowerCase();
    if (!t) return this.usuarios;
    return this.usuarios.filter(u => u.username.toLowerCase().includes(t));
  }

  guardarNuevoCliente() {
    this.personasservice.crearCliente(this.nuevoCliente).subscribe({
      next: () => {
        this.toastservice.exito('Cliente guardado correctamente');
        this.nuevoCliente = new Cliente();
        this.cargarTodo();
      },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al guardar cliente')
    });
  }

  abrirEdicionCliente(c: Cliente) {
    this.clienteEditando = { ...c };
  }

  guardarEdicionCliente() {
    this.personasservice.actualizarCliente(this.clienteEditando.idCliente!, this.clienteEditando).subscribe({
      next: () => {
        this.toastservice.exito('Cliente actualizado correctamente');
        this.cargarTodo();
      },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al actualizar cliente')
    });
  }

  eliminarCliente(id: number) {
    if (!confirm('¿Quieres eliminar este cliente?')) return;
    this.personasservice.eliminarCliente(id).subscribe({
      next: () => { this.toastservice.exito('Cliente eliminado correctamente'); this.cargarTodo(); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'No se pudo eliminar el cliente')
    });
  }

  guardarNuevoProveedor() {
    this.personasservice.crearProveedor(this.nuevoProveedor).subscribe({
      next: () => {
        this.toastservice.exito('Proveedor guardado correctamente');
        this.nuevoProveedor = new Proveedor();
        this.cargarTodo();
      },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al guardar proveedor')
    });
  }

  abrirEdicionProveedor(p: Proveedor) {
    this.proveedorEditando = { ...p };
  }

  guardarEdicionProveedor() {
    this.personasservice.actualizarProveedor(this.proveedorEditando.idProveedor!, this.proveedorEditando).subscribe({
      next: () => {
        this.toastservice.exito('Proveedor actualizado correctamente');
        this.cargarTodo();
      },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al actualizar proveedor')
    });
  }

  eliminarProveedor(id: number) {
    if (!confirm('¿Quieres eliminar este proveedor?')) return;
    this.personasservice.eliminarProveedor(id).subscribe({
      next: () => { this.toastservice.exito('Proveedor eliminado correctamente'); this.cargarTodo(); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'No se pudo eliminar el proveedor')
    });
  }

  guardarNuevoUsuario() {
    this.personasservice.crearUsuario(this.nuevoUsuario).subscribe({
      next: () => {
        this.toastservice.exito('Usuario guardado correctamente');
        this.nuevoUsuario = new UsuarioAdmin();
        this.cargarTodo();
      },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al guardar usuario')
    });
  }

  activarUsuario(id: number) {
    if (!confirm('¿Quieres activar este usuario?')) return;
    this.personasservice.activarUsuario(id).subscribe({
      next: () => { this.toastservice.exito('Usuario activado correctamente'); this.cargarTodo(); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al activar usuario')
    });
  }

  desactivarUsuario(id: number) {
    if (!confirm('¿Quieres desactivar este usuario?')) return;
    this.personasservice.desactivarUsuario(id).subscribe({
      next: () => { this.toastservice.exito('Usuario desactivado correctamente'); this.cargarTodo(); },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al desactivar usuario')
    });
  }
}
