# **OpenFusion View (v0.0.1)**

**OpenFusion View** es una estación de trabajo de visualización médica de código abierto diseñada para la medicina nuclear y el análisis radiológico avanzado. Permite la reconstrucción multiplanar (MPR) en tiempo real y la fusión de imágenes de Tomografía Computada (CT) y SPECT (NM).  
Este proyecto surge como una alternativa ligera, potente y gratuita para profesionales de la salud que requieren capacidades de análisis clínico sin depender de estaciones de trabajo propietarias costosas.

## **Características Principales**

* **Reconstrucción Multiplanar (MPR):** Visualización sincronizada de los planos Axial, Coronal y Sagital.  
* **Fusión CT/NM Dinámica:** Algoritmo de superposición (Alpha Blending) con mapa de color **Spectrum (Jet)** de alta precisión.  
* **Navegador DICOM (Mini-PACS):** Indexador automático que escanea directorios, extrae metadatos y organiza los estudios por Paciente, Modalidad y Fecha.  
* **Triangulación Espacial 3D:** Mira telescópica (Crosshair) sincronizada con 5 estilos visuales ajustables.  
* **Cuantificación Clínica:** Lectura de **Unidades Hounsfield (HU)** y **Cuentas (Counts)** mediante sondeo de vóxeles en el espacio 3D.  
* **Interfaz Moderna:** Estética "Material Dark" basada en FlatLaf para minimizar la fatiga visual.  
* **Internacionalización:** Soporte nativo para Inglés, Español, Portugués, Francés, Chino y Japonés.

## **Atajos de Teclado (Workflow Clínico)**

| Tecla | Acción |
| :---- | :---- |
| **Z** | Activar modo **Crosshair** (Triangulación) |
| **W** | Activar modo **Window / Level** (Brillo y Contraste) |
| **C** | Ciclar entre los **5 estilos de líneas** de mira |
| **1** | Preset Hounsfield: **Óseo** (W:2000, L:500) |
| **2** | Preset Hounsfield: **Partes Blandas** (W:400, L:50) |
| **3** | Preset Hounsfield: **Pulmón** (W:1500, L:-600) |
| **4** | Preset Hounsfield: **Cerebro** (W:80, L:40) |

## **Stack Tecnológico**

* **Lenguaje:** Java 17 o superior.  
* **Motor de Procesamiento:** [ImageJ](https://imagej.net/) (ij.jar).  
* **UI Framework:** Swing \+ [FlatLaf](https://www.formdev.com/flatlaf/).  
* **Gestión de Proyecto:** Maven.  
* **Empaquetado:** Launch4j para la generación de ejecutables .exe nativos.

## **Instalación y Uso**

1. **Requisitos:** Tener instalado Java JRE 17 o superior.  
2. **Descarga:** Ve a la sección de [Releases](https://www.google.com/search?q=https://github.com/GamezConZ/OpenFusionView/releases) y descarga el archivo OpenFusionView.exe.  
3. **Configuración:** Al abrir el programa, ve a **Settings**, selecciona tu carpeta de exportación DICOM y elige tu idioma preferido.

## **Licencia**

Este proyecto está bajo la **Licencia MIT**. Eres libre de usarlo, modificarlo y distribuirlo, incluso para fines comerciales en entornos clínicos.

## **Autor**

Desarrollado por **Carlos Gamez**.  
GitHub: [@GamezConZ](https://www.google.com/search?q=https://github.com/GamezConZ)

*Nota: Este software se proporciona "tal cual", con fines educativos y de soporte a la investigación. Siempre valide los hallazgos críticos en una estación de trabajo certificada por las autoridades sanitarias de su país.*