package com.grupo10.measurement_service.model;

/**
 * Enumeración que representa el periodo en que se realizó la medición de glucosa.
 * Permite clasificar el contexto de la medición para una correcta interpretación clínica.
 */
public enum PeriodoGlucosa {
    /** Medición realizada tras al menos 8 horas sin ingesta de alimentos. */
    AYUNAS,
    /** Medición realizada entre 1 y 2 horas después de una comida. */
    POSTPRANDIAL,
    /** Medición realizada durante la noche, generalmente antes de dormir. */
    NOCTURNA,
    /** Medición realizada en cualquier momento del día sin condición específica. */
    ALEATORIO
}
