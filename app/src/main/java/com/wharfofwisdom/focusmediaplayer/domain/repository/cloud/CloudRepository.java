package com.wharfofwisdom.focusmediaplayer.domain.repository.cloud;

import android.net.Uri;
import android.os.Environment;

import com.wharfofwisdom.focusmediaplayer.domain.interactor.FileRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.cloud.kiosk.file.FileService;

import java.io.File;
import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Response;

public class CloudRepository implements FileRepository {
    private final FileService fileService;

    public CloudRepository(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public Single<File> getFile(Uri uri) {
        return fileService.downloadFile(uri.toString()).flatMap(response -> saveToDisk(response, uri));
    }

    private Single<File> saveToDisk(Response<ResponseBody> response, Uri uri) {
        return Single.create(emitter -> {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsoluteFile(), uri.toString());
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                assert response.body() != null;
                sink.writeAll(response.body().source());
                sink.close();
                emitter.onSuccess(file);
            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }
}
