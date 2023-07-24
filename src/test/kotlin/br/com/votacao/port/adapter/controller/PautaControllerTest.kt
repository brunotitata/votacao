package br.com.votacao.port.adapter.controller

import br.com.votacao.application.PautaApplicationService
import br.com.votacao.port.adapters.controller.PautaController
import br.com.votacao.port.adapters.controller.dto.PautaDTO
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
    fun `deve criar uma pauta corretamente`() {

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

}