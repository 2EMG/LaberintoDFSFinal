# LABERINTO DFS
Problema:
El algoritmo Depth-First Search (DFS) es una herramienta poderosa para resolver laberintos, 
pero tiene un talón de Aquiles: en laberintos con ciclos o múltiples bifurcaciones, 
puede caer en bucles infinitos o recorrer caminos redundantes. Esto no solo lo hace ineficiente, 
sino que también pone en riesgo su funcionalidad básica, especialmente si no se maneja correctamente la recursión.

Solución:
La clave está en la memoria. Implementamos un sistema de marcado de nodos visitados que actúa como un "rastro de migajas": 
al registrar cada celda explorada, el algoritmo evita repeticiones, optimiza su recorrido y garantiza que encuentre una salida 
(si existe) sin perderse en su propio camino.
