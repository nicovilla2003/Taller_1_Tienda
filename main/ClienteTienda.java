package main;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

import model.Producto;

public class ClienteTienda {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Scanner scanner = new Scanner(System.in);
            String opcion;

            do {
                System.out.println("Menu:");
                System.out.println("1. Buscar productos");
                System.out.println("2. Realizar compra");
                System.out.println("3. Salir");
                System.out.print("Elige una opción: ");
                opcion = scanner.nextLine();

                if (opcion.equals("1")) {
                    out.writeObject("buscar");
                    out.flush();

                    List<Producto> productos = (List<Producto>) in.readObject();
                    for (Producto producto : productos) {
                        System.out.println(producto);
                    }

                } else if (opcion.equals("2")) {
                    System.out.print("Ingrese el ID del producto: ");
                    int idProducto = Integer.parseInt(scanner.nextLine());
                    System.out.print("Ingrese la cantidad: ");
                    int cantidad = Integer.parseInt(scanner.nextLine());

                    out.writeObject("comprar");
                    out.flush();
                    out.writeInt(idProducto);
                    out.flush();
                    out.writeInt(cantidad);
                    out.flush();

                    boolean exito = in.readBoolean();
                    if (exito) {
                        System.out.println("Compra realizada con éxito.");
                    } else {
                        System.out.println("No hay suficiente stock o producto no encontrado.");
                    }
                }

            } while (!opcion.equals("3"));

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
