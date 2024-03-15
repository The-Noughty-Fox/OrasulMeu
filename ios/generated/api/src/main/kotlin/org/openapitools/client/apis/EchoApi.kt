package org.openapitools.client.apis

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName


interface EchoApi {
    /**
     * 
     * 
     * Responses:
     *  - 0: 
     *
     * @return [kotlin.String]
     */
    @GET("echo")
    suspend fun getEcho(): Response<kotlin.String>

}
