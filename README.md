# 3D CLI Map Visualizer (GEO_TERRA v4.0)

A fully offline 3D Map Visualizer featuring a terminal/Command Line Interface (CLI) themed interface, styled in a sleek, highly aesthetic **Sophisticated Dark** visual scheme. Designed entirely in Kotlin and Jetpack Compose for Android.

---

## 🎨 Visual Design Concept: "Sophisticated Dark"
GEO_TERRA v4.0 leverages an eye-safe, immersive dark palette to deliver a high-tech console feeling:
* **Deep Canvas (#000000)**: Completely pitch-black background for maximum contrast on the vector-style wireframes.
* **Warm Slate Surface (#1A1C1E)**: Framing elements using consistent, dark Material 3 container colors.
* **Ice Blue & Soft Slate Accents (#D1E4FF, #A8C7FF)**: Clean illumination accents for active elements, lines, and console cursor states without glaring neon distractions.
* **Generous Negative Space & Borders**: High-contrast, thin boundaries (`#43474E`) separating interactive fields elegantly.

---

## 🚀 Key Features & Capabilities

### 1. Advanced 3D Projection Engine (100% Offline)
* **Rotational Algebra**: Live matrices rotating 3-dimensional map points natively in real-time.
* **Depth Dimming**: Distant map grids naturally fade out on the Z-axis by mapping their rotation depths to coordinate-based transparency levels.
* **Touch & Key-Based Interaction**: Fully integrated to accept both gesture inputs and hardware/emulator physical key vectors.

### 2. Multi-Format Loading Engine (Offline Simulation)
The visualizer includes real offline command parsing supporting major 3D mesh files:
* **Supported Formats**: `.obj`, `.stl`, `.fbx`
* **Auto-Detection Logic**: Auto-detects extensions or uses specified argument overrides (e.g., `--format fbx`).
* **Format Forcing Syntax**: Specify custom extension maps on dynamic files seamlessly within the interactive console input.

### 3. Dual-Rendering Modes
Enables toggling between distinct styles directly via terminal command input or the integrated modes panel:
* **Solid Shaded**: Smooth overlay with occluded background grids for absolute spatial geometry visibility.
* **Wireframe**: Traditional matrix vector visualization showcasing nested mesh structures cleanly.
* **Integrated (Both)**: Combined view showing translucent wire mesh wrapped over absolute black geometry.

---

## ⌨️ Command Console Reference

The interactive entry terminal at the bottom supports responsive execution commands. Type any of the following parameters, then hit **Send/Enter** on your soft keyboard:

| Command Syntax | Example Command | Description |
| :--- | :--- | :--- |
| `help` | `help` | Prints all available commands to the console logs. |
| `load <filename> [--format <ext>]` | `load mount_hood.fbx --format fbx` | Simulates parsing and loading of custom 3D files. |
| `mode [solid\|wireframe\|both]` | `mode wireframe` | Toggles rendering geometry live. |

---

## 🎮 Navigation Controls

You can control the viewpoint seamlessly through both touch gestures and keyboard/keypad maps entirely offline.

### Keyboard & Keypad Navigation
Ensure the application screen is focused (simply tap anywhere in the workspace) to use the following hardware keys:
* **Rotate Plot**: Arrow keys (`↑`, `↓`, `←`, `→`) to spin on the Pitch and Yaw axis.
* **Pan Map**: `W` (Up), `S` (Down), `A` (Left), `D` (Right) keys.
* **Zoom Focus**: Plus/Equals (`+`/`=`) keys to scale depth; Minus (`-`) key to zoom out.

### Gesture Navigation
* **Rotate Plot**: Drag anywhere inside the perspective drawing canvas.
* **Zoom Vector**: Multi-touch pinch layout to scale focus dynamically.
