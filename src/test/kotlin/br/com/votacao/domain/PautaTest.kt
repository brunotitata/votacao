package br.com.votacao.domain

import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.pauta.Pauta
import br.com.votacao.domain.pauta.PautaCriadoEvent
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.junit.jupiter.api.assertThrows

class PautaTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()

    override fun beforeSpec(spec: Spec) {
        DomainRegistry.defineDomainEventPublisher(publisher)
    }

    @BeforeEach
    fun setUp() = clearMocks(
        publisher
    )

    @Test
    fun `deve criar uma nova pauta com sucesso`() {

        every { publisher.publish(any()) } returns Unit

        val novaPauta = Pauta.novaPauta("Titulo da pauta")

        novaPauta.pautaId.shouldNotBeNull()
        novaPauta.titulo.shouldBe("Titulo da pauta")

        verify(exactly = 1) {
            publisher.publish(
                PautaCriadoEvent(
                    pautaId = novaPauta.pautaId
                )
            )
        }

        confirmVerified(
            publisher
        )

    }

    @Test
    fun `se o titulo for vazio, deve retornar exception`() {

        every { publisher.publish(any()) } returns Unit

        assertThrows<PautaException> {
            Pauta.novaPauta("")
        }.shouldHaveMessage("Titulo da pauta não pode ser nulo ou vazio")

        verify(exactly = 0) { publisher.publish(any()) }

        confirmVerified(
            publisher
        )

    }
}