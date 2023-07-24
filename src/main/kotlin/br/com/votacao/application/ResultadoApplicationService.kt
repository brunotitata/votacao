package br.com.votacao.application

import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.ResultadoCriadoEvent
import br.com.votacao.domain.pauta.PautaRepository
import br.com.votacao.domain.sessao.SessaoRepository
import br.com.votacao.domain.voto.VotoEnum
import br.com.votacao.port.adapters.controller.dto.ResultadoVotacaoDTO
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ResultadoApplicationService(
        private val sessaoRepository: SessaoRepository,
        private val pautaRepository: PautaRepository,
        private val objectMapper: ObjectMapper
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @Scheduled(fixedRate = 30000)
    fun verificarSeASessaoEstaEncerrado() {

        logger.info { "Checando se possui alguma sessão de votação encerrada..." }

        sessaoRepository.buscarTodasSessaoVotacao()
                .map { sessao -> sessao.capturarSessaoEncerrado() }
                .map {
                    if (it != null && !it.encerrado) {
                        pautaRepository.buscarPautaPorId(it.pautaId)?.let {

                            logger.info { "Pauta com id: ${it.pautaId} identificado como encerrado, realizando a computação de votos..." }

                            val totalVotos = it.votos
                            val resultado = ResultadoVotacaoDTO(
                                    pauta = it.titulo,
                                    sim = totalVotos.count { it.voto == VotoEnum.SIM },
                                    nao = totalVotos.count { it.voto == VotoEnum.NAO },
                                    total = totalVotos.size,
                            )

                            DomainRegistry.domainEventPublisher()
                                    .publish(
                                            ResultadoCriadoEvent(
                                                    payload = objectMapper.writeValueAsString(resultado)
                                            )
                                    )

                        } ?: throw PautaException(
                                msg = "Pauta não encontrado com id: ${it.pautaId}",
                                httpStatus = HttpStatus.NOT_FOUND
                        )

                        sessaoRepository.salvar(
                                it.copy(encerrado = true)
                        )

                    }
                }

    }
}