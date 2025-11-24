<%@ page import="java.sql.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Prueba de Conexión DB</title>
    <style>
        body { font-family: sans-serif; padding: 20px; }
        .success { color: green; font-weight: bold; }
        .error { color: red; font-weight: bold; }
        .log { background: #f0f0f0; padding: 10px; border: 1px solid #ccc; }
    </style>
</head>
<body>
<h1>Diagnóstico Directo de Base de Datos</h1>

<div class="log">
    <%
        String url = "jdbc:mariadb://localhost:3306/cocina";
        String usuario = "root";
        String clave = "020320";

        out.println("<p>Intentando conectar a: " + url + "</p>");

        try {
            // 1. Cargar el driver
            Class.forName("org.mariadb.jdbc.Driver");
            out.println("<p>Driver JDBC encontrado.</p>");

            // 2. Intentar conectar
            Connection conn = DriverManager.getConnection(url, usuario, clave);
            out.println("<p class='success'>¡CONEXIÓN EXITOSA!</p>");

            // 3. Probar una consulta simple
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM roles");
            if(rs.next()) {
                out.println("<p>Número de roles encontrados: " + rs.getInt(1) + "</p>");
            }

            conn.close();
        } catch (Exception e) {
            out.println("<p class='error'>ERROR DE CONEXIÓN:</p>");
            out.println("<pre>" + e.toString() + "</pre>");
            e.printStackTrace(new java.io.PrintWriter(out));
        }
    %>
</div>

<hr/>
<p><a href="index.html">Volver al Index</a></p>
</body>
</html>