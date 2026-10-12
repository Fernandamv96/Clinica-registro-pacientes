package com.clinica.util;

import java.util.regex.Pattern;

/**
 * Validador del RUT chileno (Rol Único Tributario / Nacional).
 * Implementa el algoritmo oficial de cálculo del dígito verificador.
 *
 * Acepta formatos:
 *   - 12345678-9
 *   - 12345678-K
 *   - 12.345.678-9
 *   - 123456789 (sin separador)
 */
public final class RutValidator {

    private static final Pattern CLEAN_PATTERN = Pattern.compile("[^0-9Kk]");

    private RutValidator() {}

    /** Normaliza: mayúsculas, sin puntos ni guión, conserva el DV. */
    public static String normalize(String rut) {
        if (rut == null) return null;
        return rut.replaceAll("[.\\s-]", "").toUpperCase();
    }

    public static boolean isValid(String rut) {
        if (rut == null || rut.isBlank()) return false;

        String normalized = normalize(rut);

        // debe tener al menos 2 chars (1 cuerpo + 1 DV)
        if (normalized.length() < 2) return false;

        String cuerpo = normalized.substring(0, normalized.length() - 1);
        char dv = normalized.charAt(normalized.length() - 1);

        if (!cuerpo.matches("\\d+")) return false;

        try {
            return calcularDV(Integer.parseInt(cuerpo)) == dv;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** Calcula el dígito verificador esperado para un número dado. */
    public static char calcularDV(int numero) {
        int suma = 0;
        int multiplicador = 2;

        while (numero > 0) {
            int digito = numero % 10;
            suma += digito * multiplicador;
            multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
            numero /= 10;
        }

        int resto = 11 - (suma % 11);
        return switch (resto) {
            case 11 -> '0';
            case 10 -> 'K';
            default -> (char) ('0' + resto);
        };
    }

    /** Formatea un RUT a 12.345.678-9 (si es válido). */
    public static String format(String rut) {
        if (!isValid(rut)) return rut;
        String normalized = normalize(rut);
        String cuerpo = normalized.substring(0, normalized.length() - 1);
        String dv = normalized.substring(normalized.length() - 1);

        StringBuilder formatted = new StringBuilder();
        int count = 0;
        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            if (count > 0 && count % 3 == 0) formatted.append('.');
            formatted.append(cuerpo.charAt(i));
            count++;
        }
        return formatted.reverse().toString() + "-" + dv;
    }
}
