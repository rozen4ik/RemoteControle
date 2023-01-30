package ru.ertel.remotecontrole.data

import ru.ertel.remotecontrole.model.License

class DataSourceLicense {
    private val license: License = License(
            ""
    )

    private lateinit var numberTokenKontur: String

    fun setMessageLicense(message: String, numberKontur: String) {
        numberTokenKontur = numberKontur.substringAfterLast("*")
        if (getAnswerLicense(message)) {
                license.solution = "200"
        } else {
            license.solution = "Пиратская копия"
        }
    }

    fun getSolution(): String {
        return license.solution
    }

    private fun getAnswerLicense(message: String): Boolean {
        // Проверка на лицензию, указывается номер лицензии контура,
        // при попытке использовать приложение на другом сервер,
        // поступит сообщение о использовании пиратской версии
        return message.contains("<attribute name=\"license\"  value=\"$numberTokenKontur\" />")
    }
}