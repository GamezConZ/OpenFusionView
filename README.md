[English Version](#english-version) | [Versión en Español](#versión-en-español)

<a id="english-version"\></a\>

# **OpenFusion View (v0.0.3)**

In many nuclear medicine and radiology departments, clinical workflows are bottlenecked by a limited number of expensive, proprietary workstations. **OpenFusion View** is built to break that bottleneck.  
It is a lightweight, open-source SPECT/CT fusion viewer that transforms any standard hospital or research computer into a capable analysis terminal—without the need for additional costly licenses. Designed to complement existing infrastructure, it empowers physicians, physicists, and researchers to review, triangulate, and quantify studies seamlessly.  
Driven by a commitment to democratize access to medical technology globally, OpenFusion View features native support for 10 languages, bridging the gap between high-end clinical tools and everyday accessibility.

## **Main Features**

* **Multiplanar Reconstruction (MPR):** Synchronized real-time visualization of Axial, Coronal, and Sagittal planes.  
* **Dynamic CT/NM Fusion:** Alpha Blending overlay algorithm with high-precision **Spectrum (Jet)** colormap.  
* **DICOM Browser (Mini-PACS):** Automatic indexer that scans directories, extracts metadata, and organizes studies by Patient, Modality, and Date.  
* **3D Spatial Triangulation:** Synchronized telescopic crosshair with 5 adjustable visual styles.  
* **Clinical Quantification:** Readout of **Hounsfield Units (HU)** and **Counts** via 3D spatial voxel probing.  
* **Universal Spatial Correction:** Automatic default alignment plus manual tools (XY rotation, Z-axis reslice, and X-axis mirroring) to correct orientation mismatches from any camera vendor.  
* **Internationalization:** Native support for English, Spanish, Portuguese, French, Italian, German, Arabic, Hindi, Chinese, and Japanese.

## **Keyboard Shortcuts (Clinical Workflow)**

| Key | Action |
| :---- | :---- |
| **Z** | Activate **Crosshair** mode (3D Triangulation) |
| **W** | Activate **Window / Level** mode (Brightness and Contrast) |
| **C** | Cycle between the **5 crosshair line styles** |
| **R** | **Rotate NM (XY):** Rotate SPECT matrix 90° to correct alignment |
| **T** | **Tilt NM (Z):** 3D Reslice (Y-Z swap) for orientation mismatch |
| **H** | **Flip NM (X):** Horizontal mirror to correct patient positioning |
| **1** | Hounsfield Preset: **Bone** (W:2000, L:500) |
| **2** | Hounsfield Preset: **Soft Tissue** (W:400, L:50) |
| **3** | Hounsfield Preset: **Lung** (W:1500, L:-600) |
| **4** | Hounsfield Preset: **Brain** (W:80, L:40) |

## **🤝 Contributing to OpenFusion View**

This project is born from real clinical needs, and it grows with community support. Whether you are a Java developer, a medical physicist, or a physician with ideas for new clinical tools, your contributions are highly welcome\!  
Feel free to open an **Issue** to report bugs or suggest features, or submit a **Pull Request** to improve the code. We are actively looking for collaborators to expand radiomic features and improve DICOM compatibility.

## **Technology Stack**

* **Language:** Java 17+  
* **Processing Engine:** [ImageJ](https://imagej.net/) (ij.jar)  
* **UI Framework:** Swing \+ [FlatLaf](https://www.formdev.com/flatlaf/)  
* **Project Management:** Maven

## **Installation and Usage**

**Option 1: Windows (Portable & Autonomous - Recommended)**
No Java installation or Administrator privileges are required on your hospital computer.

1. Go to the [Releases](https://github.com/GamezConZ/OpenFusionView/releases) tab.
2. Download the `OpenFusionView-v0.0.3-Windows-Portable.zip` file.
3. Extract the folder and double-click `OpenFusionView.exe`.

**Option 2: Linux (Debian/Ubuntu) & Mac (Requires Java 17+)**
If you don't have Java installed, you can easily install it via terminal (Debian/Ubuntu-based distributions):
```bash
sudo apt update && sudo apt install openjdk-17-jre
```

1. Download the `OpenFusionView.jar` file from the [Releases](https://github.com/GamezConZ/OpenFusionView/releases) tab.
2. Run it from the terminal:
```bash
java -jar OpenFusionView.jar
```

## **License**

This project is under the **MIT License**. You are free to use, modify, and distribute it, including for commercial and institutional purposes.

## **Author**

Developed by **Carlos Gamez**.  
GitHub: [@GamezConZ](https://github.com/GamezConZ)  

*Disclaimer: This software is provided "as is" for educational, research, and workflow support purposes. Always validate critical diagnostic findings on a workstation certified by your country's health authorities.*  

----------------------------------

<a id="versión-en-español"\></a\>

# **OpenFusion View (v0.0.3)**

En muchos departamentos de medicina nuclear y radiología, el flujo de trabajo clínico se ve limitado por un número reducido de costosas estaciones de trabajo propietarias. **OpenFusion View** nace para eliminar ese cuello de botella.  
Es un visor de fusión SPECT/CT de código abierto y ligero que transforma cualquier computadora estándar del hospital o centro de investigación en un terminal de análisis capaz, sin necesidad de licencias adicionales costosas. Diseñado para complementar la infraestructura existente, permite a médicos, físicos e investigadores revisar, triangular y cuantificar estudios de manera fluida.  
Impulsado por el compromiso de democratizar el acceso a la tecnología médica a nivel global, OpenFusion View cuenta con soporte nativo para 10 idiomas, acortando la brecha entre las herramientas clínicas de alta gama y la accesibilidad diaria.

## **Características Principales**

* **Reconstrucción Multiplanar (MPR):** Visualización sincronizada en tiempo real de los planos Axial, Coronal y Sagital.  
* **Fusión CT/NM Dinámica:** Algoritmo de superposición (Alpha Blending) con mapa de color **Spectrum (Jet)** de alta precisión.  
* **Navegador DICOM (Mini-PACS):** Indexador automático que escanea directorios, extrae metadatos y organiza los estudios.  
* **Triangulación Espacial 3D:** Mira telescópica (Crosshair) sincronizada con 5 estilos visuales ajustables.  
* **Cuantificación Clínica:** Lectura de **Unidades Hounsfield (HU)** y **Cuentas (Counts)** mediante sondeo de vóxeles en el espacio 3D.  
* **Corrección Espacial Universal:** Alineación automática por defecto, más herramientas manuales (Rotación XY, Reslice Z y Espejo X) para corregir discordancias de orientación de cámaras de cualquier fabricante.  
* **Internacionalización:** Soporte nativo para Inglés, Español, Portugués, Francés, Italiano, Alemán, Árabe, Hindi, Chino y Japonés.

## **Atajos de Teclado (Workflow Clínico)**

| Tecla | Acción |
| :---- | :---- |
| **Z** | Activar modo **Crosshair** (Triangulación 3D) |
| **W** | Activar modo **Window / Level** (Brillo y Contraste) |
| **C** | Ciclar entre los **5 estilos de líneas** de mira |
| **R** | **Rotar NM (XY):** Rotar matriz SPECT 90° para corregir alineación |
| **T** | **Inclinar NM (Z):** Reslice 3D (Intercambio Y-Z) para discordancia de plano |
| **H** | **Espejo NM (X):** Reflejo horizontal para corregir posicionamiento del paciente |
| **1** | Preset Hounsfield: **Óseo** (W:2000, L:500) |
| **2** | Preset Hounsfield: **Partes Blandas** (W:400, L:50) |
| **3** | Preset Hounsfield: **Pulmón** (W:1500, L:-600) |
| **4** | Preset Hounsfield: **Cerebro** (W:80, L:40) |

## **🤝 Contribuye a OpenFusion View**

Este proyecto nace de necesidades clínicas reales y crece con el apoyo de la comunidad. Ya seas un desarrollador Java, un físico médico o un médico con ideas para nuevas herramientas clínicas, ¡tus contribuciones son muy bienvenidas\!  
Siéntete libre de abrir un **Issue** para reportar errores o sugerir funciones, o envía un **Pull Request** para mejorar el código. Buscamos activamente colaboradores para expandir el soporte radiómico y mejorar la compatibilidad DICOM.

## **Stack Tecnológico**

* **Lenguaje:** Java 17+  
* **Motor de Procesamiento:** [ImageJ](https://imagej.net/) (ij.jar)  
* **UI Framework:** Swing \+ [FlatLaf](https://www.formdev.com/flatlaf/)  
* **Gestión de Proyecto:** Maven

## **Instalación y Uso**

**Opción 1: Windows (Portable y Autónomo - Recomendado)**
No requiere instalación de Java ni permisos de Administrador en la computadora del hospital.

1. Ve a la pestaña de [Releases](https://github.com/GamezConZ/OpenFusionView/releases).
2. Descarga el archivo `OpenFusionView-v0.0.3-Windows-Portable.zip`.
3. Descomprime la carpeta y haz doble clic en `OpenFusionView.exe`.

**Opción 2: Linux (Debian/Ubuntu) y Mac (Requiere Java 17+)**
Si no tienes Java instalado, puedes instalarlo fácilmente desde la terminal (distribuciones basadas en Debian/Ubuntu):
```bash
sudo apt update && sudo apt install openjdk-17-jre
```

1. Descarga el archivo `OpenFusionView.jar` desde la pestaña de [Releases](https://github.com/GamezConZ/OpenFusionView/releases).
2. Ejecútalo desde la terminal:
```bash
java -jar OpenFusionView.jar
```

## **Licencia**

Este proyecto está bajo la **Licencia MIT**. Eres libre de usarlo, modificarlo y distribuirlo, incluso para fines comerciales e institucionales.

## **Autor**

Desarrollado por **Carlos Gamez**.  
GitHub: [@GamezConZ](https://github.com/GamezConZ)  

*Aviso legal: Este software se proporciona "tal cual", con fines educativos, de investigación y soporte al flujo de trabajo. Siempre valide los hallazgos diagnósticos críticos en una estación de trabajo certificada por las autoridades sanitarias de su país.*
