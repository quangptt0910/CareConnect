package com.example.careconnect.dataclass

/**
 * Enum class representing various medical specializations.
 * Used for categorizing doctors or medical services.
 */
enum class Specialization {
    // Primary Care
    FAMILY_MEDICINE,
    INTERNAL_MEDICINE,
    PEDIATRICS,

    // Medical Specialties
    CARDIOLOGY,
    DERMATOLOGY,
    ENDOCRINOLOGY,
    GASTROENTEROLOGY,
    NEUROLOGY,
    ONCOLOGY,
    PULMONOLOGY,

    // Surgical Specialties
    GENERAL_SURGERY,
    ORTHOPEDIC_SURGERY,
    PLASTIC_SURGERY,

    // Women's Health
    OBSTETRICS_AND_GYNECOLOGY,

    // Mental Health
    PSYCHIATRY,
    CLINICAL_PSYCHOLOGY,

    // Diagnostics & Emergency
    RADIOLOGY,
    EMERGENCY_MEDICINE,

    // Other Common
    ALLERGY_AND_IMMUNOLOGY,
    SPORTS_MEDICINE,
    UROLOGY,
    OPHTHALMOLOGY;

    companion object {
        fun all(): List<Specialization> = enumValues<Specialization>().toList()
    }

    /**
     * Returns the human-readable name for the specialization.
     */
    fun displayName(): String = when (this) {
        // Primary Care
        FAMILY_MEDICINE -> "Family Medicine"
        INTERNAL_MEDICINE -> "Internal Medicine"
        PEDIATRICS -> "Pediatrics"

        // Medical Specialties
        CARDIOLOGY -> "Cardiology"
        DERMATOLOGY -> "Dermatology"
        ENDOCRINOLOGY -> "Endocrinology"
        GASTROENTEROLOGY -> "Gastroenterology"
        NEUROLOGY -> "Neurology"
        ONCOLOGY -> "Oncology"
        PULMONOLOGY -> "Pulmonology"

        // Surgical Specialties
        GENERAL_SURGERY -> "General Surgery"
        ORTHOPEDIC_SURGERY -> "Orthopedic Surgery"
        PLASTIC_SURGERY -> "Plastic Surgery"

        // Women's Health
        OBSTETRICS_AND_GYNECOLOGY -> "Obstetrics & Gynecology"

        // Mental Health
        PSYCHIATRY -> "Psychiatry"
        CLINICAL_PSYCHOLOGY -> "Clinical Psychology"

        // Diagnostics & Emergency
        RADIOLOGY -> "Radiology"
        EMERGENCY_MEDICINE -> "Emergency Medicine"

        // Other Common
        ALLERGY_AND_IMMUNOLOGY -> "Allergy & Immunology"
        SPORTS_MEDICINE -> "Sports Medicine"
        UROLOGY -> "Urology"
        OPHTHALMOLOGY -> "Ophthalmology"
    }
}