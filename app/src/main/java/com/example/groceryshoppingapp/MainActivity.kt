package com.example.groceryshoppingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.groceryshoppingapp.ui.theme.GroceryShoppingAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GroceryShoppingAppTheme {
                var items by remember { mutableStateOf(
                    listOf(
                        Triple("Apples", "4", "Carrefour"),
                        Triple("Bananas", "6", "Carrefour"),
                        Triple("Carrots", "10", "Carrefour")
                    )
                ) }
                var showDialog by remember { mutableStateOf(false) }
                var selectedStore by remember { mutableStateOf("Carrefour") }
                var filterSelectedStore by remember { mutableStateOf("Toate") }

                val stores = listOf("Carrefour", "Kaufland", "LIDL", "Metro", "Piață", "Selgros", "Black Friday")
                val selectStores = listOf("Toate") + stores

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { showDialog = true }
                        ) {
                            Text(
                                text = "+",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            TextField(
                                readOnly = true,
                                value = filterSelectedStore,
                                onValueChange = {},
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Dropdown Icon"
                                    )
                                },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                selectStores.forEach { store ->
                                    DropdownMenuItem(
                                        { Text(text = store, fontSize = 16.sp) },
                                        onClick = {
                                            filterSelectedStore = store
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        GroceryList(
                            items = if (filterSelectedStore == "Toate") items else items.filter { it.third == filterSelectedStore },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                if (showDialog) {
                    AddItemDialog(
                        onDismiss = { showDialog = false },
                        onAddItem = { name, quantity, store ->
                            items = items + Triple(name, quantity, store)
                            showDialog = false
                        },
                        selectedStore = selectedStore,
                        onStoreChange = { newStore -> selectedStore = newStore }
                    )
                }
            }
        }
    }
}

@Composable
fun GroceryItem(name: String, quantity: String, marketplace: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                modifier = Modifier.weight(1f).padding(start = 25.dp)
            )
            Text(
                text = quantity,
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f).padding(end = 25.dp)
            )
        }
    }
}

@Composable
fun GroceryList(items: List<Triple<String, String, String>>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp).padding(top = 50.dp),) {
        items.forEach { item ->
            GroceryItem(
                name = item.first,
                quantity = item.second,
                marketplace = item.third,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAddItem: (String, String, String) -> Unit,
    selectedStore: String,
    onStoreChange: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    val stores = listOf("Carrefour", "Kaufland", "LIDL", "Metro", "Piață", "Selgros", "Black Friday")
    val filterStores = stores + "Toate"
    var expanded by remember { mutableStateOf(false) }
    var selectedStoreText by remember { mutableStateOf(selectedStore) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Adaugă un nou produs") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Denumire") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Cantitate") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        readOnly = true,
                        value = selectedStoreText,
                        onValueChange = {},
                        label = { Text("Magazin") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown Icon"
                            )
                        },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        stores.forEach { store ->
                            DropdownMenuItem({ Text(text = store, fontSize = 16.sp) }, onClick = {
                                selectedStoreText = store
                                onStoreChange(store)
                                expanded = false
                            })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && quantity.isNotEmpty()) {
                        onAddItem(name, quantity, selectedStoreText)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}