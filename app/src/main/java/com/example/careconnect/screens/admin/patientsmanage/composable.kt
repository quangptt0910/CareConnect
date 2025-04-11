package com.example.careconnect.screens.admin.patientsmanage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.careconnect.R
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.screens.admin.doctormanage.DialogCancelButton
import com.example.careconnect.screens.admin.doctormanage.DialogConfirmButton

/**
 * Composable function to display a filled card with a title, expandable list of user products, and delete functionality.
 *
 * @param title The title of the card.
 * @param user List of user-added products to be displayed.
 * @param onDeleteProduct Callback function to handle product deletion.
 */
@Composable
fun FilledCardPatients(
    title : String,
    modifier: Modifier = Modifier,
    patients: List<Patient>,
    onDeleteProduct: (Patient) -> Unit,
) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Patient?>(null) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                patients.forEach { patients ->
                    ListItem(
                        headlineContent = { Text(patients.name + " " + patients.surname) },

                        trailingContent = {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {

                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit"
                                    )
                                }

                                Spacer(modifier = Modifier.width(2.dp))

                                IconButton(
                                    onClick = {
                                        productToDelete = patients
                                        showWarningDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }

                        }
                    )
                }
            }

            if (expanded) {

            }
        }

        if (showWarningDialog && productToDelete != null) {
            AlertDialog(
                title = { Text(stringResource(R.string.delete_patient_title)) },
                text = { Text(stringResource(R.string.delete_patient_description)) },
                dismissButton = {
                    DialogCancelButton(R.string.cancel) {
                        showWarningDialog = false
                    }
                },
                confirmButton = {
                    DialogConfirmButton(R.string.delete) {
                        onDeleteProduct(productToDelete!!)
                        showWarningDialog = false
                    }
                },
                onDismissRequest = { showWarningDialog = false }
            )
        }
    }
}


@Composable
fun FilledCardEdit(
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),

        ) {
        }

        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .align(Alignment.Start)
        )

        Text(
            text = "name surname",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ){

            Text(
                text = "address:\n12 street",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = "phone:\n123456789",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center

            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "email:\n123456789@gmail.com",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = "Gender:\nFemale",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

    }
}


@Composable
fun FilledCardAppointment(
    modifier: Modifier = Modifier,
){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .fillMaxWidth()
    ){

            Text(
                text = "Appointment details",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 8.dp)
            )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Recent appointment: 12/02/2025",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier,
            )

            Spacer(modifier = Modifier.height(5.dp))


            Text(
                text = "Next appointment: 25/04/2025",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier,
            )

            Spacer(modifier = Modifier.height(5.dp))


            Text(
                text = "Doctor: John Smith",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier,
            )

        }

    }
}