package ru.ertel.remotecontrole

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import ru.ertel.remotecontrole.StartActivity.Companion.SAVE_TOKEN
import ru.ertel.remotecontrole.controller.KonturController
import ru.ertel.remotecontrole.data.DataSourceLicense
import ru.ertel.remotecontrole.data.DataSourceToken
import java.net.ConnectException
import java.text.SimpleDateFormat
import java.util.*


class ActivateFragment : Fragment() {
    private lateinit var editTextToken: EditText
    private lateinit var editTextIdent: EditText
    private lateinit var editTextIp: EditText
    private lateinit var editTextPort: EditText
    private lateinit var buttonToken: Button
    private lateinit var settings: SharedPreferences
    private lateinit var daySet: SharedPreferences
    private lateinit var endDate: SharedPreferences
    private lateinit var identSet: SharedPreferences
    private lateinit var urlIpPort: SharedPreferences
    private lateinit var urlToken: String
    private lateinit var dataSourceToken: DataSourceToken
    private lateinit var dataSourceLicense: DataSourceLicense
    private lateinit var konturController: KonturController
    private var negativeAnswerFragment = NegativeAnswerFragment
    private lateinit var demoFragment: DemoFragment
    private var answerCheckMessage = ""
    private var statusInternet = ""
    private val checkMessage = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
            "<spd-xml-api>" +
            "<request version=\"1.1\" ruid=\"739F9F2B-AEDD-4D94-90FF-EB59A9A1FCF5\">" +
            "</request>" +
            "</spd-xml-api>"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dataSourceToken = DataSourceToken()
        dataSourceLicense = DataSourceLicense()
        konturController = KonturController()
        return inflater.inflate(R.layout.fragment_activate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextToken = view.findViewById(R.id.editTextToken)
        editTextIdent = view.findViewById(R.id.editTextIdent)
        editTextIp = view.findViewById(R.id.editTextIp)
        editTextPort = view.findViewById(R.id.editTextPort)
        buttonToken = view.findViewById(R.id.buttonToken)

        buttonToken.setOnClickListener {

            activity?.runOnUiThread {
                Toast.makeText(requireContext(), "Проверяется лицензия приложения", Toast.LENGTH_LONG).show()
            }

            if (editTextToken.text.toString() == "" || editTextIdent.text.toString() == ""
                || editTextPort.text.toString() == "" || editTextIp.text.toString() == ""
            ) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_LONG).show()
            } else {
                urlToken =
                    "http://license.u1733524.isp.regruhosting.ru/api/tokens/${editTextToken.text}"

                infoToken(konturController, dataSourceToken, urlToken)

                if (dataSourceToken.getToken().nameToken == editTextToken.text.toString()) {
                    settings =
                        requireActivity().getSharedPreferences(
                            "konturToken",
                            AppCompatActivity.MODE_PRIVATE
                        )
                    daySet =
                        requireActivity().getSharedPreferences(
                            "endDate",
                            AppCompatActivity.MODE_PRIVATE
                        )
                    identSet =
                        requireActivity().getSharedPreferences(
                            "ident",
                            AppCompatActivity.MODE_PRIVATE
                        )
                    urlIpPort =
                        requireActivity().getSharedPreferences(
                            "url",
                            AppCompatActivity.MODE_PRIVATE
                        )
                    endDate =
                        requireActivity().getSharedPreferences(
                            "date",
                            AppCompatActivity.MODE_PRIVATE
                        )

                    val formDate = SimpleDateFormat("yyyy-MM-dd")
                    val getEndDayOfYear = SimpleDateFormat("D")
                    val currentDate = formDate.parse(dataSourceToken.getToken().endDate)
                    val endDayOfYear = getEndDayOfYear.format(currentDate)
                    val endYear = "${dataSourceToken.getToken().endDate[0]}" +
                            "${dataSourceToken.getToken().endDate[1]}" +
                            "${dataSourceToken.getToken().endDate[2]}" +
                            "${dataSourceToken.getToken().endDate[3]}"
                    val dateNow = Calendar.getInstance()
                    val dateDayOfYearNow = dateNow.get(Calendar.DAY_OF_YEAR)
                    val dateYearNow = dateNow.get(Calendar.YEAR)

                    if (dateYearNow < endYear.toInt()) {
                        val saveEndDateToken: SharedPreferences.Editor = daySet.edit()
                        saveEndDateToken.putString(SAVE_TOKEN, "$endDayOfYear/$endYear")
                        saveEndDateToken.commit()

                        val numberKontur =
                            dataSourceToken.getToken().nameToken.substringAfterLast("*")
                        val saveKonturToken: SharedPreferences.Editor = settings.edit()
                        saveKonturToken.putString(SAVE_TOKEN, numberKontur)
                        saveKonturToken.commit()

                        val saveIdent: SharedPreferences.Editor = identSet.edit()
                        saveIdent.putString(SAVE_TOKEN, editTextIdent.text.toString())
                        saveIdent.commit()

                        val saveUrlIpPort: SharedPreferences.Editor = urlIpPort.edit()
                        saveUrlIpPort.putString(
                            SAVE_TOKEN,
                            "${editTextIp.text}:${editTextPort.text}"
                        )
                        saveUrlIpPort.commit()

                        val saveEndDate: SharedPreferences.Editor = endDate.edit()
                        saveEndDate.putString(SAVE_TOKEN, dataSourceToken.getToken().endDate)
                        saveEndDate.commit()

                        val settingsURL: SharedPreferences = requireActivity().getSharedPreferences(
                            "url", AppCompatActivity.MODE_PRIVATE
                        )
                        val bodyURL = settingsURL.getString(SAVE_TOKEN, "no").toString()

                        val settingsIdent: SharedPreferences =
                            requireActivity().getSharedPreferences(
                                "ident",
                                AppCompatActivity.MODE_PRIVATE
                            )
                        val bodyIdent = settingsIdent.getString(SAVE_TOKEN, "no").toString()

                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), "Проверяется соединение с сервером", Toast.LENGTH_LONG).show()
                        }

                        checkLicense(
                            konturController,
                            dataSourceLicense,
                            "http://$bodyURL/spd-xml-api",
                            bodyIdent,
                            checkMessage
                        )

                        if (statusInternet == "Неправильно указан порт и ip") {
                            Toast.makeText(
                                requireContext(),
                                "Неправильно указан порт и ip",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(activity, SettingsActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(activity, MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                    } else if (dateYearNow == endYear.toInt()) {
                        if ((dateDayOfYearNow < endDayOfYear.toInt()) && (dateYearNow <= endYear.toInt())) {
                            val saveEndDateToken: SharedPreferences.Editor = daySet.edit()
                            saveEndDateToken.putString(SAVE_TOKEN, "$endDayOfYear/$endYear")
                            saveEndDateToken.commit()

                            val numberKontur =
                                dataSourceToken.getToken().nameToken.substringAfterLast("*")
                            val saveKonturToken: SharedPreferences.Editor = settings.edit()
                            saveKonturToken.putString(SAVE_TOKEN, numberKontur)
                            saveKonturToken.commit()

                            val saveIdent: SharedPreferences.Editor = identSet.edit()
                            saveIdent.putString(SAVE_TOKEN, editTextIdent.text.toString())
                            saveIdent.commit()

                            val saveUrlIpPort: SharedPreferences.Editor = urlIpPort.edit()
                            saveUrlIpPort.putString(
                                SAVE_TOKEN,
                                "${editTextIp.text}:${editTextPort.text}"
                            )
                            saveUrlIpPort.commit()

                            val saveEndDate: SharedPreferences.Editor = endDate.edit()
                            saveEndDate.putString(SAVE_TOKEN, dataSourceToken.getToken().endDate)
                            saveEndDate.commit()

                            val settingsURL: SharedPreferences =
                                requireActivity().getSharedPreferences(
                                    "url", AppCompatActivity.MODE_PRIVATE
                                )
                            val bodyURL = settingsURL.getString(SAVE_TOKEN, "no").toString()

                            val settingsIdent: SharedPreferences =
                                requireActivity().getSharedPreferences(
                                    "ident",
                                    AppCompatActivity.MODE_PRIVATE
                                )
                            val bodyIdent = settingsIdent.getString(SAVE_TOKEN, "no").toString()

                            activity?.runOnUiThread {
                                Toast.makeText(requireContext(), "Проверяется соединение с сервером", Toast.LENGTH_LONG).show()
                            }

                            checkLicense(
                                konturController,
                                dataSourceLicense,
                                "http://$bodyURL/spd-xml-api",
                                bodyIdent,
                                checkMessage
                            )

                            if (statusInternet == "Неправильно указан порт и ip") {
                                Toast.makeText(
                                    requireContext(),
                                    "Неправильно указан порт и ip",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(activity, SettingsActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(activity, MainActivity::class.java)
                                startActivity(intent)
                                activity?.finish()
                            }
                        } else {
                            demoFragment = DemoFragment()
                            openFragment(demoFragment)
                        }
                    } else {
                        demoFragment = DemoFragment()
                        openFragment(demoFragment)
                    }
                } else {
                    openFragment(negativeAnswerFragment.newInstance(dataSourceToken.getAnswer()))
                }
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.replace(R.id.placeDateFragments, fragment)
            ?.commit()
    }

    private fun checkLicense(
        konturController: KonturController,
        dataSourceLicense: DataSourceLicense,
        url: String,
        bodyToken: String,
        checkMessage: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("CheclLicense")) {
                try {
                    answerCheckMessage = konturController.requestPOST(url, checkMessage)
                    if (answerCheckMessage.contains("<spd-xml-api>")) {
                        dataSourceLicense.setMessageLicense(answerCheckMessage, bodyToken)
                    } else {
                        statusInternet = answerCheckMessage
                    }
                } catch (e: ConnectException) {
                    statusInternet = "Неправильно указан порт и ip"
                    e.printStackTrace()
                }
            }
        }
    }

    private fun infoToken(
        konturController: KonturController,
        dataSourceToken: DataSourceToken,
        url: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("MyOwnThread")) {
                try {
                    when (val message = konturController.requestGetToken(url)) {
                        "\"Превышено количество активаций\"" -> {
                            dataSourceToken.setAnswer(message)
                        }
                        "\"Отказано в активации, ключ не действительный\"" -> {
                            dataSourceToken.setAnswer(message)
                        }
                        else -> {
                            dataSourceToken.setAnswer("Одобрено")
                            dataSourceToken.setToken(message)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = ActivateFragment()
    }
}