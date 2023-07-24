package br.com.votacao.application

import br.com.votacao.domain.DomainEventPublisher
import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainExceptions.SessaoException
import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.pauta.PautaRepository
import br.com.votacao.domain.sessao.SessaoRepository
import br.com.votacao.domain.sessao.SessaoVotacao
import br.com.votacao.domain.sessao.SessaoVotacaoCriadoEvent
import br.com.votacao.domain.voto.VotoEnum
import br.com.votacao.port.adapters.controller.dto.VotoDTO
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.*

class SessaoVotacaoApplicationServiceTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()
    private val sessaoRepository = mockk<SessaoRepository>()
    private val pautaRepository = mockk<PautaRepository>()
    private val votoApplicationService = mockk<VotoApplicationService>()
    private val sessaoApplicationService = SessaoVotacaoApplicationService(
        sessaoRepository,
        pautaRepository,
        votoApplicationService
    )

    override fun beforeSpec(spec: Spec) {
        DomainRegistry.defineDomainEventPublisher(publisher)
    }

    @BeforeEach
    fun setUp() = clearMocks(
        publisher,
        sessaoRepository,
        pautaRepository,
        votoApplicationService
    )

    @Test
    fun `deve criar uma nova sessao com sucesso`() {

        val sessao = slot<SessaoVotacao>()
        val pautaId = UUID.fromString("0e6efe69-820b-4b5c-bfd8-61fdc7edcae4")

        every { publisher.publish(any()) } returns Unit
        every { sessaoRepository.buscarSessaoPauta(pautaId) } returns null
        every { sessaoRepository.salvar(capture(sessao)) } returns Unit

        val novaSessao = sessaoApplicationService.novaSessao(pautaId, 10)

        sessao.captured.sessaoId.shouldNotBeNull()
        sessao.captured.pautaId.shouldBe(pautaId)
        sessao.captured.duracaoPauta.shouldBe(10)
        sessao.captured.inicioPauta.shouldNotBeNull()
        sessao.captured.fimPauta.shouldNotBeNull()
        sessao.captured.encerrado.shouldBe(false)

        verify { publisher.publish(SessaoVotacaoCriadoEvent(sessaoId = novaSessao)) }
        verify { sessaoRepository.buscarSessaoPauta(pautaId) }
        verify { sessaoRepository.salvar(sessao.captured) }

        confirmVerified(
            publisher,
            sessaoRepository,
            pautaRepository,
        )
    }

    @Test
    fun `caso existir uma sessao iniciada para a pauta, deve retornar exception`() {

        val pautaId = UUID.fromString("0e6efe69-820b-4b5c-bfd8-61fdc7edcae4")
        val sessaoId = UUID.fromString("e939ca02-3620-4dc4-b902-9cd432833dc5")
        val sessaoVotacao = SessaoVotacao(
            sessaoId = sessaoId,
            pautaId = pautaId,
            duracaoPauta = 5,
            inicioPauta = LocalDateTime.of(2023, 7, 23, 10, 0, 0),
            fimPauta = LocalDateTime.of(2023, 7, 23, 10, 1, 0),
            encerrado = false,
        )

        every { publisher.publish(any()) } returns Unit
        every { sessaoRepository.buscarSessaoPauta(pautaId) } returns sessaoVotacao

        assertThrows<PautaException> {
            sessaoApplicationService.novaSessao(pautaId, 10)
        }.shouldBe(
            PautaException(
                msg = "Pauta já iniciada com id: $pautaId",
                httpStatus = HttpStatus.CONFLICT
            )
        )

        verify(exactly = 0) { publisher.publish(any()) }
        verify(exactly = 1) { sessaoRepository.buscarSessaoPauta(pautaId) }
        verify(exactly = 0) { sessaoRepository.salvar(any()) }

        confirmVerified(
            publisher,
            sessaoRepository,
            pautaRepository,
        )
    }

    @Test
    fun `deve realizar o voto com sucesso`() {

        val pautaId = UUID.fromString("0e6efe69-820b-4b5c-bfd8-61fdc7edcae4")
        val sessaoId = UUID.fromString("e939ca02-3620-4dc4-b902-9cd432833dc5")
        val sessaoVotacao = SessaoVotacao(
            sessaoId = sessaoId,
            pautaId = pautaId,
            duracaoPauta = 5,
            inicioPauta = LocalDateTime.now(),
            fimPauta = LocalDateTime.now().plusMinutes(5),
            encerrado = false,
        )
        val associadoId = UUID.fromString("2cd69fc5-b59c-4d3d-918f-a13e724517b7")
        val votoDTO = VotoDTO(voto = VotoEnum.NAO, associadoId = associadoId)
        val votoId = UUID.fromString("3781c273-ff9b-4851-87d8-263343678b43")

        every { publisher.publish(any()) } returns Unit
        every { sessaoRepository.buscarSessao(sessaoId) } returns sessaoVotacao
        every { votoApplicationService.novoVoto(pautaId, votoDTO) } returns votoId

        val novoVoto = sessaoApplicationService.realizarVoto(sessaoId, votoDTO)

        novoVoto.shouldBe(votoId)

        verify { sessaoRepository.buscarSessao(sessaoId) }
        verify { votoApplicationService.novoVoto(pautaId, votoDTO) }

        confirmVerified(
            publisher,
            sessaoRepository,
            pautaRepository,
        )
    }

    @Test
    fun `quando tentar realizar um voto sem sessao em andamento, deve retornar exception`() {

        val pautaId = UUID.fromString("0e6efe69-820b-4b5c-bfd8-61fdc7edcae4")
        val sessaoId = UUID.fromString("e939ca02-3620-4dc4-b902-9cd432833dc5")
        val associadoId = UUID.fromString("2cd69fc5-b59c-4d3d-918f-a13e724517b7")
        val votoDTO = VotoDTO(voto = VotoEnum.NAO, associadoId = associadoId)

        every { publisher.publish(any()) } returns Unit
        every { sessaoRepository.buscarSessao(sessaoId) }.throws(
            SessaoException(
                msg = "Sessão não encontrado ou encerrada!",
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
            )
        )

        assertThrows<SessaoException> {
            sessaoApplicationService.realizarVoto(sessaoId, votoDTO)
        }.shouldBe(
            SessaoException(
                msg = "Sessão não encontrado ou encerrada!",
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
            )
        )

        verify(exactly = 1) { sessaoRepository.buscarSessao(sessaoId) }
        verify(exactly = 0) { votoApplicationService.novoVoto(pautaId, votoDTO) }

        confirmVerified(
            publisher,
            sessaoRepository,
            pautaRepository,
        )
    }
}