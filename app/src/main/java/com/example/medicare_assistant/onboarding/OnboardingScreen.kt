package com.example.medicare_assistant.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicare_assistant.ui.components.PrimaryButton
import com.example.medicare_assistant.ui.theme.Cream
import com.example.medicare_assistant.ui.theme.DotInactive
import com.example.medicare_assistant.ui.theme.Ink
import com.example.medicare_assistant.ui.theme.InkMuted
import com.example.medicare_assistant.ui.theme.LuminousYellow
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val title: String,
    val body: String,
    val description: String
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                "Never miss a dose",
                "Medicare reminds you at the right time, even without internet.",
                "A capsule pill"
            ),
            OnboardingPage(
                "Reminders everyone can notice",
                "Loud alarm, strong vibration and a flashlight blink, so no dose goes unnoticed.",
                "A ringing alarm clock"
            ),
            OnboardingPage(
                "Let’s get started",
                "Add your medicines once and Medicare takes care of the rest.",
                "A heart with a check mark"
            )
        )
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val lastPage = pagerState.currentPage == pages.lastIndex

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .systemBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (!lastPage) {
                TextButton(onClick = { viewModel.complete(onFinished) }) {
                    Text("Skip", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { index ->
            PageContent(index = index, page = pages[index])
        }

        PageDots(count = pages.size, current = pagerState.currentPage)
        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = if (lastPage) "Get started" else "Next",
            onClick = {
                if (lastPage) {
                    viewModel.complete(onFinished)
                } else {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                }
            }
        )
    }
}

@Composable
private fun PageContent(index: Int, page: OnboardingPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IllustrationCircle(description = page.description) {
            when (index) {
                0 -> CapsuleArt()
                1 -> AlarmArt()
                else -> HeartArt()
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = page.title,
                modifier = Modifier.semantics { heading() },
                color = Ink,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = page.body,
                color = InkMuted,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun IllustrationCircle(
    description: String,
    content: @Composable BoxScope.() -> Unit
) {
    val ring by rememberInfiniteTransition(label = "ring").animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "ring"
    )
    Box(
        modifier = Modifier
            .size(300.dp)
            .clearAndSetSemantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(260.dp)
                .scale(ring)
                .clip(CircleShape)
                .background(LuminousYellow.copy(alpha = 0.35f))
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(LuminousYellow),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
private fun CapsuleArt() {
    val angle by rememberInfiniteTransition(label = "capsule").animateFloat(
        initialValue = -44f,
        targetValue = -36f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "capsule"
    )
    Canvas(Modifier.size(180.dp).rotate(angle)) {
        val w = size.width
        val h = size.height * 0.5f
        val top = (size.height - h) / 2f
        val radius = CornerRadius(h / 2f, h / 2f)
        drawRoundRect(Color.White, Offset(0f, top), Size(w, h), radius)
        val shape = Path().apply { addRoundRect(RoundRect(0f, top, w, top + h, radius)) }
        clipPath(shape) {
            drawRect(Ink, Offset(0f, top), Size(w / 2f, h))
        }
        drawRoundRect(
            Color.White.copy(alpha = 0.35f),
            Offset(w * 0.1f, top + h * 0.2f),
            Size(w * 0.22f, h * 0.1f),
            CornerRadius(h * 0.05f, h * 0.05f)
        )
        drawRoundRect(Ink, Offset(0f, top), Size(w, h), radius, style = Stroke(8.dp.toPx()))
    }
}

@Composable
private fun AlarmArt() {
    val shake by rememberInfiniteTransition(label = "alarm").animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            tween(180, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "alarm"
    )
    Canvas(Modifier.size(190.dp).rotate(shake)) {
        val c = center
        val stroke = 8.dp.toPx()
        val r = size.minDimension * 0.34f
        val bell = size.minDimension * 0.17f
        drawCircle(Ink, radius = bell, center = Offset(c.x - r * 0.95f, c.y - r * 0.95f))
        drawCircle(Ink, radius = bell, center = Offset(c.x + r * 0.95f, c.y - r * 0.95f))
        drawLine(
            Ink,
            Offset(c.x - r * 0.7f, c.y + r * 0.95f),
            Offset(c.x - r * 0.95f, c.y + r * 1.25f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            Ink,
            Offset(c.x + r * 0.7f, c.y + r * 0.95f),
            Offset(c.x + r * 0.95f, c.y + r * 1.25f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawCircle(Color.White, radius = r, center = c)
        drawCircle(Ink, radius = r, center = c, style = Stroke(stroke))
        drawLine(Ink, c, Offset(c.x, c.y - r * 0.6f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawLine(Ink, c, Offset(c.x + r * 0.42f, c.y + r * 0.2f), strokeWidth = stroke, cap = StrokeCap.Round)
        drawCircle(Ink, radius = 5.dp.toPx(), center = c)
    }
}

@Composable
private fun HeartArt() {
    val beat by rememberInfiniteTransition(label = "heart").animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            tween(700, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "heart"
    )
    Canvas(Modifier.size(170.dp).scale(beat)) {
        val w = size.width
        val h = size.height
        val heart = Path().apply {
            moveTo(w * 0.5f, h * 0.9f)
            cubicTo(w * 0.05f, h * 0.58f, w * 0.0f, h * 0.25f, w * 0.25f, h * 0.15f)
            cubicTo(w * 0.4f, h * 0.09f, w * 0.5f, h * 0.2f, w * 0.5f, h * 0.28f)
            cubicTo(w * 0.5f, h * 0.2f, w * 0.6f, h * 0.09f, w * 0.75f, h * 0.15f)
            cubicTo(w * 1.0f, h * 0.25f, w * 0.95f, h * 0.58f, w * 0.5f, h * 0.9f)
            close()
        }
        drawPath(heart, Ink)
        val check = Path().apply {
            moveTo(w * 0.32f, h * 0.48f)
            lineTo(w * 0.45f, h * 0.61f)
            lineTo(w * 0.69f, h * 0.37f)
        }
        drawPath(
            check,
            LuminousYellow,
            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun PageDots(count: Int, current: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clearAndSetSemantics { contentDescription = "Page ${current + 1} of $count" },
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(count) { i ->
            val width by animateDpAsState(if (i == current) 28.dp else 10.dp, label = "dot")
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .height(10.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(if (i == current) Ink else DotInactive)
            )
        }
    }
}