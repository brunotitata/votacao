package br.com.votacao.application

import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.pauta.Pauta
import br.com.votacao.domain.pauta.PautaRepository
import br.com.votacao.port.adapters.controller.dto.PautaDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.*

@Component
class PautaApplicationService(
    private val pautaRepository: PautaRepository
) {

    fun cadastrarPauta(pauta: PautaDTO): UUID {

        pauta.titulo.takeIf { it.isEmpty() || it.isBlank() }
            ?.let { throw PautaException("Assunto da pauta não pode ser vazio") }

        pautaRepository.buscarPautaPorTitulo(
            titulo = pauta.titulo
        ).takeIf { it != null }
            ?.let {
                throw PautaException(
                    msg = "Pauta já foi cadastrada",
                    httpStatus = HttpStatus.CONFLICT
                )
            }

        val novaPauta = Pauta.novaPauta(
            titulo = pauta.titulo
        )

        pautaRepository.salvar(
            pauta = novaPauta
        )

        return novaPauta.pautaId
    }

}