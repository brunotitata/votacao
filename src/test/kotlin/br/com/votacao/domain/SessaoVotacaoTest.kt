package br.com.votacao.domain

import br.com.votacao.domain.DomainExceptions.SessaoException
import br.com.votacao.domain.DomainRegistry.Companion.defineDomainEventPublisher
import br.com.votacao.domain.sessao.SessaoVotacao.Companion.abrirSessaoVotacao
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime
import java.util.*

class SessaoVotacaoTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()

    override fun beforeSpec(spec: Spec) {
        defineDomainEventPublisher(publisher)
    }

    @Test
    fun abrirSessaoVotacaoComSucesso() {

        val pautaId = UUID.randomUUID()
        val duracaoPauta = 10

        coEvery { publisher.publish(any()) } returns Unit

        val sessaoVotacao = abrirSessaoVotacao(pautaId, duracaoPauta)

        assertThat(sessaoVotacao).isNotNull
        assertThat(sessaoVotacao.sessaoId).isNotNull
        assertThat(sessaoVotacao.pautaId).isEqualTo(pautaId)
        assertThat(sessaoVotacao.duracaoPauta).isEqualTo(duracaoPauta)
        assertThat(sessaoVotacao.inicioPauta).isBefore(sessaoVotacao.fimPauta)

        verify { publisher.publish(any()) }

        confirmVerified(publisher)
    }

    @Test
    fun capturarSessaoEncerrado() {

        coEvery { publisher.publish(any()) } returns Unit

        val sessaoVotacao = abrirSessaoVotacao(
                pautaId = UUID.randomUUID(),
                duracao = 1
        ).copy(
                fimPauta = LocalDateTime.now().minusMinutes(2)
        )

        assertThat(sessaoVotacao.capturarSessaoEncerrado()).isNotNull
    }

    @Test
    fun verificarSeEstaEncerrado() {

        coEvery { publisher.publish(any()) } returns Unit

        shouldThrow<SessaoException> {
            abrirSessaoVotacao(
                    pautaId = UUID.randomUUID(),
                    duracao = 1
            ).copy(
                    inicioPauta = LocalDateTime.now().minusMinutes(5),
                    fimPauta = LocalDateTime.now().minusMinutes(2)
            ).verificarSeEstaEncerrado()
        }.message.shouldBe("Sessão de votação esta encerrado!")

    }

}