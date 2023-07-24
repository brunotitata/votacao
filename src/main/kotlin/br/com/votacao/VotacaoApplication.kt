package br.com.votacao

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class VotacaoApplication

fun main(args: Array<String>) {
	runApplication<VotacaoApplication>(*args)
}
