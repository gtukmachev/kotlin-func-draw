package tga.planets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tga.planets.physics_state.G
import tga.planets.physics_state.GM
import tga.planets.physics_state.sun
import tga.planets.physics_state.sunInitialMass

@Composable
fun settingsPanel(
    dt:                          Long,    dtChange                          : (Long) -> Unit,
    simulationStepsPerSession:   Int,     simulationStepsPerSessionChange   : (Int) -> Unit,
    simulationSessionsPerSecond: Int,     simulationSessionsPerSecondChange : (Int) -> Unit,
    visualZoom:                  Float,   visualZoomChange                  : (Float) -> Unit,
    isLogCoordinatesOn:          Boolean, isLogCoordinatesOnChange          : (Boolean) -> Unit,
    sunMass:                     Float,   sunMassChange                     : (Float) -> Unit,
){
    propertyBorder { Button(modifier = Modifier.fillMaxWidth(), onClick = { isSimulationActive = !isSimulationActive }) { Text( "run/pause" ) } }
    propertySlider("Zoom", value = visualZoom, valueRange = 0.1f..300f, onValueChange = visualZoomChange)
    propertySlider("Sun mass k", value = sunMass, valueRange = 0.1f..10f, onValueChange = {
        sunMassChange(it)
        sun.m = sunInitialMass * it
        GM[sun.i] = G * sun.m

    })
    propertySlider("dt seconds", value = dt.toFloat(), valueRange = 1f..3600f*6, onValueChange = { dtChange(it.toLong()) })
    propertySlider("dt batch", value = simulationStepsPerSession.toFloat(), valueRange = 1f..500f, onValueChange = { simulationStepsPerSessionChange(it.toInt()) })
}

@Composable fun propertySlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
) {
    propertyBorder {
        Text(text = "$label: $value")
        Slider(value = value, valueRange = valueRange, onValueChange = onValueChange, steps = steps)
    }
}

@Composable fun propertyBorder(innerContent: @Composable () -> Unit) {
    Column (modifier = Modifier.padding(8.dp)) {
        innerContent()
        Divider(modifier = Modifier.fillMaxWidth(), color = Color.Gray)
    }
}
