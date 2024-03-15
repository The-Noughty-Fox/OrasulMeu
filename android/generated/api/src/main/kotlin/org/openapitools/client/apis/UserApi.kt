package org.openapitools.client.apis

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import org.openapitools.client.models.UserDto
import org.openapitools.client.models.UserUpdateDto

interface UserApi {
    /**
     * 
     * 
     * Responses:
     *  - 201: 
     *
     * @param userDto 
     * @return [Unit]
     */
    @POST("user")
    suspend fun createUser(@Body userDto: UserDto): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @param id 
     * @return [Unit]
     */
    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: java.math.BigDecimal): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 0: 
     *
     * @param sortType  (optional)
     * @param page  (optional, default to 0)
     * @param limit  (optional, default to 10)
     * @param sortBy  (optional)
     * @param searchBy  (optional)
     * @return [kotlin.collections.List<UserDto>]
     */
    @GET("user")
    suspend fun getManyUsers(@Query("sortType") sortType: kotlin.String? = null, @Query("page") page: java.math.BigDecimal? = java.math.BigDecimal("0"), @Query("limit") limit: java.math.BigDecimal? = java.math.BigDecimal("10"), @Query("sortBy") sortBy: kotlin.collections.List<kotlin.String>? = null, @Query("searchBy") searchBy: kotlin.String? = null): Response<kotlin.collections.List<UserDto>>

    /**
     * 
     * 
     * Responses:
     *  - 200: The user has been successfully authenticated.
     *
     * @param id 
     * @return [UserDto]
     */
    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: java.math.BigDecimal): Response<UserDto>

    /**
     * 
     * 
     * Responses:
     *  - 200: The user has been successfully updated.
     *
     * @param userUpdateDto User model for update
     * @return [Unit]
     */
    @PATCH("user")
    suspend fun updateUser(@Body userUpdateDto: UserUpdateDto): Response<Unit>

}
