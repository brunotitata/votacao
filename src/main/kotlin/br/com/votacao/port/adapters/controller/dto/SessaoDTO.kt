package br.com.votacao.port.adapters.controller.dto

import br.com.votacao.domain.DomainExceptions.SessaoException
import org.springframework.http.HttpStatus
import java.util.*

data class SessaoDTO(
        val pautaId: UUID,
        val duracaoEmMinutos: Int?
) {
    init {
        if (duracaoEmMinutos != null && duracaoEmMinutos <= 0)
            throw SessaoException(
                msg = "Duração da sessão deve ser maior que zero.",
                httpStatus = HttpStatus.BAD_REQUEST
            )
    }
}
