package org.openapitools.client.apis

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import org.openapitools.client.models.ActivityDto

interface ActivityApi {
    /**
     * 
     * 
     * Responses:
     *  - 201: 
     *
     * @param activityDto 
     * @return [Unit]
     */
    @POST("activity")
    suspend fun createActivity(@Body activityDto: ActivityDto): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @param id 
     * @return [Unit]
     */
    @DELETE("activity/{id}")
    suspend fun deleteActivity(@Path("id") id: java.math.BigDecimal): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @param id 
     * @return [Unit]
     */
    @GET("activity/{id}")
    suspend fun getActivity(@Path("id") id: java.math.BigDecimal): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @param sortType  (optional)
     * @param page  (optional, default to 0)
     * @param limit  (optional, default to 10)
     * @param sortBy  (optional)
     * @param searchBy  (optional)
     * @return [Unit]
     */
    @GET("activity")
    suspend fun getManyActivities(@Query("sortType") sortType: kotlin.String? = null, @Query("page") page: java.math.BigDecimal? = java.math.BigDecimal("0"), @Query("limit") limit: java.math.BigDecimal? = java.math.BigDecimal("10"), @Query("sortBy") sortBy: kotlin.collections.List<kotlin.String>? = null, @Query("searchBy") searchBy: kotlin.String? = null): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @param activityDto 
     * @return [Unit]
     */
    @PATCH("activity")
    suspend fun updateActivity(@Body activityDto: ActivityDto): Response<Unit>

}
