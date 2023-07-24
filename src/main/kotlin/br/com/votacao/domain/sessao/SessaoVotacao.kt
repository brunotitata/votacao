package br.com.votacao.domain.sessao

import br.com.votacao.domain.DomainExceptions.SessaoException
import br.com.votacao.domain.DomainRegistry
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class SessaoVotacao(
        @Id
        @Column(name = "sessao_id")
        val sessaoId: UUID,
        @Column(name = "pauta_id")
        val pautaId: UUID,
        @Column(name = "duracao_pauta")
        val duracaoPauta: Int,
        @Column(name = "inicio_pauta")
        val inicioPauta: LocalDateTime,
        @Column(name = "fim_pauta")
        val fimPauta: LocalDateTime,
        val encerrado: Boolean = false
) {

    companion object {
        fun abrirSessaoVotacao(pautaId: UUID, duracao: Int): SessaoVotacao {

            val sessaoVotacao = SessaoVotacao(
                    sessaoId = UUID.randomUUID(),
                    pautaId = pautaId,
                    duracaoPauta = duracao,
                    inicioPauta = LocalDateTime.now(),
                    fimPauta = LocalDateTime.now().plusMinutes(duracao.toLong())
            )

            DomainRegistry.domainEventPublisher()
                    .publish(
                            SessaoVotacaoCriadoEvent(
                                    sessaoId = sessaoVotacao.sessaoId
                            )
                    )

            return sessaoVotacao
        }
    }

    fun capturarSessaoEncerrado(): SessaoVotacao? {
        return if (LocalDateTime.now().isAfter(fimPauta))
            this
        else
            null
    }

    fun verificarSeEstaEncerrado() {

        if (LocalDateTime.now().isAfter(fimPauta))
            throw SessaoException(
                    msg = "Sessão de votação esta encerrado!",
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
            )

    }

    fun validarSeASessaoJaFoiEncerrado() {

        if (LocalDateTime.now().isBefore(fimPauta))
            throw SessaoException(
                    msg = "Sessão de votação ainda esta em andamento, aguarde o encerramento para consegui visualizar o resultado da sessão",
                    httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
            )

    }

}
