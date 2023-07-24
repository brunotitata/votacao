package br.com.votacao.application

import br.com.votacao.domain.DomainEventPublisher
import br.com.votacao.domain.DomainExceptions.AssociadoException
import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.DomainRegistry
import br.com.votacao.domain.associado.Associado
import br.com.votacao.domain.associado.AssociadoRepository
import br.com.votacao.domain.associado.UserPort
import br.com.votacao.domain.pauta.Pauta
import br.com.votacao.domain.pauta.PautaRepository
import br.com.votacao.domain.voto.Voto
import br.com.votacao.domain.voto.VotoEnum
import br.com.votacao.domain.voto.VotoRealizadoEvent
import br.com.votacao.domain.voto.VotoRepository
import br.com.votacao.port.adapters.controller.dto.VotoDTO
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import java.util.*

class VotoApplicationServiceTest : AnnotationSpec() {

    private val publisher = mockk<DomainEventPublisher>()
    private val votoRepository = mockk<VotoRepository>()
    private val pautaRepository = mockk<PautaRepository>()
    private val associadoRepository = mockk<AssociadoRepository>()
    private val userPort = mockk<UserPort>()
    private val votoApplicationService = VotoApplicationService(
        votoRepository,
        pautaRepository,
        associadoRepository,
        userPort
    )

    override fun beforeSpec(spec: Spec) {
        DomainRegistry.defineDomainEventPublisher(publisher)
    }

    @BeforeEach
    fun setUp() = clearMocks(
        publisher,
        votoRepository,
        pautaRepository,
        associadoRepository,
        userPort
    )

    @Test
    fun `deve realizar um novo voto com sucesso`() {

        val voto = slot<Voto>()

        val pautaId = UUID.fromString("0e6efe69-820b-4b5c-bfd8-61fdc7edcae4")
        val pauta = Pauta(
            pautaId = pautaId,
            titulo = "Você é a favor da taxação?",
            votos = listOf(),
        )
        val associadoId = UUID.fromString("2cd69fc5-b59c-4d3d-918f-a13e724517b7")
        val associado = Associado(
            associadoId = associadoId,
            nome = "Eduardo Silva",
            cpf = "20031813003"
        )
        val votoDTO = VotoDTO(voto = VotoEnum.NAO, associadoId = associadoId)

        every { publisher.publish(any()) } returns Unit
        every { pautaRepository.buscarPautaPorId(pautaId) } returns pauta
        every { associadoRepository.buscarAssociado(associadoId) } returns associado
        every { userPort.validarCpf(associado.cpf) } returns "ABLE_TO_VOTE"
        every { votoRepository.validar(associado, pauta) } returns null
        every { votoRepository.salvar(capture(voto)) } returns Unit

        val novoVoto = votoApplicationService.novoVoto(
            pautaId = pautaId,
            voto = votoDTO
        )

        voto.captured.voto.shouldBe(VotoEnum.NAO)
        voto.captured.pauta.shouldBe(pauta)
        voto.captured.associado.shouldBe(associado)
        voto.captured.votoId.shouldBe(novoVoto)

        verify(exactly = 1) { publisher.publish(VotoRealizadoEvent(votoId = novoVoto)) }
        verify(exactly = 1) { pautaRepository.buscarPautaPorId(pautaId) }
        verify(exactly = 1) { associadoRepository.buscarAssociado(associadoId) }
        verify(exactly = 1) { userPort.validarCpf(associado.cpf) }
        verify(exactly = 1) { votoRepository.validar(associado, pauta) }
        verify(exactly = 1) { votoRepository.salvar(voto.captured) }

        confirmVerified(
            publisher,
            pautaRepository,
            associadoRepository,
            userPort,
            votoRepository
        )

    }

    @Test
    fun `se nao encontrar pauta, nao deve realizar o voto`() {

        val pautaId = UUID.fromString("0e6efe69-820b-4b5c-bfd8-61fdc7edcae4")
        val associadoId = UUID.fromString("2cd69fc5-b59c-4d3d-918f-a13e724517b7")

        every { publisher.publish(any()) } returns Unit
        every { pautaRepository.buscarPautaPorId(pautaId) }.throws(
            PautaException(
                msg = "Pauta não encontrado",
                httpStatus = HttpStatus.NOT_FOUND
            )
        )

        assertThrows<PautaException> {
            votoApplicationService.novoVoto(
                pautaId = pautaId,
                voto = VotoDTO(voto = VotoEnum.NAO, associadoId = associadoId),
            )
        }.shouldBe(
            PautaException(
                msg = "Pauta não encontrado",
                httpStatus = HttpStatus.NOT_FOUND
            )
        )

        verify(exactly = 0) { publisher.publish(any()) }
        verify(exactly = 1) { pautaRepository.buscarPautaPorId(pautaId) }
        verify(exactly = 0) { associadoRepository.buscarAssociado(associadoId) }
        verify(exactly = 0) { userPort.validarCpf(any()) }
        verify(exactly = 0) { votoRepository.validar(any(), any()) }
        verify(exactly = 0) { votoRepository.salvar(any()) }

        confirmVerified(
            publisher,
            pautaRepository,
            associadoRepository,
            userPort,
            votoRepository
        )

    }

    @Test
    fun `se nao encontrar associado, nao deve realizar o voto`() {

        val pautaId = UUID.fromString("0e6efe69-820b-4b5c-bfd8-61fdc7edcae4")
        val pauta = Pauta(
            pautaId = pautaId,
            titulo = "Você é a favor da taxação?",
            votos = listOf(),
        )
        val associadoId = UUID.fromString("2cd69fc5-b59c-4d3d-918f-a13e724517b7")

        every { publisher.publish(any()) } returns Unit
        every { pautaRepository.buscarPautaPorId(pautaId) } returns pauta
        every { associadoRepository.buscarAssociado(associadoId) }.throws(
            AssociadoException(
                msg = "Associado não encontrado",
                httpStatus = HttpStatus.NOT_FOUND
            )
        )

        assertThrows<AssociadoException> {
            votoApplicationService.novoVoto(
                pautaId = pautaId,
                voto = VotoDTO(voto = VotoEnum.NAO, associadoId = associadoId),
            )
        }.shouldBe(
            AssociadoException(
                msg = "Associado não encontrado",
                httpStatus = HttpStatus.NOT_FOUND
            )
        )

        verify(exactly = 0) { publisher.publish(any()) }
        verify(exactly = 1) { pautaRepository.buscarPautaPorId(pautaId) }
        verify(exactly = 1) { associadoRepository.buscarAssociado(associadoId) }
        verify(exactly = 0) { userPort.validarCpf(any()) }
        verify(exactly = 0) { votoRepository.validar(any(), any()) }
        verify(exactly = 0) { votoRepository.salvar(any()) }

        confirmVerified(
            publisher,
            pautaRepository,
            associadoRepository,
            userPort,
            votoRepository
        )

    }

    @Test
    fun `caso o associado nao for habito para votar, deve retornar exception`() {

        val pautaId = UUID.fromString("0e6efe69-820b-4b5c-bfd8-61fdc7edcae4")
        val pauta = Pauta(
            pautaId = pautaId,
            titulo = "Você é a favor da taxação?",
            votos = listOf(),
        )
        val associadoId = UUID.fromString("2cd69fc5-b59c-4d3d-918f-a13e724517b7")
        val associado = Associado(
            associadoId = associadoId,
            nome = "Eduardo Silva",
            cpf = "20031813003"
        )

        every { publisher.publish(any()) } returns Unit
        every { pautaRepository.buscarPautaPorId(pautaId) } returns pauta
        every { associadoRepository.buscarAssociado(associadoId) } returns associado
        every { userPort.validarCpf(associado.cpf) } returns "UNABLE_TO_VOTE"

        assertThrows<AssociadoException> {
            votoApplicationService.novoVoto(
                pautaId = pautaId,
                voto = VotoDTO(voto = VotoEnum.NAO, associadoId = associadoId),
            )
        }.shouldBe(
            AssociadoException(
                msg = "Associado não esta permitido para votar",
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY
            )
        )

        verify(exactly = 0) { publisher.publish(any()) }
        verify(exactly = 1) { pautaRepository.buscarPautaPorId(pautaId) }
        verify(exactly = 1) { associadoRepository.buscarAssociado(associadoId) }
        verify(exactly = 1) { userPort.validarCpf(associado.cpf) }
        verify(exactly = 0) { votoRepository.validar(any(), any()) }
        verify(exactly = 0) { votoRepository.salvar(any()) }

        confirmVerified(
            publisher,
            pautaRepository,
            associadoRepository,
            userPort,
            votoRepository
        )

    }


}