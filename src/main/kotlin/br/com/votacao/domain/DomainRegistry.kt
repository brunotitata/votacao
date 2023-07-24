package br.com.votacao.domain

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class DomainRegistry : ApplicationContextAware {

    companion object {

        private var context: ApplicationContext? = null
        private var domainEventPublisher: DomainEventPublisher? = null

        @JvmStatic
        fun domainEventPublisher() = domainEventPublisher
                ?: throw RuntimeException("Domain Event Publisher não foi definido para Dominio Registro")

        @JvmStatic
        fun defineDomainEventPublisher(publisher: DomainEventPublisher) {
            domainEventPublisher = publisher
        }

        @JvmStatic
        fun defineApplicationContext(applicationContext: ApplicationContext) {
            context = applicationContext
            val app = applicationContext()?.getBean(DomainEventPublisher::class.java)
                    ?: throw RuntimeException("Application Context não foi definido para Dominio Registro")
            defineDomainEventPublisher(app)
        }

        @JvmStatic
        fun applicationContext(): ApplicationContext? = context

    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        defineApplicationContext(applicationContext)
    }

}