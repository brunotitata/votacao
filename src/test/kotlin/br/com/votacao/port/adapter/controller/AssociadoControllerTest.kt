package br.com.votacao.port.adapter.controller

import br.com.votacao.application.AssociadoApplicationService
import br.com.votacao.port.adapters.controller.AssociadoController
import br.com.votacao.port.adapters.controller.dto.AssociadoDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.AnnotationSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*
import java.util.UUID.*

@WebMvcTest(AssociadoController::class)
class AssociadoControllerTest : AnnotationSpec() {

    private val associadoApplicationService = mockk<AssociadoApplicationService>()

    @Autowired
    private lateinit var mvc: MockMvc

    @BeforeEach
    fun setup() {
        mvc = MockMvcBuilders
            .standaloneSetup(AssociadoController(associadoApplicationService))
            .build()
    }

    @Test
    fun `deve criar um associado corretamente`() {

        val associadoDTO = AssociadoDTO("João da Silva", "39624537097")
        val associadoId = randomUUID()

        every { associadoApplicationService.criarAssociado(associadoDTO) } returns associadoId

        mvc.perform(
            MockMvcRequestBuilders.post("/v1/associado")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jacksonObjectMapper().writeValueAsBytes(associadoDTO))
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "http://localhost/v1/associado/$associadoId"))

    }

}

