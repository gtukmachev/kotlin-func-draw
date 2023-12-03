import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun main() = application { Window(onCloseRequest = ::exitApplication) { app() } }

@Composable fun app() = MaterialTheme { Canvas(modifier = Modifier.fillMaxSize()) { paint() } }

fun DrawScope.paint() {
    drawCoordinates()

    val r2 = 200f*200f;
    drawFunSurf { (x*x + y*y) eq r2 }
    drawFunSurf { (x*x - y*y) eq r2 }

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

    drawFun{ y = x*x   / 100   }
    drawFun{ y = x*x*x / 50000 }
    drawFun{ y = 10000 / x     }
    drawFun{ y = x             }
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
        drawCircle(color, radius, screenPoint)
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

private val colorNumber = AtomicInteger(0)
private fun getNextColor(clr: Color?): Color {
    return clr ?: COLORS[colorNumber.getAndIncrement() % COLORS.size]
}

private val bgClr = Color(0xFF443C38)

private val COLORS = arrayOf (Color(0xFFFFFC9B), Color(0xFF63E0C1), Color(0xFFF5993D), Color(0xFFF6AA65),
                              Color(0xFFCB7CE5), Color(0xFF66FF00), Color(0xFF71BFFF) )