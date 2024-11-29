@file:Suppress("UNREACHABLE_CODE")

package fr.isen.bruna.isensmartcompanion

import TabView
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fr.isen.bruna.isensmartcompanion.composants.EventScreen
import fr.isen.bruna.isensmartcompanion.composants.HistoryScreen
import fr.isen.bruna.isensmartcompanion.composants.MainScreen
import fr.isen.bruna.isensmartcompanion.database.AppDatabase
import fr.isen.bruna.isensmartcompanion.ui.theme.ISENSmartCompanionTheme


data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this) // Initialisation de la base de données
        val dao = database.questionResponseDao()
        setContent {
            val mainTab = TabBarItem(
                title = "Main",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            )
            val eventsTab = TabBarItem(
                title = "Events",
                selectedIcon = Icons.Filled.Notifications,
                unselectedIcon = Icons.Outlined.Notifications
            )
            val agendaTab = TabBarItem(
                title = "Agenda",
                selectedIcon = Icons.Filled.DateRange,
                unselectedIcon = Icons.Outlined.DateRange
            )
            val historyTab = TabBarItem(
                title = "History",
                selectedIcon = Icons.Filled.MoreVert,
                unselectedIcon = Icons.Outlined.MoreVert
            )
            val tabBarItems = listOf(mainTab, eventsTab, agendaTab, historyTab)
            val navController = rememberNavController()
            ISENSmartCompanionTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(bottomBar = { TabView(tabBarItems, navController) }) {
                        NavHost(navController = navController, startDestination = mainTab.title) {
                            composable(mainTab.title) {
                                MainScreen(dao = dao)
                            }
                            composable(eventsTab.title) {
                                EventScreen()
                            }
                            composable(agendaTab.title) {
                                Text(agendaTab.title)
                            }
                            composable(historyTab.title) {
                                HistoryScreen(dao = dao)
                            }
                        }
                    }
                }
            }
        }
    }
}

