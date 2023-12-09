import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random.Default.nextDouble


val s = 3.0; val TwoPi = Math.PI*2; val halfPI = Math.PI/2; val minusHalfPI = -halfPI

var speed = 5f


fun main() = application { Window(onCloseRequest = ::exitApplication) { app() } }

@Composable fun app() = MaterialTheme {
    var x0 by remember { mutableStateOf(0f) }


    LaunchedEffect(Unit) { while(true) { delay(10L); x0 -= speed } }
    Button(onClick = { paramsArray = generateParameters() }) { Text("Refresh") }
    Canvas(modifier = Modifier.fillMaxSize()) { paint(x0) }
}

var lines = 5
var nWaves = 21
var babelsSize = 5.0
var babelsLen = 10.0
var paramsArray = generateParameters()
var lVisible = false
var lRadius = 0.5f
var lAlpha = 0.03f
var sumRadius = 1f


fun generateParameters() = Array(lines) { il ->
    val conf: Config = ConfigFactory.parseFile(File("/home/grigory/projects/tmp/demo/src/main/resources/application.conf")).resolve()
    lines = conf.getInt("lines")
    nWaves = conf.getInt("nWaves")
    babelsSize = conf.getDouble("babelsSize")
    babelsLen = conf.getDouble("babelsLen")
    speed = conf.getDouble("speed").toFloat()
    lVisible = conf.getBoolean("lVisible")
    lRadius = conf.getDouble("lRadius").toFloat()
    lAlpha = conf.getDouble("lAlpha").toFloat()
    sumRadius = conf.getDouble("sumRadius").toFloat()


    Array(nWaves) { i ->
        WaveParams(
            periodK = nextDouble(0.7, 2.0),
            offset = nextDouble() * TwoPi * s * 10,
            h = ((nextDouble() * 15.0 + 5.0) / (1 + 0.5 * i)) * s
        )
    }
}

fun DrawScope.paint(x0: Float) {
    drawCoordinates()
    var yd = size.height / lines
    var y0 = ((lines.toDouble()/2.0)*yd + yd/2).toFloat()

    for (iLine in paramsArray.indices) {
        y0 -= yd
        if (lVisible) {
            for (params in paramsArray[iLine]) {
                drawFun(radius = lRadius, clr = getNextColor(null).copy(alpha = lAlpha)) { y = params.wave(x, x0) + y0 }
            }
        }
        drawFun(radius = sumRadius,clr=COLORS[iLine]){
            var r = 0.0
            for (params in paramsArray[iLine]) r += params.wave(x, x0)
            y = r.toFloat() + y0
        }
    }



    /*
        drawField(radius = 0.5f, clr=Color(0xFFA9FFBE)) {
            val r1 = sqrt(x*x + y*y)/k1
            val Wave1 = sin(minusHalfPI + r1)

            val r2 = sqrt(x*x + y*y)/k2
            val Wave2 = sin(minusHalfPI + r2)

            (Wave1 + Wave2 + 2.0)/4.0
        }
    */



//    val r2 = 200f*200f;
//    drawFunSurf { (x*x + y*y) eq r2 }
//    drawFunSurf { (x*x - y*y) eq r2 }

//    val c1 = Point(400f, 100f); drawFunSurf { (Point(x,y)-c1).len() eq 0f }
//
//    val c2 = Point(-215f, -30f)
//
//


//    val D = (c1-c2).len() / 2
//
//    drawFunSurf {
//        val p = Point(x,y)
//        ( (p-c1).len() + (p-c2).len() ) eq D
//    }

//    drawFun{ y = x*x   / 100   }
//    drawFun{ y = x*x*x / 50000 }
//    drawFun{ y = 10000 / x     }
//    drawFun{ y = x             }
}

fun DrawScope.drawCoordinates() {
    val c = with(size / 2F) { Offset(width, height) }

    drawRect(bgClr, Offset.Zero, size)

    drawLine(color = Color.Gray, Offset(0f, c.y), Offset(size.width, c.y))
    drawLine(color = Color.Gray, Offset(c.x, 0f), Offset(c.x, size.height))
}


fun DrawScope.drawFun(dx: Float = 1f, radius: Float = 1f, clr: Color ? = null, f: Point.() -> Unit) {
    val color = getNextColor(clr)

    val center = with(size / 2F) { Offset(width, height) }

    val point = Point(-center.x, 0f)

    while (point.x < center.x) {
        point.f()
        val screenPoint = Offset(point.x + center.x, -point.y + center.y)
        val r = radius/2 + radius * (sin(point.x/babelsLen)+1)*babelsSize
        drawCircle(color, r.toFloat(), screenPoint)
        point.x += dx
    }
}

fun DrawScope.drawFunSurf(d: Float = 1f, radius: Float = 1f, clr: Color ? = null, f: Surf.() -> Float?) {
    val color = getNextColor(clr)

    val center = with(size / 2F) { Offset(width, height) }

    val p = Surf(-center.x, -center.y)
    while (p.x < center.x) {
        p.y = -center.y
        while (p.y < center.y) {
            val r = p.f()
            if (r != null) {
                val c = color.copy(alpha = 1 - r)
                val screenPoint = Offset(p.x + center.x, -p.y + center.y)
                drawCircle(c, radius, screenPoint)
            }
            p.y += d
        }
        p.x += d
    }
}

fun DrawScope.drawField(d: Float = 1f, radius: Float = 0.5f, clr: Color ? = null, f: FieldScope.() -> Double?) {
    val color = getNextColor(clr)

    val cen = with(size / 2F) { Offset(width, height) }
    val cx = cen.x.toDouble()
    val cy = cen.y.toDouble()

    val p = FieldScope(-cx, -cy)
    while (p.x < cx) {
        p.y = -cy
        while (p.y < cy) {
            val bright = p.f()
            if (bright != null) {
                val c = color.copy(alpha = bright.toFloat())
                val screenPoint = Offset( (p.x + cx).toFloat(), (-p.y + cy).toFloat() )
                drawCircle(c, radius, screenPoint)
            }
            p.y += d
        }
        p.x += d
    }
}

data class Point(var x: Float = 0f, var y: Float = 0f) {
    operator fun minus(p: Point) = Point(p.x - x, p.y - y)
    fun len(): Float = sqrt(x*x + y*y)
}

private val precision = 2000f
data class Surf(var x: Float = 0f, var y: Float = 0f) {
    infix fun Float.eq(another: Float): Float? {
        val r = abs(this - another)
        if (r < precision) return r/precision
        return null
    }
}

data class FieldScope(var x: Double = 0.0, var y: Double = 0.0)

private val colorNumber = AtomicInteger(0)
private fun getNextColor(clr: Color?): Color {
    return clr ?: COLORS[colorNumber.getAndIncrement() % COLORS.size]
}

private val bgClr = Color(0xFF443C38)

private val COLORS = arrayOf (Color(0xFFFFFC9B), Color(0xFF63E0C1), Color(0xFFF5993D), Color(0xFFF6AA65),
                              Color(0xFFCB7CE5), Color(0xFF66FF00), Color(0xFF71BFFF) )

data class WaveParams(val periodK: Double, val offset: Double, val h: Double) {
    fun wave(x: Float, x0: Float): Float {
        val r = ((x-x0 + offset) * periodK)/(15f*s)
        return (sin(r) * h).toFloat()
    }
}

