package tga.planets.zoom

import androidx.compose.ui.geometry.Offset
import tga.planets.physics_state.earth
import kotlin.math.abs
import kotlin.math.log2
import kotlin.math.sqrt

private val startPlanet = earth
enum class ZoomType {

    linear {
        override val initialScreenZoom = startPlanet.p.x / 800
        override fun transform(x: Float) = x
        override fun transform(p: Offset) = p
    },

    square {
        override val initialScreenZoom = startPlanet.p.x / (800*800)
        override fun transform(x: Float) = when {
            x == 0f -> 0f
            x  > 0f -> sqrt(x)
            else    -> -sqrt(abs(x))
        }
        override fun transform(p: Offset) = Offset(transform(p.x), transform(p.y))
    };


    abstract val initialScreenZoom: Double

    abstract fun transform(x: Float): Float

    abstract fun transform(p: Offset): Offset
}