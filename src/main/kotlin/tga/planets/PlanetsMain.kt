package tga.planets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import kotlinx.coroutines.*
import kotlin.math.max

private val bgClr = Color(0xFF443C38)

var dt: Long = 1L// hours
var simulationStepsPerSecond = 100
val simulationDelay = (1000 / simulationStepsPerSecond).toLong()

fun main() = application {
    val state = rememberWindowState(
        position = WindowPosition(Alignment.Center), size = DpSize((2*1280).dp, 1280.dp)
    )

    Window(
        onCloseRequest = ::exitApplication,
//        onKeyEvent = ::hotKeysHandler,
        state = state
    ) { app() }
}

//@OptIn(ExperimentalComposeUiApi::class)
//private fun hotKeysHandler(keyEvent: KeyEvent): Boolean {
//    return when (keyEvent.key) {
//        Key.Spacebar -> { isSimulationActive = !isSimulationActive; false }
//        else -> true
//    }
//}

var isSimulationActive: Boolean = false

@Composable fun app() = MaterialTheme {
    var t by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            if (isSimulationActive) simulationStep(tHoursAbsolute = t, dtHours = dt)
            t += dt
            delay(simulationDelay)
        }
    }

    Column {
        Button(onClick = { isSimulationActive = !isSimulationActive }) { Text( "run/pause" ) }
        Canvas(modifier = Modifier.fillMaxSize()) { paint(t) }
    }

}

const val zoom = 149.6e4/8
val zoomRadius = earth.r / 15.0


fun DrawScope.paint(t: Long) {
    drawCoordinates()
    val center = with(size / 2F) { Offset(width, height) }
    fun Vector.s(): Offset = Offset( (x/zoom).toFloat() + center.x, -(y/zoom).toFloat() + center.y)

    for (so in spaceObjects) {
        val r = max((so.r*so.rK/zoomRadius).toFloat(), 1f)
        drawCircle(so.color, r, so.p.s())
    }

}

fun DrawScope.drawCoordinates() {
    val c = with(size / 2F) { Offset(width, height) }

    drawRect(bgClr, Offset.Zero, size)

    drawLine(color = Color.Gray, Offset(0f, c.y), Offset(size.width, c.y))
    drawLine(color = Color.Gray, Offset(c.x, 0f), Offset(c.x, size.height))
}

