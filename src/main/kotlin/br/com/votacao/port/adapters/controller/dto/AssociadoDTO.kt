package br.com.votacao.port.adapters.controller.dto

import br.com.votacao.domain.isCPF
import org.valiktor.functions.isNotBlank
import org.valiktor.validate

data class AssociadoDTO(
    val nome: String,
    val cpf: String
) {
    init {
        validate(this) {
            validate(AssociadoDTO::cpf).isNotBlank().isCPF()
            validate(AssociadoDTO::nome).isNotBlank()
        }
    }
}