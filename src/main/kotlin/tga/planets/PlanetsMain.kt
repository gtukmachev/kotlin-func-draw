package tga.planets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import kotlin.math.max
import kotlin.math.min


val s = 3.0;
val TwoPi = Math.PI*2
val halfPI = Math.PI/2
val minusHalfPI = -halfPI
var speed = 1.0
private val bgClr = Color(0xFF443C38)



fun main() = application {
    val state = rememberWindowState(
        position = WindowPosition(Alignment.Center), size = DpSize(1280.dp, 1280.dp)
    )
    Window(
        onCloseRequest = ::exitApplication,
        state
    ) { app() }
}

@Composable fun app() = MaterialTheme {
    var x0 by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) { while(true) { delay(10L); x0 -= speed } }
//    Button(onClick = { paramsArray = generateParameters() }) { Text("Refresh") }
    Canvas(modifier = Modifier.fillMaxSize()) { paint(x0) }
}


const val zoom = 149.6e4/4.0
val zoomRadius = earth.r / 15.0

const val G = 6.67259e-11
val Mm = earth.m * sun.m
val GMm = G * Mm

fun DrawScope.paint(x0: Double) {
    drawCoordinates()
    val center = with(size / 2F) { Offset(width, height) }

    fun Vector.s(): Offset = Offset( (x/zoom).toFloat() + center.x, -(y/zoom).toFloat() + center.y)

    val Rpow2: Double  = earth.p.lenPow2
    val f: Double = GMm / Rpow2
    val a = f / earth.m
    val aVector = -earth.p.norm() * a

    earth.speed += aVector
    earth.p += earth.speed

    for (so in spaceObjects) {

        val r = max((so.r*so.rK/zoomRadius).toFloat(), 1f)
        drawCircle(so.color, r, so.p.s())
        //drawLine(so.color, screenPoint)
    }

}

fun DrawScope.drawCoordinates() {
    val c = with(size / 2F) { Offset(width, height) }

    drawRect(bgClr, Offset.Zero, size)

    drawLine(color = Color.Gray, Offset(0f, c.y), Offset(size.width, c.y))
    drawLine(color = Color.Gray, Offset(c.x, 0f), Offset(c.x, size.height))
}

