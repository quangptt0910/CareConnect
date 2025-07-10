package com.example.careconnect.screens.patient.chatbot

import androidx.lifecycle.viewModelScope
import com.example.careconnect.MainViewModel
import com.example.careconnect.data.repository.AuthRepository
import com.example.careconnect.data.repository.ChatMessagesRepository
import com.example.careconnect.data.repository.PatientRepository
import com.example.careconnect.dataclass.Allergy
import com.example.careconnect.dataclass.Condition
import com.example.careconnect.dataclass.Immunization
import com.example.careconnect.dataclass.MedicalHistoryEntry
import com.example.careconnect.dataclass.MedicalReport
import com.example.careconnect.dataclass.Medication
import com.example.careconnect.dataclass.Patient
import com.example.careconnect.dataclass.Role
import com.example.careconnect.dataclass.Surgery
import com.example.careconnect.dataclass.chat.Author
import com.example.careconnect.dataclass.chat.Message
import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatbotViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatMessagesRepository,
    private val patientRepository: PatientRepository
) : MainViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _patient = MutableStateFlow<Patient?>(null)
    val patient: StateFlow<Patient?> = _patient

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _medicalHistory = MutableStateFlow<List<MedicalHistoryEntry>>(emptyList())
    private val _medicalReports = MutableStateFlow<List<MedicalReport>>(emptyList())

    // Available specializations in the app
    private val availableSpecializations = listOf(
        "Family Medicine – comprehensive primary care for individuals and families",
        "Internal Medicine – adult-focused primary care and chronic disease management",
        "Pediatrics – health, growth, and development care for infants, children, and adolescents",
        "Cardiology – diagnosis and treatment of heart and blood vessel disorders",
        "Dermatology – care for skin, hair, and nails, including diseases and cosmetic issues",
        "Endocrinology – management of hormonal and metabolic disorders (e.g. diabetes, thyroid)",
        "Gastroenterology – treatment of digestive tract diseases and liver conditions",
        "Neurology – diagnosis and care of brain, spine, nerves, and muscle disorders",
        "Oncology – cancer detection, treatment (chemotherapy, radiation), and follow‑up",
        "Pulmonology – treatment of lung and respiratory system conditions",
        "General Surgery – operative and non‑operative management of abdominal and soft tissue conditions",
        "Orthopedic Surgery – surgical and non‑surgical care of bones, joints, and muscles",
        "Plastic Surgery – reconstructive and cosmetic procedures for skin, tissues, and body contours",
        "Obstetrics & Gynecology – women's reproductive health, prenatal care, and childbirth",
        "Psychiatry – medical treatment for mental health, emotional, and behavioral disorders",
        "Clinical Psychology – psychological assessment and non‑medical therapy for mental wellness",
        "Radiology – imaging‑based diagnosis using X‑rays, CT, MRI, and ultrasound",
        "Emergency Medicine – acute care for urgent and trauma conditions",
        "Allergy & Immunology – diagnosis and treatment of allergies, asthma, and immune disorders",
        "Sports Medicine – management of sports‑related injuries and exercise‑related health",
        "Urology – medical and surgical care of the urinary tract and male reproductive system",
        "Ophthalmology – eye health, vision testing, and surgical treatment for ocular conditions"
    )

    private val botAuthor = Author(
        id = "bot",
        name = "Medical Assistant",
        role = Role.MODEL,
        isBot = true
    )

    private val generativeModel: GenerativeModel = Firebase.ai(
        backend = GenerativeBackend.googleAI()
    ).generativeModel(modelName = "gemini-2.0-flash-exp",
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        })

    private var chat: Chat? = null

    init {
        loadPatientData()
        initializeChat()
    }

    private fun initializeChat() {
        val systemPrompt = buildSystemPrompt()
        chat = generativeModel.startChat(
            history = listOf(
                content(role = "model") { text(systemPrompt) },
                content(role = "model") { text("Hello! I'm your medical assistant. How can I help you today?") }
            )
        )

        // Add the initial bot message to the chat history
        _messages.update {
            it + Message(
                text = "Hello! I'm your medical assistant. I can help you understand symptoms, suggest specialists, and provide basic health guidance. What would you like to know?",
                author = botAuthor,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    private fun buildSystemPrompt(): String {
        val patient = _patient.value

        return """
        You are a medical assistant AI for CareConnect, an e-clinic, healthcare app. Your role is to:
        
        1. Help patients understand their symptoms and conditions
        2. Suggest appropriate medical specializations from our available list for doctor appointments
        3. Provide basic health guidance and wellness tips
        4. Offer supportive information about medical procedures
        
        IMPORTANT LIMITATIONS:
        - You are NOT a replacement for professional medical advice
        - Always remind patients to consult with healthcare providers for diagnosis and treatment
        - Do not provide specific medical diagnoses
        - Do not recommend specific medications or dosages
        - In emergencies, advise patients to seek immediate medical attention
        
        PATIENT INFORMATION:
        ${patient?.let { buildPatientContext(it) } ?: "Patient information not available"}
        
        AVAILABLE SPECIALIZATIONS in CareConnect:
        ${availableSpecializations.joinToString("\n")}
        
        RESPONSE GUIDELINES:
        - Be empathetic and supportive
        - Provide clear, easy-to-understand explanations
        - When suggesting specialists, mention specific ones from our list
        - Include relevant health tips and lifestyle advice
        - Always emphasize the importance of professional medical consultation
        - Be concise but comprehensive
        - If asked about emergency symptoms, prioritize immediate care advice
        
        Remember: Your goal is to be helpful and informative while ensuring patient safety through proper medical channels.
        """.trimIndent()
    }

    private fun buildPatientContext(patient: Patient): String {
        val medicalHistory = _medicalHistory.value
        val medicalReports = _medicalReports.value

        var context = """
        Name: ${patient.getFullName()}
        Age: ${calculateAge(patient.dateOfBirth)}
        Gender: ${patient.gender}
        Height: ${if (patient.height > 0) "${patient.height} cm" else "Not specified"}
        Weight: ${if (patient.weight > 0) "${patient.weight} kg" else "Not specified"}
        BMI: ${calculateBMI(patient.height, patient.weight)}
        """.trimIndent()

        // Add medical history if available
        if (medicalHistory.isNotEmpty()) {
            context += "\n\nMEDICAL HISTORY:\n"
            medicalHistory.forEach { entry ->
                when (entry) {
                    is Allergy -> context += "- Allergy: ${entry.allergen} (${entry.severity})\n"
                    is Condition -> context += "- Condition: ${entry.name} (${entry.status})\n"
                    is Medication -> context += "- Medication: ${entry.name} - ${entry.dosage}\n"
                    is Surgery -> context += "- Surgery: ${entry.surgeryName} (${entry.surgeryDate})\n"
                    is Immunization -> context += "- Vaccination: ${entry.vaccineName} (${entry.dateAdministered})\n"
                }
            }
        }

        // Add recent medical reports if available
        if (medicalReports.isNotEmpty()) {
            context += "\n\nRECENT MEDICAL REPORTS:\n"
            medicalReports.take(3).forEach { report ->
                context += "- ${report.diagnosis} (${report.reportDate})\n"
            }
        }

        return context
    }

    private fun calculateAge(dateOfBirth: String): String {
        return try {
            val parts = dateOfBirth.split("/")
            if (parts.size == 3) {
                val year = parts[2].toInt()
                val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                "${currentYear - year} years"
            } else "Not specified"
        } catch (e: Exception) {
            "Not specified"
        }
    }

    private fun calculateBMI(height: Double, weight: Double): String {
        return if (height > 0 && weight > 0) {
            val heightInMeters = height / 100
            val bmi = weight / (heightInMeters * heightInMeters)
            String.format("%.1f", bmi)
        } else "Not calculated"
    }

    fun loadPatientData() {
        launchCatching {
            _patient.value = patientRepository.getPatientById(authRepository.currentUser?.uid ?: "")
        }
    }

    // Create userAuthor dynamically based on current patient data
    private fun createUserAuthor(): Author {
        val currentPatient = _patient.value
        return Author(
            id = currentPatient?.id ?: authRepository.currentUser?.uid ?: "unknown",
            name = currentPatient?.name ?: "Patient",
            role = Role.PATIENT,
            isBot = false
        )
    }

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        viewModelScope.launch {
            _isLoading.update { true }

            try {
                // Create user author with current patient data
                val userAuthor = createUserAuthor()

                // Add user message to the chat history
                val userMessage = Message(
                    text = userInput,
                    author = userAuthor,
                    timestamp = System.currentTimeMillis()
                )
                _messages.update { it + userMessage }

                // Enhanced prompt with context
                val contextualPrompt = buildContextualPrompt(userInput)

                // Send the contextual prompt to the Gemini API
                val response = chat?.sendMessage(contextualPrompt)
                val responseText = response?.text ?: "I'm sorry, I couldn't process your request right now. Please try again."

                val botMessage = Message(
                    text = responseText,
                    author = botAuthor,
                    timestamp = System.currentTimeMillis()
                )
                _messages.update { it + botMessage }

            } catch (e: Exception) {
                val errorMessage = Message(
                    text = "I apologize, but I'm experiencing technical difficulties. Please try again in a moment, or contact support if the issue persists.",
                    author = botAuthor,
                    timestamp = System.currentTimeMillis()
                )
                _messages.update { it + errorMessage }
            } finally {
                _isLoading.update { false }
            }
        }
    }

    private fun buildContextualPrompt(userInput: String): String {
        // Check if user is asking about specializations
        val isSpecializationQuery = userInput.contains("specialist", ignoreCase = true) ||
                userInput.contains("doctor", ignoreCase = true) ||
                userInput.contains("who should I see", ignoreCase = true)

        // Check if it's an emergency-related query
        val isEmergencyQuery = userInput.contains("emergency", ignoreCase = true) ||
                userInput.contains("urgent", ignoreCase = true) ||
                userInput.contains("severe pain", ignoreCase = true) ||
                userInput.contains("can't breathe", ignoreCase = true)

        var prompt = userInput

        if (isSpecializationQuery) {
            prompt += "\n\nPlease suggest appropriate specialists from the CareConnect available specializations list."
        }

        if (isEmergencyQuery) {
            prompt += "\n\nIMPORTANT: If this is a medical emergency, please prioritize immediate care advice."
        }

        return prompt
    }

    fun sendQuickAction(action: String) {
        when (action) {
            "Find a specialist" -> sendMessage("I need help finding the right specialist for my condition")
            "Symptom checker" -> sendMessage("I'm experiencing some symptoms and would like guidance")
            "Medication info" -> sendMessage("I have questions about medications")
            "Emergency signs" -> sendMessage("What are signs that I need emergency medical care?")
        }
    }
}