package com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.file;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface FileService {

    @GET
    @Streaming
    Single<Response<ResponseBody>> downloadFile(@Url String fileUrl);
}
