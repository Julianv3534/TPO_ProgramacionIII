package imp;

import api.ConjuntoTDA;
import api.GrafoTDA;
import imp.ConjuntoTA;
import imp.GrafoLA;
import api.ConjuntoTDA;
import api.GrafoTDA;



import api.ConjuntoTDA;
import api.GrafoTDA;
import java.util.ArrayList;

import imp.GrafoLA;
import api.ConjuntoTDA;
import api.GrafoTDA;
import imp.ConjuntoTA;
import imp.GrafoLA;


public class Dijkstra {
    public static GrafoTDA dijkstra(GrafoTDA grafo, int origen) {
        GrafoTDA distanciasMinimas = new GrafoLA();
        ConjuntoTDA vertices = grafo.Vertices();

        // Inicializamos las distancias a infinito (un valor grande) excepto el origen
        int[] distancias = new int[100]; // Suponiendo un máximo de 100 vértices
        boolean[] visitados = new boolean[100];
        for (int i = 0; i < 100; i++) {
            distancias[i] = Integer.MAX_VALUE;
        }
        distancias[origen] = 0;

        // Inicializamos el grafo de distancias mínimas con los vértices del grafo original
        while (!vertices.ConjuntoVacio()) {
            int vertice = vertices.Elegir();
            vertices.Sacar(vertice);
            distanciasMinimas.AgregarVertice(vertice);
        }

        ConjuntoTDA pendientes = grafo.Vertices();
        pendientes.Sacar(origen); // Quitamos el origen de los pendientes

        // Comenzamos a procesar los vértices
        while (!pendientes.ConjuntoVacio()) {
            // Encontramos el vértice no visitado con la menor distancia
            int mejor_vertice = -1;
            int mejor_distancia = Integer.MAX_VALUE;
            for (int i = 0; i < 100; i++) {
                if (!visitados[i] && distancias[i] < mejor_distancia) {
                    mejor_distancia = distancias[i];
                    mejor_vertice = i;
                }
            }

            if (mejor_vertice == -1) break; // No hay más vértices alcanzables

            // Marcamos el vértice como visitado
            visitados[mejor_vertice] = true;
            pendientes.Sacar(mejor_vertice);

            // Relajamos las aristas desde el vértice seleccionado
            ConjuntoTDA adyacentes = grafo.Vertices();
            while (!adyacentes.ConjuntoVacio()) {
                int adyacente = adyacentes.Elegir();
                adyacentes.Sacar(adyacente);

                if (grafo.ExisteArista(mejor_vertice, adyacente)) {
                    int nueva_distancia = distancias[mejor_vertice] + grafo.PesoArista(mejor_vertice, adyacente);
                    if (nueva_distancia < distancias[adyacente]) {
                        distancias[adyacente] = nueva_distancia;
                        distanciasMinimas.AgregarArista(origen, adyacente, nueva_distancia);
                    }
                }
            }
        }

        return distanciasMinimas;
    }

}