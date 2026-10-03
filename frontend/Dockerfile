#compilacion de la aplicacion angular con node.js
FROM node:20-alpine AS builder
WORKDIR /app

#copiar los archivos de gestion de paquetes e instala dependencias
COPY package*.json ./
RUN npm install

#copia el codigo fuente y genera los archivos de produccion
COPY . .
RUN npm run build -- --configuration production

#servir los archivos estaticos con nginx
FROM nginx:alpine

#copia el resultado del buil hacia la carpeta de nginx
COPY --from=builder /app/dist/angular-ferremax/browser /usr/share/nginx/html

#copia la configuracion personalizada de nginx para manejar rutas de angular
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD [ "nginx", "-g", "daemon off;" ]