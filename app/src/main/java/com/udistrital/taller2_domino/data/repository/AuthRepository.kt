package com.udistrital.taller2_domino.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface AuthRepository {
    val currentUserFlow: StateFlow<FirebaseUser?>
    val currentUser: FirebaseUser?
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun signInAnonymously(): Result<FirebaseUser>
    fun signOut()
}

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) : AuthRepository {

    private val _currentUserFlow = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    override val currentUserFlow: StateFlow<FirebaseUser?> = _currentUserFlow.asStateFlow()

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUserFlow.value = firebaseAuth.currentUser
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return runCatching {
            suspendCancellableCoroutine { continuation ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val user = result.user
                        if (user != null) {
                            if (continuation.isActive) continuation.resume(user)
                        } else {
                            if (continuation.isActive) continuation.resumeWithException(IllegalStateException("El usuario obtenido es nulo"))
                        }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) continuation.resumeWithException(exception)
                    }
                continuation.invokeOnCancellation {
                    // Si la corrutina se cancela, no hacemos nada más
                }
            }
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser> {
        return runCatching {
            suspendCancellableCoroutine { continuation ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val user = result.user
                        if (user != null) {
                            if (continuation.isActive) continuation.resume(user)
                        } else {
                            if (continuation.isActive) continuation.resumeWithException(IllegalStateException("Error al registrar usuario"))
                        }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) continuation.resumeWithException(exception)
                    }
            }
        }
    }

    override suspend fun signInAnonymously(): Result<FirebaseUser> {
        return runCatching {
            suspendCancellableCoroutine { continuation ->
                auth.signInAnonymously()
                    .addOnSuccessListener { result ->
                        val user = result.user
                        if (user != null) {
                            if (continuation.isActive) continuation.resume(user)
                        } else {
                            if (continuation.isActive) continuation.resumeWithException(IllegalStateException("Error al ingresar de forma anónima"))
                        }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) continuation.resumeWithException(exception)
                    }
            }
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}
