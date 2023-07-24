package br.com.votacao.port.adapter.controller

import br.com.votacao.application.PautaApplicationService
import br.com.votacao.domain.voto.VotoEnum
import br.com.votacao.port.adapters.controller.PautaController
import br.com.votacao.port.adapters.controller.dto.PautaDTO
import br.com.votacao.port.adapters.controller.dto.VotoDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*
import java.util.UUID.randomUUID

@WebMvcTest(PautaController::class)
class PautaControllerTest : AnnotationSpec() {

    private val pautaApplicationService = mockk<PautaApplicationService>()

    @Autowired
    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setup() {
        mvc = MockMvcBuilders.standaloneSetup(PautaController(pautaApplicationService)).build()
    }

    @Test
    fun `criarPauta pauta criada corretamente`() {

        val pautaDTO = PautaDTO("Assunto da Pauta")
        val pautaId = randomUUID()

        every { pautaApplicationService.cadastrarPauta(pautaDTO) } returns pautaId

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/pauta")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jacksonObjectMapper().writeValueAsBytes(pautaDTO))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().exists("Location"))
            .andExpect(MockMvcResultMatchers.header().string("Location", "http://localhost/v1/pauta/$pautaId"))

    }

    @Test
    fun `votar pauta com voto criado corretamente`() {

        val pautaId = UUID.fromString("9da8ad09-d373-4e12-b452-ca9e2341cae4")
        val associadoId = UUID.fromString("5a660ae4-1f48-4647-95a3-e4b0bf5d83f9")
        val votoDTO = VotoDTO(VotoEnum.SIM)
        val votoId = UUID.fromString("da7e6313-9614-43d5-9f98-88ad58b0d686")

        every {
            pautaApplicationService.votar(
                pautaId,
                votoDTO,
                associadoId
            )
        } returns votoId

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/pauta/$pautaId/associado/$associadoId/votar")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jacksonObjectMapper().writeValueAsBytes(votoDTO))
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().exists("Location"))
            .andExpect(
                MockMvcResultMatchers.header()
                    .string("Location", "http://localhost/v1/pauta/$pautaId/associado/$associadoId/votar/$votoId")
            )

    }
}