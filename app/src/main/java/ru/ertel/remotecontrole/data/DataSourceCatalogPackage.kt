package ru.ertel.remotecontrole.data

import android.util.Log
import ru.ertel.remotecontrole.model.CatalogPackage

class DataSourceCatalogPackage {
    private val catalogPackage: CatalogPackage = CatalogPackage(
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )

    private lateinit var numberTokenKontur: String

    fun setMessagePassageCard(message: String) {
//        numberTokenKontur = numberKontur.substringAfterLast("*")
//        if (getAnswerLicense(message)) {
            if (getValidMessage(message)) {
                catalogPackage.solution = getSolution(message)
                catalogPackage.capt = getCapt(message)
                catalogPackage.numberOfPasses = getNumberOfPasses(message)
            } else {
                catalogPackage.solution = "Данные не найдены"
                catalogPackage.capt = "Данные не найдены"
                catalogPackage.numberOfPasses = "Данные не найдены"
            }
//        } else {
//            catalogPackage.solution = "Пиратская копия"
//            catalogPackage.capt = "Пиратская копия"
//            catalogPackage.numberOfPasses = "Пиратская копия"
//        }
    }

    fun setInfoCard(message: String) {
//        numberTokenKontur = numberKontur.substringAfterLast("*")
//        if (getAnswerLicense(message)) {
            if (getValidInfoCard(message)) {
                catalogPackage.passageBalance = getBalance(message)
            } else {
                catalogPackage.passageBalance = "Данные не найдены"
            }
//        } else {
//            catalogPackage.passageBalance = "Пиратская копия"
//        }
    }

    fun setAnswerDevice(message: String) {
//        numberTokenKontur = numberKontur.substringAfterLast("*")
//        if (getAnswerLicense(message)) {
        Log.d("TAG", message)
            if (getValidMessageAnswer(message)) {
                catalogPackage.deviceName = getDeviceName(message)
                catalogPackage.datePasses = getDatePasses(message)
            } else {
                catalogPackage.deviceName = "Данные не найдены"
                catalogPackage.datePasses = "Данные не найдены"
            }
//        } else {
//            catalogPackage.deviceName = "Пиратская копия"
//            catalogPackage.datePasses = "Пиратская копия"
//        }
    }

    fun getPassageCard(): CatalogPackage {
        return catalogPackage
    }

    private fun getAnswerLicense(message: String): Boolean {
        // Проверка на лицензию, указывается номер лицензии контура,
        // при попытке использовать приложение на другом сервер,
        // поступит сообщение о использовании пиратской версии
        return message.contains("<attribute name=\"license\"  value=\"$numberTokenKontur\" />")
    }

    private fun getValidMessage(message: String): Boolean {
        return message.contains("<Message>", ignoreCase = true)
    }

    private fun getValidInfoCard(message: String): Boolean {
        return message.contains("<currency name=\"RUB\"  comment=\"Российский рубль\"", ignoreCase = true)
    }

    private fun getValidMessageAnswer(message: String): Boolean {
        return message.contains("<msg", ignoreCase = true)
    }

    private fun getDeviceName(message: String): String {
        var result = message.substringBefore("\" Unit=")
        result = result.substringAfter("<State Name=\"")
        return result
    }

    private fun getSolution(message: String): String {
        var result = message.substringAfter("<Reason>")
        result = result.substringBefore("</Reason>")
        return result
    }

    private fun getCapt(message: String): String {
        var result = ""
        if (message.contains("cpRuleUse", ignoreCase = true)) {
            result =
                message.substringAfter("<Parameter name=\"cpRuleUse\" type=\"String\" values=\"")
            result = result.substringBefore("\">")
            result = result.replace("capt=", "")
        } else {
            result = "Данные не найдены"
        }
        return result
    }

    private fun getNumberOfPasses(message: String): String {
        if (message.contains("Остаток", ignoreCase = true)) {
            var result = message.substringAfter("Остаток=")
            result = result.substringBefore(",Стоимость=")
            return result
        } else {
            return "Данные не найдены"
        }
    }

    private fun getDatePasses(message: String): String {
        var result = message.substringAfter("time=\"")
        result = result.substringBefore("\" text=")
        return result
    }

    private fun getBalance(message: String): String {
        if (message.contains("<currency name=\"RUB\"", ignoreCase = true)) {
            var result = message.substringAfter("\"Российский рубль\"  value=\"")
            result = result.substringBefore("\" />")
            return "$result(RUB)"
        } else {
            return "Данные не найдены"
        }
    }
}