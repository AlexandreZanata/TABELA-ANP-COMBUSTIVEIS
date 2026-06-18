package com.anpfuel.domain.valueobject

import com.anpfuel.domain.exception.DomainException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class CnpjTest {

    @Test
    fun parseNormalizesFormattedCnpj() {
        val cnpj = Cnpj.parse("12.345.678/0001-95")

        assertEquals("12345678000195", cnpj.digits)
        assertEquals("12.345.678/0001-95", cnpj.formatted())
    }

    @Test
    fun parseAcceptsDigitsOnly() {
        val cnpj = Cnpj.parse("12345678000195")

        assertEquals("12345678000195", cnpj.digits)
    }

    @Test
    fun invalidLengthThrowsDomainException() {
        assertThrows(DomainException::class.java) {
            Cnpj.parse("123456789")
        }
    }
}
