package tga.planets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
//import androidx.compose.runtime.MutableState
import kotlinx.coroutines.*
import tga.functions.tga.planets.phisic_state.earth
import tga.functions.tga.planets.phisic_state.simulationStep
import tga.functions.tga.planets.phisic_state.spaceObjects
import tga.functions.tga.planets.visual_state.VisualBody
import tga.functions.tga.planets.visual_state.VisualState
import tga.functions.tga.planets.visual_state.asVisualState
import tga.functions.tga.planets.visual_state.toOffset


private val bgClr = Color(0xFF443C38)

var dt: Long = 60*60*10L// hours
var simulationStepsPerSecond = 1000
val simulationDelay = (1000 / simulationStepsPerSecond).toLong()

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val state = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize((2*1280).dp, 1400.dp),
    )

    fun hotKeysHandler(keyEvent: KeyEvent): Boolean {
        return when (keyEvent.key) {
            Key.Escape -> { exitApplication(); false}
            Key.Spacebar -> { isSimulationActive = !isSimulationActive; false }
            else -> true
        }
    }

    Window(
        onCloseRequest = ::exitApplication,
        onKeyEvent = ::hotKeysHandler,
        state = state
    ) { app() }


}

var isSimulationActive: Boolean = false

@Composable fun app() = MaterialTheme {
    //var t by remember { mutableStateOf(0L) }

    var planetsVisualState: VisualState by remember {
        mutableStateOf( spaceObjects.asVisualState(zoom = zoom, zoomRadius = zoomRadius) )
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (isSimulationActive) {
                simulationStep(tHoursAbsolute = 1, dtHours = dt)
                updateVisualState(planetsVisualState){ planetsVisualState = it }
                //t += dt
            }
            delay(simulationDelay)
        }
    }

    Row {
        Column(
            modifier = Modifier
                .width(500.dp)
                .fillMaxHeight()
                .background(color = Color(0xFF343434))
        ) {
            Button(onClick = { isSimulationActive = !isSimulationActive }) { Text( "run/pause" ) }
        }
        Column(
            modifier = Modifier
                //.width(500.dp)
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = Color.DarkGray)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) { paint(planetsVisualState) }
        }
    }
}

val zoom = earth.p.x / 500
val zoomRadius = earth.r / 15.0

fun updateVisualState(planetsVisualState: VisualState, stateChangeFunction: (VisualState) -> Unit) {
    var updated = false
    val newVisualBodyStates = mutableStateListOf<VisualBody>()
    for (i in planetsVisualState.bodies.indices) {
        val currState = planetsVisualState.bodies[i]
        val newState = currState.moveTo( spaceObjects[i].p.toOffset(zoom) )
        if (newState !== currState) updated = true
        newVisualBodyStates += newState
    }

    if (updated) {
        stateChangeFunction(VisualState(newVisualBodyStates))
    }
}


val tailStrokeStyle = Stroke(
    width = 1f
)

fun DrawScope.paint(planetsVisualState: VisualState) {
    drawCoordinates()
    
    scale(scaleX = 1f, scaleY = -1f) {
        translate(left = this.size.width/2, top = this.size.height/2) {
            //drawCircle(Color.Yellow, radius = 30f, Offset.Zero)
            for (i in planetsVisualState.bodies.indices) {
                val planet = planetsVisualState.bodies[i]
                val color = spaceObjects[i].color
                drawCircle(color, planet.radius, planet.position)

                val planetTail = Path()
                planetTail.moveTo(planet.position.x, planet.position.y)
                for (p in planet.tail) {
                    planetTail.lineTo(p.x, p.y)
                }

                this.drawPath(path = planetTail, color = color, style = tailStrokeStyle)
            }
        }
    }


}

fun DrawScope.drawCoordinates() {
    val c = with(size / 2F) { Offset(width, height) }
    drawLine(color = Color.Gray, Offset(0f, c.y), Offset(size.width, c.y))
    drawLine(color = Color.Gray, Offset(c.x, 0f), Offset(c.x, size.height))
}

