package com.acuy.sla_maintenance.config

import com.acuy.sla_maintenance.model.GetIJenisKategoriResponseItem
import com.acuy.sla_maintenance.model.GetITdanTolResponseItem
import com.acuy.sla_maintenance.model.GetJenisHardwareResponseItem
import com.acuy.sla_maintenance.model.GetJenisSoftwareResponseItem
import com.acuy.sla_maintenance.model.GetLokasiResponseItem
import com.acuy.sla_maintenance.model.GetTollById
import com.acuy.sla_maintenance.model.GetTollByUser
import com.acuy.sla_maintenance.model.GrafikJamKerjaResponse
import com.acuy.sla_maintenance.model.IdWorkerResponse
import com.acuy.sla_maintenance.model.JamKerjaResponse
import com.acuy.sla_maintenance.model.Login
import com.acuy.sla_maintenance.model.MaintananceResponse
import com.acuy.sla_maintenance.model.PendingResponse
import com.acuy.sla_maintenance.model.PostUsers
import com.acuy.sla_maintenance.model.ProsesResponse
import com.acuy.sla_maintenance.model.Registrasi
import com.acuy.sla_maintenance.model.ResponseListReport2
import com.acuy.sla_maintenance.model.SubmitReport
import com.acuy.sla_maintenance.model.UpdateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

//Ini untuk komunikasi dengan API
interface ApiServices {

    //    Login Register
    @FormUrlEncoded //untuk kirim data ke Http
    @POST("login") //post seperti postman
//    Login ke endpoint dengan parameter
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<Login> //memanggil respon yg ada pada model

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Registrasi>


//    //  Get  Report
//    @GET("toll")
//    fun getToll(): Call<ResponseListReport2>
    // Update the getToll() endpoint to accept page number as parameter

    @GET("toll")
    fun getToll(@Query("page") page: Int): Call<ResponseListReport2>

    //get tol by id
    @GET("toll/{id}")
    fun getTollById(@Path("id") id: Int): Call<GetTollById>

    @GET("toll")
    fun getTollFilter(@Query("company") company: String): Call<ResponseListReport2>

    @GET("toll")
    fun getTollbyStatus(@Query("status") company: String): Call<ResponseListReport2>

    @GET("toll")
    fun getTollbyLokasi(@Query("location_name") company: String): Call<ResponseListReport2>

//    @GET("toll")
//    fun getTollAllFilter(
//        @Query("company") company: String?,
//        @Query("status") status: String?,
//        @Query("lokasi_id") lokasiId: Int?
//    ): Call<ResponseListReport2>

    // Metode untuk mendapatkan daftar laporan dengan filter dan paginasi
    @GET("toll")
    fun getTollAllFilter(
        @Query("company") company: String?,
        @Query("status") status: String?,
        @Query("lokasi_id") lokasiId: Int?,
    ): Call<ResponseListReport2>

    @GET("toll/user/{userId}")
    fun getTollByUser(@Path("userId") userId: Int): Call<GetTollByUser>

    @GET("toll/user/{userId}")
    fun getTollFIlterUser(
        @Path("userId") userId: Int,
        @Query("lokasi_id") lokasiId: Int?
    ): Call<GetTollByUser>


    // worker

    @GET("activity_workers/{id}")
    fun getIdWorker(@Path("id") id: Int): Call<IdWorkerResponse>

    //prosses
    @Multipart
    @POST("activity_workers")
    fun postProses(
        @Part("activity_id") activity_id: RequestBody,
    ): Call<ProsesResponse>


    //pending
    @Multipart
    @POST("activity_workers/pending/{id}")
    fun postPending(
        @Path("id") id: Int,
        @Part("deskripsi_pending") deskripsiPending: RequestBody
    ): Call<PendingResponse>



//    @GET("activity_workers")
//    fun getWorkers(): Call<List<WorkersResponseItem>>


//

//    //    Post Report
//    @FormUrlEncoded
//    @POST("toll")
//    fun postToll(
////        @Field("id") id: Int,
////        @Field("user_id") user_id: Int,
//        @Field("company") company: String,
//        @Field("tanggal") tanggal: String,
//        @Field("jenis_hardware") jenis_hardware: String,
//        @Field("uraian_hardware") uraian_hardware: String,
//        @Field("standart_aplikasi") standart_aplikasi: String,
//        @Field("uraian_aplikasi") uraian_aplikasi: String,
//        @Field("aplikasi_it_tol") aplikasi_it_tol: String,
//        @Field("uraian_it_tol") uraian_it_tol: String,
//        @Field("catatan") catatan: String,
//        @Field("shift") shift: String,
//        @Field("lokasi_id") lokasi_id: Int,
//        @Field("kategori_id") kategori_id: Int,
//        @Field("biaya") biaya: Int,
////        @Field("foto_awal") foto_awal: String,
//        @Field("status") status: String
//    ): Call<SubmitReport>


    @Multipart
    @POST("toll")
    fun postToll(
        @Part("company") company: RequestBody,
//        @Part("tanggal") tanggal: RequestBody,
        @Part("kategori_activity") kategori_activity: RequestBody,
        @Part("jenis_hardware") jenis_hardware: RequestBody?,
        @Part("uraian_hardware") uraian_hardware: RequestBody?,
        @Part("standart_aplikasi") standart_aplikasi: RequestBody?,
        @Part("uraian_aplikasi") uraian_aplikasi: RequestBody?,
        @Part("aplikasi_it_tol") aplikasi_it_tol: RequestBody?,
        @Part("uraian_it_tol") uraian_it_tol: RequestBody?,
        @Part("catatan") catatan: RequestBody?,
        @Part("shift") shift: RequestBody,
        @Part("lokasi_id") lokasi_id: RequestBody,
        @Part("kategori_id") kategori_id: RequestBody,
//        @Part("biaya") biaya: RequestBody,
        @Part foto_awal: MultipartBody.Part,
    ): Call<SubmitReport>


    @GET("jenisHardware")
    fun getJenisHardware(@Header("Authorization") token: String): Call<List<GetJenisHardwareResponseItem>>

    @GET("jenisSoftware")
    fun getJenisSoftware(@Header("Authorization") token: String): Call<List<GetJenisSoftwareResponseItem>>

    @GET("aplikasi_it_tol")
    fun getITdanTol(@Header("Authorization") token: String): Call<List<GetITdanTolResponseItem>>

    @GET("kategori")
    fun getJenisKategori(@Header("Authorization") token: String): Call<List<GetIJenisKategoriResponseItem>>

    @GET("lokasi")
    fun getLokasi(@Header("Authorization") token: String): Call<List<GetLokasiResponseItem>>


    @POST("user")
    fun getUsers(@Header("Authorization") token: String): Call<PostUsers>

    @Multipart
    @POST("users/update/{id}")
    fun updateProfile(
        @Path("id") id: Int,
        @Part("username") username: RequestBody?,
        @Part ttd: MultipartBody.Part? = null,
        @Part foto: MultipartBody.Part? = null,
    ): Call<UpdateResponse>

    @Multipart
    @POST("toll/{id}/status")
    fun maintanance(
        @Path("id") id: Int,
        @Part fotoAkhir: MultipartBody.Part?,
        @Part("kondisi_akhir") kondisiAkhir: RequestBody
    ): Call<MaintananceResponse>

    @Multipart
    @POST("activity_workers/end/{id}")
    fun postDone(
        @Path("id") id: Int,
        @Part fotoAkhir: MultipartBody.Part?,
        @Part("kondisi_akhir") kondisiAkhir: RequestBody,
        @Part("biaya") biaya: RequestBody
    ): Call<MaintananceResponse>



    @GET("activity_workers/user/{id}")
    fun getJamKerja(@Path("id") id: Int): Call<JamKerjaResponse>

    @GET("activity_workers/user/{id}")
    fun getJamKerjaFilter(
        @Path("id") id: Int,
        @Query("month") month: Int?,
        @Query("year") year: Int?,
    ): Call<JamKerjaResponse>

    @GET("activity_workers/grafik/user/{id}/{year}")
    fun getGrafikJamKerja(@Path("id") id: Int, @Path("year") year: Int): Call<GrafikJamKerjaResponse>

    @GET("toll")
    fun getTollJamKerja(): Call<ResponseListReport2>


    //total api 22
}

