package br.com.votacao.port.adapters.controller

import br.com.votacao.application.SessaoVotacaoApplicationService
import br.com.votacao.port.adapters.controller.dto.ResultadoVotacaoDTO
import br.com.votacao.port.adapters.controller.dto.SessaoDTO
import br.com.votacao.port.adapters.controller.dto.VotoDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/v1/sessao")
@Tag(name = "Endpoint - Sessão")
class SessaoVotacaoController(
        private val sessaoVotacaoApplicationService: SessaoVotacaoApplicationService
) {

    @PostMapping
    @Operation(summary = "Endpoint responsavel por criar e gerenciar uma sessão de votação")
    fun abrirSessaoVotacao(@RequestBody sessaoVotacao: SessaoDTO): ResponseEntity<Void> {

        val sessaoId = sessaoVotacaoApplicationService.novaSessao(
                pautaId = sessaoVotacao.pautaId,
                duracao = sessaoVotacao.duracaoEmMinutos
        )

        return ResponseEntity.created(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{sessaoId}")
                        .buildAndExpand(sessaoId)
                        .toUri()
        ).build()
    }

    @PostMapping("/{sessaoId}/votar")
    @Operation(summary = "Endpoint responsavel por realizar a votação por uma pauta especifica")
    fun votar(@PathVariable sessaoId: UUID, @RequestBody voto: VotoDTO): ResponseEntity<Void> {

        val votoId = sessaoVotacaoApplicationService.realizarVoto(
            sessaoId = sessaoId,
            voto = voto
        )

        return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{votoId}")
                .buildAndExpand(votoId)
                .toUri()
        ).build()
    }

    @GetMapping("/{sessaoId}/resultado")
    @Operation(summary = "Endpoint responsavel por exibir o resultado de uma sessão de votação")
    fun exibirResultado(@PathVariable sessaoId: UUID): ResponseEntity<ResultadoVotacaoDTO> {
        return ResponseEntity.ok(
            sessaoVotacaoApplicationService.obterResultado(
                sessaoId = sessaoId
            )
        )
    }

}