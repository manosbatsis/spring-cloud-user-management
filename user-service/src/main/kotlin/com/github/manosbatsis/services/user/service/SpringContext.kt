package com.github.manosbatsis.services.user.service

import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class SpringContext : ApplicationContextAware {
    override fun setApplicationContext(context: ApplicationContext) {
        // store ApplicationContext reference to access required beans later on
        setContext(context)
    }

    companion object {
        private lateinit var context: ApplicationContext

        /**
         * Returns the Spring managed bean instance of the given class type if it exists. Returns null
         * otherwise.
         *
         * @param beanClass
         * @return
         */
        fun <T : Any?> getBean(beanClass: Class<T>): T {
            return context.getBean(beanClass)
        }

        /**
         * Private method context setting (better practice for setting a static field in a bean instance
         * - see comments of this article for more info).
         */
        @Synchronized
        private fun setContext(context: ApplicationContext) {
            Companion.context = context
        }
    }
}
