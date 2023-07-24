package br.com.votacao.application

import br.com.votacao.domain.DomainEventPublisher
import br.com.votacao.domain.DomainExceptions.AssociadoException
import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.associado.AssociadoCriadoEvent
import br.com.votacao.domain.associado.AssociadoRepository
import br.com.votacao.port.adapters.controller.dto.AssociadoDTO
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import java.util.*

class AssociadoApplicationServiceTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()
    private val associadoRepository = mockk<AssociadoRepository>()
    private val associadoApplicationService = AssociadoApplicationService(
        associadoRepository
    )

    override fun beforeSpec(spec: Spec) {
        DomainRegistry.defineDomainEventPublisher(publisher)
    }

    @BeforeEach
    fun setUp() = clearMocks(
        publisher,
        associadoRepository
    )

    @Test
    fun `deve realizar a criação de um associado`() {

        val associadoDTO = AssociadoDTO("João da Silva", "39624537097")
        val associado = slot<Associado>()

        every { publisher.publish(any()) } returns Unit
        every { associadoRepository.buscarAssociadoPorCpf(associadoDTO.cpf) } returns null
        every { associadoRepository.salvar(capture(associado)) } returns Unit

        val result = associadoApplicationService.criarAssociado(associadoDTO)

        assertEquals(associadoDTO.nome, associado.captured.nome)
        assertEquals(associadoDTO.cpf, associado.captured.cpf)
        assertNotNull(associado.captured.associadoId)
        assertNotNull(result)
        assertEquals(associado.captured.associadoId, result)

        verify(exactly = 1) { associadoRepository.buscarAssociadoPorCpf(associadoDTO.cpf) }
        verify(exactly = 1) { associadoRepository.salvar(capture(associado)) }
        verify(exactly = 1) { publisher.publish(AssociadoCriadoEvent(associadoId = result)) }

        confirmVerified(
            associadoRepository,
            publisher
        )
    }

    @Test
    fun `quando criar associado, deve lancar excecao quando o CPF ja esta cadastrado`() {

        val associadoDTO = AssociadoDTO("Maria Souza", "39624537097")
        val associadoExistente = Associado(
            associadoId = UUID.fromString("9da8ad09-d373-4e12-b452-ca9e2341cae4"),
            nome = "Maria Souza",
            cpf = "39624537097",
            pautas = listOf()
        )

        every { publisher.publish(any()) } returns Unit
        every { associadoRepository.buscarAssociadoPorCpf(associadoDTO.cpf) } returns associadoExistente

        assertThrows<AssociadoException> {
            associadoApplicationService.criarAssociado(associadoDTO)
        }.shouldHaveMessage("Associado com cpf: 39624537097 já cadastrado")

        verify(exactly = 0) { publisher.publish(any()) }
        verify(exactly = 1) { associadoRepository.buscarAssociadoPorCpf(associadoDTO.cpf) }

        confirmVerified(
            associadoRepository,
            publisher
        )
    }

    @Test
    fun `quando criar associado, deve validar se foi salvo corretamente`() {

        val associadoDTO = AssociadoDTO("Pedro Almeida", "33333333333")
        val associado = slot<Associado>()

        every { publisher.publish(any()) } returns Unit
        every { associadoRepository.buscarAssociadoPorCpf(associadoDTO.cpf) } returns null
        every { associadoRepository.salvar(capture(associado)) } returns Unit

        associadoApplicationService.criarAssociado(associadoDTO)

        verify(exactly = 1) { publisher.publish(AssociadoCriadoEvent(associadoId = associado.captured.associadoId)) }
        verify(exactly = 1) { associadoRepository.buscarAssociadoPorCpf(associadoDTO.cpf) }
        verify(exactly = 1) { associadoRepository.salvar(capture(associado)) }

        assertEquals(associadoDTO.nome, associado.captured.nome)
        assertEquals(associadoDTO.cpf, associado.captured.cpf)

        confirmVerified(
            associadoRepository,
            publisher
        )
    }
}