package br.com.votacao.port.adapters.service

import br.com.votacao.domain.associado.UserPort
import org.springframework.stereotype.Component

@Component
class UserServiceAdapter : UserPort {

    override fun validarCpf(cpf: String): String {
        // API Quebrada -> https://user-info.herokuapp.com/users/{cpf}
        return "ABLE_TO_VOTE"
    }
}