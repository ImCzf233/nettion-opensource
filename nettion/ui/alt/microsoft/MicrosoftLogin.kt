package nettion.ui.alt.microsoft

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.sun.net.httpserver.HttpServer
import org.apache.logging.log4j.LogManager
import org.lwjgl.Sys
import java.io.Closeable
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * 微软登录
 * 参考的文章:
 * https://zhuanlan.zhihu.com/p/344830045
 * https://wiki.vg/Zh:Microsoft_Authentication_Scheme
 * HMCL源码
 * FDP源码
 */

class MicrosoftLogin : Closeable {
    companion object {
        private const val CLIENT_ID = "67b74668-ef33-49c3-a75c-18cbb2481e0c"
        private const val REDIRECT_URI = "http://localhost:3434/sad"
        private const val SCOPE = "XboxLive.signin%20offline_access"

        private val URL = "https://login.live.com/oauth20_authorize.srf?client_id=<client_id>&redirect_uri=<redirect_uri>&response_type=code&display=touch&scope=<scope>"
                .replace("<client_id>", CLIENT_ID)
                .replace("<redirect_uri>", REDIRECT_URI)
                .replace("<scope>", SCOPE)

        private val gson = GsonBuilder().create()
        private val parser = JsonParser()
    }

    private val logger = LogManager.getLogger("Microsoft Login")
    private val httpServer : HttpServer?

    @Volatile
    var status = "Pending"
    @Volatile
    var logged = false
    @Volatile
    var uuid : String? = null
    @Volatile
    var userName : String? = null
    @Volatile
    var accessToken : String? = null
    @Volatile
    var refreshToken : String? = null

    constructor() {
        logger.info("Try to create http server")

        httpServer = HttpServer.create(InetSocketAddress("localhost",3434),0)
        httpServer.createContext("/sad") { exchange ->
            val query = exchange.requestURI.query

            if (query.contains("code")) {
                val code = query.split("code=")[1]

                val microsoftTokenAndRefreshToken = getMicrosoftTokenAndRefreshToken(code)
                val xBoxLiveToken = getXBoxLiveToken(microsoftTokenAndRefreshToken[0])

                @Suppress("SpellCheckingInspection")
                val xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken)
                val accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1])

                setStatusAndLog("Get information")

                val jsonObject = parser.parse(
                    get(
                        "https://api.minecraftservices.com/minecraft/profile",
                        mapOf(Pair("Authorization", "Bearer $accessToken"))
                    )
                ).asJsonObject

                this.uuid = jsonObject.get("id").asString
                this.userName = jsonObject.get("name").asString
                this.accessToken = accessToken
                this.refreshToken = microsoftTokenAndRefreshToken[1]
                this.logged = true

                setStatusAndLog("Login successful!")
            }
        }

        httpServer.start()

        logger.info("Start http server")

        logger.info("Opening browser")

        Sys.openURL(URL)
    }

    constructor(refreshToken : String) {
        this.refreshToken = refreshToken
        this.httpServer = null

        val microsoftTokenAndRefreshToken = getMicrosoftTokenFromRefreshToken(refreshToken)
        val xBoxLiveToken = getXBoxLiveToken(microsoftTokenAndRefreshToken)
        @Suppress("SpellCheckingInspection")
        val xstsTokenAndHash = getXSTSTokenAndUserHash(xBoxLiveToken)
        val accessToken = getAccessToken(xstsTokenAndHash[0], xstsTokenAndHash[1])

        setStatusAndLog("Get information")

        val jsonObject = parser.parse(
            get(
                "https://api.minecraftservices.com/minecraft/profile",
                mapOf(Pair("Authorization", "Bearer $accessToken"))
            )
        ).asJsonObject

        this.uuid = jsonObject.get("id").asString
        this.userName = jsonObject.get("name").asString
        this.accessToken = accessToken
        this.logged = true

        setStatusAndLog("Login successful!")
    }

    private fun getMicrosoftTokenFromRefreshToken(refreshToken : String) : String {
        setStatusAndLog("Try to get Microsoft Token from Refresh Token")

        val jsonObject = parser.parse(
            post(
                "https://login.live.com/oauth20_token.srf",
                "client_id=$CLIENT_ID&refresh_token=$refreshToken&grant_type=refresh_token&redirect_uri=$REDIRECT_URI",
                mapOf(Pair("Content-Type", "application/x-www-form-urlencoded"))
            )
        ).asJsonObject

        return jsonObject.get("access_token").asString
    }

    private fun getMicrosoftTokenAndRefreshToken(code : String) : Array<String> {
        setStatusAndLog("Try to get Microsoft Token and Refresh Token")

        val jsonObject = parser.parse(
            post(
                "https://login.live.com/oauth20_token.srf",
                "client_id=$CLIENT_ID&code=$code&grant_type=authorization_code&redirect_uri=$REDIRECT_URI&scope=$SCOPE",
                mapOf(Pair("Content-Type", "application/x-www-form-urlencoded"))
            )
        ).asJsonObject

        return arrayOf(
            jsonObject.get("access_token").asString,
            jsonObject.get("refresh_token").asString
        )
    }

    @Suppress("HttpUrlsUsage")
    private fun getXBoxLiveToken(microsoftToken : String) : String {
        setStatusAndLog("Try to get the X Box Live Token")

        val paramObj = JsonObject()
        val propertiesObj = JsonObject()

        propertiesObj.addProperty("AuthMethod", "RPS")
        propertiesObj.addProperty("SiteName", "user.auth.xboxlive.com")
        propertiesObj.addProperty("RpsTicket", "d=$microsoftToken")
        paramObj.add("Properties", propertiesObj)
        paramObj.addProperty("RelyingParty", "http://auth.xboxlive.com")
        paramObj.addProperty("TokenType", "JWT")

        val jsonObject = parser.parse(
            post(
                "https://user.auth.xboxlive.com/user/authenticate",
                gson.toJson(paramObj),
                mapOf(
                    Pair("Content-Type", "application/json"),
                    Pair("Accept", "application/json")
                )
            )
        ).asJsonObject

        return jsonObject.get("Token").asString
    }

    private fun getXSTSTokenAndUserHash(xboxLiveToken : String) : Array<String> {
        setStatusAndLog("Try to get the XSTS token and the User Hash")

        val paramObj = JsonObject()
        val propertiesObj = JsonObject()

        propertiesObj.addProperty("SandboxId", "RETAIL")
        propertiesObj.add("UserTokens", parser.parse(gson.toJson(arrayListOf(xboxLiveToken))))
        paramObj.add("Properties", propertiesObj)
        paramObj.addProperty("RelyingParty", "rp://api.minecraftservices.com/")
        paramObj.addProperty("TokenType", "JWT")

        val jsonObject = parser.parse(
            post(
                "https://xsts.auth.xboxlive.com/xsts/authorize",
                gson.toJson(paramObj),
                mapOf(Pair("Content-Type", "application/json"))
            )
        ).asJsonObject

        return arrayOf(
            jsonObject.get("Token").asString,
            jsonObject.get("DisplayClaims").asJsonObject.get("xui").asJsonArray.get(0).asJsonObject.get("uhs").asString
        )
    }

    @Suppress("SpellCheckingInspection")
    private fun getAccessToken(xstsToken : String, uhs : String) : String {
        setStatusAndLog("Try to get an Access Token")

        val paramObj = JsonObject()
        paramObj.addProperty("identityToken", "XBL3.0 x=$uhs;$xstsToken")

        val jsonObject = parser.parse(
            post(
                "https://api.minecraftservices.com/authentication/login_with_xbox",
                gson.toJson(paramObj),
                mapOf(
                    Pair("Content-Type", "application/json"),
                    Pair("Accept", "application/json")
                )
            )
        ).asJsonObject

        return jsonObject.get("access_token").asString
    }

    private fun setStatusAndLog(s : String) {
        logger.info(s)
        status = s
    }

    private fun post(urlString : String,param : String,requestProperty : Map<String,String>) : String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        connection.doInput = true
        connection.doOutput = true

        connection.useCaches = false

        connection.connectTimeout = 20000
        connection.readTimeout = 20000

        connection.requestMethod = "POST"

        requestProperty.forEach { (k, v) ->
            connection.setRequestProperty(k,v)
        }

        connection.outputStream.bufferedWriter(StandardCharsets.UTF_8).use { writer ->
            writer.write(param)
        }

        val readText : String

        connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            readText = reader.readText()
        }

        connection.disconnect()

        return readText
    }

    @Suppress("SameParameterValue")
    private fun get(urlString : String, requestProperty : Map<String,String>) : String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection

        connection.doInput = true
        connection.requestMethod = "GET"

        connection.connectTimeout = 20000
        connection.readTimeout = 20000

        requestProperty.forEach { (k, v) ->
            connection.setRequestProperty(k,v)
        }

        val readText : String

        connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
            readText = reader.readText()
        }

        connection.disconnect()

        return readText
    }

    override fun close() {
        httpServer?.stop(0)
    }
}