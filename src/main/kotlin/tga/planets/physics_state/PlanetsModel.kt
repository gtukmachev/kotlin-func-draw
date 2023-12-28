package tga.functions.tga.planets.phisic_state

import androidx.compose.ui.graphics.Color


data class SpaceObject(
    val i: Int,
    var m: Double,      // kg
    val r: Double,      // km
    var p: Vector,      // km, km
    //var prevVisualPosition: Vector = p,
    var speed: Vector,  // km/ch
    var color: Color,
    var rK: Double = 1.0
)

val sun     = SpaceObject(0, m=3.955e30, r=696340.0, p= v(0,0), speed= v(0,0), Color(0xFFF9FF88), rK = 2e-2)
val mercury = SpaceObject(1, m=0.32868e24, r=2439.0, p= v( 57.911014e6,0), speed= v(0.0, 47.36*3600 ), Color(0xFFFFC797) )
val venera  = SpaceObject(2, m=4.81068e24, r=6052.0, p= v(108.30782e6, 0), speed= v(0.0, 35.02*3600 ), Color(0xFFACFFD4) )
val earth   = SpaceObject(3, m=5.97600e24, r=6378.0, p= v(149.59787e9, 0), speed= v(0.0, 29.765e3 ), Color(0xFF17CBFF) )

val spaceObjects = arrayOf(
    sun,
    //mercury,
    //venera,
    earth
)
val n = spaceObjects.size

val obj = spaceObjects

//val aK = 0.015
const val Gk = 0.5

// index of mass multiplying
const val G = 6.67259e-11 * Gk
val mM : Array<Array<Double>> = Array(n){ i -> Array(n) { j -> obj[i].m * obj[j].m } }
val GmM: Array<Array<Double>> = Array(n){ i -> Array(n) { j -> G * mM[i][j] } }
val  GM: Array<Double> = Array(n){ i -> G * obj[i].m }

fun simulationStep(tHoursAbsolute: Long, dtHours: Long) {
    val dt = dtHours.toDouble()

    for (i in 1 until spaceObjects.size) {
        val planet = spaceObjects[i]

        val distanceToSunPow2  = planet.p.lenPow2
        val a = (GM[sun.i] / distanceToSunPow2)

        val aDirection = -planet.p.norm()
        val aVector = aDirection * a
        val aVectorPow2Vector = aDirection * (a * a)

        //val dp = planet.speed * dt
        val dp = (planet.speed * dt) + (aVectorPow2Vector * dt)/2.0

        planet.p += dp
        planet.speed += (aVector * dt)
    }



}