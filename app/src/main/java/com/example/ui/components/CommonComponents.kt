package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CustomerWithSummary
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatCurrency(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

class TooltipShape(
    private val arrowWidth: Dp = 10.dp,
    private val arrowHeight: Dp = 6.dp,
    private val cornerRadius: Dp = 8.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val arrowW = with(density) { arrowWidth.toPx() }
        val arrowH = with(density) { arrowHeight.toPx() }
        val radius = with(density) { cornerRadius.toPx() }
        val bodyHeight = (size.height - arrowH).coerceAtLeast(0f)

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = size.width,
                    bottom = bodyHeight,
                    cornerRadius = CornerRadius(radius, radius)
                )
            )
            val midX = size.width / 2f
            moveTo(midX - arrowW / 2f, bodyHeight)
            lineTo(midX, size.height)
            lineTo(midX + arrowW / 2f, bodyHeight)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun TrendChartIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF0066FF)
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val barWidth = w * 0.16f
        val r = barWidth / 2f

        // Bar 1 (left)
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.08f, h * 0.65f),
            size = Size(barWidth, h * 0.35f),
            cornerRadius = CornerRadius(r, r)
        )
        // Bar 2 (middle)
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.38f, h * 0.40f),
            size = Size(barWidth, h * 0.60f),
            cornerRadius = CornerRadius(r, r)
        )
        // Bar 3 (right)
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.68f, h * 0.20f),
            size = Size(barWidth, h * 0.80f),
            cornerRadius = CornerRadius(r, r)
        )

        // Arrow line
        val arrowPath = Path().apply {
            moveTo(w * 0.12f, h * 0.52f)
            lineTo(w * 0.74f, h * 0.10f)
        }
        drawPath(
            path = arrowPath,
            color = color,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Arrow head
        val headPath = Path().apply {
            moveTo(w * 0.50f, h * 0.10f)
            lineTo(w * 0.76f, h * 0.10f)
            lineTo(w * 0.76f, h * 0.36f)
        }
        drawPath(
            path = headPath,
            color = color,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

data class DayTrend(
    val day: String,
    val date: String,
    val normalizedVal: Float,
    val ratio: Float
)

@Composable
fun HeroOutstandingCard(
    totalAmount: Double,
    modifier: Modifier = Modifier,
    trendPoints: List<com.example.viewmodel.TrendPoint> = emptyList(),
    weeklyPoints: List<com.example.viewmodel.TrendPoint> = emptyList(),
    monthlyPoints: List<com.example.viewmodel.TrendPoint> = emptyList(),
    weeklyPercentage: Double = 8.2,
    monthlyPercentage: Double = 12.5,
    title: String = "Total Outstanding Credit",
    subtitle: String = "Your credit status this week"
) {
    val isDark = isSystemInDarkTheme()
    val baseAmount = if (totalAmount > 0) totalAmount else 1195.0

    var isWeekly by remember { mutableStateOf(true) }

    // Pick active list of points based on toggle
    val activePoints = if (isWeekly) {
        if (weeklyPoints.isNotEmpty()) weeklyPoints else trendPoints
    } else {
        if (monthlyPoints.isNotEmpty()) monthlyPoints else trendPoints
    }

    val effectivePoints = remember(activePoints, baseAmount, isWeekly) {
        if (activePoints.isNotEmpty()) {
            activePoints
        } else {
            if (isWeekly) {
                listOf(
                    com.example.viewmodel.TrendPoint("Mon", "12", "12 Oct", baseAmount * 0.70, 0.32f),
                    com.example.viewmodel.TrendPoint("Tue", "13", "13 Oct", baseAmount * 0.82, 0.52f),
                    com.example.viewmodel.TrendPoint("Wed", "14", "14 Oct", baseAmount * 0.76, 0.44f),
                    com.example.viewmodel.TrendPoint("Thu", "15", "15 Oct", baseAmount * 0.88, 0.62f),
                    com.example.viewmodel.TrendPoint("Fri", "16", "16 Oct", baseAmount * 0.94, 0.72f),
                    com.example.viewmodel.TrendPoint("Sat", "17", "17 Oct", baseAmount * 0.90, 0.65f),
                    com.example.viewmodel.TrendPoint("Today", "18", "Today", baseAmount, 0.85f)
                )
            } else {
                listOf(
                    com.example.viewmodel.TrendPoint("4W", "20 Sep", "20 Sep", baseAmount * 0.55, 0.28f),
                    com.example.viewmodel.TrendPoint("3W", "27 Sep", "27 Sep", baseAmount * 0.68, 0.42f),
                    com.example.viewmodel.TrendPoint("2W", "4 Oct", "4 Oct", baseAmount * 0.80, 0.58f),
                    com.example.viewmodel.TrendPoint("1W", "11 Oct", "11 Oct", baseAmount * 0.92, 0.75f),
                    com.example.viewmodel.TrendPoint("Today", "18 Oct", "Today", baseAmount, 0.85f)
                )
            }
        }
    }

    var selectedIndex by remember(effectivePoints) {
        mutableStateOf((effectivePoints.size - 1).coerceAtLeast(0))
    }

    val safeIndex = selectedIndex.coerceIn(0, (effectivePoints.size - 1).coerceAtLeast(0))
    val currentSelectedPoint = if (effectivePoints.isNotEmpty()) effectivePoints[safeIndex] else null
    val currentSelectedAmount = currentSelectedPoint?.amount ?: baseAmount
    val isLatestSelected = safeIndex == (effectivePoints.size - 1).coerceAtLeast(0)

    val currentPercentage = if (isWeekly) weeklyPercentage else monthlyPercentage
    val isPositive = currentPercentage >= 0
    val arrowSymbol = if (isPositive) "▲" else "▼"
    val percentText = "${if (isPositive) "+" else ""}${String.format(Locale.US, "%.1f", currentPercentage)}%"
    val periodComparisonText = if (isWeekly) "vs. 7 days ago" else "vs. last month"

    val cardBg = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onSurface
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val borderCol = MaterialTheme.colorScheme.outline
    val accentColor = MaterialTheme.colorScheme.primary

    GlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_credit_card"),
        shape = RoundedCornerShape(26.dp),
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            // Glass Header Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(GlassEmerald)
                    )
                    Text(
                        text = "FROSTED LEDGER • LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textSecondary,
                        letterSpacing = 0.5.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SYNCED",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // TOP HEADER ROW - Phone Sized
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Glass icon container with specular glow
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        TrendChartIcon(modifier = Modifier.size(18.dp), color = accentColor)
                    }

                    Column {
                        Text(
                            text = title,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = if (isWeekly) "7-day cumulative dues trend" else "Monthly customer dues ledger",
                            fontSize = 11.5.sp,
                            color = textSecondary
                        )
                    }
                }

                // Frosted Glass Toggle Pill (Weekly / Monthly)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f), RoundedCornerShape(16.dp))
                        .clickable { isWeekly = !isWeekly }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("live_trend_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                        Text(
                            text = if (isWeekly) "7-Day" else "Monthly",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Switch Trend Timeframe",
                            tint = accentColor,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // PRIMARY OUTSTANDING AMOUNT - Phone sized 25sp with retro mono font
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Reset to latest point on tap
                        selectedIndex = (effectivePoints.size - 1).coerceAtLeast(0)
                    },
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = formatCurrency(currentSelectedAmount),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        letterSpacing = (-0.5).sp,
                        modifier = Modifier.testTag("total_amount_text")
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    // TREND BADGE ROW
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Frosted Percentage Pill badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isPositive) accentColor.copy(alpha = 0.15f)
                                    else Color(0xFF10B981).copy(alpha = 0.15f)
                                )
                                .border(
                                    1.dp,
                                    if (isPositive) accentColor.copy(alpha = 0.35f) else Color(0xFF10B981).copy(alpha = 0.35f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 7.dp, vertical = 2.5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = arrowSymbol,
                                    fontSize = 8.5.sp,
                                    color = if (isPositive) accentColor else Color(0xFF059669)
                                )
                                Text(
                                    text = percentText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPositive) accentColor else Color(0xFF059669),
                                    modifier = Modifier.testTag("trend_percentage_text")
                                )
                            }
                        }

                        Text(
                            text = if (isLatestSelected) periodComparisonText else "${currentSelectedPoint?.fullDate ?: ""} (tap reset)",
                            fontSize = 11.5.sp,
                            color = textSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // RETRO GRAPH WITH GUIDE TICKS AND INTERACTIVE SCRUBBING
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                var pointCoords by remember { mutableStateOf<List<Offset>>(emptyList()) }
                val count = effectivePoints.size

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(effectivePoints) {
                            detectTapGestures { tapOffset ->
                                val insetX = 18.dp.toPx()
                                val chartW = size.width - 2 * insetX
                                val stepX = if (count > 1) chartW / (count - 1) else chartW
                                val tapped = ((tapOffset.x - insetX) / stepX).roundToInt().coerceIn(0, count - 1)
                                selectedIndex = tapped
                            }
                        }
                        .pointerInput(effectivePoints) {
                            detectHorizontalDragGestures { change, _ ->
                                val insetX = 18.dp.toPx()
                                val chartW = size.width - 2 * insetX
                                val stepX = if (count > 1) chartW / (count - 1) else chartW
                                val dragged = ((change.position.x - insetX) / stepX).roundToInt().coerceIn(0, count - 1)
                                selectedIndex = dragged
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val insetX = 18.dp.toPx()
                    val chartW = w - 2 * insetX
                    val stepX = if (count > 1) chartW / (count - 1) else chartW
                    val chartH = h * 0.62f
                    val bottomMargin = 5.dp.toPx()

                    // Retro grid background guides
                    val gridColor = borderCol.copy(alpha = 0.25f)
                    drawLine(
                        color = gridColor,
                        start = Offset(insetX, h * 0.25f),
                        end = Offset(w - insetX, h * 0.25f),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = gridColor,
                        start = Offset(insetX, h * 0.60f),
                        end = Offset(w - insetX, h * 0.60f),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = gridColor,
                        start = Offset(insetX, h - bottomMargin),
                        end = Offset(w - insetX, h - bottomMargin),
                        strokeWidth = 1.dp.toPx()
                    )

                    val calculatedPoints = effectivePoints.mapIndexed { index, data ->
                        val x = insetX + index * stepX
                        val y = h - bottomMargin - (chartH * data.normalizedVal)
                        Offset(x, y)
                    }
                    pointCoords = calculatedPoints

                    // Smooth curve path
                    val path = Path()
                    val fillPath = Path()

                    fillPath.moveTo(insetX, h)

                    calculatedPoints.forEachIndexed { index, pt ->
                        if (index == 0) {
                            path.moveTo(pt.x, pt.y)
                            fillPath.lineTo(pt.x, pt.y)
                        } else {
                            val prev = calculatedPoints[index - 1]
                            val midX = prev.x + (pt.x - prev.x) / 2f
                            path.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                            fillPath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                        }
                    }

                    fillPath.lineTo(insetX + chartW, h)
                    fillPath.close()

                    // Draw area gradient fill
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                accentColor.copy(alpha = 0.25f),
                                accentColor.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = h
                        )
                    )

                    // Draw curve stroke
                    drawPath(
                        path = path,
                        color = accentColor,
                        style = Stroke(width = 2.4.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw circular point dots
                    calculatedPoints.forEachIndexed { index, pt ->
                        if (index == safeIndex) {
                            // Active point halo & dot
                            drawCircle(
                                color = accentColor.copy(alpha = 0.25f),
                                radius = 7.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = accentColor,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 1.8.dp.toPx(),
                                center = pt
                            )
                        } else {
                            // Inactive points
                            drawCircle(
                                color = accentColor,
                                radius = 3.2.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 1.6.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                // FLOATING TOOLTIP PILL BADGE - Phone sized with retro font
                if (pointCoords.size > safeIndex) {
                    val activePt = pointCoords[safeIndex]
                    val density = androidx.compose.ui.platform.LocalDensity.current
                    val tooltipAmountText = formatCurrency(currentSelectedAmount)

                    Box(
                        modifier = Modifier
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                val tw = placeable.width
                                val th = placeable.height
                                val maxW = constraints.maxWidth
                                val posX = (activePt.x - tw / 2f).coerceIn(4f, (maxW - tw - 4).toFloat())
                                val posY = (activePt.y - th - with(density) { 4.dp.toPx() }).coerceAtLeast(0f)
                                layout(placeable.width, placeable.height) {
                                    placeable.placeRelative(posX.toInt(), posY.toInt())
                                }
                            }
                            .clip(TooltipShape(arrowWidth = 7.dp, arrowHeight = 3.5.dp, cornerRadius = 6.dp))
                            .background(accentColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .padding(bottom = 2.dp)
                    ) {
                        Text(
                            text = tooltipAmountText,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // X-AXIS DAY AND DATE LABELS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                effectivePoints.forEachIndexed { index, item ->
                    val isSelected = index == safeIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedIndex = index }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.dayLabel,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) accentColor else textSecondary
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = item.dateLabel,
                            fontSize = 9.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) accentColor else textSecondary.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DualStatCards(
    pendingCount: Int,
    settledCount: Int,
    onPendingClick: () -> Unit,
    onSettledClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surface

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Pending Dues Card - Frosted Glass Style
        GlassCard(
            modifier = Modifier
                .weight(1f)
                .testTag("stat_card_pending"),
            onClick = { onPendingClick() },
            shape = RoundedCornerShape(22.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoseDue.copy(alpha = 0.15f))
                            .border(1.dp, RoseDue.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Pending Dues",
                            tint = RoseDue,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(RoseDue.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PENDING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoseDue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pending Dues",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$pendingCount",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("home_pending_count")
                    )
                    Text(
                        text = "accounts",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        }

        // Settled Card - Frosted Glass Style
        GlassCard(
            modifier = Modifier
                .weight(1f)
                .testTag("stat_card_settled"),
            onClick = { onSettledClick() },
            shape = RoundedCornerShape(22.dp),
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TealSuccess.copy(alpha = 0.15f))
                            .border(1.dp, TealSuccess.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Settled",
                            tint = TealSuccess,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(TealSuccess.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SETTLED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealSuccess
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Settled",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$settledCount",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("home_settled_count")
                    )
                    Text(
                        text = "this month",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DueActionCards(
    onAddDueClick: () -> Unit,
    onRecordPaymentClick: () -> Unit,
    modifier: Modifier = Modifier,
    addDueTestTag: String = "action_card_add_due",
    recordPaymentTestTag: String = "action_card_record_payment"
) {
    val borderColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surface

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Add Due Card (Customer Credit - Frosted Glass Button)
        GlassCard(
            modifier = Modifier
                .weight(1f)
                .height(86.dp)
                .testTag(addDueTestTag),
            onClick = { onAddDueClick() },
            shape = RoundedCornerShape(20.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(RoseDue)
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Due",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "Add Due",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Give credit",
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = RoseDue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Record Payment Card (Received Money - Frosted Glass Button)
        GlassCard(
            modifier = Modifier
                .weight(1f)
                .height(86.dp)
                .testTag(recordPaymentTestTag),
            onClick = { onRecordPaymentClick() },
            shape = RoundedCornerShape(20.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(TealSuccess)
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Record Payment",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.Center) {
                        Text(
                            text = "Payment",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Settle balance",
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TealSuccess,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun TopCustomersRanking(
    topCustomers: List<CustomerWithSummary>,
    onCustomerClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (topCustomers.isEmpty()) return

    val maxDue = topCustomers.maxOfOrNull { it.balance }?.coerceAtLeast(1.0) ?: 1.0
    val borderColor = MaterialTheme.colorScheme.outline

    GlassPanel(
        modifier = modifier
            .fillMaxWidth()
            .testTag("top_customers_card"),
        shape = RoundedCornerShape(24.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GlassAmber)
                    )
                    Text(
                        text = "Top Due Accounts",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "LEADERBOARD",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            topCustomers.take(4).forEachIndexed { index, item ->
                val fraction = (item.balance / maxDue).toFloat().coerceIn(0.05f, 1f)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onCustomerClick(item.customer.id) }
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = item.customer.name,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = formatCurrency(item.balance),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (item.balance > 0) RoseDue else TealSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Glass glowing progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(GlassCyan, GlassRose)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerCard(
    summary: CustomerWithSummary,
    onClick: () -> Unit,
    onAddCredit: () -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("customer_card_${summary.customer.id}"),
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Glass Avatar squircle with glowing gradient border
                    val initial = summary.customer.name.take(1).uppercase()
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column {
                        Text(
                            text = summary.customer.name,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (summary.customer.phone.isNotBlank()) summary.customer.phone else "No phone",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Balance display in sleek glass pill
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(summary.balance),
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (summary.balance > 0) RoseDue else TealSuccess
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    val isDue = summary.balance > 0
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isDue) RoseDue.copy(alpha = 0.14f)
                                else TealSuccess.copy(alpha = 0.14f)
                            )
                            .border(
                                1.dp,
                                if (isDue) RoseDue.copy(alpha = 0.40f) else TealSuccess.copy(alpha = 0.40f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isDue) "DUE" else "SETTLED",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDue) RoseDue else TealSuccess
                        )
                    }
                }
            }
        }
    }
}



