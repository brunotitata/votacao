package br.com.votacao.application

import br.com.votacao.domain.DomainEventPublisher
import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.ResultadoCriadoEvent
import br.com.votacao.domain.pauta.Pauta
import br.com.votacao.domain.pauta.PautaRepository
import br.com.votacao.domain.sessao.SessaoRepository
import br.com.votacao.domain.sessao.SessaoVotacao
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.*
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

class ResultadoApplicationServiceTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()
    private val sessaoRepository = mockk<SessaoRepository>()
    private val pautaRepository = mockk<PautaRepository>()
    private val resultadoApplicationService = ResultadoApplicationService(
        sessaoRepository = sessaoRepository,
        pautaRepository = pautaRepository,
        objectMapper = jacksonObjectMapper()
    )

    override fun beforeSpec(spec: Spec) {
        DomainRegistry.defineDomainEventPublisher(publisher)
    }

    @BeforeEach
    fun setUp() = clearMocks(
        publisher,
        sessaoRepository,
        pautaRepository
    )

    @Test
    fun `verificar se a sessao esta encerrado e publica evento`() {

        val sessao = SessaoVotacao(
            sessaoId = UUID.fromString("1114325b-e64e-4fd2-9a31-aa01731287b4"),
            pautaId = UUID.fromString("9da8ad09-d373-4e12-b452-ca9e2341cae4"),
            duracaoPauta = 1,
            inicioPauta = LocalDateTime.of(2023, 7, 23, 10, 0, 0),
            fimPauta = LocalDateTime.of(2023, 7, 23, 10, 1, 0),
        )

        val pauta = Pauta(
            pautaId = UUID.fromString("9da8ad09-d373-4e12-b452-ca9e2341cae4"),
            titulo = "Você é a favor da taxação?",
        )

        every { publisher.publish(any()) } returns Unit
        every { sessaoRepository.buscarTodasSessaoVotacao() } returns listOf(sessao)
        every { pautaRepository.buscarPautaPorId(sessao.pautaId) } returns pauta
        every { sessaoRepository.salvar(any()) } returns Unit

        resultadoApplicationService.verificarSeASessaoEstaEncerrado()

        verify(exactly = 1) { sessaoRepository.buscarTodasSessaoVotacao() }
        verify(exactly = 1) { pautaRepository.buscarPautaPorId(sessao.pautaId) }
        verify(exactly = 1) { publisher.publish(any<ResultadoCriadoEvent>()) }
        verify(exactly = 1) { sessaoRepository.salvar(sessao.copy(encerrado = true)) }

        confirmVerified(
            sessaoRepository,
            pautaRepository,
            publisher
        )
    }

    @Test
    fun `quando pautaId for null ou nao existir deve lancar exception`() {

        val sessao = SessaoVotacao(
            sessaoId = UUID.fromString("1114325b-e64e-4fd2-9a31-aa01731287b4"),
            pautaId = UUID.fromString("9da8ad09-d373-4e12-b452-ca9e2341cae4"),
            duracaoPauta = 1,
            inicioPauta = LocalDateTime.of(2023, 7, 23, 10, 0, 0),
            fimPauta = LocalDateTime.of(2023, 7, 23, 10, 1, 0),
        )

        every { publisher.publish(any()) } returns Unit
        every { sessaoRepository.buscarTodasSessaoVotacao() } returns listOf(sessao)
        every { pautaRepository.buscarPautaPorId(sessao.pautaId) } returns null

        assertThrows<PautaException> {
            resultadoApplicationService.verificarSeASessaoEstaEncerrado()
        }.shouldHaveMessage("Pauta não encontrado com id: 9da8ad09-d373-4e12-b452-ca9e2341cae4")

        verify(exactly = 0) { publisher.publish(any<ResultadoCriadoEvent>()) }
        verify(exactly = 1) { sessaoRepository.buscarTodasSessaoVotacao() }
        verify(exactly = 1) { pautaRepository.buscarPautaPorId(sessao.pautaId) }
        verify(exactly = 0) { sessaoRepository.salvar(sessao.copy(encerrado = true)) }

        confirmVerified(
            sessaoRepository,
            pautaRepository,
            publisher
        )

    }

    @Test
    fun `verificar se o metodo lida corretamente quando a sessão ja esta encerrada`() {

        val sessao = SessaoVotacao(
            sessaoId = UUID.fromString("1114325b-e64e-4fd2-9a31-aa01731287b4"),
            pautaId = UUID.fromString("9da8ad09-d373-4e12-b452-ca9e2341cae4"),
            duracaoPauta = 1,
            inicioPauta = LocalDateTime.of(2023, 7, 23, 10, 0, 0),
            fimPauta = LocalDateTime.of(2023, 7, 23, 10, 1, 0),
        ).copy(
            encerrado = true
        )

        every { publisher.publish(any()) } returns Unit
        every { sessaoRepository.buscarTodasSessaoVotacao() } returns listOf(sessao)
        every { pautaRepository.buscarPautaPorId(sessao.pautaId) } returns null

        resultadoApplicationService.verificarSeASessaoEstaEncerrado()

        verify(exactly = 1) { sessaoRepository.buscarTodasSessaoVotacao() }
        verify(exactly = 0) { pautaRepository.buscarPautaPorId(any()) }
        verify(exactly = 0) { sessaoRepository.salvar(any()) }
        verify(exactly = 0) { publisher.publish(any()) }

        confirmVerified(
            sessaoRepository,
            pautaRepository,
            publisher
        )

    }

}