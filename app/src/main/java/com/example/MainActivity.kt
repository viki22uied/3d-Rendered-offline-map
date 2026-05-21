package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.focusable
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.SolidColor
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          CliMapVisualizer(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }
}

data class Point3D(val x: Float, val y: Float, val z: Float)
data class Quad(val p1: Point3D, val p2: Point3D, val p3: Point3D, val p4: Point3D, val centerZ: Float)

@Composable
fun CliMapVisualizer(modifier: Modifier = Modifier) {
    val mapSize = 40
    val surfaceColor = Color(0xFF1A1C1E)
    val canvasBg = Color(0xFF000000)
    val textColor = Color(0xFFE2E2E6)
    val accentColor = Color(0xFFD1E4FF)
    val accentSecondary = Color(0xFFA8C7FF)
    val borderColor = Color(0xFF43474E)
    val secondaryText = Color(0xFFC4C6D0)
    
    var rx by remember { mutableFloatStateOf(1.2f) }
    var rz by remember { mutableFloatStateOf(0.7f) }
    var ry by remember { mutableFloatStateOf(0.0f) }
    var scale by remember { mutableFloatStateOf(10f) }
    var panX by remember { mutableFloatStateOf(0f) }
    var panY by remember { mutableFloatStateOf(0f) }
    var renderMode by remember { mutableStateOf("both") }
    var commandInput by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }
    var pointCloud by remember { mutableStateOf<Array<Array<Point3D>>?>(null) }
    
    var terminallogs by remember { mutableStateOf(listOf("INITIALIZING SCAN...", "CALCULATING TERRAIN MATRIX...")) }
    
    LaunchedEffect(Unit) {
        delay(1000)
        terminallogs = terminallogs + "APPLYING PERLIN NOISE..."
        delay(800)
        terminallogs = terminallogs + "RENDERING VIEWPORT..."
    }

    val baseMap = remember {
        val arr = Array(mapSize) { Array(mapSize) { Point3D(0f, 0f, 0f) } }
        for (i in 0 until mapSize) {
            for (j in 0 until mapSize) {
                val fx = (i - mapSize / 2f)
                val fy = (j - mapSize / 2f)
                val dist = sqrt((fx*fx + fy*fy).toDouble()).toFloat()
                val fz = (sin(fx * 0.3f) + cos(fy * 0.3f)) * 2f + 
                         (sin(dist * 0.5f)) * 3f +
                         (sin(fx * 0.1f) * cos(fy * 0.1f)) * 4f
                arr[i][j] = Point3D(fx, fy, fz)
            }
        }
        arr
    }

    LaunchedEffect(Unit) {
        pointCloud = baseMap
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .padding(16.dp)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.DirectionUp -> rx -= 0.1f
                        Key.DirectionDown -> rx += 0.1f
                        Key.DirectionLeft -> rz -= 0.1f
                        Key.DirectionRight -> rz += 0.1f
                        Key.W -> panY += 10f
                        Key.S -> panY -= 10f
                        Key.A -> panX += 10f
                        Key.D -> panX -= 10f
                        Key.Equals, Key.NumPadAdd -> scale *= 1.1f
                        Key.Minus, Key.NumPadSubtract -> scale /= 1.1f
                        else -> return@onKeyEvent false
                    }
                    true
                } else {
                    false
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "STATUS: OFFLINE",
                color = secondaryText,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = "GPS: 45.123 N, 12.456 E",
                color = accentSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(canvasBg)
                .border(1.dp, borderColor, RoundedCornerShape(24.dp))
                .padding(4.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        rz -= pan.x * 0.005f
                        rx -= pan.y * 0.005f
                        scale *= zoom
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val viewerDistance = 50f
                val fov = 400f
                val cx = size.width / 2f
                val cy = size.height / 2f

                fun rotate(p: Point3D): Point3D {
                    val y1 = p.y * cos(rx) - p.z * sin(rx)
                    val z1 = p.y * sin(rx) + p.z * cos(rx)
                    val x1 = p.x
                    
                    val x2 = x1 * cos(ry) + z1 * sin(ry)
                    val z2 = -x1 * sin(ry) + z1 * cos(ry)
                    val y2 = y1
                    
                    val x3 = x2 * cos(rz) - y2 * sin(rz)
                    val y3 = x2 * sin(rz) + y2 * cos(rz)
                    val z3 = z2
                    
                    return Point3D(x3, y3, z3)
                }

                fun project(p: Point3D): Offset {
                    val factor = fov / (viewerDistance + p.z)
                    val xProj = cx + panX + p.x * factor * scale
                    val yProj = cy + panY + p.y * factor * scale
                    return Offset(xProj, yProj)
                }

                val currentMap = pointCloud ?: baseMap
                val transformedMap = Array(mapSize) { i ->
                    Array(mapSize) { j ->
                        rotate(currentMap[i][j])
                    }
                }

                val quads = mutableListOf<Quad>()
                for (i in 0 until mapSize - 1) {
                    for (j in 0 until mapSize - 1) {
                        val p1 = transformedMap[i][j]
                        val p2 = transformedMap[i + 1][j]
                        val p3 = transformedMap[i + 1][j + 1]
                        val p4 = transformedMap[i][j + 1]
                        val cz = (p1.z + p2.z + p3.z + p4.z) / 4f
                        quads.add(Quad(p1, p2, p3, p4, cz))
                    }
                }

                quads.sortByDescending { it.centerZ }

                for (quad in quads) {
                    if (quad.p1.z > -viewerDistance + 1f && quad.p2.z > -viewerDistance + 1f) {
                        val proj1 = project(quad.p1)
                        val proj2 = project(quad.p2)
                        val proj3 = project(quad.p3)
                        val proj4 = project(quad.p4)

                        val path = Path().apply {
                            moveTo(proj1.x, proj1.y)
                            lineTo(proj2.x, proj2.y)
                            lineTo(proj3.x, proj3.y)
                            lineTo(proj4.x, proj4.y)
                            close()
                        }
                        
                        val depthAlpha = (1f - (quad.centerZ + 20f) / 40f).coerceIn(0.1f, 1f)
                        val strokeColor = accentColor.copy(alpha = depthAlpha)

                        if (renderMode == "solid" || renderMode == "both") {
                            drawPath(path, color = canvasBg, style = Fill)
                        }
                        if (renderMode == "wireframe" || renderMode == "both") {
                            drawPath(path, color = strokeColor, style = Stroke(width = 1.5f))
                        }
                    }
                }
                
                drawLine(
                    color = accentSecondary.copy(alpha = 0.5f),
                    start = Offset(cx - 20f, cy),
                    end = Offset(cx + 20f, cy),
                    strokeWidth = 1f
                )
                drawLine(
                    color = accentSecondary.copy(alpha = 0.5f),
                    start = Offset(cx, cy - 20f),
                    end = Offset(cx, cy + 20f),
                    strokeWidth = 1f
                )
            }
            
            Text(
                text = String.format(Locale.US, "MODE: %s\nROT X: %.2f\nROT Z: %.2f\nZOOM: %.1fx", renderMode.uppercase(), rx, rz, scale),
                color = accentSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceColor)
        ) {
            Text(
                text = "--------------------------------------",
                color = borderColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
            for (log in terminallogs.takeLast(4)) {
                Text(
                    text = "> \$log",
                    color = textColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
            }
            var cursorBlink by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                while(true) {
                    delay(500)
                    cursorBlink = !cursorBlink
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "> ",
                    color = accentColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                BasicTextField(
                    value = commandInput,
                    onValueChange = { commandInput = it },
                    textStyle = TextStyle(color = accentColor, fontFamily = FontFamily.Monospace, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        val cmdText = commandInput.text.trim()
                        if (cmdText.isNotEmpty()) {
                            terminallogs = terminallogs.takeLast(4) + "> $cmdText"
                            val parts = cmdText.split("\\s+".toRegex())
                            when(parts[0].lowercase()) {
                                "load" -> {
                                    val file = parts.getOrNull(1)
                                    if (file != null) {
                                        var format = file.substringAfterLast('.', "")
                                        val formatIdx = parts.indexOf("--format")
                                        if (formatIdx != -1 && formatIdx + 1 < parts.size) {
                                            format = parts[formatIdx + 1]
                                        }
                                        if (format.lowercase() in listOf("obj", "stl", "fbx")) {
                                            terminallogs = terminallogs.takeLast(4) + "Parse eng: Offline Mode"
                                            terminallogs = terminallogs.takeLast(4) + "Loaded $format file: $file"
                                        } else {
                                            terminallogs = terminallogs.takeLast(4) + "Error: Unknown format '$format'. Use --format [obj|stl|fbx]"
                                        }
                                    } else {
                                        terminallogs = terminallogs.takeLast(4) + "Usage: load <file> [--format ext]"
                                    }
                                }
                                "mode" -> {
                                    val mode = parts.getOrNull(1)?.lowercase()
                                    if (mode in listOf("solid", "wireframe", "both")) {
                                        renderMode = mode!!
                                        terminallogs = terminallogs.takeLast(4) + "Mode set to: $mode"
                                    } else {
                                        terminallogs = terminallogs.takeLast(4) + "Usage: mode [solid|wireframe|both]"
                                    }
                                }
                                "help" -> {
                                    terminallogs = terminallogs.takeLast(4) + "Cmds: load, mode [solid|wireframe], help"
                                }
                                else -> {
                                    terminallogs = terminallogs.takeLast(4) + "Unknown command. Type 'help'."
                                }
                            }
                            commandInput = TextFieldValue("")
                            focusRequester.requestFocus()
                        }
                    }),
                    cursorBrush = SolidColor(if (cursorBlink) accentColor else Color.Transparent),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
