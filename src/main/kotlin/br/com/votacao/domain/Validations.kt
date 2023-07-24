package br.com.votacao.domain

import org.valiktor.Constraint
import org.valiktor.Validator

object IsCPF : Constraint

fun <E> Validator<E>.Property<String?>.isCPF(): Validator<E>.Property<String?> =
    this.validate(IsCPF) { it != null && it.matches(Regex("^\\d{3}\\d{3}\\d{3}\\d{2}\$")) }