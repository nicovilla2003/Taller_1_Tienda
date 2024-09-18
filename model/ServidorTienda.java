package model;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorTienda {
    private static List<Producto> productos = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        inicializarProductos();
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Servidor iniciado...");
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                new ClienteHandler(clienteSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void inicializarProductos() {
        productos.add(new Producto(1, "Laptop", 1200.0, 10));
        productos.add(new Producto(2, "Auriculares", 50.0, 50));
        productos.add(new Producto(3, "Teclado", 35.0, 20));
    }

    private static class ClienteHandler extends Thread {
        private Socket clienteSocket;

        public ClienteHandler(Socket socket) {
            this.clienteSocket = socket;
        }

        @Override
        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(clienteSocket.getInputStream());
                 ObjectOutputStream out = new ObjectOutputStream(clienteSocket.getOutputStream())) {

                String opcion;
                while ((opcion = (String) in.readObject()) != null) {
                    switch (opcion) {
                        case "buscar":
                        synchronized (productos) {
                            out.writeObject(new ArrayList<>(productos));
                            }
                            out.flush();
                            break;
                        case "comprar":
                            int productoId = in.readInt();
                            int cantidad = in.readInt();
                            boolean resultado = procesarCompra(productoId, cantidad);
                            out.writeBoolean(resultado);
                            out.flush();
                            break;
                        default:
                            out.writeObject("Opción no válida");
                            out.flush();
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private synchronized boolean procesarCompra(int productoId, int cantidad) {
            synchronized (productos) { // Sincronizamos el acceso a la lista de productos
                for (Producto producto : productos) {
                    if (producto.getId() == productoId) {
                        if (producto.getCantidadDisponible() >= cantidad) {
                            producto.reducirCantidad(cantidad); // Reducir la cantidad disponible
                            return true; // Compra exitosa
                        } else {
                            return false; // No hay suficiente stock
                        }
                    }
                }
                return false; // Producto no encontrado
            }
        }
    }
}
