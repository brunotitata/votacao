package br.com.votacao.application

import br.com.votacao.domain.DomainEventPublisher
import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.pauta.Pauta
import br.com.votacao.domain.pauta.PautaCriadoEvent
import br.com.votacao.domain.pauta.PautaRepository
import br.com.votacao.domain.sessao.SessaoRepository
import br.com.votacao.port.adapters.controller.dto.PautaDTO
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.assertThrows
import java.util.*

class PautaApplicationServiceTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()
    private val pautaRepository = mockk<PautaRepository>()
    private val sessaoRepository = mockk<SessaoRepository>()
    private val votoApplicationService = mockk<VotoApplicationService>()
    private val pautaApplicationService = PautaApplicationService(
        pautaRepository,
        sessaoRepository,
        votoApplicationService
    )

    override fun beforeSpec(spec: Spec) {
        DomainRegistry.defineDomainEventPublisher(publisher)
    }

    @BeforeEach
    fun setUp() = clearMocks(
        publisher,
        pautaRepository,
        sessaoRepository,
        votoApplicationService
    )


    @Test
    fun `deve cadastrar uma nova pauta corretamente`() {

        val pauta = slot<Pauta>()
        val pautaDTO = PautaDTO("Título da Pauta")

        every { publisher.publish(any()) } returns Unit
        every { pautaRepository.buscarPautaPorTitulo(pautaDTO.titulo) } returns null
        every { pautaRepository.salvar(capture(pauta)) } returns Unit

        val result = pautaApplicationService.cadastrarPauta(pautaDTO)

        verify(exactly = 1) { publisher.publish(PautaCriadoEvent(pautaId = result)) }
        verify(exactly = 1) { pautaRepository.buscarPautaPorTitulo(pautaDTO.titulo) }
        verify(exactly = 1) { pautaRepository.salvar(capture(pauta)) }

        assertEquals(pautaDTO.titulo, pauta.captured.titulo)
        assertNotNull(pauta.captured.pautaId)
        assertNotNull(result)
        assertEquals(pauta.captured.pautaId, result)

        confirmVerified(
            publisher,
            pautaRepository,
            sessaoRepository,
            votoApplicationService
        )
    }

    @Test
    fun `quando titulo estiver vazio, deve retornar exception`() {

        val pauta = slot<Pauta>()
        val pautaDTO = PautaDTO("")

        every { publisher.publish(any()) } returns Unit
        every { pautaRepository.buscarPautaPorTitulo(pautaDTO.titulo) } returns null
        every { pautaRepository.salvar(capture(pauta)) } returns Unit

        assertThrows<PautaException> {
            pautaApplicationService.cadastrarPauta(pautaDTO)
        }.shouldHaveMessage("Assunto da pauta não pode ser vazio")

        verify(exactly = 0) { publisher.publish(any()) }
        verify(exactly = 0) { pautaRepository.buscarPautaPorTitulo(pautaDTO.titulo) }
        verify(exactly = 0) { pautaRepository.salvar(capture(pauta)) }

        confirmVerified(
            publisher,
            pautaRepository,
            sessaoRepository,
            votoApplicationService
        )
    }

    @Test
    fun `quando titulo ja estiver cadastrado, deve retornar exception`() {

        val pauta = slot<Pauta>()
        val pautaDTO = PautaDTO("Título da Pauta")

        every { publisher.publish(any()) } returns Unit
        every { pautaRepository.buscarPautaPorTitulo(pautaDTO.titulo) } returns Pauta(
            pautaId = UUID.randomUUID(),
            titulo = "Título da Pauta",
            votos = listOf(),
        )
        every { pautaRepository.salvar(capture(pauta)) } returns Unit

        assertThrows<PautaException> {
            pautaApplicationService.cadastrarPauta(pautaDTO)
        }.shouldHaveMessage("Pauta já foi cadastrada")

        verify(exactly = 0) { publisher.publish(any()) }
        verify(exactly = 1) { pautaRepository.buscarPautaPorTitulo(pautaDTO.titulo) }
        verify(exactly = 0) { pautaRepository.salvar(capture(pauta)) }

        confirmVerified(
            publisher,
            pautaRepository,
            sessaoRepository,
            votoApplicationService
        )
    }
}