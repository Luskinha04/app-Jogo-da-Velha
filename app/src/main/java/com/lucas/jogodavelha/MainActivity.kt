package com.lucas.jogodavelha

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lucas.jogodavelha.ui.theme.Blue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Define o conteúdo da tela usando Compose
        setContent {
            JogoDaVelha()
        }
    }
}

@Composable
fun JogoDaVelha() {
    // Estado do tabuleiro com 9 posições vazias
    var bloco by remember { mutableStateOf(List(9) { "" }) }

    // Estado do jogador atual (X ou O)
    var jogadorAtual by remember { mutableStateOf("X") }

    // Estado do vencedor (null se ainda não há)
    var vencedor by remember { mutableStateOf<String?>(null) }

    // Função chamada ao clicar em um quadrado
    fun cliqueDoBloco(index: Int) {
        if (bloco[index] == "" && vencedor == null) {
            bloco = bloco.toMutableList().apply {
                this[index] = jogadorAtual
            }
            vencedor = verificarVencedor(bloco)
            if (vencedor == null) {
                jogadorAtual = if (jogadorAtual == "X") "O" else "X"
            }
        }
    }

    // Layout principal da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Linha com texto do jogador/vencedor e botão de reinício
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = vencedor?.let { "Vencedor: $it" } ?: "Jogador: $jogadorAtual",
                style = MaterialTheme.typography.headlineMedium
            )

            Icon(
                imageVector = Icons.Rounded.Refresh,
                contentDescription = "Reiniciar",
                tint = Blue,
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        // Reinicia o jogo
                        bloco = List(9) { "" }
                        jogadorAtual = "X"
                        vencedor = null
                    }
            )
        }

        // Componente da grade (tabuleiro)
        Grade(bloco = bloco, cliqueDoBloco = { cliqueDoBloco(it) })
    }
}

@Composable
fun Grade(
    bloco: List<String>, cliqueDoBloco: (Int) -> Unit
) {
    // Cria as 3 linhas do tabuleiro
    Column {
        for (row in 0 until 3) {
            Row {
                for (col in 0 until 3) {
                    val index = row * 3 + col
                    // Cria cada quadrado da grade
                    Quadrado(value = bloco[index], onClick = { cliqueDoBloco(index) })
                }
            }
            Spacer(modifier = Modifier.height(8.dp)) // Espaço entre linhas
        }
    }
}

@Composable
fun Quadrado(
    value: String,
    onClick: () -> Unit
) {
    // Componente visual de cada célula
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.White)
            .border(2.dp, Blue)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Exibe "X" ou "O" dentro do quadrado
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Blue,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JogoDaVelhaPreview() {
    JogoDaVelha()
}

// Função que verifica se há um vencedor ou empate
private fun verificarVencedor(bloco: List<String>): String? {
    val combinacoesVencedoras = listOf(
        listOf(0, 1, 2),
        listOf(3, 4, 5),
        listOf(6, 7, 8),
        listOf(0, 3, 6),
        listOf(1, 4, 7),
        listOf(2, 5, 8),
        listOf(0, 4, 8),
        listOf(2, 4, 6)
    )

    // Verifica se alguma combinação foi preenchida pelo mesmo jogador
    for (combinacao in combinacoesVencedoras) {
        val (a, b, c) = combinacao
        if (bloco[a] != "" && bloco[a] == bloco[b] && bloco[a] == bloco[c]) {
            return bloco[a]
        }
    }

    // Se todos os blocos estiverem preenchidos, é empate
    return if (bloco.all { it.isNotEmpty() }) "Empate" else null
}
