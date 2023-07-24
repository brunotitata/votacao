package br.com.votacao.application

import br.com.votacao.domain.DomainExceptions.AssociadoException
import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainExceptions.VotoException
import br.com.votacao.domain.associado.AssociadoRepository
import br.com.votacao.domain.associado.UserPort
import br.com.votacao.domain.pauta.PautaRepository
import br.com.votacao.domain.voto.Voto
import br.com.votacao.domain.voto.VotoRepository
import br.com.votacao.port.adapters.controller.dto.VotoDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.*

@Component
class VotoApplicationService(
    private val votoRepository: VotoRepository,
    private val pautaRepository: PautaRepository,
    private val associadoRepository: AssociadoRepository,
    private val userPort: UserPort
) {

    fun novoVoto(pautaId: UUID, voto: VotoDTO): UUID {

        val pauta = pautaRepository.buscarPautaPorId(pautaId) ?: throw PautaException(
            msg = "Pauta não encontrado",
            httpStatus = HttpStatus.NOT_FOUND
        )
        val associado = associadoRepository.buscarAssociado(voto.associadoId)
            ?: throw AssociadoException(
                msg = "Associado não encontrado",
                httpStatus = HttpStatus.NOT_FOUND
            )

        /* API quebrada para realizar a integração, então o retorno é mockado */
        associado.let {
            userPort.validarCpf(it.cpf).takeIf { it != "ABLE_TO_VOTE" }
                ?.let {
                    throw AssociadoException(
                        msg = "Associado não esta permitido para votar",
                        httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
                    )
                }
        }

        votoRepository.validar(
            associado = associado,
            pauta = pauta
        ).takeIf { it != null }?.let {
            throw VotoException(
                msg = "Você não pode votar duas vezes na mesma pauta!",
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
            )
        }

        val novoVoto = Voto.realizarVoto(
            pauta = pauta,
            associado = associado,
            voto = voto.voto

        ).also {
            votoRepository.salvar(it)
        }

        return novoVoto.votoId

    }

}