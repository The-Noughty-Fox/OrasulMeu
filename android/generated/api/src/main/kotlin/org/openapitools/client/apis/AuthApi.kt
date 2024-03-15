package org.openapitools.client.apis

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import org.openapitools.client.models.AppleToken
import org.openapitools.client.models.BaseUserDto
import org.openapitools.client.models.Token

interface AuthApi {
    /**
     * 
     * 
     * Responses:
     *  - 201: The user has been successfully authenticated.
     *
     * @param appleToken 
     * @return [BaseUserDto]
     */
    @POST("auth/apple")
    suspend fun authenticateApple(@Body appleToken: AppleToken): Response<BaseUserDto>

    /**
     * 
     * 
     * Responses:
     *  - 201: The user has been successfully authenticated.
     *
     * @param token 
     * @return [BaseUserDto]
     */
    @POST("auth/google")
    suspend fun authenticateGoogle(@Body token: Token): Response<BaseUserDto>

    /**
     * 
     * 
     * Responses:
     *  - 201: The user has been successfully authenticated.
     *
     * @param token 
     * @return [BaseUserDto]
     */
    @POST("auth/facebook")
    suspend fun authenticateWithFacebookPo(@Body token: Token): Response<BaseUserDto>

}
