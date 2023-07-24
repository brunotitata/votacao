package br.com.votacao.domain.associado

interface UserPort {

    fun validarCpf(cpf: String): String

}