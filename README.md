# **OpenFusion View (v0.0.2)**

**OpenFusion View** is an open-source medical imaging visualization workstation designed for nuclear medicine and advanced radiological analysis. It features real-time Multiplanar Reconstruction (MPR) and image fusion of Computed Tomography (CT) and SPECT (NM).

This project emerges as a lightweight, powerful, and free alternative for healthcare professionals who require clinical analysis capabilities without relying on expensive proprietary workstations.

## **Main Features**

* **Multiplanar Reconstruction (MPR):** Synchronized visualization of Axial, Coronal, and Sagittal planes.  
* **Dynamic CT/NM Fusion:** Alpha Blending overlay algorithm with high-precision **Spectrum (Jet)** colormap.  
* **DICOM Browser (Mini-PACS):** Automatic indexer that scans directories, extracts metadata, and organizes studies by Patient, Modality, and Date.  
* **3D Spatial Triangulation:** Synchronized telescopic crosshair with 5 adjustable visual styles.  
* **Clinical Quantification:** Readout of **Hounsfield Units (HU)** and **Counts** via 3D spatial voxel probing.  
* **Modern Interface:** "Material Dark" aesthetic based on FlatLaf to minimize visual fatigue.  
* **Internationalization:** Native support for English, Spanish, Portuguese, French, Chinese, and Japanese.

## **Keyboard Shortcuts (Clinical Workflow)**

| Key | Action |
| :---- | :---- |
| **Z** | Activate **Crosshair** mode (Triangulation) |
| **W** | Activate **Window / Level** mode (Brightness and Contrast) |
| **C** | Cycle between the **5 crosshair line styles** |
| **1** | Hounsfield Preset: **Bone** (W:2000, L:500) |
| **2** | Hounsfield Preset: **Soft Tissue** (W:400, L:50) |
| **3** | Hounsfield Preset: **Lung** (W:1500, L:-600) |
| **4** | Hounsfield Preset: **Brain** (W:80, L:40) |

## **Technology Stack**

* **Language:** Java 17 or higher.  
* **Processing Engine:** [ImageJ](https://imagej.net/) (ij.jar).  
* **UI Framework:** Swing \+ [FlatLaf](https://www.formdev.com/flatlaf/).  
* **Project Management:** Maven.  
* **Packaging:** Launch4j for generating native .exe executables.

## **Installation and Usage**

**Option 1: Windows (Portable, no installation required \- Recommended)** No Java installation is required on your computer to use this version.

1. Go to the [Releases](https://github.com/GamezConZ/OpenFusionView/releases) tab.  
2. Download and open the `OpenFusionView-XXX.exe` file.

**Option 2: Universal / Linux / Mac (Requires Java 21+)**

1. Ensure you have Java 21 or higher installed (we recommend [Eclipse Adoptium](https://adoptium.net/)).  
2. Download the `OpenFusionView-XXX.jar` file from the [Releases](https://github.com/GamezConZ/OpenFusionView/releases) tab.  
3. Double-click the `.jar` file or run it from the terminal:  
4. Bash

java \-jar OpenFusionView-XXX.jar

5. 

## **License**

This project is under the **MIT License**. You are free to use, modify, and distribute it, including for commercial purposes in clinical environments.

## **Author**

Developed by **Carlos Gamez**.

GitHub: [@GamezConZ](https://github.com/GamezConZ)

*Note: This software is provided "as is" for educational and research support purposes. Always validate critical findings on a workstation certified by your country's health authorities.*

---

# **OpenFusion View (v0.0.2)**

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

**Opción 1: Windows (Portable, no requiere instalación \- Recomendado)** No necesitas tener Java instalado en tu computadora para usar esta versión.

1. Ve a la pestaña de [Releases](https://github.com/GamezConZ/OpenFusionView/releases).  
2. Descarga y abre el archivo `OpenFusionView-XXX.exe`.

**Opción 2: Universal / Linux / Mac (Requiere Java 21+)**

1. Asegúrate de tener instalado Java 21 o superior (recomendamos [Eclipse Adoptium](https://adoptium.net/)).  
2. Descarga el archivo `OpenFusionView-XXX.jar` desde la pestaña de [Releases](https://github.com/GamezConZ/OpenFusionView/releases).  
3. Haz doble clic en el archivo `.jar` o ejecútalo desde la terminal:  
4. Bash

java \-jar OpenFusionView-XXX.jar

5. 

## **Licencia**

Este proyecto está bajo la **Licencia MIT**. Eres libre de usarlo, modificarlo y distribuirlo, incluso para fines comerciales en entornos clínicos.

## **Autor**

Desarrollado por **Carlos Gamez**.

GitHub: [@GamezConZ](https://github.com/GamezConZ)

*Nota: Este software se proporciona "tal cual", con fines educativos y de soporte a la investigación. Siempre valide los hallazgos críticos en una estación de trabajo certificada por las autoridades sanitarias de su país.*

