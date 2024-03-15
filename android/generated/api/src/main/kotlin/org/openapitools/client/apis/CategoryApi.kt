package org.openapitools.client.apis

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName

import org.openapitools.client.models.CategoryDto

interface CategoryApi {
    /**
     * 
     * 
     * Responses:
     *  - 201: 
     *
     * @param categoryDto 
     * @return [Unit]
     */
    @POST("category")
    suspend fun createCategory(@Body categoryDto: CategoryDto): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @param id 
     * @return [Unit]
     */
    @DELETE("category/{id}")
    suspend fun deleteCategory(@Path("id") id: java.math.BigDecimal): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @param id 
     * @return [Unit]
     */
    @GET("category/{id}")
    suspend fun getCategory(@Path("id") id: java.math.BigDecimal): Response<Unit>

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
    @GET("category")
    suspend fun getManyCategories(@Query("sortType") sortType: kotlin.String? = null, @Query("page") page: java.math.BigDecimal? = java.math.BigDecimal("0"), @Query("limit") limit: java.math.BigDecimal? = java.math.BigDecimal("10"), @Query("sortBy") sortBy: kotlin.collections.List<kotlin.String>? = null, @Query("searchBy") searchBy: kotlin.String? = null): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @param categoryDto 
     * @return [Unit]
     */
    @PATCH("category")
    suspend fun updateCategory(@Body categoryDto: CategoryDto): Response<Unit>

}
