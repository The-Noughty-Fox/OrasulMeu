package org.openapitools.client.apis

import org.openapitools.client.infrastructure.CollectionFormats.*
import retrofit2.http.*
import retrofit2.Response
import okhttp3.RequestBody
import com.google.gson.annotations.SerializedName


interface SubCategoryApi {
    /**
     * 
     * 
     * Responses:
     *  - 201: 
     *
     * @return [Unit]
     */
    @POST("sub-category")
    suspend fun createSubcategory(): Response<Unit>

    /**
     * 
     * 
     * Responses:
     *  - 200: 
     *
     * @return [Unit]
     */
    @GET("sub-category")
    suspend fun getSubcategory(): Response<Unit>

}
