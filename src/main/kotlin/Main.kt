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

fun main() = application { Window(onCloseRequest = ::exitApplication) { app() } }

@Composable fun app() = MaterialTheme { Canvas(modifier = Modifier.fillMaxSize()) { paint() } }

fun DrawScope.paint() {
    drawCoordinates()

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

    val poit = Point(-center.x, 0f)

    while (poit.x < center.x) {
        poit.f()
        val screenPoint = Offset(poit.x + center.x, -poit.y + center.y)
        drawCircle(color, radius, screenPoint)
        poit.x += dx
    }
}

data class Point(var x: Float = 0f, var y: Float = 0f)

private val colorNumber = AtomicInteger(0)
private fun getNextColor(clr: Color?): Color {
    return clr ?: COLORS[colorNumber.getAndIncrement() % COLORS.size]
}

private val bgClr = Color(0xFF443C38)

private val COLORS = arrayOf (Color(0xFFFFFC9B), Color(0xFF63E0C1), Color(0xFFF5993D), Color(0xFFF6AA65),
                              Color(0xFFCB7CE5), Color(0xFF66FF00), Color(0xFF71BFFF) )