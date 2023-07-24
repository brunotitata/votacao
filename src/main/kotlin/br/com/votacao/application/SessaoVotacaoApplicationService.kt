package br.com.votacao.application

import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainExceptions.SessaoException
import br.com.votacao.domain.pauta.PautaRepository
import br.com.votacao.domain.sessao.SessaoRepository
import br.com.votacao.domain.sessao.SessaoVotacao
import br.com.votacao.domain.voto.VotoEnum
import br.com.votacao.port.adapters.controller.dto.ResultadoVotacaoDTO
import br.com.votacao.port.adapters.controller.dto.VotoDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.*

@Component
class SessaoVotacaoApplicationService(
    private val sessaoRepository: SessaoRepository,
    private val pautaRepository: PautaRepository,
    private val votoApplicationService: VotoApplicationService
) {

    fun novaSessao(pautaId: UUID, duracao: Int?): UUID {

        sessaoRepository.buscarSessaoPauta(
                pautaId = pautaId
        ).takeIf { it != null }?.let {
            throw PautaException(
                    msg = "Pauta já iniciada com id: $pautaId",
                    httpStatus = HttpStatus.CONFLICT
            )
        }

        val sessaoVotacao = SessaoVotacao.abrirSessaoVotacao(
                pautaId = pautaId,
                duracao = duracao ?: 1
        ).also {
            sessaoRepository.salvar(it)
        }

        return sessaoVotacao.sessaoId
    }

    fun obterResultado(sessaoId: UUID): ResultadoVotacaoDTO {
        return sessaoRepository.buscarSessao(sessaoId)?.let {

            it.validarSeASessaoJaFoiEncerrado()

            pautaRepository.buscarPautaPorId(it.pautaId)?.let {

                val totalVotos = it.votos
                return ResultadoVotacaoDTO(
                        pauta = it.titulo,
                        sim = totalVotos.count { it.voto == VotoEnum.SIM },
                        nao = totalVotos.count { it.voto == VotoEnum.NAO },
                        total = totalVotos.size,
                )

            } ?: throw PautaException(
                    msg = "Pauta não encontrado com id: ${it.pautaId}",
                    httpStatus = HttpStatus.NOT_FOUND
            )
        } ?: throw SessaoException(
                msg = "Sessão não encontrado com id: $sessaoId",
                httpStatus = HttpStatus.NOT_FOUND
        )
    }

    fun realizarVoto(sessaoId: UUID, voto: VotoDTO): UUID {

        val sessaoVotacao = sessaoRepository.buscarSessao(
            sessaoId = sessaoId
        ) ?: throw SessaoException(
            msg = "Sessão não encontrado ou encerrada!",
            httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
        )

        sessaoVotacao.verificarSeEstaEncerrado()
        sessaoVotacao.let {
            return votoApplicationService.novoVoto(
                pautaId = sessaoVotacao.pautaId,
                voto = voto
            )
        }
    }

}