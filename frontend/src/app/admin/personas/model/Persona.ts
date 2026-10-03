export class Cliente {
  idCliente?: number;
  nombres!: string;
  apellidos!: string;
  dni!: string;
  telefono!: string;
  direccion!: string;
  tipoDocumento!: string;
  numeroDocumento!: string;
  emailFacturacion!: string;
  razonSocial!: string;
  direccionFiscal!: string;
}

export class Proveedor {
  idProveedor?: number;
  ruc!: string;
  razonSocial!: string;
  telefono!: string;
  correo!: string;
  direccion!: string;
}

export class Rol {
  idRol!: number;
  nombre!: string;
}

export class UsuarioAdmin {
  idUsuario?: number;
  username!: string;
  email!: string;
  password?: string;
  activo!: boolean;
  proveedorAuth!: string;
  fotoPerfil!: string | null;
  roles!: string[];
  idRol?: number;
}
