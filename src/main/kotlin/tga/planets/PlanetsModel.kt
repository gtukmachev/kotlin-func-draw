package tga.planets

import androidx.compose.ui.graphics.Color


data class SpaceObject(
    var m: Double,      // kg
    val r: Double,      // km
    var p: Vector,      // km, km
    var speed: Vector,  // km/ch
    var color: Color,
    var rK: Double = 1.0
)


val sun   = SpaceObject(m=3.955e30, r=696340.0, p=v(0      ,0), speed=v(0,0       ), Color(0xFFF9FF88), rK = 2e-2  )
val earth = SpaceObject(m=5.972e24, r=  6371.0, p=v(149.6e6,0), speed=v(0,-107_218*10), Color(0xFF17CBFF) )

val spaceObjects = listOf(
    sun,
    earth
)