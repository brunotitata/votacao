package br.com.votacao.domain

import br.com.votacao.domain.DomainExceptions.AssociadoException
import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.associado.AssociadoCriadoEvent
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.junit.jupiter.api.assertThrows

class AssociadoTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()

    override fun beforeSpec(spec: Spec) {
        DomainRegistry.defineDomainEventPublisher(publisher)
    }

    @BeforeEach
    fun setUp() = clearMocks(
        publisher
    )

    @Test
    fun `deve criar um novo associado com sucesso`() {

        every { publisher.publish(any()) } returns Unit

        val associadoEsperado = Associado.novoAssociado(
            nome = "João da Silva",
            cpf = "12345678900"
        )

        associadoEsperado.nome.shouldBe("João da Silva")
        associadoEsperado.cpf.shouldBe("12345678900")
        associadoEsperado.associadoId.shouldNotBeNull()

        verify(exactly = 1) {
            publisher.publish(
                AssociadoCriadoEvent(
                    associadoId = associadoEsperado.associadoId
                )
            )
        }

        confirmVerified(
            publisher
        )
    }

    @Test
    fun `se nome for branco ou vazio, deve retornar exception`() {

        every { publisher.publish(any()) } returns Unit

        assertThrows<AssociadoException> {
            Associado.novoAssociado(
                nome = "",
                cpf = "12345678900"
            )
        }.shouldHaveMessage("Nome não pode ser nulo ou vazio")

        verify(exactly = 0) { publisher.publish(any()) }

        confirmVerified(
            publisher
        )
    }

    @Test
    fun `se cpf for branco ou vazio, deve retornar exception`() {

        every { publisher.publish(any()) } returns Unit

        assertThrows<AssociadoException> {
            Associado.novoAssociado(
                nome = "Nome Test",
                cpf = ""
            )
        }.shouldHaveMessage("Cpf não pode ser nulo ou vazio")

        verify(exactly = 0) { publisher.publish(any()) }

        confirmVerified(
            publisher
        )
    }

}