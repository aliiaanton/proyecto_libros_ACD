package com.bookmatch.backend.enums;

/**
 * Enumeración que define los posibles estados de lectura de un libro para un usuario.
 */
public enum ReadingStatusType {
    /** El usuario quiere leer este libro */
    WANT_TO_READ,

    /** El usuario está leyendo actualmente este libro */
    READING,

    /** El usuario ha terminado de leer este libro */
    READ,

    /** El usuario ha abandonado la lectura de este libro */
    DROPPED
}