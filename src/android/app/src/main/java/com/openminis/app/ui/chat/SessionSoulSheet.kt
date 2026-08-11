package com.openminis.app.ui.chat

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openminis.app.agent.SoulStore

/**
 * [minis-fork:multi-soul] Bottom sheet choosing the session's persona.
 * Mirrors the SessionSkillsSheet shell ([StandardChatSheet]) but with a
 * single-select radio list: the global SOUL.md row plus every named
 * persona file under <filesDir>/minis-global/memory/souls/.
 *
 * Creating / editing persona files is done outside this sheet (shell or
 * the soul settings screen); this sheet only binds a session to one.
 */
@Composable
fun SessionSoulSheet(
    context: Context,
    currentSoulId: String?,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    val souls = remember { SoulStore.listSouls(context) }
    val globalName = remember {
        SoulStore.load(context)?.metadata?.name?.trim()?.ifEmpty { "Minis" } ?: "Minis"
    }

    StandardChatSheet(
        title = "Persona",
        onDismiss = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "Which personality should this session use?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 12.dp, bottom = 4.dp),
            )

            // Global row — always present, null soulId.
            PersonaRow(
                name = "Global — $globalName",
                description = "Default personality (SOUL.md)",
                selected = currentSoulId == null,
                onClick = { onSelect(null) },
            )

            if (souls.isEmpty()) {
                Text(
                    text = "No named personas yet. Drop .md files into souls/ to create them.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp),
                )
            } else {
                souls.forEach { s ->
                    PersonaRow(
                        name = s.name,
                        description = "souls/${s.soulId}.md",
                        selected = currentSoulId == s.soulId,
                        onClick = { onSelect(s.soulId) },
                    )
                }
            }

            Spacer(modifier = Modifier.padding(bottom = 24.dp))
        }
    }
}

@Composable
private fun PersonaRow(
    name: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.Face,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Medium else androidx.compose.ui.text.font.FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
            )
        }
        RadioButton(
            selected = selected,
            onClick = onClick,
        )
    }
}
