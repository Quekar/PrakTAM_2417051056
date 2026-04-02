package com.example.praktam_2417051056.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val BgDark      = Color(0xFF0F0E1A)
private val SurfaceDark = Color(0xFF1A1830)
private val BorderDark  = Color(0xFF2A2840)
private val PrimaryColor  = Color(0xFF4F46E5)
private val SecondaryColor = Color(0xFF7C3AED)
private val AccentColor   = Color(0xFF818CF8)
private val GreenAccent   = Color(0xFF22C55E)
private val TextPrimary   = Color(0xFFE2E8F0)
private val TextSecondary = Color(0xFF94A3B8)
private val TextMuted     = Color(0xFF64748B)

private data class FeatureItem(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val title: String,
    val subtitle: String
)

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit = {}
) {
    val logoScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val contentAlpha = remember { Animatable(0f) }
    val taglineAlpha = remember { Animatable(0f) }
    val pillsAlpha   = remember { Animatable(0f) }
    val buttonAlpha  = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(600))
        delay(200)
        taglineAlpha.animateTo(1f, animationSpec = tween(500))
        delay(200)
        pillsAlpha.animateTo(1f, animationSpec = tween(500))
        delay(200)
        buttonAlpha.animateTo(1f, animationSpec = tween(400))
        delay(1800)
        onNavigateToMain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SurfaceDark,
                        BgDark,
                        Color(0xFF1A1440)
                    )
                )
            )
    ) {

        GlowCircle(
            size    = 280.dp,
            color   = PrimaryColor.copy(alpha = 0.18f),
            modifier = Modifier
                .offset(x = 140.dp, y = (-80).dp)
                .align(Alignment.TopEnd)
        )
        GlowCircle(
            size    = 200.dp,
            color   = SecondaryColor.copy(alpha = 0.14f),
            modifier = Modifier
                .offset(x = (-60).dp, y = 120.dp)
                .align(Alignment.BottomStart)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .alpha(contentAlpha.value)
                    .size(88.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryColor, SecondaryColor)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                LogoIllustration()
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text  = "DailyDo",
                style = MaterialTheme.typography.displayLarge.copy(
                    color        = TextPrimary,
                    letterSpacing = (-1).sp
                ),
                modifier = Modifier.alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text  = "PLAN  ·  SCHEDULE  ·  ACHIEVE",
                style = MaterialTheme.typography.labelMedium.copy(
                    color         = AccentColor.copy(alpha = 0.85f),
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.alpha(taglineAlpha.value)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .alpha(pillsAlpha.value),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeaturePill(
                    item = FeatureItem(
                        icon      = Icons.Default.DateRange,
                        iconBg    = PrimaryColor.copy(alpha = 0.25f),
                        iconTint  = AccentColor,
                        title     = "Daily Scheduler",
                        subtitle  = "Jadwal harian visual per minggu"
                    )
                )
                FeaturePill(
                    item = FeatureItem(
                        icon      = Icons.Default.Done,
                        iconBg    = GreenAccent.copy(alpha = 0.2f),
                        iconTint  = GreenAccent,
                        title     = "Task Manager",
                        subtitle  = "Kelola tugas dengan prioritas"
                    )
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            LoadingDots(
                modifier = Modifier.alpha(pillsAlpha.value)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick  = onNavigateToMain,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .alpha(buttonAlpha.value),
                shape    = MaterialTheme.shapes.large,
                colors   = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    contentColor   = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation  = 0.dp,
                    pressedElevation  = 0.dp
                )
            ) {
                Text(
                    text  = "Mulai Sekarang  →",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color      = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp
                    )
                )
            }
        }

        Text(
            text     = "v2.0 · Teknologi & Aplikasi Mobile",
            style    = MaterialTheme.typography.bodySmall.copy(
                color = TextMuted
            ),
            textAlign = TextAlign.Center,
            modifier  = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(buttonAlpha.value)
        )
    }
}

@Composable
private fun LogoIllustration() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White.copy(alpha = 0.9f))
            )
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White.copy(alpha = 0.45f))
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White.copy(alpha = 0.45f))
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height(9.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.7f))
                )
                Box(
                    modifier = Modifier
                        .width(14.dp)
                        .height(9.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }
        }
    }
}

@Composable
private fun FeaturePill(item: FeatureItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = MaterialTheme.shapes.medium,
        colors   = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        border   = androidx.compose.foundation.BorderStroke(
            width = 0.5.dp,
            color = AccentColor.copy(alpha = 0.25f)
        )
    ) {
        Row(
            modifier            = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier        = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(item.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = item.icon,
                    contentDescription = item.title,
                    tint               = item.iconTint,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text  = item.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = TextPrimary
                    )
                )
                Text(
                    text  = item.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun GlowCircle(
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun LoadingDots(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val scales = (0..2).map { i ->
        infiniteTransition.animateFloat(
            initialValue   = 0.6f,
            targetValue    = 1f,
            animationSpec  = infiniteRepeatable(
                animation  = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(i * 150)
            ),
            label = "dot$i"
        )
    }

    Row(
        modifier            = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment   = Alignment.CenterVertically
    ) {
        scales.forEachIndexed { i, scale ->
            val alpha = if (i == 2) 0.9f else if (i == 1) 0.65f else 0.4f
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(AccentColor.copy(alpha = alpha))
            )
        }
    }
}