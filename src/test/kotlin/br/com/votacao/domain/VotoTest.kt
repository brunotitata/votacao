package br.com.votacao.domain

import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.pauta.Pauta
import br.com.votacao.domain.voto.Voto
import br.com.votacao.domain.voto.VotoEnum.NAO
import br.com.votacao.domain.voto.VotoRealizadoEvent
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.util.*

class VotoTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()

    override fun beforeSpec(spec: Spec) {
        DomainRegistry.defineDomainEventPublisher(publisher)
    }

    @BeforeEach
    fun setUp() = clearMocks(
        publisher
    )

    @Test
    fun `deve criar um novo voto com sucesso`() {

        every { publisher.publish(any()) } returns Unit

        val novaPauta = Pauta(
            pautaId = UUID.randomUUID(),
            titulo = "Você é a favor da taxação?",
        )

        val novoAssociado = Associado(
            associadoId = UUID.randomUUID(),
            nome = "Gustavo Costa",
            cpf = "92688654080"
        )

        val voto = Voto.realizarVoto(
            novaPauta,
            novoAssociado,
            NAO,
        )

        voto.pauta.pautaId.shouldBe(novaPauta.pautaId)
        voto.associado.nome.shouldBe(novoAssociado.nome)
        voto.associado.cpf.shouldBe(novoAssociado.cpf)
        voto.voto.shouldBe(NAO)

        verify(exactly = 1) {
            publisher.publish(
                VotoRealizadoEvent(
                    votoId = voto.votoId
                )
            )
        }

        confirmVerified(
            publisher
        )
    }

}