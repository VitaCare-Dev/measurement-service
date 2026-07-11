package com.grupo10.measurement_service.utils;

/**
 * Rangos fisiológicamente plausibles (no "normales") para cada tipo de
 * medición, usados como cota de validación en el backend. El objetivo es
 * atrapar errores de entrada (ej. "3000" en vez de "300"), no reemplazar el
 * criterio médico — por eso son amplios. Deben mantenerse alineados con
 * {@code vitacare-frontend/src/utils/measurementRanges.ts}.
 */
public final class RangosMedicionValidos {

    private RangosMedicionValidos() {
    }

    public static final int GLUCOSA_MIN = 20;
    public static final int GLUCOSA_MAX = 600;

    public static final int PRESION_SISTOLICA_MIN = 60;
    public static final int PRESION_SISTOLICA_MAX = 260;

    public static final int PRESION_DIASTOLICA_MIN = 30;
    public static final int PRESION_DIASTOLICA_MAX = 150;

    public static final double TEMPERATURA_MIN = 30;
    public static final double TEMPERATURA_MAX = 43;

    public static final double PESO_MIN = 2;
    public static final double PESO_MAX = 300;

    public static final double COLESTEROL_TOTAL_MIN = 50;
    public static final double COLESTEROL_TOTAL_MAX = 500;

    public static final double COLESTEROL_LDL_MIN = 20;
    public static final double COLESTEROL_LDL_MAX = 400;

    public static final double COLESTEROL_HDL_MIN = 10;
    public static final double COLESTEROL_HDL_MAX = 150;

    public static final double TRIGLICERIDOS_MIN = 20;
    public static final double TRIGLICERIDOS_MAX = 1000;
}
