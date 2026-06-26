package poebuildexporter.code.util;

import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Web {

    private static OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build();

    private Request request;

    public Web(String url) {
        request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
                .build();
    }

    public Web addHeader(String name, String val) {
        request = request.newBuilder().addHeader(name, val).build();
        return this;
    }

    public Web addBody(String body) {
        RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), body);
        request = request.newBuilder().post(requestBody).build();
        return this;
    }

    public String getResponse() {
        try (Response response = client.newCall(request).execute()) {
            if (null != response && response.body() != null) {
                try {
                    return response.body().string();
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
