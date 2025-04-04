package archivos;

import api.ConjuntoTDA;
import api.GrafoTDA;
import imp.Dijkstra;
import imp.GrafoLA;
import imp.ConjuntoTA;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void Costo(GrafoTDA grafo, ConjuntoTDA c, int[][] matriz, ArrayList<Integer> centros, ArrayList<Integer> CoPuerto, int[] volProd) {
        // Para cada cliente (0-49)
        for (int cliente = 0; cliente < 50; cliente++) {
            // Calculamos Dijkstra desde el cliente
            GrafoTDA distancias = Dijkstra.dijkstra(grafo, cliente);

            // Para cada centro usando el índice del ArrayList
            for (int i = 0; i < centros.size(); i++) {
                int centroID = centros.get(i);

                // Si existe un camino del cliente al centro
                if (distancias.ExisteArista(cliente, centroID)) {
                    // Costo total = costo del camino + costo del puerto
                    int costoCamino = distancias.PesoArista(cliente, centroID);
                    int costoTotal = costoCamino + CoPuerto.get(i);
                    matriz[i][cliente] = costoTotal * volProd[cliente] ;
                } else {
                    matriz[i][cliente] = Integer.MAX_VALUE;
                }
            }
        }

        System.out.println("Matriz de costos:");
        for (int i = 0; i < 8; i++) {
            System.out.printf("Centro %d | ", centros.get(i));  // Usamos el ID real del centro
            for (int j = 0; j < 50; j++) {
                if (matriz[i][j] == Integer.MAX_VALUE) {
                    System.out.print("INF ");
                } else {
                    System.out.printf("%1d ", matriz[i][j]);
                }
            }
            System.out.println();
        }
    }

//    public static void buscaCostoTotalPorCentro(int[][] matriz, ArrayList<Integer> CoPuerto) {
//        for (int i = 0; i < 8; i++) {
//            int suma = 0;
//            for (int j = 0; j < 50; j++) {
//                suma += matriz[i][j];
//            }
//            System.out.println("Costo total para el centro " + i + ": " + suma);
//        }
//    }


    public static void main(String[] args) {
        int[][] matriz = new int[8][50];

        GrafoLA grafo = new GrafoLA();
        grafo.InicializarGrafo();
        ConjuntoTDA Centros = new ConjuntoTA();
        Centros.InicializarConjunto();
        ConjuntoTDA Clientes = new ConjuntoTA();
        Clientes.InicializarConjunto();
        int[] volProd = new int[50];
        ArrayList<Integer> CoPuerto = new ArrayList<>();
        ArrayList<Integer> centros = new ArrayList<>();
        ArrayList<Integer> CoFijo = new ArrayList<>();

        String clientesYCentros = "C:\\Users\\Usuario\\Downloads\\Recurso\\clientesYCentros.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(clientesYCentros))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 2) {
                    try {
                        int Cliente = Integer.parseInt(datos[0].trim());
                        grafo.AgregarVertice(Cliente);
                        Clientes.Agregar(Cliente);
                        volProd[Cliente] = Integer.parseInt(datos[1].trim());
                    } catch (NumberFormatException e) {
                        System.err.println("Error al convertir datos de cliente: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de clientes y centros: " + e.getMessage());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(clientesYCentros))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 3) {
                    try {
                        int Centro = Integer.parseInt(datos[0].trim()) + 50;
                        grafo.AgregarVertice(Centro);
                        Centros.Agregar(Centro);
                        centros.add(Centro);
                        CoPuerto.add(Integer.parseInt(datos[1].trim()));
                        CoFijo.add(Integer.parseInt(datos[2].trim()));
                    } catch (NumberFormatException e) {
                        System.err.println("Error al convertir datos de centro: " + linea);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de centros: " + e.getMessage());
        }

        String rutas = "C:\\Users\\Usuario\\Downloads\\Recurso\\rutas.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(rutas))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                try {
                    int origen = Integer.parseInt(datos[0].trim());
                    int destino = Integer.parseInt(datos[1].trim());
                    int peso = Integer.parseInt(datos[2].trim());

                    if (grafo.Vertices().Pertenece(origen) && grafo.Vertices().Pertenece(destino)) {
                        grafo.AgregarArista(origen, destino, peso);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error al convertir datos de ruta: " + linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de rutas: " + e.getMessage());
        }

        Costo(grafo, Centros, matriz, centros, CoPuerto, volProd);
        CostosXCentro(matriz,CoFijo);
        calcularTodosLosCostos(matriz,CoFijo);
        calcularTodosLosCostosConPoda(matriz,CoFijo);
    }



    private static ArrayList<ArrayList<Integer>> generarCombinaciones(int n) {
        ArrayList<ArrayList<Integer>> combinaciones = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            generarCombinacionesAux(new ArrayList<>(), combinaciones, 0, n, i);
        }
        return combinaciones;
    }



    public static void calcularTodosLosCostos(int[][] matriz, ArrayList<Integer> CoFijo) {
        // Generar combinaciones y calcular los costos
        ArrayList<ArrayList<Integer>> combinaciones = generarCombinaciones(matriz.length);
        for (ArrayList<Integer> combinacion : combinaciones) {
            int costoVariable = calcularCostoParaCombinacion(matriz, combinacion);
            int costoFijoTotal = combinacion.stream().mapToInt(CoFijo::get).sum();
            int costoAnualMinimo = costoVariable + costoFijoTotal;
            System.out.println("Costo anual minimo: " + costoAnualMinimo
                    + " con la combinacion: " + combinacion
                    + " con costo total de centros: " + costoFijoTotal);
        }
    }

    private static int calcularCostoParaCombinacion(int[][] matriz, ArrayList<Integer> combinacion) {
        int suma = 0;

        // Iterar sobre los clientes (columnas)
        for (int cliente = 0; cliente < matriz[0].length; cliente++) {
            int costoMinimo = Integer.MAX_VALUE;

            // Comparar los costos de los centros seleccionados
            for (int centro : combinacion) {
                costoMinimo = Math.min(costoMinimo, matriz[centro][cliente]);
            }

            // Sumar el costo mínimo por cliente
            if (costoMinimo != Integer.MAX_VALUE) {
                suma += costoMinimo;
            }
        }

        return suma;
    }

    public static void CostosXCentro(int[][] matriz, ArrayList<Integer> CoFijo) {
        for (int i = 0; i < matriz.length; i++) {
            int suma = 0;

            // Sumar todos los costos por cliente para este centro
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j] != Integer.MAX_VALUE) {
                    suma += matriz[i][j];
                }
            }

            // Agregar el costo fijo del centro
            int costoTotal = suma + CoFijo.get(i);
            System.out.println("Costo total para el centro " + i + ": " + costoTotal
                    + " con costo fijo de: " + CoFijo.get(i));
        }
    }



    private static void generarCombinacionesAux(
            ArrayList<Integer> actual,
            ArrayList<ArrayList<Integer>> combinaciones,
            int inicio,
            int n,
            int tamano) {
        if (actual.size() == tamano) {
            combinaciones.add(new ArrayList<>(actual));
            return;
        }

        for (int i = inicio; i < n; i++) {
            actual.add(i);
            generarCombinacionesAux(actual, combinaciones, i + 1, n, tamano);
            actual.remove(actual.size() - 1);
        }
    }



    public static void calcularTodosLosCostosConPoda(int[][] matriz, ArrayList<Integer> CoFijo) {
        // Costo mínimo conocido (inicialmente muy alto)
        int costoMinimoConocido = Integer.MAX_VALUE;
        ArrayList<Integer> mejorCombinacion = new ArrayList<>();
        // Generar combinaciones y calcular los costos
        ArrayList<ArrayList<Integer>> combinaciones = generarCombinacionesConPoda(matriz.length,costoMinimoConocido,matriz,CoFijo);
        for (ArrayList<Integer> combinacion : combinaciones) {
            // Calculamos el costo variable para la combinación
            int costoVariable = calcularCostoParaCombinacion(matriz, combinacion);

            // Calculamos el costo fijo para la combinación
            int costoFijoTotal = combinacion.stream().mapToInt(CoFijo::get).sum();

            // Calculamos el costo total
            int costoTotal = costoVariable + costoFijoTotal;

            // Si el costo total es menor que el mínimo conocido, actualizamos el mínimo y lo mostramos
            if (costoTotal < costoMinimoConocido) {
                costoMinimoConocido = costoTotal;
                mejorCombinacion = combinacion;
                System.out.println("Nuevo costo mínimo encontrado: " + costoTotal
                        + " con la combinación: " + combinacion
                        + " con costo fijo de: " + costoFijoTotal);
            }
        }
           AsignarClientesXCentro(matriz,mejorCombinacion);
    }


    public static void AsignarClientesXCentro(int[][] matriz, ArrayList<Integer> mejorCombinacion){
        for (int i = 0; i<50; i++){
            int minimo = 0;
            int indiceminimo = -1;

            for (int j=0; j<mejorCombinacion.size(); j++){
                if (matriz[j][i] < minimo || indiceminimo == -1){
                    minimo = matriz[j][i];
                    indiceminimo = j;
                }
            }
            System.out.println("El cliente " + (i+1) + " es asignado al centro: " + mejorCombinacion.get(indiceminimo));
        }
    }

    private static ArrayList<ArrayList<Integer>> generarCombinacionesConPoda(int n, int costoMinimoConocido,
                                                                             int [][] matriz, ArrayList<Integer> CoFijo) {
        ArrayList<ArrayList<Integer>> combinaciones = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            generarCombinacionesAuxConPoda(matriz, new ArrayList<>(), combinaciones, 0, n, i, costoMinimoConocido, CoFijo);
        }
        return combinaciones;
    }

    private static void generarCombinacionesAuxConPoda(int [][] matriz,
            ArrayList<Integer> actual,
            ArrayList<ArrayList<Integer>> combinaciones,
            int inicio,
            int n,
            int tamano,
            int costoMinimoConocido,
            ArrayList<Integer> CoFijo) {

        // Si hemos alcanzado el tamaño de la combinación
        if (actual.size() == tamano) {
            // Calculamos el costo total de esta combinación
            int costoVariable = calcularCostoParaCombinacion(matriz, actual);
            int costoFijoTotal = actual.stream().mapToInt(CoFijo::get).sum();
            int costoTotal = costoVariable + costoFijoTotal;

            // Si el costo total es mayor que el mínimo conocido, realizamos la poda
            if (costoTotal > costoMinimoConocido) {
                return; // Poda, no seguimos generando combinaciones
            }

            combinaciones.add(new ArrayList<>(actual));
            return;
        }

        for (int i = inicio; i < n; i++) {
            actual.add(i);
            generarCombinacionesAuxConPoda(matriz, actual, combinaciones, i + 1, n, tamano, costoMinimoConocido, CoFijo);
            actual.remove(actual.size() - 1);
        }
    }


}








