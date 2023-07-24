package br.com.votacao.port.adapter.controller

import br.com.votacao.application.SessaoVotacaoApplicationService
import br.com.votacao.domain.DomainExceptions.PautaException
import br.com.votacao.domain.voto.VotoEnum
import br.com.votacao.port.adapters.controller.ExceptionHandler
import br.com.votacao.port.adapters.controller.SessaoVotacaoController
import br.com.votacao.port.adapters.controller.dto.SessaoDTO
import br.com.votacao.port.adapters.controller.dto.VotoDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

@WebMvcTest(SessaoVotacaoController::class)
class SessaoVotacaoControllerTest : AnnotationSpec() {

    private val sessaoVotacaoApplicationService = mockk<SessaoVotacaoApplicationService>()

    @Autowired
    private lateinit var mvc: MockMvc

    @AnnotationSpec.BeforeEach
    fun setup() {
        mvc = MockMvcBuilders.standaloneSetup(SessaoVotacaoController(sessaoVotacaoApplicationService))
            .setControllerAdvice(ExceptionHandler())
            .build()
    }

    @Test
    fun `deve abrir uma nova sessao de votacao`() {

        val pautaId = UUID.randomUUID()
        val sessaoId = UUID.randomUUID()
        val sessaoVotacao = SessaoDTO(pautaId = pautaId, duracaoEmMinutos = 10)

        coEvery { sessaoVotacaoApplicationService.novaSessao(pautaId, 10) } returns sessaoId

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/sessao")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jacksonObjectMapper().writeValueAsBytes(sessaoVotacao))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().exists("Location"))
            .andExpect(MockMvcResultMatchers.header().string("Location", "http://localhost/v1/sessao/$sessaoId"))
    }

    @Test
    fun `nao deve abrir uma nova sessao de votacao com pauta ja cadastrada`() {

        val pautaId = UUID.randomUUID()
        val sessaoVotacao = SessaoDTO(pautaId = pautaId, duracaoEmMinutos = 10)

        coEvery {
            sessaoVotacaoApplicationService.novaSessao(
                pautaId = sessaoVotacao.pautaId,
                duracao = sessaoVotacao.duracaoEmMinutos
            )
        } throws PautaException(
            msg = "Pauta já iniciada com id: $pautaId",
            httpStatus = HttpStatus.CONFLICT
        )

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/sessao")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jacksonObjectMapper().writeValueAsBytes(sessaoVotacao))
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").value("Pauta já iniciada com id: $pautaId"))
            .andReturn()
    }

    @Test
    fun `votar pauta com voto criado corretamente`() {

        val sessaoId = UUID.fromString("9da8ad09-d373-4e12-b452-ca9e2341cae4")
        val associadoId = UUID.fromString("5a660ae4-1f48-4647-95a3-e4b0bf5d83f9")
        val votoDTO = VotoDTO(voto = VotoEnum.SIM, associadoId = associadoId)
        val votoId = UUID.fromString("da7e6313-9614-43d5-9f98-88ad58b0d686")

        every {
            sessaoVotacaoApplicationService.realizarVoto(
                sessaoId = sessaoId,
                voto = votoDTO
            )
        } returns votoId

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/sessao/$sessaoId/votar")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jacksonObjectMapper().writeValueAsBytes(votoDTO))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().exists("Location"))
            .andExpect(
                MockMvcResultMatchers.header()
                    .string("Location", "http://localhost/v1/sessao/$sessaoId/votar/$votoId")
            )

    }

}