package com.example.loginfb.vistas

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

@Composable
fun Registrar(navController: NavHostController, auth: FirebaseAuth) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmacion by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmacion,
                onValueChange = {confirmacion = it},
                label = { Text("Repite la contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    registrarUsuario(email, password, confirmacion, auth, context, navController)
                }) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                navController.popBackStack()
            }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

private fun registrarUsuario(
    email: String,
    password: String,
    confirmacion: String,
    auth: FirebaseAuth,
    context: Context,
    navController: NavHostController
){
    if (email.isNotEmpty() && password.isNotEmpty() && confirmacion.isNotEmpty()){
        val problema = validarPassword(password, confirmacion)
        if (problema == null){
            auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        Toast.makeText(context, "Registro correcto", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }else{

                        Toast.makeText(context, mensajeError(task.exception), Toast.LENGTH_SHORT).show()
                    }

                }
        } else {
            Toast.makeText(context, problema, Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Ingrese sus credenciales", Toast.LENGTH_SHORT).show()
    }
}

private fun validarPassword(password: String, confirmacion: String): String? {
    return when {
        password != confirmacion -> "Las contraseñas no coinciden"
        password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
        password.length > 16 -> "La contraseña debe tener como máximo 16 caracteres"
        !password.any { it.isUpperCase() } -> "La contraseña debe incluir una mayúscula"
        !password.any { it.isLowerCase() } -> "La contraseña debe incluir una minúscula"
        !password.any { it.isDigit() } -> "La contraseña debe incluir un número"
        else -> null
    }
}

private fun mensajeError(error: Exception?): String {
    return when (error){
        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo"
        is FirebaseAuthWeakPasswordException -> "La contraseña no cumple los requisitos"
        is FirebaseAuthInvalidCredentialsException -> "El correo no es válido"
        is FirebaseNetworkException -> "Sin conexión a internet"
        else -> "No se pudo registrar"
    }
}
