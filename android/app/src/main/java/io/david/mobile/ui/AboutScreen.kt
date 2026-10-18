package io.david.mobile.ui

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.composables.icons.lucide.R as LucideR
import io.david.mobile.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

/*
 * Settings › About David (DAVID.IO, 2026-09-28), as David iOS's `DavidAboutPage`.
 *
 * The one page that is not paper: a night canvas, whatever the theme, told
 * as a story in chapters down a lit thread — where David began, what it grew
 * into, who builds it — then the community, a coffee and a way to write.
 * Chapters rise into place as they scroll in. Change both apps together.
 */

internal object AboutInfo {
    /** Community links are intentionally unset until the owner configures official destinations. */
    val DISCORD_INVITE: String? = null

    class Product(val name: String, val detail: String, @DrawableRes val icon: Int)

    val ecosystem = listOf(
        Product("David Android", "Where it all started", LucideR.drawable.lucide_ic_smartphone),
        Product("David iOS", "The same David on iPhone", LucideR.drawable.lucide_ic_apple),
        Product("David Desktop", "For the big screen", LucideR.drawable.lucide_ic_monitor),
        Product("David Windows", "Native on Windows", LucideR.drawable.lucide_ic_laptop),
        Product("David Linux", "Native on Linux", LucideR.drawable.lucide_ic_terminal),
        Product("NTL VANCED", "Browser extension, powered by David", LucideR.drawable.lucide_ic_puzzle),
        Product("Slither for Android", "The original app, modded into David", LucideR.drawable.lucide_ic_hexagon),
        Product("Slither for iOS", "The original app, modded into David", LucideR.drawable.lucide_ic_gamepad_2),
    )
}

/**
 * The page's colours, from the player's theme (DAVID.IO, 2026-09-28: the page must
 * follow the chosen theme). Gold is the page's accent, deeper on light themes
 * so it reads on paper; text on gold is always dark. `DavidNight` on iOS.
 */
internal object Night {
    val Sky: Color get() = David.Paper
    val Card: Color get() = David.Card
    val Rule: Color get() = David.Rule
    val Ink: Color get() = David.Ink
    val Mute: Color get() = David.Mute
    val Quiet: Color get() = David.Quiet
    val Well: Color get() = David.Well
    val Gold: Color get() = if (David.Paper.luminance() < 0.45f) Color(0xFFE3BA6B) else Color(0xFFB07A1E)
    val OnGold = Color(0xFF1C1A16)
    val Discord = Color(0xFF5865F2)
}

@Composable
fun AboutScreen(
    appVersion: String,
    insetTop: Dp,
    insetBottom: Dp,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var note by remember { mutableStateOf("") }
    var licensesOpen by remember { mutableStateOf(false) }
    var licensesText by remember { mutableStateOf("Open-source license text is loading…") }
    LaunchedEffect(licensesOpen) {
        if (licensesOpen) {
            licensesText = withContext(Dispatchers.IO) {
                fun readAsset(name: String): String = runCatching {
                    context.assets.open("licenses/$name").bufferedReader().use { it.readText() }
                }.getOrElse { "Could not load $name. The full file remains available in the project source." }
                "OPEN-SOURCE NOTICES\n\n" + readAsset("NOTICE.txt") + "\n\nGNU GENERAL PUBLIC LICENSE v3\n\n" + readAsset("LICENSE.txt")
            }
        }
    }

    fun show(text: String) {
        note = text
        scope.launch {
            delay(2_200)
            if (note == text) note = ""
        }
    }

    // A night page: light status-bar icons while it is open, whatever the theme.
    val view = LocalView.current
    DisposableEffect(view) {
        val window = (view.context as? Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val previous = controller?.isAppearanceLightStatusBars
        controller?.isAppearanceLightStatusBars = false
        onDispose { if (previous != null) controller?.isAppearanceLightStatusBars = previous }
    }

    Box(Modifier.fillMaxSize().background(Night.Sky)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = insetTop + 44.dp, bottom = insetBottom + 40.dp),
        ) {
            item { Hero() }
            item {
                Chapter("I", "It began as a mod",
                    "DAVID.IO is an independent Android game project built around fast, responsive snake gameplay.")
            }
            item {
                Chapter("II", "Then it grew a name",
                    "The project has grown through gameplay, customization, and social features designed around the player.")
            }
            item {
                Chapter("III", "Then it became an ecosystem",
                    "DAVID.IO focuses on a consistent experience across supported devices. Third-party names and assets remain subject to their owners’ rights.")
            }
            AboutInfo.ecosystem.chunked(2).forEachIndexed { row, pair ->
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(start = 72.dp, end = 22.dp, bottom = 10.dp).height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        pair.forEachIndexed { column, product ->
                            ProductCard(product, highlight = row == 0 && column == 0, delayMs = column * 80,
                                modifier = Modifier.weight(1f).fillMaxHeight())
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
            item { Spacer(Modifier.height(26.dp)) }
            item {
                Chapter("IV", "Built by one person",
                    "DAVID.IO project information and required open-source notices are available in this section.",
                    last = true)
            }
            item { Roles() }
            item { Community(onJoin = { AboutInfo.DISCORD_INVITE?.let { open(context, it) } ?: show("The Discord invite is coming soon") }) }
            item {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 18.dp)
                        .clip(RoundedCornerShape(18.dp)).background(Night.Card)
                        .clickable { licensesOpen = true }.padding(18.dp),
                ) {
                    Text("OPEN-SOURCE LICENCES", fontFamily = David.Body, fontWeight = FontWeight.Bold,
                        fontSize = 11.sp, letterSpacing = 1.4.sp, color = Night.Gold)
                    Text("View GPL v3 and required notices", fontFamily = David.Display,
                        fontSize = 22.sp, color = Night.Ink, modifier = Modifier.padding(top = 6.dp))
                    Text("Copyright and third-party notices remain available here.", fontFamily = David.Body,
                        fontSize = 13.sp, lineHeight = 19.sp, color = Night.Mute,
                        modifier = Modifier.padding(top = 5.dp))
                }
            }
            item { Footer(appVersion) }
        }

        // Header: the way back, over a fade of the night.
        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Night.Sky, Night.Sky, Night.Sky.copy(alpha = 0f))))
                .padding(top = insetTop)
                .height(44.dp)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                "‹ Settings",
                fontFamily = David.Body,
                fontSize = 16.sp,
                color = Night.Gold,
                modifier = Modifier.clickable(onClick = onBack).padding(horizontal = 6.dp, vertical = 8.dp),
            )
        }

        AnimatedVisibility(
            visible = note.isNotEmpty(),
            enter = fadeIn() + slideInVertically { -it / 2 },
            exit = fadeOut() + slideOutVertically { -it / 2 },
            modifier = Modifier.align(Alignment.TopCenter).padding(top = insetTop + 58.dp),
        ) {
            Text(
                note,
                fontFamily = David.Body,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                color = Night.Sky,
                modifier = Modifier.clip(CircleShape).background(Night.Ink).padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }

        if (licensesOpen) {
            AlertDialog(
                onDismissRequest = { licensesOpen = false },
                title = { Text("Open-source licences") },
                text = {
                    Column(Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState())) {
                        Text(licensesText, fontSize = 12.sp, lineHeight = 16.sp, color = Night.Ink)
                    }
                },
                confirmButton = {
                    TextButton(onClick = { licensesOpen = false }) { Text("Close") }
                },
            )
        }
    }
}

private fun open(context: Context, uri: String, action: String = Intent.ACTION_VIEW) {
    try {
        context.startActivity(Intent(action, Uri.parse(uri)))
    } catch (_: ActivityNotFoundException) {
    }
}

/** Rises and fades in the first time it is composed, which in a lazy list is when it scrolls in. */
@Composable
private fun Modifier.rise(delayMs: Int = 0): Modifier {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(delayMs.toLong())
        progress.animateTo(1f, spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessVeryLow))
    }
    return this.graphicsLayer {
        alpha = progress.value
        translationY = (1f - progress.value) * 26.dp.toPx()
    }
}

@Composable
private fun Hero() {
    Column(
        Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(240.dp).rise(50), contentAlignment = Alignment.Center) {
            Box(
                Modifier.fillMaxSize().background(
                    Brush.radialGradient(listOf(Night.Gold.copy(alpha = 0.35f), Color.Transparent)),
                ),
            )
            Image(
                painter = painterResource(R.drawable.about_david_mark),
                contentDescription = "DAVID.IO",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .shadow(24.dp, RoundedCornerShape(26.dp), ambientColor = Night.Gold, spotColor = Night.Gold)
                    .clip(RoundedCornerShape(26.dp)),
            )
        }
        Text("THE STORY OF", fontFamily = David.Body, fontWeight = FontWeight.Bold, fontSize = 11.sp,
            letterSpacing = 3.sp, color = Night.Gold, modifier = Modifier.rise(150))
        Text("DAVID.IO", fontFamily = David.Display, fontSize = 58.sp, color = Night.Ink,
            modifier = Modifier.padding(top = 2.dp).rise(220))
        Text(
            "One developer. One snake game.\nA whole ecosystem.",
            fontFamily = David.Body, fontSize = 15.sp, lineHeight = 22.sp, color = Night.Mute,
            textAlign = TextAlign.Center, modifier = Modifier.padding(top = 10.dp).rise(300),
        )
        Icon(
            painterResource(LucideR.drawable.lucide_ic_chevron_down), contentDescription = null, tint = Night.Quiet,
            modifier = Modifier.padding(top = 28.dp).size(16.dp).rise(500),
        )
    }
}

/** One chapter on the thread: a numeral on a lit node, the title, the text. */
@Composable
private fun Chapter(numeral: String, title: String, text: String, last: Boolean = false) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 22.dp).height(IntrinsicSize.Min).rise(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(Modifier.fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .size(34.dp)
                    .shadow(10.dp, CircleShape, ambientColor = Night.Gold, spotColor = Night.Gold)
                    .clip(CircleShape)
                    .background(Night.Gold),
                contentAlignment = Alignment.Center,
            ) { Text(numeral, fontFamily = David.Display, fontSize = 15.sp, color = Night.OnGold) }
            Box(
                Modifier
                    .width(1.5.dp)
                    .weight(1f)
                    .background(
                        Brush.verticalGradient(
                            listOf(Night.Gold.copy(alpha = 0.6f), Night.Gold.copy(alpha = if (last) 0f else 0.15f)),
                        ),
                    ),
            )
        }
        Column(Modifier.weight(1f).padding(top = 4.dp, bottom = 34.dp)) {
            Text("CHAPTER $numeral", fontFamily = David.Body, fontWeight = FontWeight.Bold, fontSize = 10.sp,
                letterSpacing = 2.sp, color = Night.Quiet)
            Spacer(Modifier.height(8.dp))
            Text(title, fontFamily = David.Display, fontSize = 27.sp, lineHeight = 32.sp, color = Night.Ink)
            Spacer(Modifier.height(8.dp))
            Text(text, fontFamily = David.Body, fontSize = 15.sp, lineHeight = 23.sp, color = Night.Mute)
        }
    }
}

@Composable
private fun ProductCard(product: AboutInfo.Product, highlight: Boolean, delayMs: Int, modifier: Modifier) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier
            .rise(delayMs)
            .heightIn(min = 118.dp)
            .clip(shape)
            .background(Night.Card)
            .border(1.dp, if (highlight) Night.Gold.copy(alpha = 0.5f) else Night.Rule, shape)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Night.Well),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(product.icon), contentDescription = null,
                tint = if (highlight) Night.Gold else Night.Ink, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(product.name, fontFamily = David.Body, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Night.Ink)
            Spacer(Modifier.height(2.dp))
            Text(product.detail, fontFamily = David.Body, fontSize = 11.5.sp, lineHeight = 15.sp, color = Night.Quiet)
        }
    }
}

@Composable
private fun Roles() {
    Column(Modifier.fillMaxWidth().padding(start = 72.dp, end = 22.dp, bottom = 44.dp).rise()) {
        listOf("Backend", "Apps", "Engine", "Design", "Releases", "Support").chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { role ->
                    Text(
                        role,
                        fontFamily = David.Body, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = Night.Ink,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f).clip(CircleShape).background(Night.Card)
                            .border(1.dp, Night.Rule, CircleShape).padding(vertical = 8.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(2.dp))
        Text("From the first line of the server to the last pixel of this page.",
            fontFamily = David.Body, fontSize = 12.5.sp, color = Night.Quiet)
    }
}

@Composable
private fun SectionKicker(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(22.dp).height(1.5.dp).background(Night.Gold))
        Spacer(Modifier.width(10.dp))
        Text(text, fontFamily = David.Body, fontWeight = FontWeight.Bold, fontSize = 10.5.sp,
            letterSpacing = 2.4.sp, color = Night.Gold)
    }
}

@Composable
private fun Section(content: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(start = 22.dp, end = 22.dp, bottom = 44.dp).rise(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) { content() }
}

@Composable
private fun Community(onJoin: () -> Unit) = Section {
    SectionKicker("THE COMMUNITY")
    Text("Play with the people\nwho play David", fontFamily = David.Display, fontSize = 28.sp, lineHeight = 33.sp, color = Night.Ink)
    Text("Updates first, ideas heard, games together.", fontFamily = David.Body, fontSize = 14.sp, color = Night.Mute)
    val soon = AboutInfo.DISCORD_INVITE == null
    Row(
        Modifier
            .padding(top = 6.dp)
            .fillMaxWidth()
            .height(56.dp)
            .shadow(16.dp, RoundedCornerShape(16.dp), ambientColor = Night.Discord, spotColor = Night.Discord)
            .clip(RoundedCornerShape(16.dp))
            .background(Night.Discord)
            .clickable(onClick = onJoin)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(painterResource(LucideR.drawable.lucide_ic_messages_square), contentDescription = null, tint = Color.White,
            modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text("Join the David Discord", fontFamily = David.Body, fontWeight = FontWeight.Bold, fontSize = 15.5.sp,
            color = Color.White, modifier = Modifier.weight(1f))
        if (soon) {
            Text("SOON", fontFamily = David.Body, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.sp,
                color = Color.White,
                modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 3.dp))
        } else {
            Text("›", fontFamily = David.Body, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
        }
    }
}

@Composable
private fun Footer(appVersion: String) {
    Column(
        Modifier.fillMaxWidth().padding(top = 10.dp).rise(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.about_david_mark),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(30.dp).clip(RoundedCornerShape(8.dp)).graphicsLayer { alpha = 0.8f },
        )
        Text("Made with care by DAVID.IO", fontFamily = David.Body, fontWeight = FontWeight.SemiBold, fontSize = 12.5.sp, color = Night.Mute)
        Text("DAVID.IO $appVersion", fontFamily = David.Body, fontSize = 11.sp, color = Night.Quiet)
    }
}
