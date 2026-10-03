package com.techcenter.api.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String uri = request.getRequestURI();
        if (uri.startsWith("/admin")) {
            return permitir(request, response, Set.of("ADMIN"));
        }
        if (uri.startsWith("/operaciones/ventas")) {
            return permitir(request, response, Set.of("ADMIN", "VENDEDOR"));
        }
        if (uri.startsWith("/operaciones/compras") || uri.startsWith("/operaciones/inventario")) {
            return permitir(request, response, Set.of("ADMIN", "ALMACENERO"));
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean permitir(HttpServletRequest request, HttpServletResponse response, Set<String> permitidos)
            throws IOException {
        Object rolesObj = request.getSession().getAttribute("roles");
        if (rolesObj instanceof Set<?> roles && roles.stream().anyMatch(r -> permitidos.contains(String.valueOf(r)))) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
}
}
